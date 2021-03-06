package handlers;

import models.Handler;
import models.User;
import wrappers.MessageResponse;
import wrappers.SimpleMessageResponse;
import wrappers.MessageUpdate;

import java.util.List;

/**
 * Обрабатывает команду /start
 */
public class StartHandler implements Handler {
    /**
     * @return обрабатываемая этим классом команда
     */
    public String getHandledCommand(){
        return "/start";
    }

    /**
     * Принимает сообщение от пользователя
     * @param user - сам пользователь
     * @param messageUpdate - сообщение от пользователя
     * @return сообщения, сгенерированные ботом
     */
    public List<MessageResponse> handleMessage(User user, MessageUpdate messageUpdate) {
        String startText = "*Привет! Я - Transport Bot\n" +
                "Помощь тут - /help*";
        SimpleMessageResponse startMessage = new SimpleMessageResponse(user.getChatId(), startText);
        return List.of(startMessage);
    }
}