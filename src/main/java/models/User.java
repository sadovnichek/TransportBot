package models;

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

    public User(String chatId) {
        this.chatId = chatId;
        setLastQueryTime();
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
}