import handlers.HelpHandler;
import handlers.NextBusHandler;
import handlers.StartHandler;
import models.Handler;
import models.UpdateReceiver;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import wrappers.ResponseMessage;
import wrappers.MessageData;

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
        Document doc = readDocument("https://www.bustime.ru/ekaterinburg/stop/");
        List<Handler> handlers = List.of(
                new StartHandler(),
                new NextBusHandler(doc),
                new HelpHandler()
        );
        updateReceiver = new UpdateReceiver(handlers);
    }

    private Document readDocument(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Отправляет сообщения пользователю
     * @param messages - список сообщений, сгенерированных ботом
     */
    private synchronized void sendMessages(List<ResponseMessage> messages) {
        for (ResponseMessage message : messages) {
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
        MessageData wrappedUpdate = new MessageData(update);
        List<ResponseMessage> responseMessages = updateReceiver.handle(wrappedUpdate);
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