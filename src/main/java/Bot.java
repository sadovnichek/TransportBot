import handlers.HelpHandler;
import handlers.LocateHandler;
import handlers.NextBusHandler;
import handlers.StartHandler;
import models.BusStops;
import models.Handler;
import models.UpdateReceiver;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
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
        Document doc = readDocument("https://www.bustime.ru/ekaterinburg/stop/");
        BusStops busStops = new BusStops(doc);
        List<Handler> handlers = List.of(
                new StartHandler(),
                new NextBusHandler(busStops),
                new HelpHandler(),
                new LocateHandler()
        );
        this.updateReceiver = new UpdateReceiver(handlers);
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