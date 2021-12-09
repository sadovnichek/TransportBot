import junit.framework.Assert;
import models.BusStopsRepository;
import models.Corrector;
import org.junit.Before;
import org.junit.Test;

public class SpellCheckerTests {
    private Corrector corrector;
    private BusStopsRepository busStops;

    @Before
    public void setUp() {
        busStops = new BusStopsRepository();
        corrector = new Corrector(busStops.getAllNames());
    }

    /**
     * Если пользователь ввёл часть слова, предложить дополнение до полного
     */
    @Test
    public void spellchecker_ShouldSuggestContinueOfWord() {
        var word = "Ленина";
        var suggestions = corrector.getSuggestions(word);
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
        var suggestions = corrector.getSuggestions(word);
        Assert.assertTrue(suggestions.contains("Трансагентство"));
    }

    /**
     * Умеет находить нужную остановку, если все первые буква
     * каждого слова в запросе неправильного регистра
     */
    @Test
    public void spellchecker_ShouldEditFirstLetters() {
        var word = "оперный Театр";
        var suggestions = corrector.getSuggestions(word);
        Assert.assertTrue(suggestions.contains("Оперный театр"));
    }
}