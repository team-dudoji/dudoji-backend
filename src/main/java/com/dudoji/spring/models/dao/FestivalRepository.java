package com.dudoji.spring.models.dao;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dudoji.spring.models.domain.Festival;

public interface FestivalRepository extends JpaRepository<Festival, Long> {

    Optional<Festival> findByName(String name);
    boolean existsByNameAndDataReferenceDate(String name, LocalDate dateReferenceDate);

    @Query("SELECT MAX(f.dataReferenceDate) FROM Festival f")
    LocalDate findMaxDateReferenceDate();
}
