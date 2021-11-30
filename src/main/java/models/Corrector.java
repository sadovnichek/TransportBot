package models;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.min;

/**
 * Класс, предлагающий исправления при опечатках/ошибках
 */
public class Corrector {
    /**
     * Множество названий всех остановок
     */
    private final List<String> dictionary;

    public Corrector(Set<String> dictionary) {
        this.dictionary = new ArrayList<>(dictionary);
    }

    /**
     * Если пользователь ввёл только часть названия остановки, или ввёл с
     * неправильным регистром первой буквы - этот метод предложит варианты
     * исправления
     * @param word - название остановки/направления
     * @return список с возможными исправлениями
     */
    public List<String> tryGetCompleteName(String word) {
        List<String> result = new ArrayList<>();
        for(String name : dictionary) {
            if (name.contains(word))
                result.add(name);
        }
        return result;
    }

    /**
     * Вычисление расстояния Левенштейна (редакторской правки)
     * @param first - исходное слово
     * @param second - слово, которое мы хотим получить
     * @return - число - искомая метрика
     */
    private int countEditorDistance(String first, String second) {
        var opt = new int[first.length() + 1][second.length() + 1];
        for (var i = 0; i <= first.length(); i++) opt[i][0] = i;
        for (var i = 0; i <= second.length(); i++) opt[0][i] = i;
        for (var i = 1; i <= first.length(); i++)
            for (var j = 1; j <= second.length(); j++) {
                if (Character.toUpperCase(first.charAt(i - 1)) == Character.toUpperCase(second.charAt(j - 1)))
                    opt[i][j] = opt[i - 1][j - 1];
                else
                    opt[i][j] = min(1 + opt[i - 1][j], min(1 + opt[i - 1][j - 1], 1 + opt[i][j - 1]));
            }
        return opt[first.length()][second.length()];
    }

    /**
     * Выводит слова в порядке возрастания метрики Левенштейна, максимум - 5 слов
     * @param word - название остановки/направление движения
     */
    private List<String> sortByEditorDistance(String word) {
        Map<Integer, String> distances = new TreeMap<>();
        List<String> result = new ArrayList<>();
        for(String name: dictionary) {
            var distance = countEditorDistance(word, name);
            if(distance == 0)
                return List.of(name);
            if(distance <= word.length() / 2)
                distances.put(distance, name);
        }
        var count = min(5, distances.size());
        for (int i = 0; i < count; i++) {
            var key = distances.keySet().toArray()[i];
            result.add(distances.get(key));
        }
        return result;
    }

    /**
     * Получает дополненные и исправленные слова в рекомендации
     * @param word название или направление
     * @return список предложений по исправлению
     */
    public List<String> getSuggestions(String word){
        var suggestedWords = tryGetCompleteName(word);
        var correctedWords = sortByEditorDistance(word);
        for(int i = 0; i < correctedWords.size() && suggestedWords.size() < 5; i++)
            suggestedWords.add(correctedWords.get(i));
        return suggestedWords.stream().distinct().collect(Collectors.toList());
    }
}