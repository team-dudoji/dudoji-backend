package com.dudoji.spring.models.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dudoji.spring.models.domain.Festival;

public interface FestivalRepository extends JpaRepository<Festival, Long> {

    Optional<Festival> findByName(String name);
    boolean existsByNameAndDataReferenceDate(String name, LocalDate dateReferenceDate);

    @Query("SELECT MAX(f.dataReferenceDate) FROM Festival f")
    LocalDate findMaxDateReferenceDate();

    @Query("""
SELECT f FROM Festival f
WHERE f.startDate <= :date AND :date <= f.endDate
AND (f.addressJibun LIKE CONCAT('%', :name, '%') OR f.addressRoad LIKE CONCAT('%', :name, '%'))
""")
    List<Festival> findAllByDateAndAddress(@Param("date") LocalDate date, @Param("name") String name);
}
