package models;

import wrappers.ResponseMessage;
import wrappers.WrappedUpdate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class UpdateReceiver {
    private final List<Handler> handlers;
    private final ConcurrentHashMap<Long, User> chatIdToUser;

    public UpdateReceiver(List<Handler> handlers) {
        this.handlers = handlers;
        chatIdToUser = new ConcurrentHashMap<>();
    }

    public List<ResponseMessage> handle(WrappedUpdate wrappedUpdate) {
        long chatId = wrappedUpdate.getChatId();

        if (!chatIdToUser.containsKey(chatId))
            chatIdToUser.put(chatId, new User(chatId));
        User user = chatIdToUser.get(chatId);

        try {
            if (wrappedUpdate.hasHasCallBackQuery())
                return getHandlerByCallBackQuery(wrappedUpdate.getMessageData())
                        .handleCallbackQuery(user, wrappedUpdate);
            return new ArrayList<>();
        } catch (UnsupportedOperationException e) {
            return Collections.emptyList();
        }
    }

    private Handler getHandlerByCallBackQuery(String query) {
        return handlers.stream()
                .filter(h -> h.handledCallBackQuery().stream()
                        .anyMatch(query::startsWith))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }
}
