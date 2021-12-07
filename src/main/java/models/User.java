package models;

import wrappers.Message;

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
    private Message lastMessage;

    public User(String chatId) {
        this.chatId = chatId;
        this.lastMessage = null;
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

    public void setLastMessage(Message message) {this.lastMessage = message; }

    public boolean IsLastMessageSameTypeAsNew(Message message) {
        if(lastMessage == null) return false;
        return message.hasCallbackQuery() && lastMessage.hasCallbackQuery() ||
                message.getLocation() != null && lastMessage.getLocation() != null
                || message.getMessageData().equals(lastMessage.getMessageData());
    }
}