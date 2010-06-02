package net.ushkinaz.storm8.configuration;

import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;

@Singleton
public class CodesReader {
// ------------------------------ FIELDS ------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(CodesReader.class);

// --------------------------- CONSTRUCTORS ---------------------------

    public CodesReader() {
    }

// -------------------------- OTHER METHODS --------------------------

    public void readFromFile(String fileName, Collection<String> list) {
        String newCode;
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(fileName));
            newCode = bufferedReader.readLine();
            do {
                list.add(newCode.trim().toUpperCase());
                newCode = bufferedReader.readLine();
            }
            while (newCode != null);
        } catch (FileNotFoundException e) {
            LOGGER.error("Error", e);
        } catch (IOException e) {
            LOGGER.error("Error", e);
        } finally {
            try {
                assert bufferedReader != null;
                bufferedReader.close();
            } catch (IOException e) {
                LOGGER.error("Error", e);
            }
        }
    }
}