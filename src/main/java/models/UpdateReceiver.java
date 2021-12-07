package models;

import wrappers.MessageResponse;
import wrappers.Message;
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
    private final BusStopsRepository busStops;

    public UpdateReceiver(List<Handler> handlers, BusStopsRepository busStops) {
        this.handlers = handlers;
        this.chatIdToUser = new ConcurrentHashMap<>();
        this.lastTimeUpdateOnServer = new Date();
        this.busStops = busStops;
    }

    /**
     * Добавляет пользователя в словарь.
     * Вызывает обработчик команды и обрабатывает её
     * @param message принимаемое сообщение от пользователя
     * @see Message
     * @return список сообщений, сгенерированных ботом
     * */
    public List<MessageResponse> handle(Message message) {
        String chatId = message.getChatId();
        if (!chatIdToUser.containsKey(chatId))
            chatIdToUser.put(chatId, new User(chatId));
        User user = chatIdToUser.get(chatId);

        if(message.getLocation() != null)
            return getHandlerByCommand("/nextbus").handleMessage(user, message);
        if(Objects.equals(message.getCommand(), "/users"))
            return List.of(new SimpleMessageResponse(chatId, chatIdToUser.size() + "\nLast start: " +
                    lastTimeUpdateOnServer));

        if (message.hasCommand()) {
            try {
                return getHandlerByCommand(message.getCommand()).handleMessage(user, message);
            }
            catch (Exception e) {
                getHandlerByCommand("/help").handleMessage(user, message);
            }
        }
        return getHandlerByCommand("/help").handleMessage(user, message);
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