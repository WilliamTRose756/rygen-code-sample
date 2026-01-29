package dev.rygen.intersectionlightcontroller.services;

import dev.rygen.intersectionlightcontroller.dtos.ConfigDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ConfigService {

    private final AtomicReference<ConfigDTO> currentConfig = new AtomicReference<>(defaultConfig());

    public ConfigDTO getConfig() {
        return currentConfig.get();
    }

    public ConfigDTO updateConfig(ConfigDTO updatedConfig) {
        currentConfig.set(updatedConfig);
        return updatedConfig;
    }

    private static ConfigDTO defaultConfig() {
        Map<String, Integer> brightness = new HashMap<>();
        brightness.put("roadA", 2);
        brightness.put("roadB", 2);
        return new ConfigDTO(true, "medium", brightness);
    }
}
