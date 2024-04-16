package prosjekt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class SearchHistoryManager {

    public static String getSearchHistory() { //Gir oss søkehistorikken fra search_results.csv, og printer dette ut i searchHistoryVabel
        try {
            File file = new File("search_results.csv");
            Scanner scanner = new Scanner(file); // scanner filen

            StringBuilder searchHistory = new StringBuilder();
            while(scanner.hasNextLine()) {
                searchHistory.append(scanner.nextLine()).append("\n");
            }

            scanner.close();
            return searchHistory.toString();

        }
        catch(FileNotFoundException e) {
            return "Du har ingen søkehistorikk enda!";
        }
    }

    public static void deleteSearchHistory() throws IOException { //kan  bare returnere en tom fil med samme navn. Vi overwriter den gamle!
        try {
            String relativeFilePath = "./search_results.csv";
            File file = new File(relativeFilePath);
           
            FileWriter writer = new FileWriter(file);
            writer.write(""); // nye filen blir bare tom som vi vil slette alt innhold i den
            writer.close();
        }

        catch(FileNotFoundException e) {
            throw new FileNotFoundException("Du har ingen søkehistorikk enda!");
        }
        catch (IOException e) {
            throw new IOException("Feil ved tømming av filen din fitte: " + e.getMessage());
        }
    }
}
