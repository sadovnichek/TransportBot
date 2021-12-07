package models;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Класс расписания по отношению к конкретной остановке
 */
public class Timetable {
    /**
     * Здесь хранится (время): (список транспорта, прибывающего в это время)
     */
    private final Map<String, List<String>> timeTable;
    private final String busStopName;
    private final String direction;
    private final Set<String> routes;

    public Timetable(String name, String direction, String timetable, Set<String> routes) {
        this.busStopName = name;
        this.timeTable = makeTimeTableFromString(timetable);
        this.direction = direction;
        this.routes = routes;
    }

    /**
     * Переводит строку со значениями в словарь (время): (номер маршрута)
     * @param source - строка, найденную в getTimeTable.
     * @return словарь
     */
    private Map<String, List<String>> makeTimeTableFromString(String source) {
        Map<String, List<String>> timeTable = new LinkedHashMap<>();
        List<String> routes = new ArrayList<>();
        var tokens = source.split("\\s+");
        String currentTime = "";
        for (var token : tokens) {
            if(token.contains(":")) {
                timeTable.put(token, routes);
                currentTime = token;
                routes = new ArrayList<>();
            }
            else {
                timeTable.get(currentTime).add(token);
            }
        }
        return timeTable;
    }

    /**
     * Некоторые маршруты "склеиваются" в одну большую строку.
     * Чтобы этого избежать, разделим её на составные части - отдельные маршруты из
     * множества routes.
     * @param route - "длинная" строка
     * @return список маршрутов
     */
    private List<String> splitLongString(String route){
        int currentPos = 0;
        List<String> result = new ArrayList<>();
        while(currentPos != route.length()) {
            if(currentPos + 3 <= route.length()) {
                var subStr = route.subSequence(currentPos, currentPos + 3).toString();
                if (routes.contains(subStr)) {
                    result.add(subStr);
                    currentPos += 3;
                }
                else {
                    subStr = route.subSequence(currentPos, currentPos + 2).toString();
                    if (routes.contains(subStr)) {
                        result.add(subStr);
                        currentPos += 2;
                    }
                }
            } else {
                var subStr = route.subSequence(currentPos, currentPos + 2).toString();
                if (routes.contains(subStr)) {
                    result.add(subStr);
                    currentPos += 2;
                }
            }
        } return result;
    }

    /**
     * Так как расписание может содержать неточности,
     * выводим не точное время, а диапазон +/- 2 минуты
     * @param time строковое представление времени в виде: hh:mm
     * @return строка вида hh:mm - hh:mm
     */
    private String getDataRange(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;
        LocalTime givenTime = LocalTime.parse(time, formatter);
        LocalTime currentTime = LocalTime.now(ZoneId.of("UTC+05:00"));
        var end = givenTime.plusMinutes(2);
        var start = givenTime.minusMinutes(2);
        if(start.isBefore(currentTime))
            start = start.plusMinutes(2);
        return start + " - " + end;
    }

    /**
     * Перегрузка toString() для форматированного вывода.
     */
    @Override
    public String toString() {
        String headline = "*" + busStopName + "-->" + direction + "*\n";
        var result = new StringBuilder(headline);
        for (var time : timeTable.keySet()) {
            String timeRange = getDataRange(time);
            result.append(timeRange).append("    ");
            int size = timeTable.get(time).size();
            for(int i = 0; i < size; i++) {
                String route = timeTable.get(time).get(i);
                if(route.length() > 3 && !route.contains("Троллейбус")) {
                    var routesList = splitLongString(route);
                    for (String s : routesList)
                        result.append(s).append("; ");
                }
                else
                    result.append(route).append("; ");
            }
            result.append('\n');
        }
        return result.toString();
    }
}