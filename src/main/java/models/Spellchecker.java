package models;

import java.util.*;

import static java.lang.Math.min;

/**
 * Класс проверки правописания
 */
public class Spellchecker {
    /**
     * Множество названий всех остановок
     */
    private final Set<String> dictionary;

    public Spellchecker(Set<String> words) {
        this.dictionary = words;
    }

    /**
     * Контроль за тем, что в названии нет недопустимых символов
     * @return true - слово содержит запрещённые символы, false - в противном случае
     */
    public boolean isWordContainsIncorrectSymbols(String word) {
        var allowedSymbols = List.of('(', ')', ' ', '.', '-');
        for(int i = 0; i < word.length(); i++) {
            char currentChar = word.charAt(i);
            if(!Character.UnicodeBlock.of(currentChar).equals(Character.UnicodeBlock.CYRILLIC)
                    && !allowedSymbols.contains(currentChar)
                    && !Character.isDigit(currentChar))
                return true;
        }
        return false;
    }

    /**
     * Если пользователь ввёл только часть названия остановки, или ввёл с
     * неправильным регистром первой буквы - этот метод предложит варианты
     * исправления
     * @param word - название остановки/направления
     * @return список с возможными исправлениями
     */
    public List<String> tryGetCorrectName(String word) {
        List<String> result = new ArrayList<>();
        var variations = generateVariations(word);
        for(String name : dictionary) {
            if (name.contains(word))
                result.add(name);
            else {
                for(var candidate : variations)
                    if (name.contains(editWord(candidate)))
                        result.add(name);
            }
        }
        return result;
    }

    /**
     * Перебирает все варианты расстановки заглавных букв на множестве слов в предложении
     * @return список вариантов
     */
    private List<String> generateVariations(String word){
        List<String> result = new ArrayList<>();
        var wordParts = word.split("[\\s]+");
        var subsetNumber = Math.pow(2.0, wordParts.length);
        for(int i = 0; i < subsetNumber; i++) {
            StringBuilder mask = new StringBuilder(Integer.toBinaryString(i));
            while(mask.length() != wordParts.length) {
                mask.insert(0, "0");
            }
            StringBuilder corrected = new StringBuilder();
            for(int j  = 0; j < mask.length(); j++) {
                var currentWord = wordParts[j];
                    if (mask.charAt(j) == '1') {
                        currentWord = replaceFirstLetter(wordParts[j]);
                    }
                    corrected.append(currentWord).append(" ");
            }
            corrected = new StringBuilder(corrected.toString().trim());
            result.add(corrected.toString());
        }
        return result;
    }

    /**
     * Вычисление расстояния Левенштейна (редакторской правки)
     * @param first - исходное слово
     * @param second - слово, которое мы хотим получить
     * @return - число - искомая метрика
     */
    private int LevenshteinDistance(String first, String second) {
        var opt = new int[first.length() + 1][second.length() + 1];
        for (var i = 0; i <= first.length(); i++) opt[i][0] = i;
        for (var i = 0; i <= second.length(); i++) opt[0][i] = i;
        for (var i = 1; i <= first.length(); i++)
            for (var j = 1; j <= second.length(); j++) {
                if (first.charAt(i - 1) == second.charAt(j - 1))
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
    public List<String> sortByEditorDistance(String word) {
        Map<Integer, String> distances = new TreeMap<>();
        List<String> result = new ArrayList<>();
        for(String name: dictionary) {
            var distance = LevenshteinDistance(word, name);
            if(distance < word.length() / 2)
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
     * Меняет регистр первой буквы слова word на противоположный
     */
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
            if(dictionary.contains(word))
                return word;
        }
        return word;
    }

    /**
     * Заменяет наиболее часто встречающиеся сокращения на правильные
     */
    public String editWord(String word){
        if (word.contains("площадь"))
            word = word.replace("площадь", "пл.");
        if (word.contains("(Трамвай)"))
            word = word.replace("(Трамвай)", "");
        if (word.contains("России"))
            word = word.replace("России", "РФ");
        if (word.contains("пр."))
            word = word.replace("пр.", "проспект");
        return word.trim();
    }
}