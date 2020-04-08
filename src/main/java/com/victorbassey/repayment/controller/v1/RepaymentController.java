package com.victorbassey.repayment.controller.v1;

import com.victorbassey.repayment.model.RepaymentUpload;
import com.victorbassey.repayment.payload.ProposedChanges;
import com.victorbassey.repayment.payload.RepaymentData;
import com.victorbassey.repayment.payload.ResponseTemplate;
import com.victorbassey.repayment.service.RepaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@RequestMapping("/api/v1/repayments")
@Validated
public class RepaymentController {
    private RepaymentService repaymentService;

    public RepaymentController(RepaymentService repaymentService) {
        this.repaymentService = repaymentService;
    }

    @GetMapping("/proposed-changes")
    @ResponseStatus(HttpStatus.OK)
    public ResponseTemplate<ProposedChanges> getProposedChanges(@Min(value = 1, message = "Customer ID must be positive")
                                                                    @RequestParam Long customerId,
                                                                @Min(value = 1, message = "Amount must be positive")
                                                                @RequestParam Long amount,
                                                                @Min(value = 0, message = "Season ID must not be negative")
                                                                    @RequestParam(defaultValue = "0") Long seasonId) {
        ProposedChanges theChanges = repaymentService.getCustomerSummaryForUpdate(customerId, amount, seasonId);
        return new ResponseTemplate<>(HttpStatus.OK, "Successfully retrieved proposed changes", theChanges);
    }

    @PostMapping("/repay")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseTemplate<List<RepaymentData>> repayDebts
            (@RequestBody @NotEmpty(message = "List of repayment upload cannot be empty")
                     List<@Valid RepaymentUpload> repaymentUploads) {
        List<RepaymentData> data = repaymentService.repayDebts(repaymentUploads);
        return new ResponseTemplate<>(HttpStatus.CREATED, "Successfully repaid debts", data);
    }
}
