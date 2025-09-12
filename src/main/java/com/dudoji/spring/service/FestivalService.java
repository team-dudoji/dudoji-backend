package com.dudoji.spring.service;

import java.time.LocalDate;
import java.util.List;

import jakarta.annotation.PostConstruct;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dudoji.spring.models.dao.FestivalRepository;
import com.dudoji.spring.models.domain.Festival;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FestivalService {

    private final int CRAWLING_BATCH_SIZE = 50;

    private final FestivalRepository festivalRepository;
    private final FestivalApiClient festivalApiClient;

    @Scheduled(fixedRate = 60000 * 60 * 24)
    public void updateFestival() {
        LocalDate lastUpdateDate = festivalRepository.findMaxDateReferenceDate();
        LocalDate today = LocalDate.now();

        for (LocalDate date = lastUpdateDate; !date.isAfter(today); date = date.plusDays(1)) {
            crawlFestival(date, 1);
        }
    }

    public void crawlFestival(LocalDate referenceDate, int pageNo) {
        log.info("[SCHEDULED] {} crawl festival start", pageNo);
        List<Festival> festivals = festivalApiClient.getFestivalData(referenceDate, CRAWLING_BATCH_SIZE, pageNo);

        if (festivals == null) {
            return;
        }

        log.info("[SCHEDULED] {} crawled num: {}", pageNo, festivals.size());
        festivals.forEach(this::saveFestival);
        log.info("[SCHEDULED] {} successfully festivals saved", pageNo);

        if (festivals.size() == CRAWLING_BATCH_SIZE) {
            crawlFestival(referenceDate, pageNo + 1);
        }
    }

    public void saveFestival(Festival festival) {
        if (festivalRepository.existsByNameAndDataReferenceDate(festival.getName(), festival.getDataReferenceDate())) {
            return;
        }

        festivalRepository.save(festival);
    }
}
