package models;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Класс - обёртка над Map<String, String>
 */
public class BusStops {
    /**
     * Здесь хранятся названия остановок в виде ключей
     * и ссылки на них же на сайте bustime.ru в виде значений
     */
    private final Map<String, String> busStops = new HashMap<>();

    /**
     * Конструктор класса. Инициализирует значение поля busStops
     */
    public BusStops() {
        try {
            Document doc = Jsoup.connect("https://www.bustime.ru/ekaterinburg/stop/").get();
            Elements headLines = doc.getElementsByClass("ui fluid vertical menu")
                    .select("a.item");
            for (Element headline : headLines) {
                busStops.put(headline.text(), headline.attr("href"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param name - название остановки
     * @return ссылку на остановку на сайте bustime.ru
     * или null, если такой остановки нет
     */
    public String getReferenceByName(String name) {
        if (!busStops.containsKey(name)) return null;
        return busStops.get(name);
    }

    /**
     * @param name - название остановки
     * @return множество направлений, куда можно попасть из
     * остановки "name"
     */
    public Set<String> getDirections(String name) {
        Set<String> result = new HashSet<>();
        try {
            Document doc = Jsoup.connect(busStops.get(name)).get();
            Elements headLines = doc.getElementsByClass("ui header");
            for (Element headline : headLines) {
                String text = headline.text();
                if(!text.equals("конечная")) {
                    var timeTables = Stream.concat(getTimeTable(name, text, false, doc).stream(),
                                    getTimeTable(name, text, true, doc).stream())
                            .collect(Collectors.toList());
                    for (TimeTable t : timeTables) {
                        result.add(text + ((t.isTram()) ? " (Трамвай)" : "") );
                    }
                }
            } return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Формирует список, т.к. может существовать две остановки
     * с одинаковым названием и направлением - обычная и трамвайная.
     * Находит нужную информацию на сайте и чистит её от мусора.
     * @param name - название остановки
     * @param direction - направление
     * @return список расписаний.
     * @see TimeTable
     */
    public List<TimeTable> getTimeTable(String name, String direction, boolean onlyTram, Document doc){
        List<TimeTable> result = new ArrayList<>();
        Elements headLines = doc.getElementsByClass("eight wide column").select("div");
        for (Element headline : headLines) {
            String timetableText = headline.text();
            if(timetableText.startsWith("табло " + direction) && timetableText.contains("время")) {
                timetableText = transformData(timetableText, direction);
                TimeTable timeTable = new TimeTable(name, direction, timetableText);
                if(onlyTram && timeTable.isTram())
                    result.add(timeTable);
                else if (!onlyTram && !timeTable.isTram())
                    result.add(timeTable);
            }
        } return result;
    }

    private String transformData(String source, String direction) {
        source = source.replace("табло", "").replace("время", "")
                .replace("+", "").replace("маршруты", "")
                .replace(direction, "").trim();
        if (source.contains("ТВ"))
            source = source.replace("ТВ", " Трамвай-");
        else if (source.contains("Т"))
            source = source.replace("Т", " Троллейбус-");
        return source;
    }
}
