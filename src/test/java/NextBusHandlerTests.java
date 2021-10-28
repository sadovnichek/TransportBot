import handlers.NextBusHandler;
import models.BusStops;
import models.User;
import org.junit.Before;
import org.junit.Test;
import wrappers.MessageData;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;

public class NextBusHandlerTests {
    private User user;
    private NextBusHandler nextBusHandler;
    private MessageData update;
    private BusStops busStops;

    @Before
    public void setUp() {
        user = new User(321);
        nextBusHandler = new NextBusHandler();
        update = mock(MessageData.class);
        busStops = mock(BusStops.class);
    }

    @Test
    public void nextBusHandler_ShouldChangeUserLastQueryTime() {
        long preQueryTime = user.getLastQueryTime();
        nextBusHandler.handleMessage(user, update);
        assertNotSame(preQueryTime, user.getLastQueryTime());
    }

    @Test
    public void nextBusHandler_ShouldResponse() {
        var responses = nextBusHandler.handleMessage(user, update);
        assertSame(1, responses.size());
    }

    @Test
    public void nextBusHandler_UpdateBusStops() {
        var responses = nextBusHandler.handleMessage(user, update);
        System.out.println(busStops.getReferenceByName("Профессорская"));
    }
}