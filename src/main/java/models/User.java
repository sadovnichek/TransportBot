package models;

import java.util.Date;

/**
 * Класс пользователя
 */
public class User {
    private final long chatId;
    private long lastQueryTime;

    public User(long chatId) {
        this.chatId = chatId;
        setLastQueryTime();
    }

    public long getChatId() {
        return chatId;
    }

    public void setLastQueryTime() {
        this.lastQueryTime = new Date().getTime();
    }

    public long getLastQueryTime() {
        return this.lastQueryTime;
    }
}