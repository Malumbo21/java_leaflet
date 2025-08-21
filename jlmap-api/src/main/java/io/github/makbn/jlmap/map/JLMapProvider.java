package io.github.makbn.jlmap.map;

import lombok.NonNull;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public interface JLMapProvider {
    String getName();

    String getUrl();

    String getAttribution();

    int getMaxZoom();

    Map<String, String> getParameters();

    default void addParameter(@NonNull String key, @NonNull String value) {
        getParameters().put(key, value);
    }

    default String mapProviderAddress() {
        StringBuilder fullUrl = new StringBuilder(getUrl());

        if (getParameters() != null && !getParameters().isEmpty()) {
            fullUrl.append("?");
            for (Map.Entry<String, String> entry : getParameters().entrySet()) {
                String encodedKey = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8);
                String encodedValue = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8);
                fullUrl.append(encodedKey).append("=").append(encodedValue).append("&");
            }
            fullUrl.setLength(fullUrl.length() - 1);
        }

        return fullUrl.toString();
    }
}
