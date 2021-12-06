package handlers;

import models.BusStop;
import models.Handler;
import models.Location;
import models.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import wrappers.Message;
import wrappers.MessageResponse;
import wrappers.SimpleMessageResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, активирующий кнопку для передачи GPS пользователя
 */
public class LocateHandler implements Handler {

    private final List<BusStop> busStopList = new ArrayList<>();

    public LocateHandler() {
        File file = new File("src/main/resources/coordinates.txt");
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String line = reader.readLine();
            while (line != null) {
                String[] args = line.split(",");
                String name = args[0];
                String direction = args[1];
                Location location = new Location(args[3], args[2]);
                BusStop busStop = new BusStop(name, direction, location);
                busStopList.add(busStop);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // выкинуть
    public BusStop getNearestBusStop(Location userLocation) {
        double min = Double.MAX_VALUE;
        BusStop nearest = new BusStop();
        for(BusStop busStop : busStopList) {
            Location busStopLocation = busStop.getLocation();
            double distance = userLocation.distanceTo(busStopLocation);
            if (distance < min) {
                min = distance;
                nearest = busStop;
            }
        }
        return nearest;
    }

    /**
     * @return обрабатываемая команда
     */
    @Override
    public String getHandledCommand() {
        return "/locate";
    }

    /**
     * Создаё клавиатуру с кнопкой и прикрепляет её к сообщению
     * @param user - пользователь
     * @param message - сообщение от пользователя
     * @return сообщение
     */
    @Override
    public List<MessageResponse> handleMessage(User user, Message message) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        var button = new KeyboardButton("Определить по GPS \uD83D\uDCCC");
        button.setRequestLocation(true);
        KeyboardRow row = new KeyboardRow();
        row.add(button);
        keyboardMarkup.setKeyboard(List.of(row));
        keyboardMarkup.setResizeKeyboard(true);

        return List.of(new SimpleMessageResponse(user.getChatId(),
                "Доступно определение по GPS", keyboardMarkup));
    }
}
