package com.dudoji.spring.models.domain;

import lombok.Getter;
import lombok.Setter;

import static com.dudoji.spring.models.domain.MapSection.BASIC_ZOOM_SIZE;
import static com.dudoji.spring.models.domain.MapSection.TILE_SIZE;

@Getter
@Setter
public class Point {
    // Basic Coordinate System: Geographic
    private double lng, lat;
    private Double googleX = null;
    private Double googleY = null;

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
    public Pair<Double, Double> getGoogleMap() {
        if (googleX == null || googleY == null) {
            Pair<Double, Double> googlePoint = convertLatLngToGoogleMercator(lng, lat);
            googleX = googlePoint.getX();
            googleY = googlePoint.getY();
        }
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Point)) return false;

        Point that = (Point) obj;
        double epsilon = 1e-9;

        return (Math.abs(this.getLat() - that.getLat()) < epsilon)
                && (Math.abs(this.getLng() - that.getLng()) < epsilon);
    }

}
