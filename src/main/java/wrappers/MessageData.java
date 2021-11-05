package wrappers;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Класс-обёртка над сообщением
 */
public class MessageData {
    private long chatId;
    private String messageData;
    private String command;
    private boolean hasCommand;

    public boolean hasCommand() {
        return hasCommand;
    }

    public String getMessageData() {
        if (hasCommand) return messageData.replace(command, "");
        return messageData;
    }

    public long getChatId() {
        return chatId;
    }

    public String getCommand() { return command; }

    /**
     * Обрабатывает сообщение, вычленяет команду для обработки
     */
    public MessageData(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            messageData = update.getMessage().getText();
            chatId = update.getMessage().getChatId();

            if(messageData.charAt(0) == '/') {
                hasCommand = true;
                command = messageData.split("[\\s]")[0];
            }
        }
    }
}
