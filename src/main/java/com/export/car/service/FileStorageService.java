package com.export.car.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;
import java.util.Set;

@Service
public class FileStorageService {

    public record StoredFile(String publicId, String url) {
    }

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final RestClient restClient = RestClient.create();
    private final String cloudName;
    private final String apiKey;
    private final String apiSecret;

    public FileStorageService(@Value("${CLOUDINARY_CLOUD_NAME:}") String cloudName,
                              @Value("${CLOUDINARY_API_KEY:}") String apiKey,
                              @Value("${CLOUDINARY_API_SECRET:}") String apiSecret) {
        this.cloudName = cloudName;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public StoredFile store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Only JPG, PNG and WEBP images are allowed");
        }
        if (!isConfigured()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Image storage is not configured");
        }

        ByteArrayResource resource;
        try {
            resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    String name = file.getOriginalFilename();
                    return name != null ? name : "image";
                }
            };
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not read file");
        }

        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);
        body.add("api_key", apiKey);
        body.add("timestamp", timestamp);
        body.add("signature", sign("timestamp=" + timestamp));

        try {
            Map<String, Object> response = restClient.post()
                    .uri("https://api.cloudinary.com/v1_1/" + cloudName + "/image/upload")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            if (response == null || response.get("public_id") == null || response.get("secure_url") == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Could not upload image");
            }
            return new StoredFile((String) response.get("public_id"), (String) response.get("secure_url"));
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Could not upload image");
        }
    }

    public void delete(String publicId) {
        if (publicId == null || publicId.isBlank() || !isConfigured()) {
            return;
        }

        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("public_id", publicId);
        form.add("api_key", apiKey);
        form.add("timestamp", timestamp);
        form.add("signature", sign("public_id=" + publicId + "&timestamp=" + timestamp));

        try {
            restClient.post()
                    .uri("https://api.cloudinary.com/v1_1/" + cloudName + "/image/destroy")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ignored) {
        }
    }

    private boolean isConfigured() {
        return !cloudName.isBlank() && !apiKey.isBlank() && !apiSecret.isBlank();
    }

    private String sign(String params) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest((params + apiSecret).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}