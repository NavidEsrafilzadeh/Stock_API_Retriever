package prosjekt;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;

public class FileReaderTest {
    private StockData testStockData = new StockData("MSFT");

    @Test
    public void TestSavingToCSV() throws FileNotFoundException, IOException {
        String testFile = "test_search_results.csv";

        String ticker = "MSFT";
        String datatype = "open";
        double openValue = 150.0;
        LocalDate yesterday = LocalDate.now().minusDays(1);

        testStockData.saveSearchResultsToCSV(ticker, datatype, yesterday, openValue);

        // Leser av testfilen
        try (BufferedReader reader = new BufferedReader(new FileReader(testFile))) {
            String line;
            boolean dataFound = false;

            // sjekker hvis den finner linjen med informasjon den leter etter
            while ((line = reader.readLine()) != null) {
                if (line.contains(ticker) && line.contains(datatype) && line.contains(String.valueOf(openValue))
                        && line.contains(yesterday.toString())) {
                    dataFound = true;
                    break;
                }
            }
            assertEquals(true, dataFound);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
