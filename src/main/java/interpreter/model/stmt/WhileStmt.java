package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.IExp;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;

public record WhileStmt(IExp cond, IStmt body) implements IStmt {
    @Override
    public PrgState execute(PrgState state) throws MyException {
        IValue val = cond.eval(state.getSymTable(), state.getHeap());
        if (!(val instanceof BoolValue boolVal)) {
            throw new StatementException("while: condition " + cond + " is not a boolean");
        }
        if (boolVal.val()) {
            state.getExeStack().push(this);
            state.getExeStack().push(body);
        }
        return state;
    }

    @Override
    public String toString() { return "(while (" + cond + ") " + body + ")"; }
}