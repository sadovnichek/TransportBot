package handlers;

import models.Handler;
import models.User;
import wrappers.ResponseMessage;
import wrappers.SimpleMessageResponse;
import wrappers.WrappedUpdate;

import java.util.List;

public class StartHandler implements Handler {

    public String getHandledCommand(){
        return "/start";
    }

    public List<ResponseMessage> handleMessage(User user, WrappedUpdate message) {
        String startText = "*Привет! Я - Transport Bot *";
        SimpleMessageResponse startMessage = new SimpleMessageResponse(user.getChatId(), startText);
        startMessage.enableMarkdown();
        user.setLastQueryTime();
        return List.of(startMessage);
    }
}
