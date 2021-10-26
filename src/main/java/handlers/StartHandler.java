package handlers;

import models.Handler;
import models.User;
import wrappers.ResponseMessage;
import wrappers.SimpleMessageResponse;
import wrappers.MessageData;

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
     * @param message - сообщение от пользователя
     * @return сообщения, сгенерированные ботом
     */
    public List<ResponseMessage> handleMessage(User user, MessageData message) {
        String startText = "*Привет! Я - Transport Bot\n" +
                "Я подскажу тебе через сколько минут приедет твой автобус\n" +
                "Помощь тут - /help*";
        SimpleMessageResponse startMessage = new SimpleMessageResponse(user.getChatId(), startText);
        startMessage.enableMarkdown();
        return List.of(startMessage);
    }
}
