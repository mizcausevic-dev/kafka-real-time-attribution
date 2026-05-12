package com.mizcausevic.kafkarealtimeattribution;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.mizcausevic.kafkarealtimeattribution.services.AttributionService;

@SpringBootTest
@AutoConfigureMockMvc
class AttributionServiceTests {
    @Autowired
    private AttributionService attributionService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void dashboardSummarizesRevenue() {
        var summary = attributionService.dashboardSummary();
        assertThat(summary.touchCount()).isEqualTo(9);
        assertThat(summary.totalRevenue()).isGreaterThan(100000);
        assertThat(summary.topChannel()).isNotBlank();
    }

    @Test
    void sampleEndpointReturnsPayload() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/sample"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.dashboard.touchCount").value(9))
                .andExpect(MockMvcResultMatchers.jsonPath("$.channels[0].channel").isNotEmpty());
    }
}
