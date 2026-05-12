$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
$port = "4649"
$process = $null
$stdout = Join-Path $repoRoot "screenshots\\spring-boot.stdout.log"
$stderr = Join-Path $repoRoot "screenshots\\spring-boot.stderr.log"
$edgeCandidates = @(
    "C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe",
    "C:\Program Files\Microsoft\Edge\Application\msedge.exe"
)

function Get-EdgePath {
    foreach ($candidate in $edgeCandidates) {
        if (Test-Path $candidate) {
            return $candidate
        }
    }
    throw "Microsoft Edge was not found."
}

function Wait-ForUrl {
    param([string]$Url)
    for ($i = 0; $i -lt 60; $i++) {
        try {
            Invoke-WebRequest -Uri $Url -UseBasicParsing | Out-Null
            return
        } catch {
            Start-Sleep -Seconds 1
        }
    }
    throw "Timed out waiting for $Url"
}

try {
    $env:PORT = $port
    $process = Start-Process -FilePath (Join-Path $repoRoot "mvnw.cmd") `
        -ArgumentList "spring-boot:run" `
        -WorkingDirectory $repoRoot `
        -RedirectStandardOutput $stdout `
        -RedirectStandardError $stderr `
        -PassThru

    Wait-ForUrl "http://127.0.0.1:$port/"

    $edge = Get-EdgePath
    $targets = @(
        @{ Url = "http://127.0.0.1:$port/"; File = "01-overview.png" },
        @{ Url = "http://127.0.0.1:$port/journeys"; File = "02-journeys.png" },
        @{ Url = "http://127.0.0.1:$port/verification"; File = "03-verification.png" },
        @{ Url = "http://127.0.0.1:$port/topology"; File = "04-topology.png" }
    )

    foreach ($target in $targets) {
        & $edge `
            --headless `
            --disable-gpu `
            --hide-scrollbars `
            --window-size=1440,920 `
            "--screenshot=$(Join-Path $repoRoot "screenshots\\$($target.File)")" `
            $target.Url | Out-Null
    }
} finally {
    if ($process -and -not $process.HasExited) {
        Stop-Process -Id $process.Id -Force
    }
}
