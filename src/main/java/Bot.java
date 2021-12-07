import handlers.HelpHandler;
import handlers.LocateHandler;
import handlers.NextBusHandler;
import handlers.StartHandler;
import models.BusStopsRepository;
import models.Handler;
import models.UpdateReceiver;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import wrappers.MessageResponse;
import wrappers.Message;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.List;

/**
 * Класс бота
 */
public class Bot extends TelegramLongPollingBot {
    private final String token;
    private final UpdateReceiver updateReceiver;

    /**
     * В конструкторе регистрируются обработчики команд
     */
    public Bot(String token) {
        this.token = token;
        Document doc = readDocument();
        assert doc != null;
        BusStopsRepository busStops = new BusStopsRepository(doc);
        List<Handler> handlers = List.of(
                new StartHandler(),
                new NextBusHandler(busStops),
                new HelpHandler(),
                new LocateHandler()
        );
        updateReceiver = new UpdateReceiver(handlers, busStops);
    }

    private Document readDocument() {
        try {
            return Jsoup.connect("https://www.bustime.ru/ekaterinburg/stop/").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Отправляет сообщения пользователю
     * @param messages - список сообщений, сгенерированных ботом
     */
    private synchronized void sendMessages(List<MessageResponse> messages) {
        for (MessageResponse message : messages) {
            try {
                execute(message.createMessage());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Принимает сообщения от пользователя и возвращает
     * ответ, сгенерированный ботом
     */
    @Override
    public void onUpdateReceived(Update update) {
        Message message = new Message(update);

        if(update.hasCallbackQuery()){
            AnswerCallbackQuery answer = new AnswerCallbackQuery();
            answer.setCallbackQueryId(update.getCallbackQuery().getId());
            try {
                execute(answer);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        List<MessageResponse> responseMessages = updateReceiver.handle(message);
        sendMessages(responseMessages);
    }

    @Override
    public String getBotUsername() {
        return "EkatTransportBot";
    }

    @Override
    public String getBotToken() {
        return token;
    }
}