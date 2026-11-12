package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.type.IntType;
import interpreter.model.type.StringType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StmtTest {
    @Test
    void varDeclAndAssignWorkAndStringDefaultIsEmpty() throws MyException {
        PrgState state = PrgState.of(new NopStmt());
        new VarDeclStmt("s", new StringType()).execute(state);
        assertEquals(new StringValue(""), state.getSymTable().lookup("s"));

        new VarDeclStmt("x", new IntType()).execute(state);
        new AssignStmt("x", new ValueExp(new IntValue(42))).execute(state);
        assertEquals(new IntValue(42), state.getSymTable().lookup("x"));
    }

    @Test
    void assignTypeMismatchThrows() throws MyException {
        PrgState state = PrgState.of(new NopStmt());
        new VarDeclStmt("x", new IntType()).execute(state);
        assertThrows(StatementException.class,
                () -> new AssignStmt("x", new ValueExp(new BoolValue(true))).execute(state));
    }
}