package models;

import java.util.HashSet;
import java.util.Set;

public class Spellchecker {
    private Set<String> names;

    public Spellchecker(BusStops busStops) {
        this.names = busStops.getAllNames();
    }

    public Set<String> tryHelpName(String word) {
        Set<String> result = new HashSet<>();
        for(String name : names) {
            if (name.contains(word))
                result.add(name);
        }
        return result;
    }

    public String editWord(String word){
        if (word.contains("площадь"))
            word = word.replace("площадь", "пл.");
        if (word.contains("(Трамвай)"))
            word = word.replace("(Трамвай)", "");
        return word.trim();
    }
}
