package com.victorbassey.repayment.controller.v1;

import com.victorbassey.repayment.model.Season;
import com.victorbassey.repayment.service.SeasonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SeasonControllerTest {

    @Mock
    SeasonService seasonService;

    @InjectMocks
    SeasonController seasonController;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(seasonController).build();
    }

    @Test
    void getSeasons() throws Exception {
        List<Season> theSeasons = List.of(new Season(), new Season(), new Season());
        when(seasonService.getAllSeasons()).thenReturn(theSeasons);

        mockMvc.perform(get("/api/v1/seasons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }
}