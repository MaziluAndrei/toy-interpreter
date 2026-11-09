package interpreter.repository;

import interpreter.exceptions.MyException;
import interpreter.exceptions.RepositoryException;
import interpreter.model.PrgState;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Repository implements IRepository {
    private final List<PrgState> states = new ArrayList<>();
    private final String logFilePath; // new in A3

    public Repository(PrgState initial, String logFilePath) {
        states.add(initial);
        this.logFilePath = logFilePath;
    }

    @Override public PrgState getCrtPrg() { return states.getFirst(); }

    @Override
    public void logPrgStateExec() throws MyException {
        try (var logFile = new PrintWriter(
                new BufferedWriter(new FileWriter(logFilePath, true)))) { // append
            logFile.println(getCrtPrg().toLogString());
        } catch (IOException e) {
            throw new RepositoryException("cannot write the log file " + logFilePath
                    + ": " + e.getMessage());
        }
    }
}