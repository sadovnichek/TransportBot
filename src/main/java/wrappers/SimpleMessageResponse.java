package wrappers;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

/**
 * Класс текстового сообщения
 */
public class SimpleMessageResponse implements MessageResponse {
    private final String chatId;
    private final String messageText;
    private final ReplyKeyboardMarkup keyboardMarkup;

    public SimpleMessageResponse(String chatId) {
        this.chatId = chatId;
        this.messageText = "";
        this.keyboardMarkup = null;
    }

    public SimpleMessageResponse(String chatId, String message) {
        this.chatId = chatId;
        this.messageText = message;
        this.keyboardMarkup = null;
    }

    public SimpleMessageResponse(String chatId, String message, ReplyKeyboardMarkup keyboardMarkup) {
        this.chatId = chatId;
        this.messageText = message;
        this.keyboardMarkup = keyboardMarkup;
    }

    /**
     * Отправляет сообщение, сгенерированное ботом, пользователю
     */
    @Override
    public BotApiMethod createMessage() {
        SendMessage sendMessage = new SendMessage(chatId, messageText);
        sendMessage.enableMarkdown(true);
        if(keyboardMarkup != null)
            sendMessage.setReplyMarkup(keyboardMarkup);
        return sendMessage;
    }

    public String getMessageText() {
        return this.messageText;
    }

    public MessageResponse setText(String text) {
        return new SimpleMessageResponse(this.chatId, text);
    }
}
