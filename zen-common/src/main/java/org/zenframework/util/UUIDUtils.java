package org.zenframework.util;

import java.util.UUID;

/**
 * UUID utility
 */
public class UUIDUtils {

    public static String randomUUID(boolean shortUUID) {
        return shortUUID ? shortUUID() : defaultUUID();
    }

    public static String defaultUUID() {
        UUID uuid = UUID.randomUUID();
        StringBuilder sb = new StringBuilder(32);
        sb.append(hexDigits(uuid.getMostSignificantBits() >> 32, 8));
        sb.append(hexDigits(uuid.getMostSignificantBits() >> 16, 4));
        sb.append(hexDigits(uuid.getMostSignificantBits(), 4));
        sb.append(hexDigits(uuid.getLeastSignificantBits() >> 48, 4));
        sb.append(hexDigits(uuid.getLeastSignificantBits(), 12));
        return sb.toString();
    }

    public static String shortUUID() {
        UUID uuid = UUID.randomUUID();
        StringBuilder sb = new StringBuilder(19);
        sb.append(digits(uuid.getMostSignificantBits() >> 32, 8));
        sb.append(digits(uuid.getMostSignificantBits() >> 16, 4));
        sb.append(digits(uuid.getMostSignificantBits(), 4));
        sb.append(digits(uuid.getLeastSignificantBits() >> 48, 4));
        sb.append(digits(uuid.getLeastSignificantBits(), 12));
        return sb.toString();
    }

    /** Returns val represented by the specified number of hex digits. */
    private static String hexDigits(long val, int digits) {
        long hi = 1L << (digits * 4);
        return Long.toHexString(hi | (val & (hi - 1))).substring(1);
    }

    private static String digits(long val, int digits) {
        long hi = 1L << (digits * 4);
        return NumberUtils.toRadix(hi | (val & (hi - 1)), 62)
                .substring(1);
    }

    public static void main(String[] args) throws Exception {
        System.out.println(shortUUID());
//        System.out.println(Integer.toHexString(255));
    }

}
