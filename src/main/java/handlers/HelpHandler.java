package handlers;

import models.Handler;
import models.User;
import wrappers.ResponseMessage;
import wrappers.SimpleMessageResponse;
import wrappers.WrappedUpdate;

import java.util.List;

public class HelpHandler implements Handler {

    @Override
    public String getHandledCommand() {
        return "/help";
    }

    @Override
    public List<ResponseMessage> handleMessage(User user, WrappedUpdate message) {
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
        user.setLastQueryTime();
        return List.of(startMessage);
    }
}
