package src;

import java.util.ArrayList;
import java.util.List;

public class CsvUtils {
    static List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        boolean insideQuote = false;
        StringBuilder sb = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '\"') {
                insideQuote = !insideQuote;
            } else if (c == ',' && !insideQuote) {
                values.add(sb.toString());
                sb.setLength(0); // Clear StringBuilder
            } else {
                sb.append(c);
            }
        }

        if (sb.length() > 0) {
            values.add(sb.toString());
        }

        return values;
    }

    // Helper method to escape commas in CSV values
    static String escapeCsv(String input) {
        if (input.contains(",")) {
            return "\"" + input + "\"";
        }
        return input;
    }

}
