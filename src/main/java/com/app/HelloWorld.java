package com.app;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HelloWorld {
    private static Map<String, String> update() throws IOException {
        Map<String,String> busStopsNames = new HashMap<>();
        Document doc = Jsoup.connect("https://www.bustime.ru/ekaterinburg/stop/").get();
        Elements headLines = doc.getElementsByClass("ui fluid vertical menu")
                .select("a.item");
        for (Element headline : headLines) {
            busStopsNames.put(headline.text(), headline.attr("href"));
        }
        return busStopsNames;
    }

    public static void main(String[] argv) throws IOException {
        Map<String, String> source = update();
        System.out.println(source.get("Профессорская"));
    }
}
