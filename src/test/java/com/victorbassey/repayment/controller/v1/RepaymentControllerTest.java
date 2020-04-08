package com.victorbassey.repayment.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.victorbassey.repayment.payload.ProposedChanges;
import com.victorbassey.repayment.payload.RepaymentData;
import com.victorbassey.repayment.service.RepaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    void repayDebtsWithValidRequestBody() throws Exception {
        List<RepaymentData> repaymentDataList = List.of(new RepaymentData(), new RepaymentData());
        when(repaymentService.repayDebts(anyList()))
                .thenReturn(repaymentDataList);

        mockMvc.perform(post("/api/v1/repayments/repay")
                .content(asJsonString(repaymentDataList))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Successfully repaid debts"));
    }

    @Test
    void returnNotFoundStatusWhenRouteDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/repayments/find"))
                .andExpect(status().isNotFound());
    }

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}