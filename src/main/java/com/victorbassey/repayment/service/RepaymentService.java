package com.victorbassey.repayment.service;

import com.victorbassey.repayment.model.RepaymentUpload;
import com.victorbassey.repayment.payload.ProposedChanges;
import com.victorbassey.repayment.payload.RepaymentData;

import java.util.List;

public interface RepaymentService {
    List<RepaymentData> repayDebts(List<RepaymentUpload> repaymentUploads);
    ProposedChanges getCustomerSummaryForUpdate(Long customerId, Long amount, Long seasonId);
}
