package models;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.Reader;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Хранилище всех остановок с методами доступа
 */
public class BusStopsRepository {

    /**
     * Список остановок
     */
    private final List<BusStop> busStops = new ArrayList<>();
    /**
     * Отображение имя остановки -> ссылка на bustime.ru
     */
    private final Map<String, String> busStopReferences = new HashMap<>();
    /**
     * Множество всех автобусных маршрутов
     */
    private final Set<String> routes;

    public BusStopsRepository(Document doc) {
        Elements headLines = doc.getElementsByClass("ui fluid vertical menu")
                .select("a.item");
        for (Element headline : headLines) {
            busStopReferences.put(headline.text(), headline.attr("href"));
        }
        this.routes = new HashSet<>(Reader.readLines("src/main/resources/bus_routes.txt"));
        List<String> lines = Reader.readLines("src/main/resources/coordinates.txt");
        for(String line : lines) {
            String[] args = line.split(",");
            String name = args[0].trim();
            String direction = args[1].trim();
            Location location = new Location(args[3], args[2]);
            BusStop busStop = new BusStop(name, direction, location);
            busStops.add(busStop);
        }
    }

    /**
     * @return множество имен всех остановок
     */
    public Set<String> getAllNames() {
        return busStopReferences.keySet();
    }

    /**
     * @param name имя остановки
     * @return ссылка на остановку на сайте bustime.ru
     */
    public String getReferenceByName(String name) {
        return busStopReferences.get(name);
    }

    /**
     * Находит остановку по хеш-коду её названия
     * @param hashcode хеш-код
     * @return имя остановки
     */
    public String getNameByHashcode(long hashcode) {
        for(String name : busStopReferences.keySet()) {
            if(name.hashCode() == hashcode)
                return name;
        }
        return null;
    }

    /**
     * @param name имя остановки
     * @param doc источник (страница bustime.ru), где есть расписание
     * @return список направлений
     */
    public List<String> getDirections(String name, Document doc) {
        List<String> result = new ArrayList<>();
        Elements headLines = doc.getElementsByClass("ui header");
        for (Element headline : headLines) {
            String text = headline.text();
            if(!text.equals("конечная")) {
                result.add(text);
            }
        }
        return result;
    }

    /**
     * @param name имя остановки
     * @param direction направление
     * @param doc источник (страница bustime.ru)
     * @return расписание по названию и направлению
     */
    public List<Timetable> getTimetable(String name, String direction, Document doc) {
        List<Timetable> result = new ArrayList<>();
        Elements headLines = doc.getElementsByClass("eight wide column").select("div");
        for (Element headline : headLines) {
            String timetableText = headline.text();
            if(timetableText.startsWith("табло " + direction) && timetableText.contains("время")) {
                timetableText = transformText(timetableText, direction);
                try {
                    Timetable timeTable = new Timetable(name, direction, timetableText, routes);
                    result.add(timeTable);
                } catch (NullPointerException e) {
                    continue;
                }
            }
        }
        return result;
    }

    /**
     * Находит 3 ближайших остановки относительно location
     * @param userLocation местоположение пользователя
     * @return список состоящий максимум из 3 остановок
     */
    public List<BusStop> getNearestBusStop(Location userLocation) {
        Map<Double, BusStop> nearestStops = new TreeMap<>();
        for(BusStop busStop : busStops) {
            Location busStopLocation = busStop.getLocation();
            double distance = userLocation.distanceTo(busStopLocation);
            if (distance <= 40)
                nearestStops.put(distance, busStop);
        }
        return nearestStops.values().stream().limit(3).collect(Collectors.toList());
    }

    /**
     * Убирает лишнюю информацию из строки
     */
    private String transformText(String originalText, String direction) {
        String text = originalText;
        text = text.replace("табло", "").replace("время", "")
                .replace("+", "").replace("маршруты", "")
                .replace(direction, "").trim();
        if (text.contains("ТВ"))
            text = text.replace("ТВ", " Трамвай-");
        else if (text.contains("Т"))
            text = text.replace("Т", " Троллейбус-");
        return text;
    }
}