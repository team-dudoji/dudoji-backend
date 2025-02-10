package com.dudoji.spring.util;

import com.dudoji.spring.models.domain.MapSection;
import com.dudoji.spring.models.domain.Pair;
import com.dudoji.spring.models.domain.Point;
import org.locationtech.proj4j.*;
import org.yaml.snakeyaml.util.Tuple;

import java.util.Arrays;

// UTM-K 좌표계는 (N, E)로 통일하겠습니다.
public class BitmapUtil {
    public static final int MAP_SECTION_SIZE = 128; // 128 * 128bit bitmap
    public static final int GRID_SIZE = 10; // 1280 / 128 = 10
    /**
     * 기준 포인트를 기준으로 주어진 위도 경도의 위치를 계산하여 해당 비트맵의 위치를 1로 바꿉니다.
     * @param bitmap 변환할 비트맵
     * @param basePoint 기준 포인트
     * @param targetPoint 타겟 포인트
     * @return 범위를 벗어날 시 기존 비트맵 그대로, 범위에 있을 시 1로 변환시킨 비트맵
     */
    public static void setBit(byte[] bitmap, Point basePoint, Point targetPoint) {
        Pair<Integer, Integer> baseUTM = basePoint.getUtmk();
        Pair<Integer, Integer> targetUTM = targetPoint.getUtmk();

        double distance = calculateDistance(baseUTM.getX(), baseUTM.getY(), targetUTM.getX(), targetUTM.getY());

        // 해당 섹션에 속하는지 확인
        if (distance > MapSection.MAP_SECTION_WIDTH) {
            System.out.println("해당 섹션에 속하지 않습니다.");
            // TODO: 에러 코드를 띄워야 할 것 같습니다.
            return;
        }

        // bitmap index
        int xIndex = Math.floorDiv(targetUTM.getX() - baseUTM.getX(), GRID_SIZE);
        int yIndex = Math.floorDiv(targetUTM.getY() - baseUTM.getY(), GRID_SIZE);

        Pair<Integer, Integer> newIndex = transformByteToBitIndex(xIndex, yIndex);
        bitmap[newIndex.getX()] |= (byte) (1 << (7 - newIndex.getY()));
    }
    /**
     * 주어진 반경 내의 모든 비트들을 1로 설정합니다.
     * @param bitmap 변환시킬 비트맵
     * @param basePoint 기본 포인트
     * @param targetPoint 반경의 중심 포인트
     * @param radius 반경 (m 단위)
     */
    public static void setCloseBit(byte[] bitmap, Point basePoint, Point targetPoint, int radius) {
        int radiusBit = radius / 10;
        int radiusBitSq = radiusBit * radiusBit;

        Pair<Integer, Integer> baseUTM = basePoint.getUtmk();
        Pair<Integer, Integer> targetUTM = targetPoint.getUtmk();

        double distance = calculateDistance(baseUTM.getX(), baseUTM.getY(), targetUTM.getX(), targetUTM.getY());

        // 해당 섹션에 속하는지 확인
        if (distance > MapSection.MAP_SECTION_WIDTH) {
            System.out.println("해당 섹션에 속하지 않습니다.");
            // TODO: 에러 코드를 띄워야 할 것 같습니다.
            return;
        }
        // bitmap index
        int xIndex = Math.floorDiv(targetUTM.getX() - baseUTM.getX(), GRID_SIZE);
        int yIndex = Math.floorDiv(targetUTM.getY() - baseUTM.getY(), GRID_SIZE);

        int startX = xIndex - radiusBit;
        int endX = xIndex + radiusBit;
        int startY = yIndex - radiusBit;
        int endY = yIndex + radiusBit;

        for (int y = startY; y <= endY; y++) {
            if (y < 0 || y >= MAP_SECTION_SIZE) continue; // TODO: 다른 섹션에 넘겨주는 거 필요할 듯

            for (int x = startX; x<= endX; x++) {
                if (x < 0 || x >= MAP_SECTION_SIZE) continue;

                int dx = x - xIndex;
                int dy = y - yIndex;
                if (dx*dx + dy*dy <= radiusBitSq) {
                    setBit(bitmap, basePoint,Point.fromDUDOJI(x, y));
                }
            }
        }
    }
    /**
     * bitmap이 80% 이상 진행됐는 지 검사하는 함수
     * @param bitmap 검사를 진행할 bitmap
     * @return true - 80% 이상 진행, false - 그 외 경우
     */
    public static boolean isExplored(byte[] bitmap) {
        int totalBits = (int) Math.pow(MAP_SECTION_SIZE, 2);
        int threshold = (int) (totalBits * 0.8);
        int count = 0;

        // 순회하면서 카운트
        for (byte b : bitmap) {
            count += Integer.bitCount(b & 0xFF);
        }
        return count >= threshold;
    }
    /**
     * 두 점 사이의 거리를 반환합니다
     * @param x1 첫번째 점의 x 좌표
     * @param y1 첫번째 점의 y 좌표
     * @param x2 두번째 점의 x 좌표
     * @param y2 첫번째 점의 y 좌표
     * @return double형의 거리를 반환합니다.
     */
    private static double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2 - y1, 2));
    }

    public static void printBitmap(byte[] bitmap) {
        System.out.println("비트맵 상태:");
        for (byte b : bitmap) {
            System.out.println(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }
    }

    /**
     * 2차원 배열 값을 byte array에 맞게 반환합니다.
     * @param x 2차원 배열의 x 값
     * @param y 2차원 배열의 y 값
     * @return ByteIndex, BitIndex로 구성되어 있는 Pair
     */
    public static Pair<Integer, Integer> transformByteToBitIndex(int x, int y) {
        return new Pair<>(y * MAP_SECTION_SIZE / 8 + x / 8, x % 8);
    }

//    public static void main(String[] args) {
//        Tuple<Integer, Integer> sex = transformLatLngToUTMK(129082240, 35230912);
//        System.out.println(sex._1());
//
//        // 8×8 비트맵 (byte[8] 배열)
//        byte[] bitmap = new byte[8];
//
//        // 기준점 (Base UTM-K 좌표)
//        int baseLng = 129082285; // 경도 127.0°E
//        int baseLat = 35230929;  // 위도 37.0°N
//
//        // 테스트할 좌표 (기준점에서 한 칸 오른쪽, 한 칸 위쪽)
//        int testLng = 129113959;
//        int testLat = 35232191;
//
//        // 비트맵 업데이트
//        setBit(bitmap, baseLng, baseLat, testLng, testLat);
//
//        // 비트맵 상태 출력 (이진수로 변환해서 보기 쉽게 출력)
//        printBitmap(bitmap);
//
//        // 일부 비트를 1로 설정 (테스트용)
//        bitmap[0] = (byte) 0xFF; // 11111111
//        bitmap[1] = (byte) 0xFF; // 11111111
//        bitmap[2] = (byte) 0xFF; // 11111111
//        bitmap[3] = (byte) 0xFF; // 11111111
//        bitmap[4] = (byte) 0xFF; // 11111111
//        bitmap[5] = (byte) 0b11110000; // 11110000 (4개 추가)
//        bitmap[6] = (byte) 0xFF;
//        bitmap[7] = (byte) 0xFF;
//
//        // 총 52개 이상 1인지 확인
//        boolean isAbove80 = isExplored(bitmap);
//        System.out.println("비트맵이 80% 이상 1인가? " + isAbove80);
//    }
}
