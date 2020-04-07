package com.victorbassey.repayment.payload;

import com.victorbassey.repayment.model.RepaymentUpload;

import java.util.ArrayList;
import java.util.List;

public class ProposedChanges {
    private RepaymentUpload repaymentUpload;
    private List<SummaryToUpdate> summariesToUpdate;

    public ProposedChanges() {
        this.summariesToUpdate = new ArrayList<>();
    }

    public RepaymentUpload getRepaymentUpload() {
        return repaymentUpload;
    }

    public void setRepaymentUpload(RepaymentUpload repaymentUpload) {
        this.repaymentUpload = repaymentUpload;
    }

    public List<SummaryToUpdate> getSummariesToUpdate() {
        return summariesToUpdate;
    }

    public void setSummariesToUpdate(List<SummaryToUpdate> summariesToUpdate) {
        this.summariesToUpdate = summariesToUpdate;
    }
}
