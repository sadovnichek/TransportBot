package wrappers;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Класс текстового сообщения
 */
public class SimpleMessageResponse implements ResponseMessage{
    private final long chatId;
    private final String message;
    private boolean enableMarkdown;

    public SimpleMessageResponse(long chatId, String message) {
        this.chatId = chatId;
        this.message = message;
    }

    /**
     * Включает поддержку Markdown
     */
    public void enableMarkdown() {
        enableMarkdown = true;
    }

    /**
     * Отправляет сообщение, сгенерированное ботом, пользователю
     */
    @Override
    public BotApiMethod createMessage() {
        SendMessage sendMessage = new SendMessage(chatId,
                message);
        sendMessage.enableMarkdown(enableMarkdown);
        return sendMessage;
    }

    public String getMessage() {
        return this.message;
    }
}
