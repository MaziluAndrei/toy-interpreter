package interpreter.model.exp;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.value.IValue;
import interpreter.model.adt.IHeap;

public record VarExp(String id) implements IExp {
    @Override
    public IValue eval(IDictionary<String, IValue> symTable, IHeap heap) throws MyException {
        if (!symTable.isDefined(id)) {
            throw new ExpressionException("variable " + id + " is not defined");
        }
        return symTable.lookup(id);
    }

    @Override
    public String toString() {
        return id;
    }
}