package models;

import wrappers.ResponseMessage;
import wrappers.MessageData;

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
    List<ResponseMessage> handleMessage(User user, MessageData message);
}
