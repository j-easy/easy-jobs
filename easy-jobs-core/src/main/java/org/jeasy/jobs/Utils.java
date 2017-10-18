package org.jeasy.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Utils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, String> parseParameters(String parameters) throws Exception {
        return objectMapper.readValue(parameters, HashMap.class);
    }

    public static String md5(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("md5");
            byte[] bytes = messageDigest.digest(password.getBytes("UTF-8"));
            return String.format("%032x", new BigInteger(1, bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to find MD5 message digest", e);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unable to find UTF-8 encoding", e);
        }
    }
}
