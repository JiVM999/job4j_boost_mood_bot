package ru.job4j.bmb.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;

@Service
public class TelegramBotService implements BeanNameAware {
    private final BotCommandHandler handler;

    @PostConstruct
    public void init() {
        System.out.println("Bean TelegramBotService is going through init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Bean TelegramBotService will be destroyed now.");
    }

    public TelegramBotService(BotCommandHandler handler) {
        this.handler = handler;
    }

    public void receive(Content content) {
        handler.receive(content);
    }

    @Override
    public void setBeanName(String name) {
        System.out.println("TelegramBotService");
    }
}