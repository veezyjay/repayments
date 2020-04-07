package com.victorbassey.repayment.controller.v1;

import com.victorbassey.repayment.model.RepaymentUpload;
import com.victorbassey.repayment.payload.ProposedChanges;
import com.victorbassey.repayment.payload.RepaymentData;
import com.victorbassey.repayment.payload.ResponseTemplate;
import com.victorbassey.repayment.service.RepaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/repayments")
public class RepaymentController {
    private RepaymentService repaymentService;

    public RepaymentController(RepaymentService repaymentService) {
        this.repaymentService = repaymentService;
    }

    @GetMapping("/proposed-changes")
    @ResponseStatus(HttpStatus.OK)
    public ResponseTemplate<ProposedChanges> getProposedChanges(@RequestParam Long customerId, @RequestParam Long amount,
                                                                @RequestParam(defaultValue = "0") Long seasonId) {
        ProposedChanges theChanges = repaymentService.getCustomerSummaryForUpdate(customerId, amount, seasonId);
        return new ResponseTemplate<>(HttpStatus.OK, "Successfully retrieved proposed changes", theChanges);
    }

    @PostMapping("/repay")
    public ResponseTemplate<List<RepaymentData>> repayDebts(@RequestBody List<RepaymentUpload> repaymentUploads) {
        List<RepaymentData> data = repaymentService.repayDebts(repaymentUploads);
        return new ResponseTemplate<>(HttpStatus.CREATED, "Successfully repaid debts", data);
    }
}
