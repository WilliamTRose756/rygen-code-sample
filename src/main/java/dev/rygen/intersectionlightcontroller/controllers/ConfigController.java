package dev.rygen.intersectionlightcontroller.controllers;

import dev.rygen.intersectionlightcontroller.dtos.ConfigDTO;
import dev.rygen.intersectionlightcontroller.services.ConfigService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/config")
public class ConfigController {

    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @GetMapping
    public ConfigDTO getConfig() {
        return configService.getConfig();
    }

    @PutMapping
    public ConfigDTO updateConfig(@RequestBody ConfigDTO updatedConfig) {
        return configService.updateConfig(updatedConfig);
    }
}
