import handlers.NextBusHandler;
import junit.framework.Assert;
import models.BusStopsRepository;
import models.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import wrappers.MessageUpdate;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NextBusHandlerTests {
    private User user;
    private NextBusHandler handler;
    private MessageUpdate messageUpdate;
    private BusStopsRepository busStops;

    @Before
    public void setUp() {
        user = new User("123");
        messageUpdate = mock(MessageUpdate.class);
        busStops = new BusStopsRepository();
        handler = new NextBusHandler(busStops);
    }

    /**
     * Проверяет, что бот умеет предлагать направление на выбор, если
     * оно не указано
     */
    @Test
    public void nextBusHandler_ShouldSuggestDirections() throws IOException {
        File input = new File("src/test/resources/bazhova.html");
        Document doc = Jsoup.parse(input, "UTF-8");
        var suggestions = busStops.getDirections(doc);
        assertEquals(3, suggestions.size());
        Assert.assertTrue(suggestions.contains("Трамвайный парк (Чапаева)"));
        Assert.assertTrue(suggestions.contains("Аэроагентство"));
        Assert.assertTrue(suggestions.contains("Белинского (Декабристов)"));
    }

    /**
     * Проверяет, что бот вернёт расписание, если всё правильно
     */
    @Test
    public void nextBusHandler_ShouldGetTimetable() throws IOException {
        File input = new File("src/test/resources/bazhova.html");
        Document doc = Jsoup.parse(input, "UTF-8");
        var suggestions = busStops.getTimetable("Музей Бажова", "Трамвайный парк (Чапаева)", doc);
        assertEquals(1, suggestions.size());
        var timetable = suggestions.get(0).toString();
        Assert.assertTrue(timetable.contains("*Музей Бажова-->Трамвайный парк (Чапаева)*\n"));
    }

    /**
     * Если указанной остановки нет, бот сообщит об этом
     */
    @Test
    public void nextBusHandler_ShouldReplyIfBusStop_NotExist() {
        when(messageUpdate.getMessageData()).thenReturn("Морской путь");
        var simpleMessageResponse = handler.handleMessage(user, messageUpdate).get(0);
        var reply = simpleMessageResponse.getMessageText();
        assertEquals(reply, "*Такой остановки нет*");
    }

    /**
     * Проверяет, что принятое сообщение меняет время последнего запроса
     * Это нужно для контроля активности - нельзя отправлять боту сообщение
     * чаще, чем 1 раз в 10 с.
     */
    @Test
    public void nextBusHandler_ShouldChangeUserLastQueryTime() {
        long preQueryTime = user.getLastQueryTime();
        when(messageUpdate.getMessageData()).thenReturn("/nextbus a");
        handler.handleMessage(user, messageUpdate);
        assertNotSame(preQueryTime, user.getLastQueryTime());
    }

    /**
     * Проверяет, что /nextbus не обрабатывает запросы, содержащие некорректные
     * символы, а выдает сообщение об ошибке
     */
    @Test
    public void nextBusHandler_ShouldIgnoreInvalidSymbols() {
        when(messageUpdate.getMessageData()).thenReturn("/nextbus @#$%^&");
        var simpleMessageResponse = handler.handleMessage(user, messageUpdate).get(0);
        var reply = simpleMessageResponse.getMessageText();
        assertEquals("*Такой остановки нет*", reply);
    }
}
