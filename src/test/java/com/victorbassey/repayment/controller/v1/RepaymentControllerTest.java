package com.victorbassey.repayment.controller.v1;

import com.victorbassey.repayment.payload.ProposedChanges;
import com.victorbassey.repayment.service.RepaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RepaymentControllerTest {

    @Mock
    RepaymentService repaymentService;

    @InjectMocks
    RepaymentController repaymentController;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(repaymentController).build();
    }

    @Test
    void getProposedChangesWithValidArguments() throws Exception {
        ProposedChanges proposedChanges = new ProposedChanges();
        when(repaymentService.getCustomerSummaryForUpdate(anyLong(), anyLong(), anyLong()))
                .thenReturn(proposedChanges);

        mockMvc.perform(get("/api/v1/repayments/proposed-changes?customerId=3&amount=200&seasonId=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());;
    }

    @Test
    void handleCustomerSummaryRequestWithInvalidArguments() throws Exception {
        when(repaymentService.getCustomerSummaryForUpdate(anyLong(), anyLong(), anyLong()))
                .thenThrow(IllegalArgumentException.class);
        try {
            mockMvc.perform(get("/api/v1/repayments/proposed-changes?customerId=-5&amount=200&seasonId=-5"));
        } catch (NestedServletException e) {
            assertEquals(IllegalArgumentException.class, e.getRootCause().getClass());
        }
    }
}