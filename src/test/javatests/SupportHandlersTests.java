package javatests;

import handlers.HelpHandler;
import handlers.StartHandler;
import models.User;
import org.junit.Before;
import org.junit.Test;
import wrappers.MessageData;
import wrappers.SimpleMessageResponse;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;

public class SupportHandlersTests {
    private User user;
    private StartHandler startHandler;
    private HelpHandler helpHandler;
    private MessageData update;

    @Before
    public void setUp() {
        user = new User(123);
        startHandler = new StartHandler();
        helpHandler = new HelpHandler();
        update = mock(MessageData.class);
    }

    @Test
    public void startHandler_ShouldChangeUserLastQueryTime() {
        long preQueryTime = user.getLastQueryTime();
        startHandler.handleMessage(user, update);
        assertNotSame(preQueryTime, user.getLastQueryTime());
    }

    @Test
    public void startHandler_ShouldResponse() {
        var responses = startHandler.handleMessage(user, update);
        assertSame(1, responses.size());
    }

    @Test
    public void startHandler_CheckMessage() {
        var responses = startHandler.handleMessage(user, update);
        var actual = responses.get(0).getMessage();
        var expected = "*Привет! Я - Transport Bot\n" +
                "Я подскажу тебе через сколько минут приедет твой автобус\n" +
                "Помощь тут - /help*";
        assertSame(expected, actual);
    }

    @Test
    public void helpHandler_ShouldChangeUserLastQueryTime() {
        long preQueryTime = user.getLastQueryTime();
        helpHandler.handleMessage(user, update);
        assertNotSame(preQueryTime, user.getLastQueryTime());
    }

    @Test
    public void helpHandler_ShouldResponse() {
        var responses = helpHandler.handleMessage(user, update);
        assertSame(1, responses.size());
    }

    @Test
    public void helpHandler_CheckMessage() {
        var responses = helpHandler.handleMessage(user, update);
        var actual = responses.get(0).getMessage();
        assertTrue(actual.startsWith("*Я здесь, чтобы помочь тебе."));
    }
}