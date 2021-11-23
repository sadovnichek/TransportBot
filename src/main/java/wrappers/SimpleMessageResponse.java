package wrappers;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Класс текстового сообщения
 */
public class SimpleMessageResponse implements MessageResponse {
    private final long chatId;
    private final String messageText;
    private final boolean enableMarkdown;

    public SimpleMessageResponse(long chatId) {
        this.chatId = chatId;
        this.messageText = "";
        this.enableMarkdown = true;
    }

    public SimpleMessageResponse(long chatId, String message) {
        this.chatId = chatId;
        this.messageText = message;
        this.enableMarkdown = true;
    }

    /**
     * Отправляет сообщение, сгенерированное ботом, пользователю
     */
    @Override
    public BotApiMethod createMessage() {
        SendMessage sendMessage = new SendMessage(chatId, messageText);
        sendMessage.enableMarkdown(enableMarkdown);
        return sendMessage;
    }

    public String getMessageText() {
        return this.messageText;
    }

    public MessageResponse setText(String text) {
        return new SimpleMessageResponse(this.chatId, text);
    }
}
