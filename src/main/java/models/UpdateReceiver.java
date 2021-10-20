package models;

import handlers.StartHandler;
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
            if (wrappedUpdate.hasCommand())
                return getHandlerByQuery(wrappedUpdate.getCommand())
                        .handleMessage(user, wrappedUpdate);
            return new ArrayList<>();
        } catch (UnsupportedOperationException e) {
            return Collections.emptyList();
        }
    }

    private Handler getHandlerByQuery(String query) {
        var x = handlers.stream()
                .filter(h -> h.getHandledCommand().startsWith(query))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
        return x;
    }
}
