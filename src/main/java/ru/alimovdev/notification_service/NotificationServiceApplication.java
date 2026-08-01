package ru.alimovdev.notification_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

}
/**
Д.З.5 - Реализовать микросервис(notification-service) для отправки сообщения на почту
при удалении или добавлении пользователя. Использовать необходимые модули spring и kafka.
При удалении или создании юзера приложение, реализованное до этого(user-service), должно
отправлять сообщение в kafka, в котором содержится информация об операции (удаление или
создание) и email юзера. Новый микросервис(notification-service) должен получить
сообщение из kafka и отправить сообщение на почту юзера в зависимости от операции:
удаление - Здравствуйте! Ваш аккаунт был удалён. Создание - Здравствуйте! Ваш аккаунт
на сайте ваш сайт был успешно создан.
Также отдельно добавить API, которая будет отправлять сообщение на почту(почти тот же функционал, что и через кафку)???
Написать интеграционные тесты для проверки отправки сообщения на почту.
 */

 /*
al.dimitry@yandex.ru

Запуск Kafka
Откройте терминал в папке, где лежит ваш файл docker-compose.yml.
Выполните команду для запуска контейнера в фоновом режиме:
  docker compose up -d

Чтобы убедиться, что Kafka успешно запустилась и готова к работе, можно посмотреть логи контейнера:
  docker logs broker

В конце вывода вы должны увидеть строку, похожую на:
[2026-...] INFO [BrokerServer id=1] Started (org.apache.kafka.server.BrokerServer)

Также можно проверить статус контейнера:
  docker ps

Остановка Kafka (когда закончите работу)
Когда вы закончите работу над проектом, контейнер можно остановить, чтобы он не занимал ресурсы. Для этого из той же папки выполните:
  docker compose down

Kafka UI Conduktor, веб-интерфейс:  http://localhost:8080/



 */