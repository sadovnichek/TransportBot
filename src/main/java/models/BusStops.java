package models;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
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
    private final Set<String> routes;

    /**
     * Конструктор класса. Инициализирует значение поля busStops
     */
    public BusStops(Document doc) {
        this.routes = new HashSet<>();
        Elements headLines = doc.getElementsByClass("ui fluid vertical menu")
                .select("a.item");
        for (Element headline : headLines) {
            busStops.put(headline.text(), headline.attr("href"));
        }
        File file = new File("src/main/resources/bus_routes.txt");
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String line = reader.readLine();
            while (line != null) {
                var busRoutes = line.split("[\\s]+");
                this.routes.addAll(List.of(busRoutes));
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return множество названий всех остановок
     */
    public Set<String> getAllNames() {
        return busStops.keySet();
    }

    /**
     * @param name - название остановки
     * @return ссылку на остановку на сайте bustime.ru
     * или null, если такой остановки нет
     */
    public String getReferenceByName(String name) {
        return busStops.get(name);
    }

    /**
     * Вернуть название остановки по хэш-коду строки
     * @param hashcode хэш-код строки-названия остановки
     * @return если нашлось название с таким хэш-кодом - вернет название, иначе - null
     */
    public String getNameByHashcode(long hashcode){
        for(String name : busStops.keySet()){
            if(name.hashCode() == hashcode)
                return name;
        }
        return null;
    }

    /**
     * @param name - название остановки
     * @return множество направлений, куда можно попасть из
     * остановки "name"
     */
    public List<String> getDirections(String name, Document doc) {
        Set<String> result = new HashSet<>();
        Elements headLines = doc.getElementsByClass("ui header");
        for (Element headline : headLines) {
            String text = headline.text();
            if(!text.equals("конечная")) {
                var timeTables = Stream.concat(getTimeTable(name, text, doc).stream(),
                                getTimeTable(name, text, doc).stream())
                        .collect(Collectors.toList());
                for (TimeTable t : timeTables) {
                    result.add(text);
                }
            }
        }
        return new ArrayList<>(result);
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
    public List<TimeTable> getTimeTable(String name, String direction, Document doc){
        List<TimeTable> result = new ArrayList<>();
        Elements headLines = doc.getElementsByClass("eight wide column").select("div");
        for (Element headline : headLines) {
            String timetableText = headline.text();
            if(timetableText.startsWith("табло " + direction) && timetableText.contains("время")) {
                timetableText = transformData(timetableText, direction);
                try {
                    if (timetableText.equals("")) continue;
                    TimeTable timeTable = new TimeTable(name, direction, timetableText, routes);
                    result.add(timeTable);
                } catch (NullPointerException e) {
                    continue;
                }
            }
        } return result;
    }

    /**
     * форматирует полученную строку: убирает лишние данные, заменяет сокращения
     * @param source - полученная строка
     * @param direction направление движения (следующая остановка)
     */
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