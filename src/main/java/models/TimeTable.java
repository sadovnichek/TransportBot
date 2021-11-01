package models;

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

    public TimeTable(String name, String direction, Map<String, List<String>> timeTable) {
        this.busStopName = name;
        this.timeTable = timeTable;
        this.direction = direction;
        isTram = timeTable.get(timeTable.keySet().toArray()[0]).get(0).startsWith("Трамвай");
    }

    public boolean isTram()
    {
        return isTram;
    }

    public String getName()
    {
        return busStopName;
    }

    /**
     * Перегрузка toString() для форматированного вывода.
     */
    @Override
    public String toString() {
        if(timeTable.size() == 0)
            return "\n";
        String headline = "*" + busStopName + "-->" + direction;
        if(isTram)
            headline += " (Трамвай)*\n";
        else
            headline += "*\n";
        var result = new StringBuilder(headline);
        for (var time : timeTable.keySet()) {
            result.append(time).append("\t");
            int size = timeTable.get(time).size();
            for(int i = 0; i < size; i++) {
                String route = timeTable.get(time).get(i);
                String space = (i != size - 1) ? ", " : "";
                result.append(route).append(space);
            }
            result.append('\n');
        }
        return result.toString();
    }
}
