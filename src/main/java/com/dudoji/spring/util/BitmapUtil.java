package com.dudoji.spring.util;

public class BitmapUtil {

    public static byte[] setBit(byte[] bitmap, int baseLng, int baseLat, int lng, int lat) {
        // TODO
        // 위도 경도에 맞는 위치의 bit를 1로 바꾸는 함수
        // return은 bitmap
        return null;
    }
    public static byte[] setCloseBits(byte[] bitmap, int baseLng, int baseLat, int lng, int lat, float radius) {
        // TODO
        // 위도 경도에 맞는 위치의 bit들을 1로 바꾸는 함수 (원 모양)
        // return은 bitmap
        return null;
    }

    public static boolean isExplored(byte[] bitmap) {
        // TODO
        // 80 이상 탐험 됐는지 체크
        // 함수명은 좀 더 General하게 바꾸는게 좋을듯
        return false;
    }
}
