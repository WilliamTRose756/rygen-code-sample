package dev.rygen.intersectionlightcontroller.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.rygen.intersectionlightcontroller.dtos.ConfigDTO;
import dev.rygen.intersectionlightcontroller.services.ConfigService;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ConfigController.class)
class ConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConfigService configService;

    @Test
    void getConfigReturnsCurrentConfig() throws Exception {
        ConfigDTO config = new ConfigDTO(true, "medium", Map.of("roadA", 2, "roadB", 2));
        when(configService.getConfig()).thenReturn(config);

        mockMvc.perform(get("/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.powerOn").value(true))
                .andExpect(jsonPath("$.speed").value("medium"))
                .andExpect(jsonPath("$.lightBrightness.roadA").value(2))
                .andExpect(jsonPath("$.lightBrightness.roadB").value(2));
    }
}
