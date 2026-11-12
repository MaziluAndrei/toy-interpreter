package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.type.BoolType;
import interpreter.model.type.IntType;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;

class FileStmtTest {
    @TempDir
    Path dir;
    private PrgState state;
    private ValueExp file;

    @BeforeEach
    void setup() throws Exception {
        Path f = dir.resolve("numbers.in");
        Files.writeString(f, "7\n");
        file = new ValueExp(new StringValue(f.toString()));
        state = PrgState.of(new NopStmt());
        new VarDeclStmt("x", new IntType()).execute(state);
    }

    @Test
    void readGivesTheNextNumberThenZeroAtTheEnd() throws MyException {
        new OpenRFileStmt(file).execute(state);
        new ReadFileStmt(file, "x").execute(state);
        assertEquals(new IntValue(7), state.getSymTable().lookup("x"));
        new ReadFileStmt(file, "x").execute(state);
        assertEquals(new IntValue(0), state.getSymTable().lookup("x"));
        new CloseRFileStmt(file).execute(state);
        assertTrue(state.getFileTable().toMap().isEmpty());
    }

    @Test
    void openRFileErrors() throws MyException {
        // Non-string expression
        assertThrows(StatementException.class, () -> new OpenRFileStmt(new ValueExp(new IntValue(1))).execute(state));

        // Missing file
        ValueExp missing = new ValueExp(new StringValue(dir.resolve("missing.in").toString()));
        assertThrows(StatementException.class, () -> new OpenRFileStmt(missing).execute(state));

        new OpenRFileStmt(file).execute(state);
        assertThrows(StatementException.class, () -> new OpenRFileStmt(file).execute(state));
        new CloseRFileStmt(file).execute(state);
    }

    @Test
    void readFileErrors() throws Exception {
        // Variable not declared
        assertThrows(StatementException.class, () -> new ReadFileStmt(file, "undeclared").execute(state));

        // Variable not int
        new VarDeclStmt("flag", new BoolType()).execute(state);
        assertThrows(StatementException.class, () -> new ReadFileStmt(file, "flag").execute(state));

        // File not open
        assertThrows(StatementException.class, () -> new ReadFileStmt(file, "x").execute(state));

        // Noninteger line in file
        Path badFile = dir.resolve("bad.in");
        Files.writeString(badFile, "not_a_number\n");
        ValueExp badExp = new ValueExp(new StringValue(badFile.toString()));
        new OpenRFileStmt(badExp).execute(state);
        assertThrows(StatementException.class, () -> new ReadFileStmt(badExp, "x").execute(state));
        new CloseRFileStmt(badExp).execute(state);
    }

    @Test
    void closeRFileErrors() {
        // Close file not open
        assertThrows(StatementException.class, () -> new CloseRFileStmt(file).execute(state));

        // Nonstring expression
        assertThrows(StatementException.class, () -> new CloseRFileStmt(new ValueExp(new IntValue(1))).execute(state));
    }
}