import junit.framework.Assert;
import models.BusStop;
import models.BusStopsRepository;
import models.Location;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class LocationTests {
    private BusStopsRepository busStops;

    @Before
    public void setUp() throws IOException {
        File input = new File("src/test/resources/bus_stops.html");
        Document doc = Jsoup.parse(input, "UTF-8", "https://www.bustime.ru/ekaterinburg/stop/");
        busStops = new BusStopsRepository(doc);
    }

    /**
     * Возвращает пустой список, если пользователь не на остановке
     */
    @Test
    public void shouldRecognizeBusStopIfUserDoNotNearWithIt() {
        Location location = new Location(56.830879, 60.621304);
        List<BusStop> nearest = busStops.getNearestBusStop(location);
        Assert.assertEquals(0, nearest.size());
    }
}
