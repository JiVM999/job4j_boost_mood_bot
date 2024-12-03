package ru.job4j.bmb.config;

import org.springframework.beans.factory.annotation.Value;

public class AppData {

    @Value("${app.name}")
    private String appName;
}
