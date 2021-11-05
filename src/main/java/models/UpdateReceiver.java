package models;

import wrappers.ResponseMessage;
import wrappers.MessageData;
import wrappers.SimpleMessageResponse;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
    private final Date lastTimeUpdateOnServer;

    public UpdateReceiver(List<Handler> handlers) {
        this.handlers = handlers;
        chatIdToUser = new ConcurrentHashMap<>();
        lastTimeUpdateOnServer = new Date();
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
            if (message.hasCommand()) {
                if(Objects.equals(message.getCommand(), "/users"))
                    return List.of(new SimpleMessageResponse(chatId, String.valueOf(chatIdToUser.size())),
                    new SimpleMessageResponse(chatId, "Last start: " + lastTimeUpdateOnServer));
                if(Objects.equals(message.getCommand(), "/nextbus") && message.getMessageData().trim().equals(""))
                    return getHandlerByCommand("/help")
                            .handleMessage(user, message);
                return getHandlerByCommand(message.getCommand())
                        .handleMessage(user, message);
            }
            else
                return getHandlerByCommand("/help")
                        .handleMessage(user, message);
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
