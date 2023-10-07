## Explore With ME
_Ссылка на пул-реквест: https://github.com/Oligarhselmasha/java-explore-with-me/pull/5_

В приложении реализован функционал бэкенда сервиса афиши событий. Приложение разбито на 4 отдельных контейнера,
запусаемых посредством Docker:

1. ewm-service - основной сервис - отвечает за основную логику приложения;
2. ewm_postgres_main - база данных основного сервиса;
3. docker_stat - обработка статистики по просмотрам событий;
4. ewm_postgres_stat - хранение обработанной статистики.

Приложение по сути является API, реализующим возможности фреймворка Spring Boot. Модульная структура и экспорт библиотек
осуществлены с помощью фреймворка Maven. Хранение данных организовано с помощью решения от Postgres. Для большей 
легковестности проекта (сокращения шаблонного кода) использована библиотека Lombok. Приложение построено по принципам 
SOLID. Контроллеры и сервисы разделены в соответсвии с выполняемым функционалом. Тело запросов и возращаемый ответ
передаются в формате JSON.

####    В основном сервисе контроллеры делятся на три типа, отнесенных к соответсвующим пакетам:

#####   1. Админская часть. Доступ возможет для пользователя с правами админа. Включает следующие эндпоинты:

1.1 **POST** _/admin/categories_: Добавляет новую категорию;

1.2 **DELETE** _/admin/categories/{catId}_: Удаляет существующую категорию;

1.3 **PATCH** _/admin/categories/{catId}_: Изменяет существующую категорию;

1.4 **POST** _/admin/compilations/{compId}_: Добавляет новую компиляцию событий;

1.5 **DELETE** _/admin/compilations/{compId}_: Удаляет компиляцию;

1.6 **PATCH** _/admin/compilations/{compId}_: Изменяет существующую компиляцию;

1.7 **GET** _/admin/events_: Получает полную информацию обо всех событиях подходящих под переданные условия;

1.8 **PATCH** _/admin/events/{eventId}_: Изменяет любые события (в т.ч. публикация);

1.9 **GET** _/admin/users_: Получает информацию обо всех пользователях;

1.10 **GET** _/admin/users/{userId}_: Получает информацию о конкретном пользователе;

1.11 **GET** _/admin/comments_: Получает комментарии всех пользователей;

1.12 **DELETE** _/admin/comments/{commentId}_: Удаляет комментарий пользователя по id;

1.13 **DELETE** _/admin/comments_: Удаляет все комментарии;

1.14 **PATCH** _/admin/comments_: Редактирует определенный комментарий;

##### 2. Публичная часть. Доступна всем желающим: Важно: доступ только к опубликованным событиям. Включает следующие эндпоинты:

2.1 **GET** _/compilations_: Получает подборки событий;

2.2 **GET** _/compilations/{compId}_: Получает подборку по id;

2.3 **GET** _/categories_: Получает список категорий;

2.4 **GET** _/categories/{catId}_: Получает категорию по id;

2.5 **GET** _/events_: Получает список событий;

2.5 **GET** _/events/{id}_: Получает событие по id.

##### 3. Приватная часть. Доступна только после авторизации. Включает следующие эндпоинты:

3.1 **GET** _/users/{userId}/events_: Получает события, добавленные текущим пользователем;

3.2 **POST** _/users/{userId}/events_: Добавляет новое событие;

3.3 **GET** _/users/{userId}/events/{eventId}_: Получает полную информацию о событии, добавленного текущим пользователем;

3.4 **PATCH** _/users/{userId}/events/{eventId}_: Изменяет событие, добавленное текущим пользователем;

3.5 **GET** _/users/{userId}/events/{eventId}/requests_: Получает информацию о запросах на участие в событии;

3.6 **PATCH** _/users/{userId}/events/{eventId}/requests_: Подтверждает/отклоняет заявку на участие в событии;

3.7 **GET** _/users/{userId}/requests_: Получает заявки на участие в чужих событих;

3.8 **POST** _/users/{userId}/requests_: Отправка запроса на участие в чужом событии;

3.9 **DELETE** _/users/{userId}/requests/{requestId}/cancel_: Отмена запроса на участие в событии;

3.10 **POST** _/users/{userId}/comments/{eventId}_: Оставление комментария к событию;

3.11 **_PATCH_** _/users/{userId}/comments_: Редактирование комментария;

3.12 **DELETE** _/users/{userId}/comments/{commentId}_: Удаление своего коммнтария;

3.13 **GET** _/users/{userId}/comments_: Получения комментарием определенного автора.

####    В сервисе статистики реализовано два эндпоинта, предназначенных для обработки статистики:

1. **GET** _/stats_: Предоставляет информацию о количестве обращений к событиям;
2. **POST** _/hit_: Заносит в базу данных информацию об обращении к событию

Взаимодействие API основного сервиса и сервиса статистики организовано посредством http-клиента на основе 
шаблона RestTemplate.

