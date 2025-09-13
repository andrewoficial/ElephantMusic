package ru.kantser.service.lastfm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kantser.controller.AboutPanelController;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);
    private static final HttpClient httpClient = HttpClient.newHttpClient(); // Reuse HttpClient
    public String generateSignature(Map<String, String> params, String API_SECRET) {
        StringBuilder sigString = new StringBuilder();
        params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> sigString.append(entry.getKey()).append(entry.getValue()));
        sigString.append(API_SECRET);

        String signatureInput = sigString.toString();
        logger.info("Signature input: {}", signatureInput); // Логируем входную строку для подписи

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(signatureInput.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            String signature = hexString.toString();
            logger.info("Generated signature: {}", signature); // Логируем подпись
            return signature;
        } catch (NoSuchAlgorithmException e) {
            logger.error("MD5 algorithm not available", e);
            return "";
        }
    }

    public String sendRequest(Map<String, String> params) throws IOException, InterruptedException {
        StringBuilder formData = new StringBuilder();
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (formData.length() > 0) formData.append("&");
            formData.append(param.getKey())
                    .append("=")
                    .append(java.net.URLEncoder.encode(param.getValue(), "UTF-8"));
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://ws.audioscrobbler.com/2.0/"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData.toString()))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        logger.info("Last.fm response body: {}", response.body()); // Log the response
        return response.body();
    }

    String extractSessionKey(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            if (root.has("error")) {
                logger.error("Last.fm error: {}", root.get("message").asText());
                return null;
            }
            JsonNode session = root.path("session").path("key");
            return session.isMissingNode() ? null : session.asText();
        } catch (Exception e) {
            logger.error("Failed to parse session key from JSON: {}", json, e);
            return null;
        }
    }

    String extractUsername(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            if (root.has("error")) {
                logger.error("Last.fm error: {}", root.get("message").asText());
                return null;
            }
            JsonNode name = root.path("session").path("name");
            return name.isMissingNode() ? null : name.asText();
        } catch (Exception e) {
            logger.error("Failed to parse username from JSON: {}", json, e);
            return null;
        }
    }
}
