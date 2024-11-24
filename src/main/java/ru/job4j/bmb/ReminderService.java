package ru.job4j.bmb;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

@Service
public class ReminderService {

    @PostConstruct
    public void init() {
        System.out.println("Bean TelegramBotService is going through init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Bean TelegramBotService will be destroyed now.");
    }
}
