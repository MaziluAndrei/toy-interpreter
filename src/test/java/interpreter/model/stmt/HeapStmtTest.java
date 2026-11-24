package interpreter.model.stmt;

import interpreter.exceptions.*;
import interpreter.model.PrgState;
import interpreter.model.adt.MyHeap;
import interpreter.model.exp.*;
import interpreter.model.type.*;
import interpreter.model.value.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HeapStmtTest {
    private PrgState state;

    @BeforeEach
    void setUp() throws MyException {
        state = PrgState.of(new NopStmt());
        new VarDeclStmt("v", new RefType(new IntType())).execute(state);
    }

    @Test
    void refTypeDefaultsAndEquality() throws MyException {
        assertEquals(new RefValue(0, new IntType()), state.getSymTable().lookup("v"));
        IType nested1 = new RefType(new RefType(new IntType()));
        IType nested2 = new RefType(new RefType(new IntType()));
        assertEquals(nested1, nested2);
        assertNotEquals(nested1, new RefType(new IntType()));
    }

    @Test
    void heapAllocatesSequentiallyAndThrowsOnMissingAddress() throws MyException {
        var heap = new MyHeap();
        assertEquals(1, heap.allocate(new IntValue(10)));
        assertEquals(2, heap.allocate(new IntValue(20)));
        assertEquals(3, heap.allocate(new IntValue(30)));
        assertThrows(ADTException.class, () -> heap.get(99));
        assertThrows(ADTException.class, () -> heap.update(99, new IntValue(1)));
    }

    @Test
    void newStmtAllocatesAndValidatesErrors() throws MyException {
        new NewStmt("v", new ValueExp(new IntValue(20))).execute(state);
        assertEquals(new RefValue(1, new IntType()), state.getSymTable().lookup("v"));
        assertEquals(new IntValue(20), state.getHeap().get(1));

        // Undeclared variable
        assertThrows(StatementException.class,
                () -> new NewStmt("missing", new ValueExp(new IntValue(1))).execute(state));
        // Not a reference
        new VarDeclStmt("x", new IntType()).execute(state);
        assertThrows(StatementException.class,
                () -> new NewStmt("x", new ValueExp(new IntValue(1))).execute(state));
        // Wrong value type
        assertThrows(StatementException.class,
                () -> new NewStmt("v", new ValueExp(new BoolValue(true))).execute(state));
    }

    @Test
    void readHeapReadsAndValidatesErrors() throws MyException {
        new NewStmt("v", new ValueExp(new IntValue(42))).execute(state);
        IValue read = new ReadHeapExp(new VarExp("v")).eval(state.getSymTable(), state.getHeap());
        assertEquals(new IntValue(42), read);

        // Non-reference expression
        assertThrows(ExpressionException.class,
                () -> new ReadHeapExp(new ValueExp(new IntValue(1))).eval(state.getSymTable(), state.getHeap()));
        // Address not in heap (address 0)
        new VarDeclStmt("nullRef", new RefType(new IntType())).execute(state);
        assertThrows(ExpressionException.class,
                () -> new ReadHeapExp(new VarExp("nullRef")).eval(state.getSymTable(), state.getHeap()));
    }

    @Test
    void writeHeapUpdatesAndValidatesErrors() throws MyException {
        new NewStmt("v", new ValueExp(new IntValue(20))).execute(state);
        new WriteHeapStmt("v", new ValueExp(new IntValue(99))).execute(state);
        assertEquals(new IntValue(99), state.getHeap().get(1));

        // Not a reference
        new VarDeclStmt("x", new IntType()).execute(state);
        assertThrows(StatementException.class,
                () -> new WriteHeapStmt("x", new ValueExp(new IntValue(1))).execute(state));
        // Address not in heap
        new VarDeclStmt("nullRef", new RefType(new IntType())).execute(state);
        assertThrows(StatementException.class,
                () -> new WriteHeapStmt("nullRef", new ValueExp(new IntValue(1))).execute(state));
        // Wrong value type
        assertThrows(StatementException.class,
                () -> new WriteHeapStmt("v", new ValueExp(new BoolValue(false))).execute(state));
    }

    @Test
    void whileStmtZeroOneAndNonBoolean() throws MyException {
        // Non-boolean condition throws
        var badLoop = new WhileStmt(new ValueExp(new IntValue(1)), new NopStmt());
        assertThrows(StatementException.class, () -> badLoop.execute(state));

        // Condition false -> pushes nothing (0 iterations)
        PrgState s0 = PrgState.of(new NopStmt());
        s0.getExeStack().pop();
        new WhileStmt(new ValueExp(new BoolValue(false)), new PrintStmt(new ValueExp(new IntValue(1)))).execute(s0);
        assertTrue(s0.getExeStack().isEmpty());

        // Condition true -> pushes while then body (body on top)
        PrgState s1 = PrgState.of(new NopStmt());
        s1.getExeStack().pop();
        IStmt body = new PrintStmt(new ValueExp(new IntValue(1)));
        IStmt loop = new WhileStmt(new ValueExp(new BoolValue(true)), body);
        loop.execute(s1);
        assertEquals(body, s1.getExeStack().pop());
        assertEquals(loop, s1.getExeStack().pop());
    }
}