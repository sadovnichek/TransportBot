package wrappers;

import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Класс-обёртка над сообщением
 */
public class Message {
    private String chatId;
    private String messageData;
    private String command;
    private boolean hasCommand;
    private boolean hasCallbackQuery;
    private Location location;

    public boolean hasCommand() {
        return hasCommand;
    }

    /**
     * Сохраняет сообщение без команды
     */
    public String getMessageData() {
        return messageData;
    }

    public String getChatId() {
        return chatId;
    }

    public String getCommand() { return command; }

    public Location getLocation(){
        return location;
    }

    public void recognizeCommand(){
        if(messageData.charAt(0) == '/') {
            hasCommand = true;
            command = messageData.split("[\\s]")[0];
            messageData = messageData.replace(command, "").trim();
        }
    }

    /**
     * Обрабатывает сообщение, вычленяет команду для обработки
     */
    public Message(Update update) {
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId().toString();
            if(update.getMessage().hasText()) {
                messageData = update.getMessage().getText();
                recognizeCommand();
            }
            if(update.getMessage().hasLocation())
                location = update.getMessage().getLocation();
        }
        else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            messageData = update.getCallbackQuery().getData();
            hasCallbackQuery = true;
            recognizeCommand();
        }
    }
}