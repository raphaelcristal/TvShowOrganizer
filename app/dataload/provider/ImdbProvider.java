package dataload.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImdbProvider {

    private final Path releaseDates;

    /**
     * Files can be found here: http://www.imdb.com/interfaces
     * Please read the section about licensing, since you may only use these
     * file for personal use.
     *
     * @param releaseDates path to the file "release-dates.list"
     */
    public ImdbProvider(Path releaseDates) {
        this.releaseDates = releaseDates;
    }

    /**
     * Creates a file read with the file provided in the constructor
     *
     * @return BufferedRead
     * @throws IOException
     */
    public BufferedReader getFileReader() throws IOException {
        return Files.newBufferedReader(releaseDates, Charset.forName("latin1"));
    }
}
