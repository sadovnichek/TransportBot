package models;

import java.util.*;

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
    private final Set<String> routes;

    public TimeTable(String name, String direction, String timetable, Set<String> routes) {
        this.busStopName = name;
        this.timeTable = makeTimeTableFromString(timetable);
        this.direction = direction;
        this.routes = routes;
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

    public List<String> splitLongString(String route){
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
                if(route.length() > 3 && !route.contains("Троллейбус") && !isTram) {
                    var routesList = splitLongString(route);
                    for(int j = 0; j < routesList.size(); j++) {
                        String space = (j != routesList.size() - 1) ? ", " : " ";
                        result.append(routesList.get(j)).append(space);
                    }
                }
                else {
                    String space = (i != size - 1) ? ", " : " "; // space by comma or not, if last
                    result.append(route).append(space);
                }
            }
            result.append('\n');
        }
        return result.toString();
    }
}