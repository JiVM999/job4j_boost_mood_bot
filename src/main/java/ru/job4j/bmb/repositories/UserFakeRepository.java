package ru.job4j.bmb.repositories;

import org.springframework.test.fake.CrudRepositoryFake;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserFakeRepository extends CrudRepositoryFake<User, Long> implements UserRepository {

    public List<User> findAll() {
        return new ArrayList<>(memory.values());
    }

    @Override
    public List<User> findByClientId(Long clientId) {
        return memory.values().stream()
                .filter(user -> user.getClientId() == clientId)
                .collect(Collectors.toList());
    }
}