package com.victorbassey.repayment.controller.v1;

import com.victorbassey.repayment.model.Season;
import com.victorbassey.repayment.payload.ResponseTemplate;
import com.victorbassey.repayment.service.SeasonService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seasons")
public class SeasonController {
    private SeasonService seasonService;

    public SeasonController(SeasonService seasonService) {
        this.seasonService = seasonService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseTemplate<List<Season>> getSeasons() {
        List<Season> allSeasons = seasonService.getAllSeasons();
        return new ResponseTemplate<>(HttpStatus.OK, "Successfully retrieved all seasons", allSeasons);
    }
}
