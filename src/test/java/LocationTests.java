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
    public void setUp() {
        busStops = new BusStopsRepository();
    }

    /**
     * Возвращает пустой список, если пользователь не на остановке
     */
    @Test
    public void shouldRecognizeBusStopIfUserDoNotNearToIt() {
        Location location = new Location(56.830879, 60.621304);
        List<BusStop> nearest = busStops.getNearestBusStop(location);
        Assert.assertEquals(0, nearest.size());
    }

    /**
     * Возвращает ближайшую остановку список, если пользователь находится рядом с ней
     */
    @Test
    public void shouldRecognizeBusStopIfUserNearToIt() {
        Location location = new Location(56.827859, 60.616862);
        BusStop nearest = busStops.getNearestBusStop(location).get(0);
        Assert.assertEquals("Куйбышева (Белинского)", nearest.getName());
        Assert.assertEquals("Карла Маркса (Белинского)", nearest.getDirection());
    }
}
