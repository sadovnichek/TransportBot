package models;

import wrappers.ResponseMessage;
import wrappers.WrappedUpdate;

import java.util.List;

public interface Handler {
    String getHandledCommand();

    List<ResponseMessage> handleMessage(User user, WrappedUpdate message);
}
