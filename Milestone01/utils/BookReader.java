package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for high performance text files read
 *
 * @author Rui Lebre (<a href="mailto:ruilebre@ua.pt">ruilebre@ua.pt</a>)
 */
public class BookReader {

    private String path;
    private BufferedReader reader;
    private final int linesPerPage;
    private boolean hasNextPage;
    private int pageNumber;

    /**
     * Given a path, instantiates a new BookReader
     *
     * @param bookPath Path to book to be read
     * @throws BookReaderException Book Reader Exception thrown
     */
    public BookReader(String bookPath) throws BookReaderException {
        this.path = bookPath;
        try {
            FileInputStream fstream = new FileInputStream(path);
            reader = new BufferedReader(new InputStreamReader(fstream));
        } catch (FileNotFoundException ex) {
            throw new BookReaderException("File not found.");
        }

        linesPerPage = 36;
        hasNextPage = true;
        pageNumber = 0;
    }

    /**
     * Reads next page on file
     *
     * @return String containing a book page
     */
    public String readNextPage() {
        String toReturn = "";
        pageNumber++;
        for (int i = 0; i < linesPerPage; i++) {
            String aux = null;
            try {
                aux = reader.readLine();
            } catch (IOException ex) {
                Logger.getLogger(BookReader.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (aux == null) {
                hasNextPage = false;
                return toReturn + "\n" + pageNumber + "\n";
            }
            toReturn += aux + "\n";
        }
        return toReturn + "\n" + pageNumber + "\n-------------------------------------------------------";
    }

    /**
     * Checks if file has a next page
     *
     * @return True if file has next page to be read. False otherwise.
     */
    public boolean hasNextPage() {
        return hasNextPage;
    }
}
