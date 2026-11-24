package interpreter.controller;

import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.stmt.IStmt;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import interpreter.repository.Repository;
import interpreter.view.Examples;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {
    @TempDir
    Path dir;

    private PrgState run(IStmt program) throws MyException {
        PrgState state = PrgState.of(program);
        var repo = new Repository(state, dir.resolve("log.txt").toString());
        new Controller(repo, false).allStep();
        return state;
    }

    @Test
    void examplesOneToThreeAndFiveProduceExpectedOutput() throws MyException {
        assertEquals(new IntValue(2), run(Examples.EX1).getOut().get(0));
        assertEquals(new IntValue(18), run(Examples.EX2).getOut().get(0));
        assertEquals(new IntValue(2), run(Examples.EX3).getOut().get(0));
        assertEquals(new IntValue(5), run(Examples.EX5).getOut().get(0));
    }

    @Test
    void fileExampleReadsBothNumbersAndClosesTheFile() throws Exception {
        Path in = dir.resolve("test.in");
        Files.writeString(in, "15\n50\n");
        PrgState s = run(Examples.fileExample(in.toString()));
        assertEquals(new IntValue(15), s.getOut().get(0));
        assertEquals(new IntValue(50), s.getOut().get(1));
        assertTrue(s.getFileTable().toMap().isEmpty());
    }

    @Test
    void fileExampleWithEmptyFilePrintsZeros() throws Exception {
        Path emptyIn = dir.resolve("empty.in");
        Files.writeString(emptyIn, "");

        PrgState s = run(Examples.fileExample(emptyIn.toString()));
        assertEquals(new IntValue(0), s.getOut().get(0));
        assertEquals(new IntValue(0), s.getOut().get(1));
        assertTrue(s.getFileTable().toMap().isEmpty());
    }

    @Test
    void logFileContainsTheFourSectionsInOrder() throws Exception {
        run(Examples.EX1);
        String log = Files.readString(dir.resolve("log.txt"));
        int a = log.indexOf("ExeStack:"), b = log.indexOf("SymTable:"),
            c = log.indexOf("Out:"), d = log.indexOf("FileTable:");
        assertTrue(0 <= a && a < b && b < c && c < d);
    }

    @Test
    void ex6ThroughEx10ProduceExpectedOutputs() throws Exception {
        PrgState s6 = run(Examples.EX6);
        assertEquals(List.of(new RefValue(1, new IntType()), new RefValue(2, new RefType(new IntType()))),
                s6.getOut().toList());

        PrgState s7 = run(Examples.EX7);
        assertEquals(List.of(new IntValue(20), new IntValue(25)), s7.getOut().toList());

        PrgState s8 = run(Examples.EX8);
        assertEquals(List.of(new IntValue(20), new IntValue(35)), s8.getOut().toList());

        PrgState s9 = run(Examples.EX9);
        assertEquals(List.of(new IntValue(20)), s9.getOut().toList());

        PrgState s10 = run(Examples.EX10);
        assertEquals(List.of(new IntValue(4), new IntValue(3), new IntValue(2), new IntValue(1), new IntValue(0)),
                s10.getOut().toList());
    }
}