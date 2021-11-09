import junit.framework.Assert;
import models.BusStops;
import models.Spellchecker;
import org.junit.Before;
import org.junit.Test;

public class SpellCheckerTests {
    private Spellchecker spellchecker;
    private BusStops busStops;

    @Before
    public void setUp() {
        busStops = new BusStops();
        spellchecker = new Spellchecker(busStops.getAllNames());
    }

    /**
     * Умеет заменять первую букву на заглавную
     */
    @Test
    public void spellchecker_ShouldEditFirstLetter() {
        var word = "профессорская";
        var suggestions = spellchecker.tryGetCorrectName(word);
        Assert.assertEquals(1, suggestions.size());
        String correct_word = suggestions.get(0);
        Assert.assertEquals("Профессорская", correct_word);
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
}
