package com.victorbassey.repayment.service;

import com.victorbassey.repayment.model.Season;
import com.victorbassey.repayment.repository.SeasonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeasonServiceImpl implements SeasonService {
    private SeasonRepository seasonRepository;

    public SeasonServiceImpl(SeasonRepository seasonRepository) {
        this.seasonRepository = seasonRepository;
    }

    @Override
    public List<Season> getAllSeasons() {
        return seasonRepository.findAll();
    }
}
