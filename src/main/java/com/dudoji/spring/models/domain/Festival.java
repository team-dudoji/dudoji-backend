package com.dudoji.spring.models.domain;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "festival")
@AllArgsConstructor
@NoArgsConstructor
public class Festival {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private String organizerName;
    private String hostName;
    private String sponsorName;
    private String phoneNumber;
    private String websiteUrl;
    private String relatedInfo;
    private String addressRoad;
    private String addressJibun;
    private Double lat;
    private Double lng;
    private LocalDate dataReferenceDate;
    private String providerCode;
    private String providerName;
    private Long landmarkId;

    public Festival(
            String name,
            String location,
            LocalDate startDate,
            LocalDate endDate,
            String description,
            String organizerName,
            String hostName,
            String sponsorName,
            String phoneNumber,
            String websiteUrl,
            String relatedInfo,
            String addressRoad,
            String addressJibun,
            Double lat,
            Double lng,
            LocalDate dataReferenceDate,
            String providerCode,
            String providerName) {
        this(null, name, location, startDate, endDate, description, organizerName, hostName, sponsorName, phoneNumber, websiteUrl, relatedInfo, addressRoad, addressJibun, lat, lng, dataReferenceDate, providerCode, providerName, null);
    }
}
