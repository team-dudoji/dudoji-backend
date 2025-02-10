package com.dudoji.spring.models.domain;

import lombok.Getter;
import lombok.Setter;
import org.locationtech.proj4j.*;

@Getter
@Setter
public class Point {
    public static final int BASE_LONGITUDE = 129082255; // 경도 x * 10^6
    public static final int BASE_LATITUDE = 35230853; // 위도 y * 10^6

    // Basic Coordinate System: Geographic
    private int lng, lat;

    // Other Coordinate System
    // 1. UTM-K (Expressed in meters)
    private Integer northing = null;
    private Integer easting = null;
    // 2. DUDOJI System (Expressed in ONLY int)
    private Integer dudojiX = null;
    private Integer dudojiY = null;

    // Constructor Part
    private Point(int lng, int lat) {
        this.lng = lng;
        this.lat = lat;
    }

    /**
     * 지리좌표계를 통해 Point 객체를 생성합니다.
     * @param lng lng * 10^6 한 값
     * @param lat lat * 10^6 한 값
     * @return lng, lat 값을 지닌 Point 객체
     */
    public static Point fromGeographic(int lng, int lat) {
        return new Point(lng, lat);
    }

    /**
     * UTM-K 좌표계를 통해 Point 객체를 생성합니다.
     * @param northing UTM-K N 값
     * @param easting UTM-K E 값
     * @return 해당 값을 lng, lat로 해석해 만든 Point 객체
     */
    public static Point fromUTMK(int northing, int easting) {
        Pair<Integer, Integer> geographicPosition = transformUtmkToLngLat(northing, easting);

        Point result = new Point (geographicPosition.getX(), geographicPosition.getY());
        result.setUtmk(northing, easting);

        return result;
    }

    /**
     * DUDOJI 좌표계를 통해 Point 객체를 생성합니다.
     * @param dudojiX DUDOJI 좌표계 x 값
     * @param dudojiY DUDOJI 좌표계 y 값
     * @return 해당 값을 lng, lat로 해석해 만든 Point 객체
     */
    public static Point fromDUDOJI(int dudojiX, int dudojiY) {
        Pair<Integer, Integer> geographicPosition = transformDudojiToLngLat(dudojiX, dudojiY);

        Point result = new Point (geographicPosition.getX(), geographicPosition.getY());
        result.setDudoji(dudojiX, dudojiY);

        return result;
    }

    // Getter Part
    /**
     * Point 객체의 지리좌표계 값을 가져옵니다.
     * @return (lng, lat)로 되어있는 Pair
     */
    public Pair<Integer, Integer> getLngLat() {
        return new Pair<>(lng, lat);
    }

    /**
     * Point 객체의 UTM-K 좌표계 값을 가져옵니다.
     * @return (northing, easting)로 되어있는 Pair
     */
    public Pair<Integer, Integer> getUtmk() {
        if (northing == null || easting == null) {
            Pair<Integer, Integer> utmkPosition = transformLngLatToUtmk(lng, lat);
            this.setUtmk(utmkPosition.getX(), utmkPosition.getY());
        }
        return new Pair<>(northing, easting);
    }

    /**
     * Point 객체의 DUDOJI 좌표계 값을 가져옵니다.
     * @return (dudojiX, dudojiY)로 되어있는 Pair
     */
    public Pair<Integer, Integer> getDudoji() {
        if (dudojiX == null || dudojiY == null) {
            Pair<Integer, Integer> dudojiPosition = transformLngLatToDudoji(lng, lat);
            this.setDudoji(dudojiPosition.getX(), dudojiPosition.getY());
        }
        return new Pair<>(dudojiX, dudojiY);
    }

    // Setter Part
    /**
     * Point 객체의 UTM-K 값을 지정합니다.
     * @param northing
     * @param easting
     */
    private void setUtmk(int northing, int easting) {
        this.northing = northing;
        this.easting = easting;
    }

    /**
     * Point 객체의 Dudoji 값을 지정합니다.
     * @param dudojiX
     * @param dudojiY
     */
    private void setDudoji(int dudojiX, int dudojiY) {
        this.dudojiX = dudojiX;
        this.dudojiY = dudojiY;
    }

    // Transform Part
    /**
     * 위도 경도 시스템을 Dudoji 형식으로 바꿉니다.
     * @param lng 경도 * 10^6 한 값
     * @param lat 위도 * 10^6 한 값
     * @return (dudojiX, dudojiY)로 되어있는 Pair
     */
    private static Pair<Integer, Integer> transformLngLatToDudoji(int lng, int lat){
        int dudojiX = Math.floorDiv((lng - BASE_LONGITUDE), MapSection.MAP_SECTION_WIDTH);
        int dudojiY = Math.floorDiv((lat - BASE_LATITUDE), MapSection.MAP_SECTION_WIDTH);
        return new Pair<>(dudojiX, dudojiY);
    }

    /**
     * Dudoji 시스템을 위도 경도 형식으로 바꿉니다.
     * @param dudojiX 두도지 좌표계 x 값
     * @param dudojiY 두도지 좌표계 y 값
     * @return (lng, lat)로 되어있는 Pair
     */
    private static Pair<Integer, Integer> transformDudojiToLngLat(int dudojiX, int dudojiY){
        int longitude = dudojiX * MapSection.MAP_SECTION_WIDTH + BASE_LONGITUDE;
        int latitude  = BASE_LATITUDE + MapSection.MAP_SECTION_WIDTH * dudojiY;
        return new Pair<>(longitude, latitude);
    }

    /**
     * 위도 경도 시스템을 UTM-K 형식으로 바꿉니다.
     * @param lng 경도 * 10^6 한 값
     * @param lat 위도 * 10^6 한 값
     * @return (N,E)로 되어있는 Pair
     */
    private static Pair<Integer, Integer> transformLngLatToUtmk(int lng, int lat) {
        CoordinateReferenceSystem wgs84System = getWGS84System();
        CoordinateReferenceSystem utmkSystem = getUTMKSystem();

        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CoordinateTransform coordinateTransform = ctFactory.createTransform(wgs84System, utmkSystem);

        // 변환 과정
        ProjCoordinate p = new ProjCoordinate();
        ProjCoordinate p2 = new ProjCoordinate();

        p.x = lng * 10e-7;
        p.y = lat * 10e-7;

        ProjCoordinate projCoordinate = coordinateTransform.transform(p,p2);

        // x가 Easting y가 Northing
        return new Pair<>((int) projCoordinate.y, (int) projCoordinate.x);
    }

    /**
     * UTM-K 좌표계에서 지리좌표계로 치환합니다.
     * @param northing UTM-K 좌표계에서의 N
     * @param easting UTM-K 좌표계에서의 E
     * @return (lng, lat)로 되어있는 Pair
     */
    private static Pair<Integer, Integer> transformUtmkToLngLat(int northing, int easting) {
        CoordinateReferenceSystem wgs84System = getWGS84System();
        CoordinateReferenceSystem utmkSystem = getUTMKSystem();

        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CoordinateTransform coordinateTransform = ctFactory.createTransform(utmkSystem, wgs84System);

        // 변환 과정
        // 순서 주의
        ProjCoordinate p = new ProjCoordinate(easting, northing);
        ProjCoordinate p2 = new ProjCoordinate();

        coordinateTransform.transform(p,p2);

        int lng = (int) (p2.x * 10e5);
        int lat = (int) (p2.y * 10e5);

        return new Pair<>(lng, lat);
    }

    // Util Part
    /**
     * wgs84System을 반환합니다.
     * @return wgs84system
     */
    private static CoordinateReferenceSystem getWGS84System() {
        CRSFactory crsFactory = new CRSFactory();
        // WGS84 system 정의
        String wgs84Name = "WGS84";
        String wgs84Proj = "+proj=longlat +datum=WGS84 +no_defs";
        CoordinateReferenceSystem wgs84System = crsFactory.createFromParameters(wgs84Name, wgs84Proj);

        return wgs84System;
    }

    /**
     * UTM-K System을 반환합니다.
     * @return utmkSystem
     */
    private static CoordinateReferenceSystem getUTMKSystem(){
        CRSFactory crsFactory = new CRSFactory();
        // UTM-K system 정의
        String utmkName = "UTMK";
        // x: easting, y: northing
        String utmkProj = "+proj=tmerc +lat_0=38 +lon_0=127.5 +k=0.9996 +x_0=1000000 +y_0=2000000 +ellps=GRS80 +units=m +no_defs";
        CoordinateReferenceSystem utmkSystem = crsFactory.createFromParameters(utmkName, utmkProj);

        return utmkSystem;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            if (this.getLat() == ((Point) obj).getLat() && this.getLng() == ((Point) obj).getLng()){
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    public static void main(String[] args) {
//        Point firstPoint = Point.fromGeographic(BASE_LONGITUDE, BASE_LATITUDE);
//
//        int[] firstLngLat = firstPoint.getLngLat();
//        System.out.printf("First Geographic: %d, %d\n", firstLngLat[0], firstLngLat[1]);
//
//        int[] firstUTMK = firstPoint.getUtmk();
//        System.out.printf("First UTM-K: %d, %d\n", firstUTMK[0], firstUTMK[1]);
//
//        int[] firstDudoji = firstPoint.getDudoji();
//        System.out.printf("First Dudoji: %d, %d\n", firstDudoji[0], firstDudoji[1]);
//
//        Point secondPoint = Point.fromUTMK(firstUTMK[0], firstUTMK[1]);
//
//        int[] secondLngLat = secondPoint.getLngLat();
//        System.out.printf("Second Geographic: %d, %d\n", secondLngLat[0], secondLngLat[1]);
//
//        int[] secondUTMK = secondPoint.getUtmk();
//        System.out.printf("Second UTM-K: %d, %d\n", secondUTMK[0], secondUTMK[1]);
//
//        int[] secondDudoji = secondPoint.getDudoji();
//        System.out.printf("Second Dudoji: %d, %d\n", secondDudoji[0], secondDudoji[1]);
//
//        Point thirdPoint = Point.fromDUDOJI(firstDudoji[0], firstDudoji[1]);
//
//        int[] thirdLngLat = thirdPoint.getLngLat();
//        System.out.printf("Third Geographic: %d, %d\n", thirdLngLat[0], thirdLngLat[1]);
//
//        int[] thirdUTMK = thirdPoint.getUtmk();
//        System.out.printf("Third UTM-K: %d, %d\n", thirdUTMK[0], thirdUTMK[1]);
//
//        int[] thirdDudoji = thirdPoint.getDudoji();
//        System.out.printf("Third Dudoji: %d, %d\n", thirdDudoji[0], thirdDudoji[1]);
    }
}
