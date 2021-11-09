import handlers.NextBusHandler;
import junit.framework.Assert;
import models.User;
import org.junit.Before;
import org.junit.Test;
import wrappers.MessageData;

import static junit.framework.Assert.assertNotSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NextBusHandlerTests {
    private User user;
    private NextBusHandler handler;
    private MessageData update;

    @Before
    public void setUp() {
        user = new User(123);
        handler = new NextBusHandler();
        update = mock(MessageData.class);
    }

    /**
     * Проверяет, что бот умеет предлагать направление на на выбор, если
     * оно не указано
     */
    @Test
    public void nextBusHandler_ShouldSuggestDirections() {
        when(update.getMessageData()).thenReturn("Музей Бажова");
        var simpleMessageResponse = handler.handleMessage(user, update).get(0);
        var reply = simpleMessageResponse.getMessage();
        Assert.assertTrue(reply.contains("Укажите направление из возможных:"));
        var lines = reply.split("[\n]+");
        Assert.assertTrue(lines.length > 1);
    }

    /**
     * Проверяет, что бот вернёт расписание, если всё правильно
     */
    @Test
    public void nextBusHandler_ShouldGetTimetable() {
        when(update.getMessageData()).thenReturn("Большакова (Белинского): Декабристов (Белинского)");
        var simpleMessageResponse = handler.handleMessage(user, update).get(0);
        var reply = simpleMessageResponse.getMessage();
        Assert.assertTrue(reply.contains("Большакова (Белинского)-->Декабристов (Белинского)"));
        var lines = reply.split("[\n]+");
        Assert.assertTrue(lines.length > 1);
    }

    /**
     * Если указанной остановки нет, бот сообщит об этом
     */
    @Test
    public void nextBusHandler_ShouldReplyIfBusStop_NotExist() {
        when(update.getMessageData()).thenReturn("Морской путь");
        var simpleMessageResponse = handler.handleMessage(user, update).get(0);
        var reply = simpleMessageResponse.getMessage();
        Assert.assertTrue(reply.contains("Такой остановки нет. Возможно, вы имели в виду:"));
    }

    /**
     * Проверяет, что принятое сообщение меняет время последнего запроса
     * Это нужно для контроля активности - нельзя отправлять боту сообщение
     * чаще, чем 1 раз в 10 с.
     */
    @Test
    public void nextBusHandler_ShouldChangeUserLastQueryTime() {
        long preQueryTime = user.getLastQueryTime();
        when(update.getMessageData()).thenReturn("/nextbus");
        handler.handleMessage(user, update);
        assertNotSame(preQueryTime, user.getLastQueryTime());
    }
}
