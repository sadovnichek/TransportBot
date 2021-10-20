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
            if (wrappedUpdate.hasCommand())
                return getHandlerByCommand(wrappedUpdate.getCommand())
                        .handleMessage(user, wrappedUpdate);
            return new ArrayList<>();
        } catch (UnsupportedOperationException e) {
            return Collections.emptyList();
        }
    }

    private Handler getHandlerByCommand(String command) {
        return handlers.stream()
                .filter(h -> h.getHandledCommand().startsWith(command))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }
}
