package prosjekt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;


public class StockData extends ValidTicker implements StockDataInterface {
    private String BASE_URL = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=";
    private String API_KEY = "5N2XIJPUWHDMML4I";
    private String ticker;

    public StockData(String ticker) {
        if (isValidTicker(ticker)) {
            this.ticker = ticker.toUpperCase();
        } else {
            throw new IllegalArgumentException("Error: Tickeren er ikke gyldig!");
        }
    }

    public String constructURL(String ticker) {
        String URL = BASE_URL + ticker + "&apikey=" + API_KEY;
        return URL;
    }

    public static boolean validDateFormat(String date) {
        String regexDate = "^\\d{4}-\\d{2}-\\d{2}$";

        Pattern pattern = Pattern.compile(regexDate);
        Matcher matcher = pattern.matcher(date);
        return matcher.matches();
    }

    public JSONObject pullData() { // Selve API som puller data fra url og lager en JSON-objekt med all data
        String url = constructURL(ticker);

        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responsebody = response.body();
                JSONObject JSONResponse = new JSONObject(responsebody); // Parser responsen fra HTTP til som JSON
                return JSONResponse;
            } else {
                System.out.println("HTTP request feilet med følgende status-kode: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            System.out.println("du får følgende error: " + e.getMessage());
            return null;
        }
    }

    public JSONObject pullStockSpecificData(String date) {
        JSONObject JSONResponse = pullData();

        if (!validDateFormat(date)) {
            throw new IllegalArgumentException("Ikke gyldig format på dato. Følgende format gjelder: yyyy-mm-dd");
        }

        try {
            if (JSONResponse.has("Time Series (Daily)")) {
                JSONObject dailyData = JSONResponse.getJSONObject("Time Series (Daily)");
                if (dailyData.has(date)) {
                    JSONObject specificDateData = dailyData.getJSONObject(date); // data for spesifikke datoen

                    return specificDateData;

                } else {
                    throw new IllegalAccessException("Ingen handelsdag denne dagen");
                }
            } else {
                throw new IllegalAccessException(
                        "Time Series (Daily) er ikke tilgjengelig. Du har brukt opp antall API calls for dagen.");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error: " + e.getMessage());
        }
    }

    public double getDataFromJSONfile(String datatype, LocalDate date) { // henter verdien til en gitt datatype for en
                                                                         // gitt dato
        LocalDate today = LocalDate.now();
        LocalDate sixtyDaysAgo = today.minusDays(60);
        JSONObject JSONResponse = pullData();

        if (date.isAfter(today)) {
            throw new IllegalArgumentException("Datoen for å hente data kan ikke være i framtiden.");
        }

        if (date.isBefore(sixtyDaysAgo)) {
            throw new IllegalArgumentException("API-en henter bare data fra de siste 60 dagene.");
        }

        Map<String, String> dataHashMap = Map.of(
                "open", "1. open",
                "high", "2. high",
                "low", "3. low",
                "close", "4. close",
                "volume", "5. volume");

        if (JSONResponse == null) {
            throw new IllegalStateException("JSON response er null");
        }

        String key = dataHashMap.get(datatype);
        if (key == null) {
            throw new IllegalArgumentException("Ikke gyldig datatype");
        }

        JSONObject specificDateData = pullStockSpecificData(date.toString());
        String valueString = specificDateData.getString(key);

        double value = 0.0;
        try {
            value = Double.parseDouble(valueString);
            if (datatype.equals("volume")) {
                DecimalFormat df = new DecimalFormat("#,###"); /// Denne funker fortsatt ikke!!!
                valueString = df.format(value);
            }

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Verdien for den gitte datatypen er ikke en gyldig verdi.");
        }

        return value;
    }

    public void saveSearchResultsToCSV(String ticker, String dataType, LocalDate date, double value) {
        String csvFile = "search_results.csv";

        try (FileWriter writer = new FileWriter(csvFile, true)) {
            if (new File(csvFile).length() == 0) {
                writer.append("Ticker, Datatype, Value, Date\n");
            }
            writer.append(ticker.toUpperCase());
            writer.append(", ");
            writer.append(dataType);
            writer.append(", ");
            writer.append(String.valueOf(value));
            writer.append(", ");
            writer.append(date.toString());
            writer.append("\n");

            System.out.println("Søkeresultat lagret i: " + csvFile);
        } catch (IOException e) {
            System.out.println("Du fikk følgende error ved lagring av søkeresultat: " + e.getMessage());
        }
    }

    @Override
    public double getOpenPrice(LocalDate date) {
        return getDataFromJSONfile("open", date);
    }

    @Override
    public double getClosePrice(LocalDate date) {
        return getDataFromJSONfile("close", date);
    }

    @Override
    public double getHighPrice(LocalDate date) {
        return getDataFromJSONfile("high", date);
    }

    @Override
    public double getLowPrice(LocalDate date) {
        return getDataFromJSONfile("close", date);
    }

    @Override
    public double getDailyVolume(LocalDate date) {
        return getDataFromJSONfile("volume", date);
    }

    public static void main(String[] args) {
        StockData Microsoft = new StockData("MSFT");
        LocalDate MSFTdate = LocalDate.parse("2024-04-08");

        double lowMSFT = Microsoft.getLowPrice(MSFTdate);
        double highMSFT = Microsoft.getHighPrice(MSFTdate);

        System.out.println("Open price for Microsoft: " + lowMSFT);
        System.out.println("Highest price for Microsoft: " + highMSFT);
    }
}