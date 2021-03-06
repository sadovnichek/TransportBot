package wrappers;

import models.Location;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Класс-обёртка над сообщением от пользователя
 */
public class MessageUpdate {
    private String chatId;
    private String messageData;
    private String command;
    private Location location;
    private boolean hasCommand;
    private boolean hasCallbackQuery;

    public boolean hasCommand() {
        return hasCommand;
    }

    public boolean hasCallbackQuery() { return hasCallbackQuery; }

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

    /**
     * Сохраняет команду из сообщения, если она есть
     */
    private void recognizeCommand() {
        if(messageData.charAt(0) == '/') {
            hasCommand = true;
            command = messageData.split("[\\s]")[0];
            messageData = messageData.replace(command, "").trim();
        }
    }

    /**
     * Сохраняет нужные данные из сообщения
     */
    public MessageUpdate(Update update) {
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId().toString();
            if(update.getMessage().hasText()) {
                messageData = update.getMessage().getText();
                recognizeCommand();
            }
            if(update.getMessage().hasLocation())
                location = new Location(update.getMessage().getLocation().getLatitude(),
                        update.getMessage().getLocation().getLongitude());
        }
        else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            messageData = update.getCallbackQuery().getData();
            hasCallbackQuery = true;
            recognizeCommand();
        }
    }
}