package models;

import wrappers.MessageResponse;
import wrappers.MessageUpdate;
import wrappers.SimpleMessageResponse;

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
    /**
     * отображение: id чата -> пользователь
     */
    private final ConcurrentHashMap<String, User> chatIdToUser;
    /**
     * Время последнего запуска на сервере
     */
    private final Date lastTimeUpdateOnServer;

    public UpdateReceiver(List<Handler> handlers) {
        this.handlers = handlers;
        this.chatIdToUser = new ConcurrentHashMap<>();
        this.lastTimeUpdateOnServer = new Date();
    }

    /**
     * Добавляет пользователя в словарь.
     * Вызывает обработчик команды и обрабатывает её
     * @param messageUpdate принимаемое сообщение от пользователя
     * @see MessageUpdate
     * @return список сообщений, сгенерированных ботом
     * */
    public List<MessageResponse> handle(MessageUpdate messageUpdate) {
        String chatId = messageUpdate.getChatId();
        if (!chatIdToUser.containsKey(chatId))
            chatIdToUser.put(chatId, new User(chatId));
        User user = chatIdToUser.get(chatId);
        if(messageUpdate.getLocation() != null) // has location?
            return getHandlerByCommand("/nextbus").handleMessage(user, messageUpdate);
        if(Objects.equals(messageUpdate.getCommand(), "/users"))
            return List.of(new SimpleMessageResponse(chatId, chatIdToUser.size() + "\nLast start: " +
                    lastTimeUpdateOnServer));
        if (messageUpdate.hasCommand()) {
            try {
                return getHandlerByCommand(messageUpdate.getCommand()).handleMessage(user, messageUpdate);
            }
            catch (Exception e) {
                e.printStackTrace();
                getHandlerByCommand("/help").handleMessage(user, messageUpdate);
            }
        }
        else {
            return getHandlerByCommand("/nextbus").handleMessage(user, messageUpdate);
        }
        return getHandlerByCommand("/help").handleMessage(user, messageUpdate);
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