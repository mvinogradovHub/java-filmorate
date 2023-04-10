package ru.yandex.practicum.filmorate.storage.user.impl.db;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;


@Component
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserExtractor userExtractor;

    public UserDbStorage(JdbcTemplate jdbcTemplate, UserExtractor userExtractor) {
        this.jdbcTemplate = jdbcTemplate;
        this.userExtractor = userExtractor;
    }

    @Override
    public User addUser(User user) {
        String sql = "INSERT INTO \"USER\" (USER_ID,EMAIL,LOGIN,NAME, BIRTHDAY) VALUES (?,?,?,?,?)";
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
        String sql = "SELECT u.USER_ID, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY, f.RECIPIENT_ID AS FRIEND_ID \n" +
                "FROM \"USER\" u\n" +
                "LEFT JOIN FRIENDSHIP f ON u.USER_ID = f.SENDER_ID\n" +
                "ORDER BY u.USER_ID ASC";
        Set<User> users = jdbcTemplate.query(sql, userExtractor);
        if (users.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(users);
    }

    @Override
    public User getUserById(Long id) {
        String sqlSelectFilm = "SELECT u.USER_ID, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY, f.RECIPIENT_ID AS FRIEND_ID \n" +
                "FROM \"USER\" u\n" +
                "LEFT JOIN FRIENDSHIP f ON u.USER_ID = f.SENDER_ID\n" +
                "WHERE u.USER_ID = ?";
        Set<User> users = jdbcTemplate.query(sqlSelectFilm, userExtractor, id);
        if (users.isEmpty()) {
            return null;
        }
        return users.stream().findFirst().get();
    }

    @Override
    public Set<User> getFriends(Long id) {
        String sql = "SELECT common.USER_ID, common.EMAIL, common.LOGIN, common.NAME, common.BIRTHDAY, f.RECIPIENT_ID AS FRIEND_ID\n" +
                "FROM (SELECT g.USER_ID, g.EMAIL, g.LOGIN, g.NAME, g.BIRTHDAY\n" +
                "FROM FRIENDSHIP f2\n" +
                "JOIN \"USER\" g ON g.USER_ID = f2.RECIPIENT_ID\n" +
                "WHERE f2.SENDER_ID = ?) AS common\n" +
                "LEFT JOIN FRIENDSHIP f ON common.USER_ID = f.SENDER_ID\n" +
                "ORDER BY FRIEND_ID ASC";

        Set<User> userSet = new TreeSet<>(Comparator.comparingLong(User::getId));
        userSet.addAll(jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id));
        if (userSet.isEmpty()) {
            return Collections.emptySet();
        }
        return userSet;
    }

    @Override
    public Set<User> getCommonFriends(Long userId, Long otherId) {
        String sql = "SELECT common.USER_ID, common.EMAIL, common.LOGIN, common.NAME, common.BIRTHDAY, f.RECIPIENT_ID AS FRIEND_ID\n" +
                "FROM ( SELECT g.USER_ID, g.EMAIL, g.LOGIN, g.NAME, g.BIRTHDAY \n" +
                "FROM FRIENDSHIP f2\n" +
                "JOIN \"USER\" g ON g.USER_ID = f2.RECIPIENT_ID\n" +
                "WHERE f2.SENDER_ID = ?\n" +
                "INTERSECT SELECT g.USER_ID, g.EMAIL, g.LOGIN, g.NAME, g.BIRTHDAY\n" +
                "FROM FRIENDSHIP f2\n" +
                "JOIN \"USER\" g ON g.USER_ID = f2.RECIPIENT_ID\n" +
                "WHERE f2.SENDER_ID = ?\n" +
                ") AS common\n" +
                "LEFT JOIN FRIENDSHIP f ON common.USER_ID = f.SENDER_ID\n" +
                "ORDER BY common.USER_ID ASC";

        Set<User> userSet = new TreeSet<>(Comparator.comparingLong(User::getId));
        userSet.addAll(jdbcTemplate.query(sql, userExtractor, userId, otherId));
        if (userSet.isEmpty()) {
            return Collections.emptySet();
        }
        return userSet;
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
        return new User(id, email, login, name, birthday);
    }

    private Long makeLustId(ResultSet rs) throws SQLException {
        return rs.getLong("USER_ID");
    }

}
