package com.victorbassey.repayment.service;

import com.victorbassey.repayment.model.Season;
import com.victorbassey.repayment.repository.SeasonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeasonServiceImplTest {

    @Mock
    SeasonRepository seasonRepository;

    @InjectMocks
    SeasonServiceImpl seasonService;

    @Test
    void getAllSeasons() {
        List<Season> theSeasons = List.of(new Season(), new Season(), new Season());
        when(seasonRepository.findAll()).thenReturn(theSeasons);
        List<Season> allSeasons = seasonService.getAllSeasons();
        assertNotNull(allSeasons);
        assertEquals(3, allSeasons.size());
    }
}