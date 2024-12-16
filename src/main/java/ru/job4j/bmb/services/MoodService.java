package ru.job4j.bmb.services;

import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.Achievement;
import ru.job4j.bmb.model.Award;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.AchievementRepository;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class MoodService {
    private final MoodLogRepository moodLogRepository;
    private final RecommendationEngine recommendationEngine;
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    public MoodService(MoodLogRepository moodLogRepository,
                       RecommendationEngine recommendationEngine,
                       UserRepository userRepository,
                       AchievementRepository achievementRepository) {
        this.moodLogRepository = moodLogRepository;
        this.recommendationEngine = recommendationEngine;
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
    }

    public Content chooseMood(User user, Long moodId) {
        return recommendationEngine.recommendFor(user.getChatId(), moodId);
    }

    public Optional<Content> weekMoodLogCommand(long chatId, Long clientId) {
        var content = new Content(chatId);
        LocalDate month = LocalDate.now().minusDays(30);
        content.setText(formatMoodLogs(moodLogRepository.findMoodLogsForMonth(clientId,
                month.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()), "Статистика за неделю"));
        return Optional.of(content);
    }

    public Optional<Content> monthMoodLogCommand(long chatId, Long clientId) {
        var content = new Content(chatId);
        LocalDate week = LocalDate.now().minusDays(7);
        content.setText(formatMoodLogs(moodLogRepository.findMoodLogsForWeek(clientId,
                week.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()), "Статистика за месяц"));
        return Optional.of(content);
    }

    private String formatMoodLogs(List<MoodLog> logs, String title) {
        if (logs.isEmpty()) {
            return title + ":\nNo mood logs found.";
        }
        var sb = new StringBuilder(title + ":\n");
        logs.forEach(log -> {
            String formattedDate = formatter.format(Instant.ofEpochSecond(log.getCreatedAt()));
            sb.append(formattedDate).append(": ").append(log.getMood().getText()).append("\n");
        });
        return sb.toString();
    }

    private String formatAwards(List<Award> awards, String title) {
        if (awards.isEmpty()) {
            return title + ":\nNo awards found.";
        }
        var sb = new StringBuilder(title + ":\n");
        awards.forEach(award -> {
            sb.append("Дней для получения: ").append(award.getDays()).append("\n")
                    .append("Заголовок: ").append(award.getTitle()).append("\n")
                    .append("Описание: ").append(award.getDescription()).append("\n");
        });
        return sb.toString();
    }

    public Optional<Content> awards(long chatId, Long clientId) {
        var content = new Content(chatId);
        var user = userRepository.findByClientId(clientId);
        List<Award> achievementAwards = achievementRepository.findAll().stream()
                .filter(value -> value.getUser().equals(user)
                        && (value.getCreateAt() <= Instant.now().getEpochSecond()
                        && (value.getCreateAt() >= Instant.now().getEpochSecond() - 30 * 24 * 60 * 60)))
                .map(Achievement::getAward)
                .toList();
        content.setText(formatAwards(achievementAwards, "Полученные награды за месяц"));
        return Optional.of(content);
    }
}

