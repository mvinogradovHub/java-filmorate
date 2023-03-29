package ru.yandex.practicum.filmorate.storage.user.impl.db;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.UtilsUser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Component
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        String sql = "INSERT INTO \"USER\" (USER_ID,EMAIL,LOGIN,NAME, BIRTHDAY) VALUES (?,?,?,?,?)";
        UtilsUser.writeUserNameFromLogin(user);
        Long userId = getNewUserId();
        jdbcTemplate.update(sql, userId, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        return getUserById(userId);
    }

    public Long getNewUserId() {
        String sql = "SELECT USER_ID FROM \"USER\" ORDER BY USER_ID DESC LIMIT 1";
        Optional<Long> userId = jdbcTemplate.query(sql, (rs, rowNum) -> makeLustId(rs)).stream().findFirst();
        return userId.map(aLong -> aLong + 1).orElse(1L);

    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE \"USER\" SET EMAIL = ?,LOGIN = ?, NAME = ?,BIRTHDAY = ? WHERE USER_ID = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return getUserById(user.getId());
    }

    @Override
    public ArrayList<User> getUsers() {
        String sql = "SELECT * FROM \"USER\"";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
        if (users.isEmpty()) {
            return new ArrayList<>();
        }
        return (ArrayList<User>) users;
    }

    @Override
    public User getUserById(Long id) {
        String sqlSelectFilm = "SELECT * FROM \"USER\" WHERE USER_ID = ?";
        List<User> users = jdbcTemplate.query(sqlSelectFilm, (rs, rowNum) -> makeUser(rs), id);
        if (users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }

    @Override
    public Set<User> getFriends(Long id) {
        String sql = "SELECT\n" +
                "\tg.USER_ID,\n" +
                "\tg.EMAIL,\n" +
                "\tg.LOGIN,\n" +
                "\tg.NAME,\n" +
                "\tg.BIRTHDAY\n" +
                "FROM\n" +
                "\tFRIENDSHIP f2\n" +
                "JOIN \"USER\" g ON\n" +
                "\tg.USER_ID = f2.RECIPIENT_ID\n" +
                "WHERE\n" +
                "\tf2.SENDER_ID = ? ORDER BY g.USER_ID ASC";

        Set<User> userSet = new TreeSet<>(Comparator.comparingLong(User::getId));
        userSet.addAll(jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id));
        if (userSet.isEmpty()) {
            return Collections.emptySet();
        }
        return userSet;
    }

    @Override
    public Set<User> getCommonFriends(Long userId, Long otherId) {
        return getFriends(userId).stream().filter(getFriends(otherId)::contains).collect(Collectors.toSet());
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        String sql = "INSERT INTO FRIENDSHIP (SENDER_ID,RECIPIENT_ID) VALUES (?,?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM FRIENDSHIP WHERE SENDER_ID = ? AND RECIPIENT_ID = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        Long id = rs.getLong("USER_ID");
        String email = rs.getString("EMAIL");
        String login = rs.getString("LOGIN");
        String name = rs.getString("NAME");
        LocalDate birthday = rs.getDate("BIRTHDAY").toLocalDate();
        Set<Long> friends = getFriends(id).stream().map(User::getId).collect(Collectors.toSet());
        User user = new User(id, email, login, name, birthday);
        user.setFriends(friends);
        return user;
    }

    private Long makeLustId(ResultSet rs) throws SQLException {
        return rs.getLong("USER_ID");
    }
}
