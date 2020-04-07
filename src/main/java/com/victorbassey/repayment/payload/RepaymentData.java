package com.victorbassey.repayment.payload;

import com.victorbassey.repayment.model.Repayment;

import java.util.List;

public class RepaymentData {
    private Repayment originalRepayment;
    private List<Repayment> adjustmentRepayments;

    public RepaymentData() {
    }

    public RepaymentData(Repayment originalRepayment) {
        this.originalRepayment = originalRepayment;
    }

    public Repayment getOriginalRepayment() {
        return originalRepayment;
    }

    public void setOriginalRepayment(Repayment originalRepayment) {
        this.originalRepayment = originalRepayment;
    }

    public List<Repayment> getAdjustmentRepayment() {
        return adjustmentRepayments;
    }

    public void setAdjustmentRepayment(List<Repayment> adjustmentRepayment) {
        this.adjustmentRepayments = adjustmentRepayment;
    }

    @Override
    public String toString() {
        return "RepaymentData{" +
                "originalRepayment=" + originalRepayment +
                ", adjustmentRepayment=" + adjustmentRepayments +
                '}';
    }
}
