package com.example.lksynthesizeapp.ChiFen.test;

public class ByteUtil {

    public static String toHexString(byte[] input, String separator) {
        if (input == null) return null;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length; i++) {
            if (separator != null && sb.length() > 0) {
                sb.append(separator);
            }
            String str = Integer.toHexString(input[i] & 0xff);
            if (str.length() == 1) str = "0" + str;
            sb.append(str);
        }
        return sb.toString();
    }

    public static String toHexString(byte[] input) {
        return toHexString(input, " ");
    }

    public static byte[] fromInt32(int input) {
        byte[] result = new byte[4];
        result[3] = (byte) (input >> 24 & 0xFF);
        result[2] = (byte) (input >> 16 & 0xFF);
        result[1] = (byte) (input >> 8 & 0xFF);
        result[0] = (byte) (input & 0xFF);
        return result;
    }

    public static byte[] fromInt32R(int input) {
        byte[] result = new byte[4];
        result[0] = (byte) ((input >> 24) & 0xFF);
        result[1] = (byte) ((input >> 16) & 0xFF);
        result[2] = (byte) ((input >> 8) & 0xFF);
        result[3] = (byte) (input & 0xFF);
        return result;
    }

    public static byte[] fromInt16(int input) {
        byte[] result = new byte[2];
        result[0] = (byte) (input >> 8 & 0xFF);
        result[1] = (byte) (input & 0xFF);
        return result;
    }

    public static int fromBytes(byte a, byte b) {
        return ((a & 0xFF) << 8) + (b & 0xFF);
    }

    public static byte[] fromInt16Reversal(int input) {
        byte[] result = new byte[2];
        result[1] = (byte) (input >> 8 & 0xFF);
        result[0] = (byte) (input & 0xFF);
        return result;
    }

    public static int byteArrayToInt16(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < 2; i++) {
            int shift = (1 - i) * 8;
            value += (bytes[i] & 0xFF) << shift;
        }
        return value;
    }

    /**
     * byte[]转int
     *
     * @param bytes 需要转换成int的数组
     * @return int值
     */
    public static int byteArrayToInt(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (3 - i) * 8;
            value += (bytes[i] & 0xFF) << shift;
        }
        return value;
    }

}

