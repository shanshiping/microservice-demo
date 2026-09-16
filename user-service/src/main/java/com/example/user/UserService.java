package com.example.user;

import com.example.api.entity.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public UserService() {
        User user1 = new User();
        user1.setId(idGenerator.getAndIncrement());
        user1.setUsername("alice");
        user1.setEmail("alice@example.com");
        users.put(user1.getId(), user1);

        User user2 = new User();
        user2.setId(idGenerator.getAndIncrement());
        user2.setUsername("bob");
        user2.setEmail("bob@example.com");
        users.put(user2.getId(), user2);
    }

    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User createUser(User user) {
        user.setId(idGenerator.getAndIncrement());
        users.put(user.getId(), user);
        return user;
    }
}
