package test.dataload.provider;

import dataload.provider.ImdbProvider;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Paths;

import static org.fest.assertions.Assertions.assertThat;

public class Test_Provider_Imdb {

    @Test
    public void createBufferedReaderFromFile() {

        ImdbProvider imdbProvider = new ImdbProvider(Paths.get("test/test/data", "release-dates.list"));
        try (BufferedReader bufferedReader = imdbProvider.getFileReader()) {

            String firstLine = bufferedReader.readLine();
            assertThat(firstLine).startsWith("CRC: 0xE81809FB");

        } catch (IOException e) {
            //this should never be reached!
            assertThat(true).isFalse();
        }


    }

}
