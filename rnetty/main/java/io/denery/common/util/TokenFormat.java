package io.denery.common.util;

import java.nio.charset.StandardCharsets;

public class TokenFormat {

    /*
        Generates simple token from String, can be easily complicated.
        Looks like: 99#100#87#99#100#87#99#100#87#
     */
    public static String generateToken(String nickname) {
        byte[] bytes = nickname.getBytes(StandardCharsets.UTF_8);
        String stringToken = "";
        try {
            for (byte b : bytes) {
                stringToken += b + "#";
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return stringToken;
    }
    /*
        Parses token from byte array coded in UTF-8.
     */
    public static String getNameByToken(byte[] byteArray) {
        String token = new String(byteArray, StandardCharsets.UTF_8);
        char[] ch = new char[token.length()];
        String nickname = "";

        for (int i = 0; i < token.length(); i++) {
            ch[i] = token.charAt(i);
        }

        String s = "";
        for (char c : ch) {
            byte x;
            if (c == '#') {
                x = Byte.parseByte(s);
                nickname += new String(new byte[]{x}, StandardCharsets.UTF_8);
                s = "";
            } else {
                byte b = Byte.parseByte(String.valueOf(c));
                s += b;
            }
        }
        return nickname;
    }
}
