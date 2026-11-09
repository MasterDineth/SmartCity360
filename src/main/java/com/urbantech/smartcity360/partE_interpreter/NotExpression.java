package com.urbantech.smartcity360.partE_interpreter;

import java.util.Objects;

/**
 * NonTerminalExpression in the GoF Interpreter Pattern.
 * Evaluates the logical negation (NOT) of a child expression node.
 */
public class NotExpression implements Expression {

    private final Expression expression;

    public NotExpression(Expression expression) {
        this.expression = Objects.requireNonNull(expression, "expression cannot be null");
    }

    @Override
    public boolean interpret(PolicyContext context) {
        boolean childResult = expression.interpret(context);
        boolean finalResult = !childResult;

        if (context != null) {
            context.addTrace(String.format("[NonTerminal: NOT] (NOT %b) -> %b",
                    childResult, finalResult));
        }

        return finalResult;
    }

    @Override
    public String toASTString(int indent) {
        String ind = "  ".repeat(indent);
        return ind + "[NonTerminal: NOT]\n" + expression.toASTString(indent + 1);
    }

    public Expression getExpression() {
        return expression;
    }
}
