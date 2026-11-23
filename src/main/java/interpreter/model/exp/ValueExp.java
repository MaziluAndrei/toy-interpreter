package interpreter.model.exp;

import interpreter.model.adt.IDictionary;
import interpreter.model.value.IValue;
import interpreter.model.adt.IHeap;

public record ValueExp(IValue value) implements IExp {
    @Override
    public IValue eval(IDictionary<String, IValue> symTable, IHeap heap) {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}