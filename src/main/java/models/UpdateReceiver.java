package models;

import wrappers.ResponseMessage;
import wrappers.MessageData;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Принимает обновление от пользователя и обрабатывает его
 */
public class UpdateReceiver {
    /**
     * Список с обработчиками команд
     */
    private final List<Handler> handlers;
    private final ConcurrentHashMap<Long, User> chatIdToUser;

    public UpdateReceiver(List<Handler> handlers) {
        this.handlers = handlers;
        chatIdToUser = new ConcurrentHashMap<>();
    }

    /**
     * Добавляет пользователя в словарь.
     * Вызывает обработчик команды и обрабатывает её
     * @param message
     * @see MessageData
     * @return список сообщений, сгенерированных ботом
     * */
    public List<ResponseMessage> handle(MessageData message) {
        long chatId = message.getChatId();

        if (!chatIdToUser.containsKey(chatId))
            chatIdToUser.put(chatId, new User(chatId));
        User user = chatIdToUser.get(chatId);

        try {
            if (message.hasCommand())
                return getHandlerByCommand(message.getCommand())
                        .handleMessage(user, message);
            else
            {
                return getHandlerByCommand("/help")
                        .handleMessage(user, message);
            }
        } catch (UnsupportedOperationException e) {
            return Collections.emptyList();
        }
    }

    /**
     * Выбирает обработчик для данной команды
     * @param command - команда, поступившая от пользователя
     * @return обработчик (handler)
     */
    private Handler getHandlerByCommand(String command) {
        return handlers.stream()
                .filter(h -> h.getHandledCommand().startsWith(command))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }
}
