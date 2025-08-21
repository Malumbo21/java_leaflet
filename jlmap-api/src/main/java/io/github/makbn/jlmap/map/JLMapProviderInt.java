package io.github.makbn.jlmap.map;

import io.github.makbn.jlmap.exception.JLException;
import io.github.makbn.jlmap.model.JLMapOption;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface JLMapProviderInt {
    String getName();

    String getUrl();

    String getAttribution();

    int getMaxZoom();

    Set<JLMapOption.Parameter> getParameters();

    Set<String> getRequiredParametersName();

    default String getMapProviderAddress() {
        StringBuilder fullUrl = new StringBuilder(getUrl());

        Set<String> availableParameters = Optional.ofNullable(getParameters())
                .stream()
                .flatMap(Set::stream)
                .map(JLMapOption.Parameter::key)
                .collect(Collectors.toSet());

        if (!Optional.ofNullable(getRequiredParametersName())
                .stream()
                .flatMap(Set::stream)
                .allMatch(availableParameters::contains)) {
            throw new JLException("Missing required parameters for map provider: " + getRequiredParametersName());
        }

        if (getParameters() != null && !getParameters().isEmpty()) {
            fullUrl.append("?");
            for (JLMapOption.Parameter entry : getParameters()) {
                String encodedKey = URLEncoder.encode(entry.key(), StandardCharsets.UTF_8);
                String encodedValue = URLEncoder.encode(entry.value(), StandardCharsets.UTF_8);
                fullUrl.append(encodedKey).append("=").append(encodedValue).append("&");
            }
            fullUrl.setLength(fullUrl.length() - 1);
        }

        return fullUrl.toString();
    }
}
