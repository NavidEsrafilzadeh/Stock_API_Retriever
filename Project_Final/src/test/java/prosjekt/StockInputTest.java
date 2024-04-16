package prosjekt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class StockInputTest {

    @Test
    void TestConstructURL() {
        StockData stockData = new StockData("AAPL");
        String expectedURL = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=AAPL&apikey=5N2XIJPUWHDMML4I";
        assertEquals(expectedURL, stockData.constructURL("AAPL"));
    }

    @Test
    void TestIsValidTicker() {
        assertEquals(true, ValidTicker.isValidTicker("AAPL")); // Tester om AAPL sin ticker er gyldig. Det skal det
                                                               // være.
        assertEquals(true, ValidTicker.isValidTicker("msft")); // Tester om msft (MSFT) ticker er gyldig. Det skal den
                                                               // være.
        assertEquals(false, ValidTicker.isValidTicker("abcdef"));
    }

    @Test
    void TestIsValidDateFormat() {
        assertEquals(true, StockData.validDateFormat("2024-02-04"));
        assertEquals(false, StockData.validDateFormat("10-04-2024"));
    }

    @Test
    void TestSuccessfulPullData() { // Tester om det klarer å pulle data i JSON-format fra API-et
        StockData stockData = new StockData("MSFT");
        assertNotNull(stockData.pullData());

    }

    @Test
    void TestGetLowPrice() {
        LocalDate date = LocalDate.now().minusDays(8);

        double expectedLowPrice = 169.65;
        StockData apple = new StockData("AAPL");

        double actualLowprice = apple.getLowPrice(date);

        assertEquals(expectedLowPrice, actualLowprice);

    }

}
