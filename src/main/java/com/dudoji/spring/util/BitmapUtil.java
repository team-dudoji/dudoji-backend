package com.dudoji.spring.util;

import com.dudoji.spring.models.domain.Pair;
import com.dudoji.spring.models.domain.Point;

import static com.dudoji.spring.models.domain.MapSection.BASIC_ZOOM_SIZE;
import static com.dudoji.spring.models.domain.MapSection.TILE_SIZE;

public class BitmapUtil {
    public static final int BYTE_VALUE = 8;
    public static final double EARTH_RADIUS = 6378137.0;
    public static final double EXPLORED_THRESHOLD = 80.0;
    /**
     * 비트맵에서 해당하는 위치를 1로 변환합니다.
     * @param bitmap 변환시킬 비트맵
     * @param targetPoint 원하는 위치
     */
    public static void setBit(byte[] bitmap, Point targetPoint) {
        // 자기 자신이 타일이 어디인 지 알고 있으므로, targetPoint만 있으면 됨.
        Pair<Integer, Integer> targetPixelPoint = Point.convertGoogleMercatorToPixel(targetPoint.getGoogleX(), targetPoint.getGoogleY());

        int targetX = targetPixelPoint.getX() % TILE_SIZE;
        int targetY = targetPixelPoint.getY() % TILE_SIZE;
        Pair<Integer, Integer> targetByte = convertByteToBitIndex(targetX, targetY);

        bitmap[targetByte.getX()] |= (byte) (1 << (7 - targetByte.getY()));
    }

    /**
     * Sets a specific bit in the bitmap to 1.
     * This method takes a byte array (`bitmap`) that represents a collection of bits,
     * along with two integer parameters (`bitX` and `bitY`) that denote the target bit's
     * position in a conceptual 2D layout. It converts these coordinates to the actual
     * byte index and the bit position within that byte by calling the helper method
     * `convertByteToBitIndex(bitX, bitY)`. Once the correct byte and bit position are
     * determined, it uses a bitwise OR operation to set the bit at that position to 1,
     * ensuring that only the target bit is modified while all other bits remain unchanged.
     *
     * @param bitmap the byte array representing the bitmap where the bit will be set
     * @param bitX the horizontal coordinate (or index) for the target bit
     * @param bitY the vertical coordinate (or bit position within the byte) for the target bit
     */
    public static void setBitWithBitIndex(byte[] bitmap, int bitX, int bitY) {
        Pair<Integer, Integer> targetByte = convertByteToBitIndex(bitX, bitY);
        bitmap[targetByte.getX()] |= (byte) (1 << (7 - targetByte.getY()));
    }

    public static void setCloseBits(byte[] bitmap, Point centerPoint, double radiusMeters, int zoom) {
        Pair<Double, Double> centerGoogle = centerPoint.getGoogleMap();
        Pair<Integer, Integer> centerPixel = Point.convertGoogleMercatorToPixel(centerGoogle.getX(), centerGoogle.getY());

        int centerX = centerPixel.getX() % TILE_SIZE;
        int centerY = centerPixel.getY() % TILE_SIZE;

        double pixelRadius = getPixelRadius(centerPoint, zoom, radiusMeters);

        int minX = (int)Math.floor(centerX - pixelRadius);
        int maxX = (int)Math.ceil(centerX + pixelRadius);
        int minY = (int)Math.floor(centerY - pixelRadius);
        int maxY = (int)Math.ceil(centerY + pixelRadius);

        for (int px = minX; px <= maxX; px++) {
            for (int py = minY; py <= maxY; py++) {
                if (px < 0 || px >= TILE_SIZE || py < 0 || py >= TILE_SIZE) {
                    continue;
                }
                int dx = px - centerX;
                int dy = py - centerY;
                int distSq = dx*dx + dy*dy;
                if (distSq <= pixelRadius * pixelRadius) {
                    Pair<Integer, Integer> targetByte = convertByteToBitIndex(px, py);
                    bitmap[targetByte.getX()] |= (byte) (1 << (7 - targetByte.getY()));
                }
            }
        }
    }
    public static void setCloseBits(byte[] bitmap, Point centerPoint, double radiusMeters) {
        setCloseBits(bitmap, centerPoint, radiusMeters, BASIC_ZOOM_SIZE);
    }

    public static boolean isExplored(byte[] bitmap) {
        int counts = 0;
        for (byte b : bitmap) {
            counts += Integer.bitCount(b & 0xFF);
        }

        return counts >= (TILE_SIZE * TILE_SIZE) * EXPLORED_THRESHOLD / 100.0;
    }

    /**
     * 간단한 유틸 함수: bitmap에서 'count'개의 비트를 순서대로 켠다.
     * 기존 내용이 유지되므로, 이전에 켠 상태 위에 추가로 더 켤 수도 있음.
     *
     * @param bitmap byte 배열
     * @param count  켜고자 하는 비트 수(최대 65536)
     */
    public static void fillBits(byte[] bitmap, int count) {
        // 범위 초과 시 count를 최댓값 제한
        int maxCount = TILE_SIZE * TILE_SIZE; // 65536
        if (count > maxCount) {
            count = maxCount;
        }

        // 0번 비트부터 'count-1'번 비트까지 켠다
        for (int i = 0; i < count; i++) {
            int byteIndex = i >> 3;        // i / 8
            int bitOffset = i & 0x07;      // i % 8
            bitmap[byteIndex] |= (1 << (7 - bitOffset)); // TODO: 얘도 바꿔야 하나?
        }
    }

    /**
     * BitIndex 를 byte Index 로 바꿉니다
     * @param x bit index 에서의 x
     * @param y bit index 에서의 y
     * @return (byteIndex, bitOffset)
     */
    private static Pair<Integer, Integer> convertByteToBitIndex(int x, int y) {
        int bitIndex = y * TILE_SIZE + x;
        int byteIndex = bitIndex / BYTE_VALUE;
        int bitOffset = bitIndex % BYTE_VALUE;

        return new Pair<>(byteIndex, bitOffset);
    }

    /**
     * 비트맵에서 targetPoint 가 위치하는 부분의 값을 가져옵니다
     * @param bitmap 탐색할 비트맵
     * @param targetPoint 원하는 위치
     * @return 해당 부분의 boolean 값
     */
    public static boolean getBit(byte[] bitmap, Point targetPoint) {
        Pair<Double, Double> googlePoint = targetPoint.getGoogleMap();
        Pair<Integer, Integer> targetPixelPoint = Point.convertGoogleMercatorToPixel(googlePoint.getX(), googlePoint.getY());

        int targetX = targetPixelPoint.getX() % TILE_SIZE;
        int targetY = targetPixelPoint.getY() % TILE_SIZE;
        Pair<Integer, Integer> targetByte = convertByteToBitIndex(targetX, targetY);

        byte tempByte = bitmap[targetByte.getX()];
        return (tempByte & (1 << (7 - targetByte.getY()))) != 0;
    }
    /**
     * 비트맵에서 해당 위치의 값을 가져옵니다.
     * @param bitmap 탐색할 비트맵
     * @param bitmapX 0~255 값 중 하나
     * @param bitmapY 0~255 값 중 하나
     * @return 해당 위치의 boolean 값
     */
    public static boolean getBit(byte[] bitmap, int bitmapX, int bitmapY) {
        Pair<Integer, Integer> idx = convertByteToBitIndex(bitmapX, bitmapY);
        int byteIndex = idx.getX();
        int bitOffset = idx.getY();

        return (bitmap[byteIndex] & (1 << (7 - bitOffset))) != 0;
    }

    public static double getPixelRadius(Point centerPoint, int zoom, double radiusMeters) {
        double groundRes = (Math.cos(Math.toRadians(centerPoint.getLat())) * 2.0 * Math.PI * EARTH_RADIUS / (256.0 * (1 << zoom)));
        double pixelRadius = radiusMeters / groundRes;
        return pixelRadius;
    }
}
