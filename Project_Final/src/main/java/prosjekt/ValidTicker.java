package prosjekt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ValidTicker {
    private static String relativePathlistedstockString = "./src/main/listing_status.csv";

    public static List<String[]> readCSV(String relativePathlistedstockString) {
        List<String[]> rows = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(relativePathlistedstockString));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                rows.add(data);
            }
            // reader.close();
        } catch (IOException e) {
            System.out.println("Du får følgende error: " + e.getMessage());
        }
        return rows;
    }

    public static boolean isValidTicker(String ticker) {
        String tickerInCaps = ticker.toUpperCase();
        List<String[]> rows = readCSV(relativePathlistedstockString);
        List<String> tickers = new ArrayList<>();

        for (String[] row : rows) {
            String stockTicker = row[0];
            tickers.add(stockTicker);
            if (tickers.contains(tickerInCaps)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        String validTicker = "AAPL";
        boolean isValid = isValidTicker(validTicker);
        System.out.println("Is " + validTicker + " a valid ticker? " + isValid);
    }

}
