import handlers.HelpHandler;
import handlers.NextBusHandler;
import handlers.StartHandler;
import models.Handler;
import models.UpdateReceiver;
import wrappers.ResponseMessage;
import wrappers.WrappedUpdate;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class Bot extends TelegramLongPollingBot {
    private final String token;
    private final UpdateReceiver updateReceiver;

    public Bot(String token) {
        this.token = token;
        List<Handler> handlers = List.of(
                new StartHandler(),
                new NextBusHandler(),
                new HelpHandler()
        );
        updateReceiver = new UpdateReceiver(handlers);
    }

    private synchronized void sendMessages(List<ResponseMessage> messages) {
        for (ResponseMessage message : messages) {
            try {
                execute(message.createMessage());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        WrappedUpdate wrappedUpdate = new WrappedUpdate(update);
        List<ResponseMessage> responseMessages = updateReceiver.handle(wrappedUpdate);
        sendMessages(responseMessages);
    }

    @Override
    public String getBotUsername() {
        return "Transport Bot";
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
