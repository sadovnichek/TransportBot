package handlers;

import models.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import wrappers.ButtonsMessageResponse;
import wrappers.MessageResponse;
import wrappers.SimpleMessageResponse;
import wrappers.Message;

import java.io.IOException;
import java.util.Collections;
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
     * Класс, предлагающий исправления, если такой остановки не нашлось
     */
    private final Corrector corrector;

    public NextBusHandler(BusStops busStops) {
        this.busStops = busStops;
        this.corrector = new Corrector(busStops.getAllNames());
    }

    /**
     * @return обрабатываемая этим классом команда
     */
    @Override
    public String getHandledCommand() {
        return "/nextbus";
    }

    /**
     * Является ли строка числом или нет
     * @param str - строка
     * @return true - число, false - не число
     */
    private boolean isNumber(String str) {
        if (str == null || str.isEmpty()) return false;
        for (int i = 0; i < str.length(); i++) {
            if(str.charAt(0) == '-') continue;
            else if (!Character.isDigit(str.charAt(i))) return false;
        }
        return true;
    }

    /**
     * Обрабатывает входящее от пользователя сообщение с командой /nextbus
     * @param user - пользователь
     * @param message - сообщение от пользователя
     * @return список сообщений в ответ пользователю
     */
    @Override
    public List<MessageResponse> handleMessage(User user, Message message) {
        String userMessage = message.getMessageData();
        String[] tokens = userMessage.trim().split("[:]+");
        if(message.getMessageData().equals(""))
            throw new IllegalArgumentException("command cannot be empty");
        String name = normalizeWord(tokens[0]).trim();
        if(isNumber(name))
            name = busStops.getNameByHashcode(Integer.parseInt(name));
        var suggestedWords = corrector.getSuggestions(name);
        if(isWordContainsIncorrectSymbols(name) || suggestedWords.size() == 0)
            return List.of(new SimpleMessageResponse(user.getChatId(), "*Такой остановки нет.*"));
        if(busStops.getReferenceByName(name) == null) {
            if(suggestedWords.size() == 1)
                name = suggestedWords.get(0);
            else {
                var reply = "*Такой остановки нет. Возможно, вы имели в виду:*";
                return List.of(new ButtonsMessageResponse(user.getChatId(), reply, suggestedWords));
            }
        } if(tokens.length == 2) // defined direction
            return List.of((processDefinedDirection(name, tokens[1].trim(), user)));
        if (tokens.length == 1) { // if not defined direction
            return List.of(processNonDefinedDirection(name, user));
        }
        return Collections.emptyList();
    }

    /**
     * Обрабатывает входные данные, если указаны название и направление.
     * @param name имя остановки
     * @param direction направление - следующая остановка по пути маршрута
     * @return расписание в виде строки
     */
    private MessageResponse processDefinedDirection(String name, String direction, User user) {
        if(isNumber(direction))
            direction = busStops.getNameByHashcode(Integer.parseInt(direction));
        direction = normalizeWord(direction);
        var suggestedWords = corrector.getSuggestions(direction);
        if (isWordContainsIncorrectSymbols(direction) || suggestedWords.size() == 0)
            return new SimpleMessageResponse(user.getChatId(), "*Такого направления нет*");
        if (busStops.getReferenceByName(direction) == null) {
            if (suggestedWords.size() == 1)
                direction = suggestedWords.get(0);
            else {
                var reply = "*Такого направления нет. Возможно, вы имели в виду:*";
                return new ButtonsMessageResponse(user.getChatId(), reply, suggestedWords, name);
            }
        }
        try {
            StringBuilder reply = new StringBuilder();
            Document doc = Jsoup.connect(busStops.getReferenceByName(name)).get();
            var timetables = busStops.getTimeTable(name, direction, doc);
            if (timetables.size() == 0)
                return new SimpleMessageResponse(user.getChatId(), "*Нет транспорта в ближайшее время.*");
            for(TimeTable timetable : timetables){
                reply.append(timetable).append("\n");
            }
            return new SimpleMessageResponse(user.getChatId(), reply.toString());
        }
        catch (IOException e) {
            e.printStackTrace();
            return new SimpleMessageResponse(user.getChatId(), "Ошибка при подключении к данным");
        }
    }

    /**
     * Обрабатывает входные данные, если указано только название
     * @param name имя остановки
     * @return строка с названиями направлений
     */
    private MessageResponse processNonDefinedDirection(String name, User user) {
        var reply = "*" + name + "\n\nУкажите направление из возможных:*";
        try {
            Document doc = Jsoup.connect(busStops.getReferenceByName(name)).get();
            var directions = busStops.getDirections(name, doc);
            if (directions.size() == 0)
                return new SimpleMessageResponse(user.getChatId(), "*Нет транспорта в ближайшее время.*");
            return new ButtonsMessageResponse(user.getChatId(), reply, directions, name);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return new SimpleMessageResponse(user.getChatId(), "Ошибка при подключении к данным");
    }

    /**
     * Заменяет наиболее часто встречающиеся сокращения на правильные
     */
    private String normalizeWord(String word){
        if (word.contains("площадь"))
            word = word.replace("площадь", "пл.");
        if (word.contains("(Трамвай)"))
            word = word.replace("(Трамвай)", "");
        if (word.contains("России"))
            word = word.replace("России", "РФ");
        if (word.contains("пр."))
            word = word.replace("пр.", "проспект");
        return word;
    }

    /**
     * Контроль за тем, что в названии нет недопустимых символов
     * @return true - слово содержит запрещённые символы, false - в противном случае
     */
    private boolean isWordContainsIncorrectSymbols(String word) {
        var allowedSymbols = List.of('(', ')', ' ', '.', '-');
        for(int i = 0; i < word.length(); i++) {
            char currentChar = word.charAt(i);
            if(!Character.UnicodeBlock.of(currentChar).equals(Character.UnicodeBlock.CYRILLIC)
                    && !allowedSymbols.contains(currentChar)
                    && !Character.isDigit(currentChar))
                return true;
        }
        return false;
    }
}