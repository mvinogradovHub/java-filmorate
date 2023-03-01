package ru.yandex.practicum.filmorate.exception.user;

public class UserNotFoundFriendException extends RuntimeException{

    public UserNotFoundFriendException(String message) {
        super(message);
    }
}
