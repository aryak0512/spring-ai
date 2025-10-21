package com.aryak.springai.model;

import java.util.List;

public record CountryCitiesResponseDto(
        String country,
        List<String> cities
) {
}
