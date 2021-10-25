package handlers;

import models.Handler;
import models.User;
import wrappers.ResponseMessage;
import wrappers.SimpleMessageResponse;
import wrappers.MessageData;

import java.util.List;

/**
 * Обрабатывает команду /help
 */
public class HelpHandler implements Handler {

    /**
     * @return обрабатываемая этим классом команда
     */
    @Override
    public String getHandledCommand() {
        return "/help";
    }

    /**
     * Принимает сообщение от пользователя
     * @param user - сам пользователь
     * @param message - сообщение от пользователя
     * @return сообщение-помощь, сгенерированное ботом
     */
    @Override
    public List<ResponseMessage> handleMessage(User user, MessageData message) {
        String startText = "*Я здесь, чтобы помочь тебе.*" +
                "\n\n*Базовые команды*\n" +
                "/start - начало работы\n" +
                "/help - вывод этой справки\n" +
                "/nextbus - Укажите название остановки и направление в формате: *<остановка>:  <направление>*. " +
                "Направление - следующая остановка по маршруту. " +
                "Если вы затрудняетесь указать направление, то не указывайте его. " +
                "Бот попробует вам его подсказать\n";

        SimpleMessageResponse startMessage = new SimpleMessageResponse(user.getChatId(), startText);
        startMessage.enableMarkdown();
        return List.of(startMessage);
    }
}
