package funky.fangs.application;

import lombok.Getter;

import java.io.InputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

/**
 * An interpreter for the Smash esoteric programming language.
 *
 * @author funkyFangs
 */
@Getter
class SmashInterpreter {

    private static final Pattern COMMENTS = Pattern.compile("[^asdfhjkl]++");

    private final Scanner scanner;
    private final PrintStream output;
    private final String code;
    private final byte[] cells;
    private final Map<Integer, Integer> loopPoints;

    /**
     * Builds a {@link SmashInterpreter} instance with the given code and cell size.
     * Input and output are handled by the respective standard streams.
     *
     * @param code the code to interpret
     * @param cellSize the number of cells to allocate
     * @throws ParseException if the code has syntactical errors
     * @see #SmashInterpreter(String, int, InputStream, PrintStream)
     */
    SmashInterpreter(String code, int cellSize) throws ParseException {
        this(code, cellSize, System.in, System.out);
    }

    /**
     * Builds a {@link SmashInterpreter} instance with the given code, cell size, input,
     * and output.
     *
     * @param code the code to interpret
     * @param cellSize the number of cells to allocate
     * @throws ParseException if the code has syntactical errors
     */
    SmashInterpreter(String code, int cellSize, InputStream input, PrintStream output) throws ParseException {
        this.code = COMMENTS.matcher(code).replaceAll("");
        this.cells = new byte[cellSize];
        scanner = new Scanner(input);
        this.output = requireNonNull(output);

        // Builds loop points and validates loop balance
        Map<Integer, Integer> loopPoints = new HashMap<>();
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < this.code.length(); ++i) {
            switch (this.code.charAt(i)) {
                case 'k' -> stack.push(i);
                case 'l' -> {
                    // If an 'l' is found without a 'k'
                    if (stack.isEmpty()) {
                        throw new ParseException("l", i);
                    }
                    loopPoints.put(stack.pop(), i);
                }
            }
        }

        // If a 'k' is found without an 'l'
        if (!stack.isEmpty()) {
            throw new ParseException("k", stack.pop());
        }

        this.loopPoints = Map.copyOf(loopPoints);
    }

    void interpret() {
        interpret(0, 0, code.length());
    }

    private void interpret(int position, int cell, int end) {
        while (position < end) {
            switch (code.charAt(position)) {
                case 'a' -> cell = Math.floorMod(cell + 1, cells.length);
                case 's' -> cell = Math.floorMod(cell - 1, cells.length);
                case 'd' -> ++cells[cell];
                case 'f' -> --cells[cell];
                case 'h' -> output.print(Character.toString(cells[cell]));
                case 'j' -> cells[cell] = scanner.nextByte();
                case 'k' -> {
                    int loopEnd = loopPoints.get(position);
                    while (cells[cell] != 0) {
                        interpret(position + 1, cell, loopEnd);
                    }
                    position = loopEnd;
                }
            }
            ++position;
        }
    }

}