package com.victorbassey.repayment.service;

import com.victorbassey.repayment.exception.ResourceNotFoundException;
import com.victorbassey.repayment.model.CustomerSummary;
import com.victorbassey.repayment.model.Repayment;
import com.victorbassey.repayment.model.RepaymentUpload;
import com.victorbassey.repayment.payload.ProposedChanges;
import com.victorbassey.repayment.payload.RepaymentData;
import com.victorbassey.repayment.repository.CustomerSummaryRepository;
import com.victorbassey.repayment.repository.RepaymentRepository;
import com.victorbassey.repayment.repository.RepaymentUploadRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepaymentServiceImplTest {

    @Mock
    CustomerSummaryRepository customerSummaryRepository;

    @Mock
    RepaymentRepository repaymentRepository;

    @Mock
    RepaymentUploadRepository repaymentUploadRepository;

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

    @Test
    void repayDebtsForOverrideSingleUploadInList() {
        RepaymentUpload repaymentUpload = new RepaymentUpload(4L, 400L, 180L);
        List<RepaymentUpload> uploads = new ArrayList<>();
        uploads.add(repaymentUpload);
        CustomerSummary summary = new CustomerSummary();
        summary.setCustomerId(4L);
        summary.setTotalRepaid(6200L);
        summary.setTotalCredit(7200L);
        summary.setSeasonId(180L);
        Repayment repayment = new Repayment(4L, 180L, 400L);

        when(customerSummaryRepository.findByCustomerIdAndSeasonId(anyLong(), anyLong()))
                .thenReturn(Optional.of(summary));
        when(customerSummaryRepository.save(any())).thenReturn(summary);
        when(repaymentRepository.save(any())).thenReturn(repayment);
        when(repaymentUploadRepository.save(any())).thenReturn(repaymentUpload);

        List<RepaymentData> repaymentData = repaymentService.repayDebts(uploads);
        assertEquals(1, repaymentData.size());
        assertNotNull(repaymentData.get(0).getOriginalRepayment());
        assertNotNull(repaymentData.get(0).getUpdatedSummaries());
        assertNull(repaymentData.get(0).getAdjustmentRepayments());
        assertEquals(400L, repaymentData.get(0).getOriginalRepayment().getAmount());
    }

    @Test
    void repayDebtsForOverpaidSingleUploadInList() {
        RepaymentUpload repaymentUpload = new RepaymentUpload(4L, 300L, 0L);
        List<RepaymentUpload> uploads = new ArrayList<>();
        uploads.add(repaymentUpload);
        CustomerSummary summary = new CustomerSummary();
        summary.setCustomerId(4L);
        summary.setTotalRepaid(6200L);
        summary.setTotalCredit(7200L);
        summary.setSeasonId(180L);
        Repayment repayment = new Repayment(4L, 150L, 300L);

        when(customerSummaryRepository.findCustomerSummariesWithDebts(anyLong()))
                .thenReturn(Collections.emptyList());
        when(customerSummaryRepository.findMostRecentCustomerSummary(anyLong()))
                .thenReturn(Optional.of(summary));
        when(customerSummaryRepository.save(any())).thenReturn(summary);
        when(repaymentRepository.save(any())).thenReturn(repayment);
        when(repaymentUploadRepository.save(any())).thenReturn(repaymentUpload);

        List<RepaymentData> repaymentData = repaymentService.repayDebts(uploads);
        assertEquals(1, repaymentData.size());
        assertNotNull(repaymentData.get(0).getOriginalRepayment());
        assertNotNull(repaymentData.get(0).getUpdatedSummaries());
        assertNull(repaymentData.get(0).getAdjustmentRepayments());
        assertEquals(300L, repaymentData.get(0).getOriginalRepayment().getAmount());
    }

    @Test
    void repayDebtsForCascadeSingleUploadInListWithLargeAmount() {
        RepaymentUpload repaymentUpload = new RepaymentUpload(4L, 2500L, 0L);
        List<RepaymentUpload> uploads = new ArrayList<>();
        uploads.add(repaymentUpload);
        CustomerSummary summary1 = new CustomerSummary();
        summary1.setCustomerId(4L);
        summary1.setTotalRepaid(6200L);
        summary1.setTotalCredit(7200L);
        summary1.setSeasonId(180L);
        CustomerSummary summary2 = new CustomerSummary();
        summary2.setCustomerId(4L);
        summary2.setTotalRepaid(4000L);
        summary2.setTotalCredit(4500L);
        summary2.setSeasonId(180L);
        CustomerSummary summary3 = new CustomerSummary();
        summary3.setCustomerId(4L);
        summary3.setTotalRepaid(900L);
        summary3.setTotalCredit(1500L);
        summary3.setSeasonId(180L);
        Repayment repayment = new Repayment(4L, 150L, 2500L);

        when(customerSummaryRepository.findCustomerSummariesWithDebts(anyLong()))
                .thenReturn(List.of(summary1, summary2, summary3));
        when(customerSummaryRepository.save(any())).thenReturn(summary1);
        when(repaymentRepository.save(any())).thenReturn(repayment);
        when(repaymentUploadRepository.save(any())).thenReturn(repaymentUpload);

        List<RepaymentData> repaymentData = repaymentService.repayDebts(uploads);
        assertEquals(1, repaymentData.size());
        assertNotNull(repaymentData.get(0).getOriginalRepayment());
        assertNotNull(repaymentData.get(0).getUpdatedSummaries());
        assertNotNull(repaymentData.get(0).getAdjustmentRepayments());
        assertEquals(2, repaymentData.get(0).getAdjustmentRepayments().size());
        assertEquals(2500L, repaymentData.get(0).getOriginalRepayment().getAmount());
    }

    @Test
    void repayDebtsForCascadeSingleUploadInListWithLittleAmount() {
        RepaymentUpload repaymentUpload = new RepaymentUpload(4L, 300L, 0L);
        List<RepaymentUpload> uploads = new ArrayList<>();
        uploads.add(repaymentUpload);
        CustomerSummary summary1 = new CustomerSummary();
        summary1.setCustomerId(4L);
        summary1.setTotalRepaid(6200L);
        summary1.setTotalCredit(7200L);
        summary1.setSeasonId(180L);
        CustomerSummary summary2 = new CustomerSummary();
        summary2.setCustomerId(4L);
        summary2.setTotalRepaid(4000L);
        summary2.setTotalCredit(4500L);
        summary2.setSeasonId(180L);
        Repayment repayment = new Repayment(4L, 150L, 300L);

        when(customerSummaryRepository.findCustomerSummariesWithDebts(anyLong()))
                .thenReturn(List.of(summary1, summary2));
        when(customerSummaryRepository.save(any())).thenReturn(summary1);
        when(repaymentRepository.save(any())).thenReturn(repayment);
        when(repaymentUploadRepository.save(any())).thenReturn(repaymentUpload);

        List<RepaymentData> repaymentData = repaymentService.repayDebts(uploads);
        assertEquals(1, repaymentData.size());
        assertNotNull(repaymentData.get(0).getOriginalRepayment());
        assertNotNull(repaymentData.get(0).getUpdatedSummaries());
        assertNull(repaymentData.get(0).getAdjustmentRepayments());
        assertEquals(300L, repaymentData.get(0).getOriginalRepayment().getAmount());
    }

    @Test
    void repayDebtsForCascadeMultipleUploadInList() {
        RepaymentUpload upload1 = new RepaymentUpload(4L, 4300L, 0L);
        RepaymentUpload upload2 = new RepaymentUpload(2L, 300L, 0L);
        RepaymentUpload upload3 = new RepaymentUpload(1L, 500L, 110L);
        List<RepaymentUpload> uploads = List.of(upload1, upload2, upload3);
        CustomerSummary summary1 = new CustomerSummary();
        summary1.setCustomerId(4L);
        summary1.setTotalRepaid(6200L);
        summary1.setTotalCredit(7200L);
        summary1.setSeasonId(180L);
        CustomerSummary summary2 = new CustomerSummary();
        summary2.setCustomerId(4L);
        summary2.setTotalRepaid(4000L);
        summary2.setTotalCredit(4500L);
        summary2.setSeasonId(180L);
        CustomerSummary summary3 = new CustomerSummary();
        summary3.setCustomerId(1L);
        summary3.setTotalRepaid(900L);
        summary3.setTotalCredit(1500L);
        summary3.setSeasonId(150L);
        Repayment repayment = new Repayment(4L, 150L, 300L);

        // for override
        when(customerSummaryRepository.findByCustomerIdAndSeasonId(1L, 110L))
                .thenReturn(Optional.of(summary3));
        // for cascade
        when(customerSummaryRepository.findCustomerSummariesWithDebts(4L))
                .thenReturn(List.of(summary1, summary2));
        // for overpaid
        when(customerSummaryRepository.findCustomerSummariesWithDebts(2L))
                .thenReturn(Collections.emptyList());
        when(customerSummaryRepository.findMostRecentCustomerSummary(anyLong()))
                .thenReturn(Optional.of(summary1));

        when(customerSummaryRepository.save(any())).thenReturn(summary1);
        when(repaymentRepository.save(any())).thenReturn(repayment);
        when(repaymentUploadRepository.save(any())).thenReturn(upload1);



        List<RepaymentData> repaymentData = repaymentService.repayDebts(uploads);
        assertEquals(3, repaymentData.size());
        for (int i = 0; i < 3; i++) {
            assertNotNull(repaymentData.get(0).getOriginalRepayment());
            assertNotNull(repaymentData.get(0).getUpdatedSummaries());
        }
        // Check that the cascade payment produces adjustment repayments
        assertNotNull(repaymentData.get(0).getAdjustmentRepayments());
        // Check that the overpaid payment does not produce adjustment repayments
        assertNull(repaymentData.get(1).getAdjustmentRepayments());
        // Check that the override payment does not produce adjustment repayments
        assertNull(repaymentData.get(2).getAdjustmentRepayments());
    }

    @Test
    void throwErrorWhenArgumentsAreInvalid() {
        assertThrows(IllegalArgumentException.class, () -> repaymentService
                .getCustomerSummaryForUpdate(10L, -300L, 110L));

    }

}