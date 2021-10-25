package models;

import wrappers.ResponseMessage;
import wrappers.MessageData;

import java.util.List;

public interface Handler {
    String getHandledCommand();

    List<ResponseMessage> handleMessage(User user, MessageData message);
}
