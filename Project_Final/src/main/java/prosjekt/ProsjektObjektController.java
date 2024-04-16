package prosjekt;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.function.Function;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

public class ProsjektObjektController {
    @FXML
    private TextField ticker;

    @FXML
    private DatePicker date;

    @FXML
    private Label valueLabel;

    @FXML
    private Label searchHistoryLabel;

    @FXML
    private SplitMenuButton dataType;

    private static StockData stockData;
    private static  HashMap<String, Function<LocalDate, Double>> DatatypeHashMap = new HashMap<>();
    
    private void initiateAPIcall(String ticker) {
        if (!ticker.isEmpty()) {
            stockData = new StockData(ticker);

        DatatypeHashMap.put("open", stockData::getOpenPrice);
        DatatypeHashMap.put("high", stockData::getHighPrice);
        DatatypeHashMap.put("low", stockData::getLowPrice);
        DatatypeHashMap.put("close", stockData::getClosePrice);
        DatatypeHashMap.put("volume", stockData::getDailyVolume);
        } 
        
        else {
            throw new IllegalArgumentException("Error: Ticker-boksen kan ikke være tom!");
        }
    }

    @FXML
    private void handleButtonClick() {

        String tickerString = ticker.getText();
        LocalDate selectedDate = date.getValue();
        String selectedDataType = dataType.getText();
        LocalDate today = LocalDate.now();

        try {
            if (selectedDate != null) {
                if (selectedDate.isAfter(today)) {
                    throw new IllegalArgumentException("Error: Valgt dato kan ikke være i fremtiden!");
                } 
                else if (selectedDate.isBefore(today.minusDays(60))) {
                    throw new IllegalArgumentException("Error: Datoen kan ikke være lenger enn 60 dager tilbake!");
                }
        

                initiateAPIcall(tickerString);

                Function<LocalDate, Double> function = DatatypeHashMap.get(selectedDataType); // Henter funksjonen og tilhørende verdi tilknyttet denn spesifikke datatypen
                if(function == null) {
                    throw new IllegalArgumentException("Error: Du må velge en datatype!");
                }

                double value = 0.0;
                value = function.apply(selectedDate); // bruker selectedDate på funksjonen som henter valgt datatype fra hashmapet

                stockData.saveSearchResultsToCSV(tickerString, selectedDataType, selectedDate, value);
                valueLabel.setText(selectedDataType + " value: " + value + "$"); /// FIKS AT NÅR DET ER VOLUME KAN VI IKKE HA $-TEGN
            }
            else {
                valueLabel.setText("Error: Vennligst velg en dato");
            }
        }
        catch (IllegalArgumentException e) {
            valueLabel.setText(e.getMessage());

        }
    }

    @FXML
    private void handleDataTypeSelection(ActionEvent event) { // Setter teksten i datatype boksen til den vi velger
    MenuItem menuItem = (MenuItem) event.getSource();
    dataType.setText(menuItem.getText());
    }

    @FXML
    private void getSearchHistory() { //Gir oss søkehistorikken fra search_results.csv, og printer dette ut i searchHistoryVabel
        String searchHistory = SearchHistoryManager.getSearchHistory();
        searchHistoryLabel.setText(searchHistory);
    }

    @FXML
    private void deleteSearchHistory() {
        try {
            SearchHistoryManager.deleteSearchHistory();
            searchHistoryLabel.setText("Søkehistorikken er slettet!");
        }
        catch(IOException e) {
            searchHistoryLabel.setText("Error ved sletting av fil: " + e.getMessage());
        }
    }
}
