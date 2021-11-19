package wrappers;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс текстового сообщения
 */
public class SimpleMessageResponse implements ResponseMessage{
    private final long chatId;
    private final String message;
    private boolean enableMarkdown;
    private final InlineKeyboardMarkup inlineKeyboardMarkup;
    private final InlineKeyboardButton inlineKeyboardButton1;

    public SimpleMessageResponse(long chatId, String message) {
        this.chatId = chatId;
        this.message = message;
        this.inlineKeyboardMarkup = new InlineKeyboardMarkup();
        this.inlineKeyboardButton1 = new InlineKeyboardButton();
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
        SendMessage sendMessage = new SendMessage(chatId, message);
        sendMessage.enableMarkdown(enableMarkdown).setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    public String getMessage() {
        return this.message;
    }
}
