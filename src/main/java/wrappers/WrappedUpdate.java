package wrappers;

import org.telegram.telegrambots.meta.api.objects.Update;

public class WrappedUpdate {
    private long chatId;
    private String messageData;
    private String command;
    private boolean hasCommand;

    //region Property
    public boolean hasCommand() {
        return hasCommand;
    }

    public String getMessageData() {
        return messageData;
    }

    public long getChatId() {
        return chatId;
    }

    public String getCommand() { return command; }

    //endregion

    public WrappedUpdate(Update update) {
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
