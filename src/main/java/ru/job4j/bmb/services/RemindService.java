package ru.job4j.bmb.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.repository.UserRepository;

@Service
public class RemindService {
    private final TelegramBotService tgRemoteService;
    private final UserRepository userRepository;

    public RemindService(TelegramBotService tgRemoteService, UserRepository userRepository) {
        this.tgRemoteService = tgRemoteService;
        this.userRepository = userRepository;
    }

    @Scheduled(fixedRateString = "${remind.period}")
    public void ping() {
        for (var user : userRepository.findAll()) {
            var message = new SendMessage();
            message.setChatId(user.getChatId());
            message.setText("Ping");
            /*
            Content content = new Content(user.getChatId());
            content.setText("ping");
            tgRemoteService.sent(content);

             */
        }
    }
}
