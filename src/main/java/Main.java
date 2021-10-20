import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Main {
    public static void main(String[] argv) {
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            botsApi.registerBot(new Bot("2006409243:AAHIVdBxD5t_s57nvC7tyCVPTMTjjXmNcJE"));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
