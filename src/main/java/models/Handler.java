package models;

import wrappers.MessageResponse;
import wrappers.MessageUpdate;

import java.util.List;

public interface Handler {
    /**
     * Обрабатываемая команда
     */
    String getHandledCommand();

    /**
     * Обработчик сообщения
     * @param user - пользователь
     * @param messageUpdate - сообщение от пользователя
     * @return список ответами от бота
     */
    List<MessageResponse> handleMessage(User user, MessageUpdate messageUpdate);
}