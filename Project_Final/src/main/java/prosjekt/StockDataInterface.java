package prosjekt;

import java.io.IOException;
import java.time.LocalDate;

public interface StockDataInterface {
    double getOpenPrice(LocalDate date);

    double getHighPrice(LocalDate date);

    double getLowPrice(LocalDate date);

    double getClosePrice(LocalDate date) throws IOException;

    double getDailyVolume(LocalDate date);
}
