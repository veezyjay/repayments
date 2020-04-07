package com.victorbassey.repayment.service;

import com.victorbassey.repayment.exception.ResourceNotFoundException;
import com.victorbassey.repayment.model.CustomerSummary;
import com.victorbassey.repayment.payload.ProposedChanges;
import com.victorbassey.repayment.repository.CustomerSummaryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepaymentServiceImplTest {

    @Mock
    CustomerSummaryRepository customerSummaryRepository;

    @InjectMocks
    RepaymentServiceImpl repaymentService;

    @Test
    void getCustomerSummaryForUpdateOverride() {
        CustomerSummary summary = new CustomerSummary();
        summary.setCustomerId(1L);
        summary.setTotalRepaid(7200L);
        summary.setTotalCredit(7200L);
        summary.setSeasonId(110L);
        when(customerSummaryRepository.findByCustomerIdAndSeasonId(anyLong(), anyLong()))
                .thenReturn(Optional.of(summary));

        ProposedChanges proposedChanges = repaymentService.getCustomerSummaryForUpdate(1L, 300L, 110L);
        Long amountToAdd = proposedChanges.getSummariesToUpdate().get(0).getAmountToAdd();
        Long seasonId = proposedChanges.getSummariesToUpdate().get(0).getSummary().getSeasonId();
        assertNotNull(proposedChanges);
        assertEquals(300L, amountToAdd);
        assertEquals(110L, seasonId);
    }

    @Test
    void getCustomerSummaryForUpdateOverpaid() {
        CustomerSummary summary = new CustomerSummary();
        summary.setCustomerId(4L);
        summary.setTotalRepaid(6200L);
        summary.setTotalCredit(7200L);
        summary.setSeasonId(180L);

        when(customerSummaryRepository.findCustomerSummariesWithDebts(anyLong()))
                .thenReturn(Collections.emptyList());
        when(customerSummaryRepository.findMostRecentCustomerSummary(anyLong()))
                .thenReturn(Optional.of(summary));

        ProposedChanges proposedChanges = repaymentService.getCustomerSummaryForUpdate(1L, 600L, 0L);
        Long amountToAdd = proposedChanges.getSummariesToUpdate().get(0).getAmountToAdd();
        assertEquals(600L, amountToAdd);
        assertNotNull(proposedChanges.getRepaymentUpload());
    }

    @Test
    void getCustomerSummaryForUpdateCascadeWithFewDebts() {
        List<CustomerSummary> customerSummaries = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            CustomerSummary summary = new CustomerSummary();
            summary.setCustomerId(1L);
            summary.setSeasonId((long) (i * 10));
            summary.setTotalRepaid((long) (i * 80));
            summary.setTotalCredit((long) (i * 100));
            customerSummaries.add(summary);
        }
        when(customerSummaryRepository.findCustomerSummariesWithDebts(anyLong()))
                .thenReturn(customerSummaries);

        ProposedChanges proposedChanges = repaymentService.getCustomerSummaryForUpdate(1L, 600L, 0L);
        Long totalAmount = proposedChanges.getRepaymentUpload().getAmount();
        Long firstSummaryAmountToAdd = proposedChanges.getSummariesToUpdate().get(0).getAmountToAdd();
        Long secondSummaryAmountToAdd = proposedChanges.getSummariesToUpdate().get(1).getAmountToAdd();
        assertEquals(600L, totalAmount);
        assertEquals(20L, firstSummaryAmountToAdd);
        assertEquals(580L, secondSummaryAmountToAdd);
    }

    @Test
    void getCustomerSummaryForUpdateCascadeWithMultipleDebtsAndLittleAmount() {
        List<CustomerSummary> customerSummaries = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            CustomerSummary summary = new CustomerSummary();
            summary.setCustomerId(1L);
            summary.setSeasonId((long) (i * 10));
            summary.setTotalRepaid((long) (i * 80));
            summary.setTotalCredit((long) (i * 100));
            customerSummaries.add(summary);
        }
        when(customerSummaryRepository.findCustomerSummariesWithDebts(anyLong()))
                .thenReturn(customerSummaries);

        ProposedChanges proposedChanges = repaymentService.getCustomerSummaryForUpdate(1L, 10L, 0L);
        Long totalAmount = proposedChanges.getRepaymentUpload().getAmount();
        Long firstSummaryAmountToAdd = proposedChanges.getSummariesToUpdate().get(0).getAmountToAdd();
        assertEquals(1, proposedChanges.getSummariesToUpdate().size());
        assertEquals(10L, totalAmount);
        assertEquals(10L, firstSummaryAmountToAdd);
    }

    @Test
    void getCustomerSummaryForUpdateCascadeWithMultipleDebtsAndLargeAmount() {
        List<CustomerSummary> customerSummaries = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            CustomerSummary summary = new CustomerSummary();
            summary.setCustomerId(3L);
            summary.setSeasonId((long) (i * 10));
            summary.setTotalRepaid((long) (i * 80));
            summary.setTotalCredit((long) (i * 100));
            customerSummaries.add(summary);
        }
        when(customerSummaryRepository.findCustomerSummariesWithDebts(anyLong()))
                .thenReturn(customerSummaries);

        ProposedChanges proposedChanges = repaymentService.getCustomerSummaryForUpdate(1L, 1000L, 0L);
        Long totalAmount = proposedChanges.getRepaymentUpload().getAmount();
        Long firstSummaryAmountToAdd = proposedChanges.getSummariesToUpdate().get(0).getAmountToAdd();
        int lastSummaryIndex = proposedChanges.getSummariesToUpdate().size() - 1;
        Long lastSummaryAmountToAdd = proposedChanges.getSummariesToUpdate().get(lastSummaryIndex).getAmountToAdd();
        assertEquals(4, proposedChanges.getSummariesToUpdate().size());
        assertEquals(1000L, totalAmount);
        assertEquals(20L, firstSummaryAmountToAdd);
        assertEquals(880L, lastSummaryAmountToAdd);
    }

    @Test
    void getCustomerSummaryThatDoesNotExist() {
        when(customerSummaryRepository.findByCustomerIdAndSeasonId(anyLong(), anyLong()))
                .thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> repaymentService
                .getCustomerSummaryForUpdate(1000L, 300L, 110L));

    }

}