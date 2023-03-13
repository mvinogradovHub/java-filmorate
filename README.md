# java-filmorate
Template repository for Filmorate project.

![Схема базы данных](https://github.com/mvinogradovHub/java-filmorate/blob/main/QuickDBD-Free%20Diagram.png)

## Примеры запросов

### Получить список друзей для пользователя с ID 3
SELECT
    sender_id
FROM
    friendship f
WHERE
    recipient_id  = 3
    AND confirmed = true
UNION
SELECT
    recipient_id
FROM
    friendship f2
WHERE
    sender_id     = 3
    AND confirmed = true
    
### Получить список обзих друзей для пользователей с ID 3 и 5
SELECT
    s.sender_id
FROM
    friendship s
join friendship r ON s.sender_id = r.recipient_id 
WHERE
    s.recipient_id  = 3
    AND s.confirmed = true
    and r.confirmed = true
UNION
SELECT
    s.sender_id
FROM
    friendship s
JOIN friendship r ON s.sender_id = r.recipient_id 
WHERE
    s.recipient_id  = 5
    AND s.confirmed = true
    AND r.confirmed = true
    
### Получить топ 10 популярных фильмов
SELECT
    film_id,
    COUNT (user_id) AS like_Count
FROM
    film_like fl
GROUP BY
    film_id
ORDER BY
    like_Count desc 
LIMIT 10
