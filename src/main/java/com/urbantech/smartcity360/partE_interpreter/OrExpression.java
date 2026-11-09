package com.urbantech.smartcity360.partE_interpreter;

import java.util.Objects;

/**
 * NonTerminalExpression in the GoF Interpreter Pattern.
 * Evaluates the logical disjunction (OR) of two subordinate expression nodes.
 */
public class OrExpression implements Expression {

    private final Expression left;
    private final Expression right;

    public OrExpression(Expression left, Expression right) {
        this.left = Objects.requireNonNull(left, "left expression cannot be null");
        this.right = Objects.requireNonNull(right, "right expression cannot be null");
    }

    @Override
    public boolean interpret(PolicyContext context) {
        boolean leftResult = left.interpret(context);
        boolean rightResult = right.interpret(context);
        boolean finalResult = leftResult || rightResult;

        if (context != null) {
            context.addTrace(String.format("[NonTerminal: OR] (%b OR %b) -> %b",
                    leftResult, rightResult, finalResult));
        }

        return finalResult;
    }

    @Override
    public String toASTString(int indent) {
        String ind = "  ".repeat(indent);
        return ind + "[NonTerminal: OR]\n" + left.toASTString(indent + 1) + "\n" + right.toASTString(indent + 1);
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }
}
