package com.example.hello_work.utils;

import java.nio.charset.StandardCharsets;

public class EncryptPassword {

    public static String encryptPassword(String pass) {
        StringBuilder passEncoding = new StringBuilder();
        for (byte encoding : pass.getBytes(StandardCharsets.UTF_8)) {
            passEncoding.append(encoding);
        }
        return passEncoding.toString();
    }
}
