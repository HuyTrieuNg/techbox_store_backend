package vn.techbox.techbox_store.payment.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;

public final class VnPayUtils {
    private static final SecureRandom RANDOM = new SecureRandom();

    private VnPayUtils() {}

    public static String hmacSHA512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac.init(secretKey);
            byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Error generating HMAC SHA512 signature", e);
        }
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String getIpAddress(HttpServletRequest request) {
        if (request == null) return "0.0.0.0";
        String ip = getHeader(request, "X-Forwarded-For");
        if (ip != null) {
            int comma = ip.indexOf(',');
            return comma > 0 ? ip.substring(0, comma).trim() : ip.trim();
        }
        ip = getHeader(request, "X-Real-IP");
        if (ip != null) return ip;
        ip = getHeader(request, "CF-Connecting-IP");
        if (ip != null) return ip;
        return request.getRemoteAddr();
    }

    private static String getHeader(HttpServletRequest req, String name) {
        String v = req.getHeader(name);
        return (v == null || v.isBlank() || "unknown".equalsIgnoreCase(v)) ? null : v;
    }

    public static String getRandomNumber(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    public static String buildCanonicalQuery(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                sb.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                  .append("=")
                  .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (i < fieldNames.size() - 1) sb.append("&");
            }
        }
        return sb.toString();
    }
}

