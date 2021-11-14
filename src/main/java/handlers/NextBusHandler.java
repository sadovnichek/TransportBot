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

    public NextBusHandler(Document doc) {
        busStops = new BusStops(doc);
        spellchecker = new Spellchecker(busStops.getAllNames());
    }

    /**
     * @return обрабатываемая этим классом команда
     */
    @Override
    public String getHandledCommand() {
        return "/nextbus";
    }

    /**
     * Принимает сообщение от пользователя, возвращает ответ
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
    private String parseData(String data) {
        String[] words = data.trim().split("[:]+");
        int length = words.length;
        String name = spellchecker.editWord(words[0]);
        if(spellchecker.isWordContainsIncorrectSymbols(name))
            return "*Такой остановки нет.*";
        if(busStops.getReferenceByName(name) == null) {
            var suggestedWords = spellchecker.tryGetCorrectName(name);
            if(suggestedWords.size() == 1)
                name = suggestedWords.get(0);
            else {
                if (suggestedWords.size() < 5)
                    suggestedWords.addAll(spellchecker.sortByEditorDistance(name));
                return "*Такой остановки нет. Возможно, вы имели в виду:*\n" + printSuggestions(suggestedWords);
            }
        } if(length == 2) { // correct
            boolean onlyTram = words[1].contains("(Трамвай)");
            var direction = spellchecker.editWord(words[1]);
            if (spellchecker.isWordContainsIncorrectSymbols(direction))
                return "*Такого направления нет.*";
            if (busStops.getReferenceByName(direction) == null) {
                var suggestedWords = spellchecker.tryGetCorrectName(direction);
                if (suggestedWords.size() == 1)
                    direction = suggestedWords.get(0);
                else {
                    if (suggestedWords.size() < 5)
                        suggestedWords.addAll(spellchecker.sortByEditorDistance(direction));
                    return "*Такого направления нет. Возможно, вы имели в виду:*\n" + printSuggestions(suggestedWords);
                }
            }
            return processDefinedDirection(name, direction, onlyTram);
        }
        else if (length == 1) // if not defined direction
            return processNonDefinedDirection(name);
        return "*Произошла неизвестная ошибка.*";
    }

    /**
     * Обрабатывает входные данные, если указаны название и направление.
     * @param name имя остановки
     * @param direction направление - следующая остановка по пути маршрута
     * @param onlyTram - является ли остановка трамвайной
     * @return расписание в виде строки
     */
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

    /**
     * Обрабатывает входные данные, если указано только название
     * @param name имя остановки
     * @return строка с названиями направлений
     */
    private String processNonDefinedDirection(String name) {
        StringBuilder reply = new StringBuilder("*" + name + "\n\nУкажите направление из возможных:*\n");
        try {
            Document doc = Jsoup.connect(busStops.getReferenceByName(name)).get();
            Set<String> directions = busStops.getDirections(name, doc);
            if (directions.size() == 0)
                return "*Нет транспорта в ближайшее время.*";
            for(String route : directions){
                reply.append(route).append("\n");
            }
            return reply.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } return null;
    }

    /**
     * Переводит список строк - подсказок в единую строку
     * @param words список подсказок для пользователя, если он ошибся
     */
    private String printSuggestions(List<String> words) {
        StringBuilder reply = new StringBuilder();
        for (var hint : words)
            reply.append(hint).append("\n");
        return reply.toString();
    }
}