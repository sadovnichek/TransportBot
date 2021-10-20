package handlers;

import models.Handler;
import models.User;
import wrappers.ResponseMessage;
import wrappers.SimpleMessageResponse;
import wrappers.WrappedUpdate;

import java.util.List;

public class NextBusHandler implements Handler {
    // класс BusStops

    public NextBusHandler()
    {
        System.out.println("NextBusHandler constructor!");
    }

    @Override
    public String getHandledCommand() {
        return "/nextbus";
    }

    @Override
    public List<ResponseMessage> handleMessage(User user, WrappedUpdate message) {
        String data = message.getMessageData();
        String reply = ParseData(data);
        SimpleMessageResponse messageResponse = new SimpleMessageResponse(user.getChatId(), reply);
        messageResponse.enableMarkdown();
        return List.of(messageResponse);
    }

    private String ParseData(String command){
        String[] words = command.split("[\\s!,.:]+");
        int length = words.length;
        // нужно проверить, есть ли такая остановка вообще
        if(length == 3) // правильно
            return "*" + words[1] + "--->" +  words[2] + "*";
        else if (length == 2) { // почти правильно, осталось указать направление
            // BusStops.route
            return "*Укажите направление.*";
        }
        else if (length == 1) // команда без аргументов
            return "*Укажите название остановки и направление в формате: <остановка>: <направление>.\n" +
                    "Направление - следующая остановка по маршруту.\n" +
                    "Если вы затрудняетесь указать направление, то не указывайте его.\n" +
                    "Бот попробует вам его подсказать.*";
        return "*Произошла неизвестная ошибка.*";
    }
}
