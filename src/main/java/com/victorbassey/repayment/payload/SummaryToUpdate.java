package com.victorbassey.repayment.payload;

import com.victorbassey.repayment.model.CustomerSummary;

public class SummaryToUpdate {
    private CustomerSummary summary;
    private Long amountToAdd;

    public SummaryToUpdate(CustomerSummary summary, Long amountToAdd) {
        this.summary = summary;
        this.amountToAdd = amountToAdd;
    }

    public CustomerSummary getSummary() {
        return summary;
    }

    public void setSummary(CustomerSummary summary) {
        this.summary = summary;
    }

    public Long getAmountToAdd() {
        return amountToAdd;
    }

    public void setAmountToAdd(Long amountToAdd) {
        this.amountToAdd = amountToAdd;
    }
}
