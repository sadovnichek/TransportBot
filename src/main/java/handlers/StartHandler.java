package handlers;

import models.Handler;
import models.User;
import wrappers.ResponseMessage;
import wrappers.SimpleMessageResponse;
import wrappers.WrappedUpdate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StartHandler implements Handler {
    @Override
    public List<ResponseMessage> handleMessage(User user, WrappedUpdate message) {
        String startText = "*Привет! Я Transport Bot*";
        SimpleMessageResponse startMessage = new SimpleMessageResponse(user.getChatId(), startText);
        startMessage.enableMarkdown();
        user.setLastQueryTime();
        List<ResponseMessage> responseMessages = new ArrayList<>();
        responseMessages.add(startMessage);
        return responseMessages;
    }

    @Override
    public List<ResponseMessage> handleCallbackQuery(User user, WrappedUpdate callbackQuery) {
        return Collections.emptyList();
    }

    @Override
    public List<String> handledCallBackQuery() {
        return Collections.emptyList();
    }
}
