package funky.fangs.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;

/**
 * The main executable for the {@link SmashInterpreter}. Smash is a dialect of
 * <a href="https://esolangs.org/wiki/Brainfuck">Brainfuck</a>, an esoteric
 * programming language
 *
 * @author funkyFangs
 */
@Command(name = "smash")
public class SmashApplication implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(SmashApplication.class);

    @Parameters(index = "0", description = "The source file to interpret")
    private String fileName;

    @Option(names = {"-s", "--size"}, description = "The number of cells available for runtime")
    private int cellSize = 30_000;

    @Override
    public void run() {
        try {
            new SmashInterpreter(Files.readString(Paths.get(fileName)), cellSize).interpret();
        }
        catch (IOException ioException) {
            LOGGER.error("Failed to open file: {}", fileName);
        }
        catch (ParseException parseException) {
            LOGGER.error("Unmatched '{}' found at position {}", parseException.getMessage(),
                         parseException.getErrorOffset());
        }
    }

    public static void main(String[] args) {
        System.exit(new CommandLine(new SmashApplication()).execute(args));
    }
}