import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Main {
    public static void main(String[] argv) {
        ApiContextInitializer.init();
        String token = System.getenv("BOT_TOKEN");
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            botsApi.registerBot(new Bot(token));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
