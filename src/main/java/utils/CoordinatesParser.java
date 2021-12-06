package utils;

import models.BusStops;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoordinatesParser {

    private String getName(String s){
        Pattern namePattern = Pattern.compile("name:\".+?\"");
        for(String token : s.split(",[\\s]+")){
            Matcher matcher = namePattern.matcher(token);
            if(matcher.matches())
                return token.replace("name:\"", "").replace("\"", "");
        }
        return "";
    }

    private String getDirection(String s){
        Pattern namePattern = Pattern.compile("moveto:\".+?\"");
        for(String token : s.split(",[\\s]+")){
            Matcher matcher = namePattern.matcher(token);
            if(matcher.matches())
                return token.replace("moveto:\"", "").replace("\"", "");
        }
        return "";
    }

    private String getX(String s){
        Pattern namePattern = Pattern.compile("x:\\d+.\\d+");
        for(String token : s.split(",[\\s]+")){
            Matcher matcher = namePattern.matcher(token);
            if(matcher.matches())
                return token.replace("x:", "");
        }
        return "";
    }

    private String getY(String s){
        Pattern namePattern = Pattern.compile("y:\\d+.\\d+");
        for(String token : s.split(",[\\s]+")){
            Matcher matcher = namePattern.matcher(token);
            if(matcher.matches())
                return token.replace("y:", "");
        }
        return "";
    }

    public CoordinatesParser(BusStops busStops) {
        Pattern queryPattern = Pattern.compile("id:.+}");
        for(String busStop : busStops.getAllNames()){
            String reference = busStops.getReferenceByName(busStop);
            try {
                Document doc = Jsoup.connect(reference).get();
                String[] jsCode = doc.getElementsByAttributeValue("type", "text/javascript").get(1)
                        .html().split("[\\n]");
                for(String s : jsCode) {
                    Matcher matcher = queryPattern.matcher(s);
                    if(matcher.find()) {
                        if(getDirection(s).equals("конечная")) continue;
                        try(FileWriter writer = new FileWriter("coordinates.txt", true)){
                            var output = getName(s) + ", " + getDirection(s) + ", " + getX(s) + ", " + getY(s);
                            writer.append(output).append("\n");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
