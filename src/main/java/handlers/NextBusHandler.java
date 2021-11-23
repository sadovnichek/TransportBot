package handlers;

import models.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import wrappers.MessageResponse;
import wrappers.SimpleMessageResponse;
import wrappers.Message;

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

    @Override
    public List<MessageResponse> handleMessage(User user, Message message) {
        String data = message.getMessageData();
        var response = new SimpleMessageResponse(user.getChatId());
        String[] words = data.trim().split("[:]+");
        String name = spellchecker.normalizeWord(words[0]);
        if(spellchecker.isWordContainsIncorrectSymbols(name))
            return List.of(response.setText("*Такой остановки нет.*"));
        if(busStops.getReferenceByName(name) == null) {
            var suggestedWords = spellchecker.getSuggestions(name);
            if(suggestedWords.size() == 1)
                name = suggestedWords.get(0);
            else {
                var reply = "*Такой остановки нет. Возможно, вы имели в виду:*\n" + printSuggestions(suggestedWords);
                return List.of(response.setText(reply)); //!
            }
        }
        if(words.length == 2) // defined direction
            return List.of(response.setText(processDefinedDirection(name, words[1])));
        if (words.length == 1) // if not defined direction
            return List.of(response.setText(processNonDefinedDirection(name))); //!
        return List.of(response);
    }

    /**
     * Обрабатывает входные данные, если указаны название и направление.
     * @param name имя остановки
     * @param direction направление - следующая остановка по пути маршрута
     * @return расписание в виде строки
     */
    private String processDefinedDirection(String name, String direction) {
        boolean onlyTram = direction.contains("(Трамвай)");
        direction = spellchecker.normalizeWord(direction);
        if (spellchecker.isWordContainsIncorrectSymbols(direction))
            return "*Такого направления нет.*";
        if (busStops.getReferenceByName(direction) == null) {
            var suggestedWords = spellchecker.getSuggestions(direction);
            if (suggestedWords.size() == 1)
                direction = suggestedWords.get(0);
            else
                return "*Такого направления нет. Возможно, вы имели в виду:*\n" + printSuggestions(suggestedWords);
        }
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
            return null;
        }
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