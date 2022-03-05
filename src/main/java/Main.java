import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;

public class Main {
    public static void main(String[] argv) {
        try {
            String token = System.getenv("BOT_TOKEN");
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new TelegramBot(token));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}