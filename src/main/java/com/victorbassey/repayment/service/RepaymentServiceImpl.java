package com.victorbassey.repayment.service;

import com.victorbassey.repayment.exception.ResourceNotFoundException;
import com.victorbassey.repayment.model.CustomerSummary;
import com.victorbassey.repayment.model.Repayment;
import com.victorbassey.repayment.model.RepaymentUpload;
import com.victorbassey.repayment.payload.ProposedChanges;
import com.victorbassey.repayment.payload.RepaymentData;
import com.victorbassey.repayment.payload.SummaryToUpdate;
import com.victorbassey.repayment.repository.CustomerSummaryRepository;
import com.victorbassey.repayment.repository.RepaymentRepository;
import com.victorbassey.repayment.repository.RepaymentUploadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RepaymentServiceImpl implements RepaymentService {

    private CustomerSummaryRepository customerSummaryRepository;
    private RepaymentRepository repaymentRepository;
    private RepaymentUploadRepository repaymentUploadRepository;

    public RepaymentServiceImpl(CustomerSummaryRepository customerSummaryRepository,
                                RepaymentRepository repaymentRepository,
                                RepaymentUploadRepository repaymentUploadRepository) {
        this.customerSummaryRepository = customerSummaryRepository;
        this.repaymentRepository = repaymentRepository;
        this.repaymentUploadRepository = repaymentUploadRepository;
    }

    /**
     * Repays the debts of different customers provided in the list of repayment upload
     * @param repaymentUploads, data for the upload
     * @return list of repayments with their respective adjustment repayments
     */
    @Override
    @Transactional
    public List<RepaymentData> repayDebts(List<RepaymentUpload> repaymentUploads) {
        List<RepaymentData> allRepayments = new ArrayList<>();

        for (RepaymentUpload upload : repaymentUploads) {
            Long customerId = upload.getCustomerId();
            Long seasonId = upload.getSeasonId();
            Long amount = upload.getAmount();

            if (customerId < 1 || amount < 1) {
                throw new IllegalArgumentException("Customer ID and amount most both be positive");
            }

            // Override logic is contained in the following block
            if (seasonId != null && seasonId != 0) {
                if (seasonId < 0) throw new IllegalArgumentException("season ID must not be negative");
                CustomerSummary summary = customerSummaryRepository.findByCustomerIdAndSeasonId(customerId, seasonId)
                        .orElseThrow(() -> new ResourceNotFoundException("Customer summary does not exist"));
                summary.setTotalRepaid(summary.getTotalRepaid() + amount);
                Repayment newRepayment = repaymentRepository.save(new Repayment(customerId, seasonId, amount));
                CustomerSummary updatedSummary = customerSummaryRepository.save(summary);
                RepaymentData currentRepaymentData = new RepaymentData(newRepayment);
                currentRepaymentData.getUpdatedSummaries().add(updatedSummary);
                allRepayments.add(currentRepaymentData);
                repaymentUploadRepository.save(upload);

            } else {
                List<CustomerSummary> summariesWithDebts = customerSummaryRepository
                        .findCustomerSummariesWithDebts(customerId);

                // Overpaid logic is contained in the following block
                if (summariesWithDebts.isEmpty()) {
                    CustomerSummary mostRecentSeasonSummary = customerSummaryRepository
                            .findMostRecentCustomerSummary(customerId)
                            .orElseThrow(() -> new ResourceNotFoundException("Customer summary does not exist"));
                    mostRecentSeasonSummary.setTotalRepaid(mostRecentSeasonSummary.getTotalRepaid() + amount);
                    Repayment newRepayment = repaymentRepository.save(new Repayment(customerId, seasonId, amount));
                    CustomerSummary updatedSummary = customerSummaryRepository.save(mostRecentSeasonSummary);
                    RepaymentData currentRepaymentData = new RepaymentData(newRepayment);
                    currentRepaymentData.getUpdatedSummaries().add(updatedSummary);
                    allRepayments.add(currentRepaymentData);
                    repaymentUploadRepository.save(upload);

                // Cascade logic is contained in the following block
                } else {
                    long balance = amount;
                    Long parentId = null;
                    RepaymentData currentRepaymentData = new RepaymentData();
                    for (int i = 0; i < summariesWithDebts.size(); i++) {
                        CustomerSummary currentSummary = summariesWithDebts.get(i);
                        long debt = currentSummary.getTotalCredit() - currentSummary.getTotalRepaid();
                        currentSummary.setTotalRepaid(currentSummary.getTotalRepaid() + balance);
                        Repayment repayment = new Repayment(customerId, currentSummary.getSeasonId(), balance);
                        if (parentId != null) {
                            repayment.setParentId(parentId);
                        }
                        Repayment savedRepayment = repaymentRepository.save(repayment);
                        if (savedRepayment.getParentId() == null) {
                            currentRepaymentData.setOriginalRepayment(savedRepayment);
                        } else {
                            currentRepaymentData.getAdjustmentRepayments().add(savedRepayment);
                        }
                        balance -= debt;
                        if (balance <= 0) {
                            CustomerSummary updatedSummary = customerSummaryRepository.save(currentSummary);
                            if (currentRepaymentData.getUpdatedSummaries() == null) {
                                currentRepaymentData.setUpdatedSummaries(new ArrayList<>());
                            }
                            currentRepaymentData.getUpdatedSummaries().add(updatedSummary);
                            break;
                        }

                        if (currentSummary.getTotalRepaid() > currentSummary.getTotalCredit()
                                && i != summariesWithDebts.size() - 1) {
                            currentSummary.setTotalRepaid(currentSummary.getTotalRepaid() - balance);
                            Repayment newRepayment = new Repayment(customerId, currentSummary.getSeasonId(),
                                    balance * -1);
                            parentId = savedRepayment.getRepaymentId();
                            newRepayment.setParentId(parentId);
                            Repayment theRepayment = repaymentRepository.save(newRepayment);
                            if (currentRepaymentData.getAdjustmentRepayments() == null) {
                                currentRepaymentData.setAdjustmentRepayments(new ArrayList<>());
                            }
                            currentRepaymentData.getAdjustmentRepayments().add(theRepayment);
                        }
                        CustomerSummary updatedSummary = customerSummaryRepository.save(currentSummary);
                        if (currentRepaymentData.getUpdatedSummaries() == null) {
                            currentRepaymentData.setUpdatedSummaries(new ArrayList<>());
                        }
                        currentRepaymentData.getUpdatedSummaries().add(updatedSummary);
                    }
                    allRepayments.add(currentRepaymentData);
                    repaymentUploadRepository.save(upload);
                }
            }
        }

        return allRepayments;
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
        if (customerId < 1 || amount < 1 ) {
            throw new IllegalArgumentException("customer ID and amount must both be greater than zero");
        }

        ProposedChanges proposedChanges = new ProposedChanges();

        if (seasonId != null && seasonId != 0) {
            if (seasonId < 0) throw new IllegalArgumentException("season ID must not be negative");
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
