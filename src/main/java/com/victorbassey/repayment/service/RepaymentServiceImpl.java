package com.victorbassey.repayment.service;

import com.victorbassey.repayment.exception.ResourceNotFoundException;
import com.victorbassey.repayment.model.CustomerSummary;
import com.victorbassey.repayment.model.RepaymentUpload;
import com.victorbassey.repayment.payload.ProposedChanges;
import com.victorbassey.repayment.payload.RepaymentData;
import com.victorbassey.repayment.payload.SummaryToUpdate;
import com.victorbassey.repayment.repository.CustomerSummaryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepaymentServiceImpl implements RepaymentService {

    private CustomerSummaryRepository customerSummaryRepository;

    public RepaymentServiceImpl(CustomerSummaryRepository customerSummaryRepository) {
        this.customerSummaryRepository = customerSummaryRepository;
    }

    /**
     * Repays the debts of different customers provided in the list of repayment upload
     * @param repaymentUploads, data for the upload
     * @return list of repayments with their respective adjustment repayments
     */
    @Override
    public List<RepaymentData> repayDebts(List<RepaymentUpload> repaymentUploads) {
        return null;
    }

    /**
     * Gets customer summaries of a particular customer that would be updated based on the amount
     * @param customerId, the ID of the customer
     * @param amount, the amount to be added
     * @param seasonId, the ID of the season. It may or may not be provided
     * @return the changes that would be made if the repayment is successful
     */
    @Override
    public ProposedChanges getCustomerSummaryForUpdate(Long customerId, Long amount, Long seasonId) {
        ProposedChanges proposedChanges = new ProposedChanges();

        if (seasonId != null && seasonId != 0) {
            setProposedChangesForOverride(customerId, amount, seasonId, proposedChanges);
        } else {
            List<CustomerSummary> summariesWithDebts = customerSummaryRepository
                    .findCustomerSummariesWithDebts(customerId);
            if (summariesWithDebts.isEmpty()) {
                setProposedChangesForOverPaid(customerId, amount, proposedChanges);
            } else {
                setProposedChangesForCascade(customerId, amount, proposedChanges, summariesWithDebts);
            }
        }

        return proposedChanges;
    }

    private void setProposedChangesForCascade(Long customerId, Long amount, ProposedChanges proposedChanges,
                                              List<CustomerSummary> summariesWithDebts) {
        long balance = amount;
        RepaymentUpload repaymentUpload = new RepaymentUpload(customerId, amount);
        proposedChanges.setRepaymentUpload(repaymentUpload);
        for (int i = 0; i < summariesWithDebts.size(); i++) {
            CustomerSummary currentSummary = summariesWithDebts.get(i);
            SummaryToUpdate summaryToUpdate = new SummaryToUpdate(currentSummary, balance);
            long debt = currentSummary.getTotalCredit() - currentSummary.getTotalRepaid();
            balance -= debt;
            if (balance <= 0) {
                proposedChanges.getSummariesToUpdate().add(summaryToUpdate);
                break;
            }
            if (currentSummary.getTotalRepaid() + summaryToUpdate.getAmountToAdd() > currentSummary.getTotalCredit()
                    && i != summariesWithDebts.size() - 1) {
                summaryToUpdate.setAmountToAdd(summaryToUpdate.getAmountToAdd() - balance);
            }
            proposedChanges.getSummariesToUpdate().add(summaryToUpdate);
        }
    }

    private void setProposedChangesForOverPaid(Long customerId, Long amount, ProposedChanges proposedChanges) {
        CustomerSummary mostRecentSeasonSummary = customerSummaryRepository
                .findMostRecentCustomerSummary(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer summary does not exist"));
        ;
        RepaymentUpload repaymentUpload = new RepaymentUpload(customerId, amount);
        proposedChanges.setRepaymentUpload(repaymentUpload);
        SummaryToUpdate summaryToUpdate = new SummaryToUpdate(mostRecentSeasonSummary, amount);
        proposedChanges.getSummariesToUpdate().add(summaryToUpdate);
    }

    private void setProposedChangesForOverride(Long customerId, Long amount, Long seasonId, ProposedChanges proposedChanges) {
        RepaymentUpload repaymentUpload = new RepaymentUpload(customerId, amount, seasonId);
        proposedChanges.setRepaymentUpload(repaymentUpload);
        CustomerSummary summary = customerSummaryRepository.
                findByCustomerIdAndSeasonId(customerId, seasonId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer summary does not exist"));
        SummaryToUpdate summaryToUpdate = new SummaryToUpdate(summary, amount);
        proposedChanges.getSummariesToUpdate().add(summaryToUpdate);
    }
}
