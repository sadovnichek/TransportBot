package wrappers;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Класс-обёртка над сообщением
 */
public class Message {
    private long chatId;
    private String messageData;
    private String command;
    private boolean hasCommand;

    public boolean hasCommand() {
        return hasCommand;
    }

    /**
     * Сохраняет сообщение без команды
     */
    public String getMessageData() {
        return messageData;
    }

    public long getChatId() {
        return chatId;
    }

    public String getCommand() { return command; }

    /**
     * Обрабатывает сообщение, вычленяет команду для обработки
     */
    public Message(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            messageData = update.getMessage().getText();
            chatId = update.getMessage().getChatId();
            if(messageData.charAt(0) == '/') {
                hasCommand = true;
                command = messageData.split("[\\s]")[0];
                messageData = messageData.replace(command, "").trim();
            }
        }
    }
}
