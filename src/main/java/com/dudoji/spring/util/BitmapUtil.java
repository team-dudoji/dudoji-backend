package com.dudoji.spring.util;

public class BitmapUtil {
    /**
        기준 위도 경도를 기준으로 주어진 위도 경도의 위치를 계산하여 해당 비트맵의 위치를 1로 바꿉니다.

        @param bitmap 변환시킬 비트맵
        @param baseLng 기본 경도
        @param baseLat 기본 위도
        @param lng 경도 변수
        @param lat 위도 변수
     */
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
