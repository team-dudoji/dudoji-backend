package com.dudoji.spring.models.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Point {
    // Basic Coordinate System: Geographic
    private double lng, lat;
    private int googleX, googleY;

    public static final int TILE_SIZE = 256;
    public static final int BASIC_ZOOM_SIZE = 15;

    // Constructor Part
    private Point(double lng, double lat) {
        this.lng = lng;
        this.lat = lat;
    }

    /**
     * 지리좌표계를 통해 Point 객체를 생성합니다.
     * @param lng lng 값
     * @param lat lat * 10^6 한 값
     * @return lng, lat 값을 지닌 Point 객체
     */
    public static Point fromGeographic(double lng, double lat) {
        return new Point(lng, lat);
    }

    /**
     * 구긂 좌표로 부터 Point 객체를 생성합니다.
     * @param googleX 구글 좌표의 x 값
     * @param googleY 구글 좌표의 y 값
     * @return
     */
    public static Point fromGoogleMap(int googleX, int googleY) {
        // TODO: Some Logic For GoogleMap to Lng Lat
        return new Point(1, 2);
    }
    // Getter Part
    /**
     * Point 객체의 지리좌표계 값을 가져옵니다.
     * @return (lng, lat)로 되어있는 Pair
     */
    public Pair<Double, Double> getLngLat() {
        return new Pair<>(lng, lat);
    }

    /**
     * Point 객체의 구글 타입맵 좌표를 가져옵니다.
     * @return (googleX, googleY)로 되어있는 Pair
     */
    public Pair<Integer, Integer> getGoogleMap() {
        return new Pair<>(googleX, googleY);
    }

    /**
     * 위도 경도를 구글 메르카도르 기법으로 바꿉니다.
     * @param lat 위도 값
     * @param lng 경도 값
     * @return (googleX, googleY) 인 Double 형 Pair
     */
    public static Pair<Double, Double> convertLatLngToGoogleMercator(double lat, double lng) {
        double siny = Math.sin(lat * Math.PI / 180);

        siny = Math.min(Math.max(siny, -0.9999), 0.9999);

        return new Pair<>(
                TILE_SIZE * (0.5 + lng / 360),
                TILE_SIZE * (0.5 - Math.log((1 + siny) / (1 - siny)) / (4 * Math.PI))
        );
    }

    /**
     * BASIC_ZOOM_SIZE 로 Tile 좌표를 구합니다
     * @param googleX google 좌표계의 X 값
     * @param googleY google 좌표계의 Y 값
     * @return (tileX, tileY) 인 Integer Pair
     */
    public static Pair<Integer, Integer> convertGoogleMercatorToTile(double googleX, double googleY) {
        return convertGoogleMercatorToTile(googleX, googleY, BASIC_ZOOM_SIZE);
    }

    /**
     * zoom_size를 설정하여 Tile 좌표를 구합니다
     * @param googleX google 좌표계의 X 값
     * @param googleY google 좌표계의 Y 값
     * @param zoom_size 원하는 zoom level 값
     * @return (tileX, tileY) 인 Integer Pair
     */
    public static Pair<Integer, Integer> convertGoogleMercatorToTile(double googleX, double googleY, Integer zoom_size) {
        int scale = 1 << zoom_size;
        return new Pair<>(
                (int) Math.floor((googleX * scale) / TILE_SIZE),
                (int) Math.floor((googleY * scale) / TILE_SIZE)
        );
    }

    /**
     * BASIC_ZOOM_SIZE 로 Pixel 좌표를 구합니다
     * @param googleX google 좌표계의 X 값
     * @param googleY google 좌표계의 Y 값
     * @return (pixelX, pixelY) 인 Integer Pair
     */
    public static Pair<Integer, Integer> convertGoogleMercatorToPixel(double googleX, double googleY) {
        return convertGoogleMercatorToPixel(googleX, googleY, BASIC_ZOOM_SIZE);
    }

    /**
     * 원하는 zoom_size 로 Pixel 좌표를 구합니다
     * @param googleX google 좌표계의 X 값
     * @param googleY google 좌표계의 Y 값
     * @param zoom_size 원하는 zoom level 값
     * @return (pixelX, pixelY) 인 Integer Pair
     */
    public static Pair<Integer, Integer> convertGoogleMercatorToPixel(double googleX, double googleY, int zoom_size) {
        int scale = 1 << zoom_size;
        return new Pair<>(
                (int) Math.floor(googleX * scale),
                (int) Math.floor(googleY * scale)
        );
    }
}
