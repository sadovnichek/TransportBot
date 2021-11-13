package models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс расписания по отношению к конкретной остановке
 */
public class TimeTable {
    /**
     * Здесь хранится (время): (список транспорта, прибывающего в это время)
     */
    private final Map<String, List<String>> timeTable;
    private final String busStopName;
    private final String direction;
    private final boolean isTram;

    public TimeTable(String name, String direction, String timetable) {
        this.busStopName = name;
        this.timeTable = makeTimeTableFromString(timetable);
        this.direction = direction;
        isTram = this.timeTable.get(this.timeTable.keySet().toArray()[0]).get(0).startsWith("Трамвай");
    }

    public boolean isTram()
    {
        return isTram;
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
     * Перегрузка toString() для форматированного вывода.
     */
    @Override
    public String toString() {
        String headline = "*" + busStopName + "-->" + direction;
        headline += (isTram) ? " (Трамвай)*\n" : "*\n";
        var result = new StringBuilder(headline);
        for (var time : timeTable.keySet()) {
            result.append(time).append("    ");
            int size = timeTable.get(time).size();
            for(int i = 0; i < size; i++) {
                String route = timeTable.get(time).get(i);
                String space = (i != size - 1) ? ", " : ""; // space by comma or not, if last
                result.append(route).append(space);
            }
            result.append('\n');
        }
        return result.toString();
    }
}
