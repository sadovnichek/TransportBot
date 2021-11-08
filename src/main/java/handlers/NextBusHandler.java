package handlers;

import models.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import wrappers.ResponseMessage;
import wrappers.SimpleMessageResponse;
import wrappers.MessageData;

import java.io.IOException;
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
    private final Spellchecker spellchecker;

    /**
     * Конструктор - создание нового объекта и инициализация busStops
     */
    public NextBusHandler() {
        busStops = new BusStops();
        spellchecker = new Spellchecker(busStops);
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
        String reply = parseData(data);
        SimpleMessageResponse messageResponse = new SimpleMessageResponse(user.getChatId(), reply);
        messageResponse.enableMarkdown();
        return List.of(messageResponse);
    }

    /**
     * Обрабатывает данные, полученные от пользователя
     * @param data - сообщение от пользователя без команды /nextbus
     * @return генерирует ответ - строку
     */
    private String parseData(String data){
        String[] words = data.trim().split("[:]+");
        int length = words.length;
        String name = spellchecker.editWord(words[0]);
        if(busStops.getReferenceByName(name) == null) {
            System.out.println(name);
            var suggestedWords = spellchecker.tryGetCorrectName(name);
            if(suggestedWords.size() == 1)
                name = suggestedWords.iterator().next();
            else {
                suggestedWords.addAll(spellchecker.sortByEditorDistance(name));
                return "*Такой остановки нет. Возможно, вы имели в виду:*\n"
                        + printSuggestions(suggestedWords);
            }
        } if(length == 2) { // correct
            boolean onlyTram = words[1].contains("(Трамвай)");
            var direction = spellchecker.editWord(words[1]);
            if(busStops.getReferenceByName(direction) == null) {
                System.out.println(direction);
                var suggestedWords = spellchecker.tryGetCorrectName(direction);
                if(suggestedWords.size() == 1)
                    name = suggestedWords.iterator().next();
                else {
                    suggestedWords.addAll(spellchecker.sortByEditorDistance(direction));
                    return "*Такого направления нет. Возможно, вы имели в виду:*\n" + printSuggestions(suggestedWords);
                }
            } return processDefinedDirection(name, direction, onlyTram);
        }
        else if (length == 1) // if not defined direction
            return processNonDefinedDirection(name);
        return "*Произошла неизвестная ошибка.*";
    }

    private String processDefinedDirection(String name, String direction, boolean onlyTram) {
        StringBuilder reply = new StringBuilder();
        try {
            Document doc = Jsoup.connect(busStops.getReferenceByName(name)).get();
            var timetables = busStops.getTimeTable(name, direction, onlyTram, doc);
            if (timetables.size() == 0)
                return "*Нет транспорта в ближайшее время.*";
            for(TimeTable timetable : timetables){
                reply.append(timetable).append("\n");
            }
            return reply.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String processNonDefinedDirection(String name) {
        StringBuilder reply = new StringBuilder("*Укажите направление из возможных:*\n\n");
        Set<String> directions = busStops.getDirections(name);
        if (directions.size() == 0)
            return "*Нет транспорта в ближайшее время.*";
        for(String route : directions){
            reply.append(route).append("\n");
        }
        return reply.toString();
    }

    private String printSuggestions(List<String> words) {
        StringBuilder reply = new StringBuilder();
        for (var hint : words)
            reply.append(hint).append("\n");
        return reply.toString();
    }
}
