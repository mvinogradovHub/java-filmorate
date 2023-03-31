package ru.yandex.practicum.filmorate.storage.user.impl.db;

import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {
    private final UserDbStorage userStorage;
    User user;

    @BeforeEach
    void init() {
        User tempUser = new User(null, "mike@mail.ru", "Login", "Mike", LocalDate.of(2000, 12, 27));
        user = userStorage.addUser(tempUser);
    }

    @Test
    public void addUser_addedUserMustHaveIdGreaterThan_1() {
        User userInStorage = userStorage.addUser(user);
        assertThat(userInStorage.getId(), Matchers.greaterThan(1L));
    }

    @Test
    public void getUserById_UserMustHaveTheName_Mike() {
        User user = userStorage.getUserById(1L);
        assertThat(user.getName(), Matchers.is("Mike"));
    }

    @Test
    public void updateUser_UserMustHaveTheName_Misha() {
        user.setName("Misha");
        userStorage.updateUser(user);
        User userInStorage = userStorage.getUserById(user.getId());
        assertThat(userInStorage.getName(), Matchers.is("Misha"));
    }

    @Test
    public void getUsers_NumberOfUsersInListMustBeGreaterThan_Zero() {
        List<User> users = userStorage.getUsers();
        assertThat(users.size(), Matchers.greaterThan(0));
    }

    @Test
    public void addFriend_numberOfTheUsersFriendsMustBeGreaterThan_Zero() {
        User friend = new User(null, "mike@mail.ru", "Login", "Friend", LocalDate.of(2000, 12, 27));
        User friendInStorage = userStorage.addUser(friend);
        userStorage.addFriend(user.getId(), friendInStorage.getId());
        Set<User> users = userStorage.getFriends(user.getId());
        assertThat(users.size(), Matchers.greaterThan(0));
    }

    @Test
    public void getFriends_numberOfTheUsersFriendsMustBeGreaterThan_Zero() {
        User friend = user;
        friend = userStorage.addUser(friend);
        userStorage.addFriend(user.getId(), friend.getId());
        Set<User> users = userStorage.getFriends(user.getId());
        assertThat(users.size(), Matchers.greaterThan(0));
    }

    @Test
    public void deleteFriend_NumberOfTheUsersFriendsMustBe_Original() {
        User friend = user;
        friend = userStorage.addUser(friend);
        userStorage.addFriend(user.getId(), friend.getId());
        Set<User> users = userStorage.getFriends(user.getId());
        userStorage.deleteFriend(user.getId(), friend.getId());
        Set<User> users2 = userStorage.getFriends(user.getId());
        assertThat(users.size(), Matchers.greaterThan(users2.size()));
    }

    @Test
    public void getCommonFriends_MutualFriendsShouldBeMoreThan_Zero() {
        User friend = user;
        friend = userStorage.addUser(friend);
        User user2 = user;
        user2 = userStorage.addUser(user2);
        userStorage.addFriend(user.getId(), friend.getId());
        userStorage.addFriend(user2.getId(), friend.getId());
        Set<User> commonFriends = userStorage.getCommonFriends(user.getId(), user2.getId());
        assertThat(commonFriends.size(), Matchers.greaterThan(0));
    }
}
