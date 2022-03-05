import handlers.HelpHandler;
import handlers.LocateHandler;
import handlers.NextBusHandler;
import handlers.StartHandler;
import models.BusStopsRepository;
import models.Handler;
import models.UpdateReceiver;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import wrappers.MessageResponse;
import wrappers.MessageUpdate;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

/**
 * Класс бота
 */
public class TelegramBot extends TelegramLongPollingBot {
    private final String token;
    private final UpdateReceiver updateReceiver;

    /**
     * В конструкторе регистрируются обработчики команд
     */
    public TelegramBot(String token) {
        this.token = token;
        BusStopsRepository busStops = new BusStopsRepository();
        List<Handler> handlers = List.of(
                new StartHandler(),
                new NextBusHandler(busStops),
                new HelpHandler(),
                new LocateHandler()
        );
        updateReceiver = new UpdateReceiver(handlers);
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
        MessageUpdate messageUpdate = new MessageUpdate(update);

        if(update.hasCallbackQuery()){
            AnswerCallbackQuery answer = new AnswerCallbackQuery();
            answer.setCallbackQueryId(update.getCallbackQuery().getId());
            try {
                execute(answer);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        List<MessageResponse> responseMessages = updateReceiver.handle(messageUpdate);
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