package io.denery.util;

import java.nio.charset.StandardCharsets;

public class TokenFormat {

    /**
     *   Generates simple token from String, can be easily complicated.
     *   Looks like: 99#100#87#99#100#87#99#100#87#
     */
    public static String generateToken(String nickname) {
        byte[] bytes = nickname.getBytes(StandardCharsets.UTF_8);
        StringBuilder stringToken = new StringBuilder();
        try {
            for (byte b : bytes) {
                stringToken.append(b).append("#");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return stringToken.toString();
    }
    /**
     *   Parses token from byte array coded in UTF-8.
     */
    public static String getNameByToken(byte[] byteArray) {
        String token = new String(byteArray, StandardCharsets.UTF_8);
        char[] ch = token.toCharArray();
        StringBuilder nickname = new StringBuilder();

        StringBuilder s = new StringBuilder();
        for (char c : ch) {
            byte x;
            if (c == '#') {
                x = Byte.parseByte(s.toString());
                nickname.append(new String(new byte[]{x}, StandardCharsets.UTF_8));
                s = new StringBuilder();
            } else {
                s.append(c);
            }
        }
        return nickname.toString();
    }
}
