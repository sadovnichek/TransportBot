package javatests;

import handlers.StartHandler;
import models.User;
import org.junit.Before;
import org.junit.Test;
import wrappers.MessageData;

import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertSame;
import static org.mockito.Mockito.mock;

public class StartHandlerTests {
    private User user;
    private StartHandler handler;
    private MessageData update;

    @Before
    public void setUp() {
        user = new User(123);
        handler = new StartHandler();
        update = mock(MessageData.class);
    }

    @Test
    public void handleMessage_ShouldChangeUserLastQueryTime() {
        long preQueryTime = user.getLastQueryTime();
        handler.handleMessage(user, update);
        assertNotSame(preQueryTime, user.getLastQueryTime());
    }

    @Test
    public void handleMessage_ShouldResponse() {
        var responses = handler.handleMessage(user, update);
        assertSame(1, responses.size());
    }
}