package dev.rygen.intersectionlightcontroller.dtos;

import java.util.Map;

public record ConfigDTO(
        boolean powerOn,
        String speed,
        Map<String, Integer> lightBrightness
) {
}
