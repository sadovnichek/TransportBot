package models;

import java.util.*;

import static java.lang.Math.min;

public class Spellchecker {
    private final Set<String> names;

    public Spellchecker(BusStops busStops) {
        this.names = busStops.getAllNames();
    }

    public List<String> tryGetCorrectName(String word) {
        List<String> result = new ArrayList<>();
        for(String name : names) {
            if (name.contains(word))
                result.add(name);
            else {
                word = replaceFirstLetter(word);
                if (name.contains(word))
                    result.add(name);
            }
        }
        return result;
    }

    private int LevenshteinDistance(String first, String second)
    {
        var opt = new int[first.length() + 1][second.length() + 1];
        for (var i = 0; i <= first.length(); i++)
            opt[i][0] = i;
        for (var i = 0; i <= second.length(); i++) opt[0][i] = i;
        for (var i = 1; i <= first.length(); i++)
            for (var j = 1; j <= second.length(); j++)
            {
                if (first.charAt(i - 1) == second.charAt(j - 1))
                    opt[i][j] = opt[i - 1][j - 1];
                else
                    opt[i][j] = min(1 + opt[i - 1][j], min(1 + opt[i - 1][j - 1], 1 + opt[i][j - 1]));
            }
        return opt[first.length()][second.length()];
    }

    public List<String> sortByEditorDistance(String word)
    {
        Map<Integer, String> distances = new TreeMap<>();
        List<String> result = new ArrayList<>();
        for(String name: names) {
            var distance = LevenshteinDistance(word, name);
            if (distance > 5)
                continue;
            else
                distances.put(distance, name);
        }
        var count = min(5, distances.size());
        for (int i = 0; i < count; i++) {
            var key = distances.keySet().toArray()[i];
            result.add(distances.get(key));
        }
        return result;
    }

    private String replaceFirstLetter(String word) {
        if(Character.isLetter(word.charAt(0))) {
            if(Character.isLowerCase(word.charAt(0))) {
                var charToChange = Character.toUpperCase(word.charAt(0));
                word = charToChange + word.substring(1);
            }
            else if(Character.isUpperCase(word.charAt(0))) {
                var charToChange = Character.toLowerCase(word.charAt(0));
                word = charToChange + word.substring(1);
            }
            if(names.contains(word))
                return word;
        }
        return word;
    }

    public String editWord(String word){
        if (word.contains("площадь"))
            word = word.replace("площадь", "пл.");
        if (word.contains("Площадь"))
            word = word.replace("Площадь", "пл.");
        if (word.contains("(Трамвай)"))
            word = word.replace("(Трамвай)", "");
        return word.trim();
    }
}
