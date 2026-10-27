package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.type.BoolType;
import interpreter.model.type.IntType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.Test;

class StmtTest {

    private static PrgState newState(IStmt stmt) {
        return new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), stmt);
    }

    @Test
    void varDeclAndDuplicateDeclarationThrows() throws MyException {
        IStmt decl = new VarDeclStmt("v", new IntType());
        PrgState state = newState(decl);
        state.getExeStack().pop().execute(state);

        assertTrue(state.getSymTable().isDefined("v"));
        assertEquals(new IntValue(0), state.getSymTable().lookup("v"));

        assertThrows(StatementException.class, () -> decl.execute(state));
    }

    @Test
    void assignUndeclaredAndTypeMismatchThrow() throws MyException {
        IStmt assignUndeclared = new AssignStmt("x", new ValueExp(new IntValue(5)));
        PrgState state = newState(assignUndeclared);
        state.getExeStack().pop();
        assertThrows(StatementException.class, () -> assignUndeclared.execute(state));

        new VarDeclStmt("x", new IntType()).execute(state);
        IStmt badAssign = new AssignStmt("x", new ValueExp(new BoolValue(true)));
        assertThrows(StatementException.class, () -> badAssign.execute(state));
    }

    @Test
    void ifStmtBranchesAndNonBooleanConditionThrows() throws MyException {
        IStmt nonBoolIf = new IfStmt(
                new ValueExp(new IntValue(1)),
                new NopStmt(),
                new NopStmt()
        );
        PrgState state = newState(nonBoolIf);
        state.getExeStack().pop();
        assertThrows(StatementException.class, () -> nonBoolIf.execute(state));

        new VarDeclStmt("res", new IntType()).execute(state);
        IStmt validIf = new IfStmt(
                new ValueExp(new BoolValue(false)),
                new AssignStmt("res", new ValueExp(new IntValue(10))),
                new AssignStmt("res", new ValueExp(new IntValue(20)))
        );
        validIf.execute(state);
        state.getExeStack().pop().execute(state);
        assertEquals(new IntValue(20), state.getSymTable().lookup("res"));
    }

    @Test
    void printAndNopStatementsExecuteCorrectly() throws MyException {
        PrgState state = newState(new NopStmt());
        state.getExeStack().pop().execute(state);
        assertTrue(state.getExeStack().isEmpty());

        new PrintStmt(new ValueExp(new IntValue(99))).execute(state);
        assertEquals(1, state.getOut().size());
        assertEquals(new IntValue(99), state.getOut().get(0));
    }
}