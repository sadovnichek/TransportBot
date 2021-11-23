package models;

import wrappers.MessageResponse;
import wrappers.Message;

import java.util.List;

public interface Handler {
    /**
     * Обрабатываемая команда
     */
    String getHandledCommand();

    /**
     * Обработчик сообщения
     * @param user - пользователь
     * @param message - сообщение от пользователя
     * @return список ответами от бота
     */
    List<MessageResponse> handleMessage(User user, Message message);
}
