package wrappers;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class ButtonsMessageResponse implements MessageResponse {
    private final String chatId;
    private final String messageText;
    private final InlineKeyboardMarkup inlineKeyboardMarkup;
    private final List<String> buttonsText;
    private final String busStopName;

    public ButtonsMessageResponse(String chatId, String messageText, List<String> buttonsText) {
        this.chatId = chatId;
        this.messageText = messageText;
        this.inlineKeyboardMarkup = new InlineKeyboardMarkup();
        this.buttonsText = buttonsText;
        this.busStopName =  null;
    }

    public ButtonsMessageResponse(String chatId, String messageText, List<String> buttonsText, String name) {
        this.chatId = chatId;
        this.messageText = messageText;
        this.inlineKeyboardMarkup = new InlineKeyboardMarkup();
        this.buttonsText = buttonsText;
        this.busStopName = name;
    }

    @Override
    public BotApiMethod createMessage() {
        SendMessage sendMessage = new SendMessage(chatId, messageText);
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for(String buttonText : buttonsText) {
            String callbackData;
            if(busStopName == null) // если ошибка в имени
                callbackData = "/nextbus " + buttonText.hashCode();
            else                    // если не указано направление
                callbackData = "/nextbus " + busStopName.hashCode() + ": " + buttonText.hashCode();
            var button = new InlineKeyboardButton();
            button.setCallbackData(callbackData);
            button.setText(buttonText);
            buttons.add(List.of(button));
        }
        inlineKeyboardMarkup.setKeyboard(buttons);
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    @Override
    public String getMessageText() {
        return messageText;
    }
}