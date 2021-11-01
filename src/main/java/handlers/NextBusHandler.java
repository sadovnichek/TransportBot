package handlers;

import models.BusStops;
import models.Handler;
import models.TimeTable;
import models.User;
import wrappers.ResponseMessage;
import wrappers.SimpleMessageResponse;
import wrappers.MessageData;

import java.util.List;
import java.util.Set;

/**
 * Класс, обрабатывающий команду /nextbus.
 */
public class NextBusHandler implements Handler {
    /**
     * Поле типа BusStops.
     * Этот класс хранит словарь <название остановки> : <ссылка на неё на сайте bustime.ru>
     */
    private final BusStops busStops;

    /**
     * Конструктор - создание нового объекта и инициализация busStops
     */
    public NextBusHandler() {
        busStops = new BusStops();
    }

    /**
     * @return обрабатываемая этим классом команда
     */
    @Override
    public String getHandledCommand() {
        return "/nextbus";
    }

    /**
     * Принимает сообщение от пользователя
     * @param user - сам пользователь
     * @param message - сообщение от пользователя
     * @return сообщения, сгенерированные ботом
     */
    @Override
    public List<ResponseMessage> handleMessage(User user, MessageData message) {
        String data = message.getMessageData().replace("/nextbus", "");
        String reply = ParseData(data);
        SimpleMessageResponse messageResponse = new SimpleMessageResponse(user.getChatId(), reply);
        messageResponse.enableMarkdown();
        return List.of(messageResponse);
    }

    /**
     * Обрабатывает данные, полуенные от пользователя
     * @param data - сообщение от пользователя без команды /nextbus
     * @return генерирует ответ - строку
     */
    private String ParseData(String data){
        String[] words = data.trim().split("[:]+");
        int length = words.length;
        if (data.equals("")) // without args
            return "*Укажите название остановки и направление в формате: <остановка>: <направление>.\n" +
                    "Направление - следующая остановка по маршруту.\n" +
                    "Если вы затрудняетесь указать направление, то не указывайте его.\n" +
                    "Бот попробует вам его подсказать.*";
        String name = words[0];
        if(busStops.getReferenceByName(name) == null)
            return "*Такой остановки нет. Проверьте правильность написания.*";
        if(length == 2) { // correct
            boolean onlyTram = words[1].contains("(Трамвай)");
            String direction = words[1].replace("(Трамвай)", "").trim();
            StringBuilder reply = new StringBuilder();
            if(busStops.getReferenceByName(direction) == null)
                return "*Такого направления нет. Проверьте правильность написания.*";
            var timetables = busStops.getTimeTable(name, direction, onlyTram);
            if (timetables.size() == 0)
                return "*Нет транспорта в ближайшее время.*";
            for(TimeTable timetable : timetables){
                reply.append(timetable).append("\n");
            }
            return reply.toString();
        }
        else if (length == 1) { // if not defined direction
            StringBuilder reply = new StringBuilder("*Укажите направление из возможных:*\n\n");
            Set<String> directions = busStops.getDirections(name);
            for(String route : directions){
                reply.append(route).append("\n");
            }
            return reply.toString();
        }
        return "*Произошла неизвестная ошибка.*";
    }
}
