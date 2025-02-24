package com.github.kingschan1204.easycrawl.helper.encryption;

import java.security.MessageDigest;

public class Md5Helper {
    public String md5(String text) {
        try {

            // Get an instance of MD5 MessageDigest
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Update the digest with the input string bytes
            md.update(text.getBytes());

            // Compute the MD5 hash (returns a byte array)
            byte[] digest = md.digest();

            // Convert the byte array to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
