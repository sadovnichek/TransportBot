package models;

import wrappers.MessageUpdate;

import java.util.Date;

/**
 * Класс пользователя
 */
public class User {
    /**
     * id чата с данным пользователем
     */
    private final String chatId;
    /**
     * Время последнего запроса данного пользователя
     */
    private long lastQueryTime;
    private MessageUpdate lastMessageUpdate;

    public User(String chatId) {
        this.chatId = chatId;
        this.lastMessageUpdate = null;
    }

    public String getChatId() {
        return chatId;
    }

    public void setLastQueryTime() {
        this.lastQueryTime = new Date().getTime();
    }

    public long getLastQueryTime() {
        return this.lastQueryTime;
    }

    public void setLastMessage(MessageUpdate messageUpdate) {this.lastMessageUpdate = messageUpdate; }

    public boolean IsLastMessageSameTypeAsNew(MessageUpdate messageUpdate) {
        if(lastMessageUpdate == null) return false;
        return messageUpdate.hasCallbackQuery() && lastMessageUpdate.hasCallbackQuery() ||
                messageUpdate.getLocation() != null && lastMessageUpdate.getLocation() != null
                || messageUpdate.getMessageData().equals(lastMessageUpdate.getMessageData());
    }
}