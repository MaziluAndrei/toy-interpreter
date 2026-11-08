package interpreter.model.exp;

public enum RelationalOp {
    LT("<"), LE("<="), EQ("=="), NE("!="), GT(">"), GE(">=");
    private final String symbol;
    RelationalOp(String symbol) { this.symbol = symbol; }
    @Override public String toString() { return symbol; }
}