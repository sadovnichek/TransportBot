package wrappers;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class SimpleMessageResponse implements ResponseMessage{
    private final long chatId;
    private final String message;
    private boolean enableMarkdown;

    //region Property

    public long getChatId() {
        return chatId;
    }

    public String getMessage() {
        return message;
    }

    //endregion

    public SimpleMessageResponse(long chatId, String message) {
        this.chatId = chatId;
        this.message = message;
    }

    public void enableMarkdown() {
        enableMarkdown = true;
    }

    @Override
    public BotApiMethod createMessage() {
        SendMessage sendMessage = new SendMessage(chatId,
                message);
        sendMessage.enableMarkdown(enableMarkdown);
        return sendMessage;
    }
}
