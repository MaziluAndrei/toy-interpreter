package interpreter.model.exp;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyHeap;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import org.junit.jupiter.api.Test;

class ArithExpTest {
    private final IDictionary<String, IValue> symTable = new MyDictionary<>();

    @Test
    void arithmeticOperatorsAndDivisionByZero() throws MyException {
        IExp add = new ArithExp(ArithOp.ADD, new ValueExp(new IntValue(2)), new ValueExp(new IntValue(3)));
        assertEquals(new IntValue(5), add.eval(symTable, new MyHeap()));

        IExp divZero = new ArithExp(ArithOp.DIV, new ValueExp(new IntValue(10)), new ValueExp(new IntValue(0)));
        assertThrows(ExpressionException.class, () -> divZero.eval(symTable, new MyHeap()));
    }

    @Test
    void relationalOperatorsTrueAndFalse() throws MyException {
        IExp five = new ValueExp(new IntValue(5));
        IExp seven = new ValueExp(new IntValue(7));

        assertEquals(new BoolValue(true), new RelationalExp(RelationalOp.LT, five, seven).eval(symTable, new MyHeap()));
        assertEquals(new BoolValue(false), new RelationalExp(RelationalOp.LT, seven, five).eval(symTable, new MyHeap()));

        assertEquals(new BoolValue(true), new RelationalExp(RelationalOp.LE, five, five).eval(symTable, new MyHeap()));
        assertEquals(new BoolValue(false), new RelationalExp(RelationalOp.LE, seven, five).eval(symTable, new MyHeap()));

        assertEquals(new BoolValue(true), new RelationalExp(RelationalOp.EQ, five, five).eval(symTable, new MyHeap()));
        assertEquals(new BoolValue(false), new RelationalExp(RelationalOp.EQ, five, seven).eval(symTable, new MyHeap()));

        assertEquals(new BoolValue(true), new RelationalExp(RelationalOp.NE, five, seven).eval(symTable, new MyHeap()));
        assertEquals(new BoolValue(false), new RelationalExp(RelationalOp.NE, five, five).eval(symTable, new MyHeap()));

        assertEquals(new BoolValue(true), new RelationalExp(RelationalOp.GT, seven, five).eval(symTable, new MyHeap()));
        assertEquals(new BoolValue(false), new RelationalExp(RelationalOp.GT, five, seven).eval(symTable, new MyHeap()));

        assertEquals(new BoolValue(true), new RelationalExp(RelationalOp.GE, five, five).eval(symTable, new MyHeap()));
        assertEquals(new BoolValue(false), new RelationalExp(RelationalOp.GE, five, seven).eval(symTable, new MyHeap()));
    }

    @Test
    void relationalExpThrowsOnNonIntegerOperands() {
        IExp str = new ValueExp(new StringValue("abc"));
        IExp num = new ValueExp(new IntValue(5));
        assertThrows(ExpressionException.class, () -> new RelationalExp(RelationalOp.LT, str, num).eval(symTable, new MyHeap()));
        assertThrows(ExpressionException.class, () -> new RelationalExp(RelationalOp.LT, num, str).eval(symTable, new MyHeap()));
    }
}