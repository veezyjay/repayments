package com.victorbassey.repayment.payload;

import com.victorbassey.repayment.model.CustomerSummary;
import com.victorbassey.repayment.model.Repayment;

import java.util.ArrayList;
import java.util.List;

public class RepaymentData {
    private Repayment originalRepayment;
    private List<Repayment> adjustmentRepayments;
    private List<CustomerSummary> updatedSummaries;

    public RepaymentData() {
    }

    public RepaymentData(Repayment originalRepayment) {
        this.originalRepayment = originalRepayment;
        this.updatedSummaries = new ArrayList<>();
    }

    public Repayment getOriginalRepayment() {
        return originalRepayment;
    }

    public void setOriginalRepayment(Repayment originalRepayment) {
        this.originalRepayment = originalRepayment;
    }

    public List<Repayment> getAdjustmentRepayments() {
        return adjustmentRepayments;
    }

    public void setAdjustmentRepayments(List<Repayment> adjustmentRepayments) {
        this.adjustmentRepayments = adjustmentRepayments;
    }

    public List<CustomerSummary> getUpdatedSummaries() {
        return updatedSummaries;
    }

    public void setUpdatedSummaries(List<CustomerSummary> updatedSummaries) {
        this.updatedSummaries = updatedSummaries;
    }

    @Override
    public String toString() {
        return "RepaymentData{" +
                "originalRepayment=" + originalRepayment +
                ", adjustmentRepayments=" + adjustmentRepayments +
                ", updatedSummaries=" + updatedSummaries +
                '}';
    }
}
