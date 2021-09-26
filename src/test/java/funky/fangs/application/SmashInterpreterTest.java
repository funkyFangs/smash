package funky.fangs.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.io.PrintStream;
import java.text.ParseException;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SmashInterpreterTest {

    @Mock
    private InputStream input;

    @Mock
    private PrintStream output;

    @Test
    void testInterpret() throws ParseException {
        SmashInterpreter interpreter = new SmashInterpreter("dddddhfhfh",
                                                            4,
                                                            input,
                                                            output);

        interpreter.interpret();

        verify(output, times(3)).print(anyString());
        assertThat(interpreter.getCells()).containsExactly(3, 0, 0, 0);
    }

    @Test
    void testConstructor() throws ParseException {
        String code = "kadkadsflsfkll";
        int cellSize = 8;
        SmashInterpreter interpreter = new SmashInterpreter(code, cellSize);

        assertThat(interpreter.getCode()).isEqualTo(code);
        assertThat(interpreter.getCells()).hasSize(cellSize);
        assertThat(interpreter.getLoopPoints()).containsOnly(entry(0, 13),
                                                             entry(3, 8),
                                                             entry(11, 12));
    }

}