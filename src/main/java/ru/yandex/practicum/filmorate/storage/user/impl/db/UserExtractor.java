package ru.yandex.practicum.filmorate.storage.user.impl.db;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
public class UserExtractor implements ResultSetExtractor<Set<User>> {
    @Override
    public Set<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Set<User> users = new HashSet<>();
        Set<Long> friends = new HashSet<>();
        while (rs.next()) {
            Long id = rs.getLong("USER_ID");
            String email = rs.getString("EMAIL");
            String login = rs.getString("LOGIN");
            String name = rs.getString("NAME");
            LocalDate birthday = rs.getDate("BIRTHDAY").toLocalDate();
            Long friendId = rs.getLong("FRIEND_ID");
            User user = new User(id, email, login, name, birthday);
            if (users.add(user)) {
                friends = new HashSet<>();
                user.setFriends(friends);
            }
            if (friendId != 0) {
                friends.add(friendId);
            }
        }
        return users;
    }
}
