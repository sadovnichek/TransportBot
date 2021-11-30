package handlers;

import models.Handler;
import models.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import wrappers.Message;
import wrappers.MessageResponse;
import wrappers.SimpleMessageResponse;

import java.util.List;

/**
 * Класс, активирующий кнопку для передачи GPS пользователя
 */
public class LocateHandler implements Handler {

    /**
     * @return обрабатываемая команда
     */
    @Override
    public String getHandledCommand() {
        return "/locate";
    }

    /**
     * Создаёи клавиатуру с кнопкой и прикрепляет её к сообщению
     * @param user - пользователь
     * @param message - сообщение от пользователя
     * @return сообщение
     */
    @Override
    public List<MessageResponse> handleMessage(User user, Message message) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        var button = new KeyboardButton("Определить по GPS \uD83D\uDCCC");
        button.setRequestLocation(true);
        KeyboardRow row = new KeyboardRow();
        row.add(button);
        keyboardMarkup.setKeyboard(List.of(row));
        keyboardMarkup.setResizeKeyboard(true);

        return List.of(new SimpleMessageResponse(user.getChatId(),
                "Доступно определение по GPS", keyboardMarkup));
    }
}
