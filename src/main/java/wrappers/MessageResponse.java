package wrappers;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

public interface MessageResponse {
    BotApiMethod createMessage();
    String getMessageText();
}
