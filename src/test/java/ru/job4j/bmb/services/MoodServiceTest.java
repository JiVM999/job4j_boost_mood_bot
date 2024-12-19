package ru.job4j.bmb.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.Achievement;
import ru.job4j.bmb.model.Award;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.AchievementRepository;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.repository.UserRepository;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MoodServiceTest {

    @Mock
    private MoodLogRepository moodLogRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AchievementRepository achievementRepository;

    @InjectMocks
    private MoodService moodService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setChatId(123L);
        user.setClientId(456L);
    }

    @Test
    void testWeekMoodLogCommandWithLogs() {
        MoodLog log = new MoodLog();
        log.setCreatedAt(Instant.now().getEpochSecond());
        log.setMood(new Mood("Happy", true));
        when(moodLogRepository.findMoodLogsForMonth(anyLong(), anyLong())).thenReturn(List.of(log));

        Optional<Content> result = moodService.weekMoodLogCommand(user.getChatId(), user.getClientId());

        assertTrue(result.isPresent());
        assertTrue(result.get().getText().contains("Статистика за неделю"));
        assertTrue(result.get().getText().contains("Happy"));
    }

    @Test
    void testWeekMoodLogCommandWithoutLogs() {
        when(moodLogRepository.findMoodLogsForMonth(anyLong(), anyLong())).thenReturn(Collections.emptyList());

        Optional<Content> result = moodService.weekMoodLogCommand(user.getChatId(), user.getClientId());

        assertTrue(result.isPresent());
        assertEquals("Статистика за неделю:\nNo mood logs found.", result.get().getText());
    }

    @Test
    void testMonthMoodLogCommandWithLogs() {
        MoodLog log = new MoodLog();
        log.setCreatedAt(Instant.now().getEpochSecond());
        log.setMood(new Mood("Sad", false));
        when(moodLogRepository.findMoodLogsForWeek(anyLong(), anyLong())).thenReturn(List.of(log));

        Optional<Content> result = moodService.monthMoodLogCommand(user.getChatId(), user.getClientId());

        assertTrue(result.isPresent());
        assertTrue(result.get().getText().contains("Статистика за месяц"));
        assertTrue(result.get().getText().contains("Sad"));
    }

    @Test
    void testMonthMoodLogCommandWithoutLogs() {
        when(moodLogRepository.findMoodLogsForWeek(anyLong(), anyLong())).thenReturn(Collections.emptyList());

        Optional<Content> result = moodService.monthMoodLogCommand(user.getChatId(), user.getClientId());

        assertTrue(result.isPresent());
        assertEquals("Статистика за месяц:\nNo mood logs found.", result.get().getText());
    }

    @Test
    void testAwardsWithAchievements() {
        Award award = new Award();
        award.setDays(5);
        award.setTitle("Best Mood");
        award.setDescription("Awarded for maintaining a positive mood.");

        Achievement achievement = new Achievement();
        achievement.setAward(award);
        achievement.setUser(user);
        achievement.setCreateAt(Instant.now().getEpochSecond() - 1000);

        when(userRepository.findByClientId(user.getClientId())).thenReturn(List.of(user));
        when(achievementRepository.findAll()).thenReturn(List.of(achievement));

        Optional<Content> result = moodService.awards(user.getChatId(), user.getClientId());

        assertTrue(result.isPresent());
        assertTrue(result.get().getText().contains("Полученные награды за месяц"));
        assertTrue(result.get().getText().contains("Best Mood"));
    }

    @Test
    void testAwardsWithoutAchievements() {
        when(userRepository.findByClientId(user.getClientId())).thenReturn(List.of(user));
        when(achievementRepository.findAll()).thenReturn(Collections.emptyList());

        Optional<Content> result = moodService.awards(user.getChatId(), user.getClientId());

        assertTrue(result.isPresent());
        assertEquals("Полученные награды за месяц:\nNo awards found.", result.get().getText());
    }
}