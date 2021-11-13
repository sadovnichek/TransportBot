import junit.framework.Assert;
import models.BusStops;
import models.Spellchecker;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class SpellCheckerTests {
    private Spellchecker spellchecker;
    private BusStops busStops;

    @Before
    public void setUp() throws IOException {
        File input = new File("src/test/resources/bus_stops.html");
        Document doc = Jsoup.parse(input, "UTF-8", "https://www.bustime.ru/ekaterinburg/stop/");
        busStops = new BusStops(doc);
        spellchecker = new Spellchecker(busStops.getAllNames());
    }

    /**
     * Если пользователь ввёл часть слова, предложить дополнение до полного
     */
    @Test
    public void spellchecker_ShouldSuggestContinueOfWord() {
        var word = "Ленина";
        var suggestions = spellchecker.tryGetCorrectName(word);
        Assert.assertEquals(2, suggestions.size());
        Assert.assertTrue(suggestions.contains("Проспект Ленина (Карла Либкнехта)"));
        Assert.assertTrue(suggestions.contains("Уральских Рабочих (Ленина)"));
    }

    /**
     * Умеет находить слова, похожие на введённое,
     * например, с опечатками
     */
    @Test
    public void spellchecker_ShouldSuggestCorrections() {
        var word = "Транзагенство";
        var suggestions = spellchecker.sortByEditorDistance(word);
        Assert.assertTrue(suggestions.contains("Трансагентство"));
    }

    /**
     * Умеет находить нужную остановку, если все первые буква
     * каждого слова в запросе неправильного регистра
     */
    @Test
    public void spellchecker_ShouldEditFirstLetters() {
        var word = "оперный Театр";
        var suggestions = spellchecker.tryGetCorrectName(word);
        Assert.assertTrue(suggestions.contains("Оперный театр"));
    }
}
