package com.urbantech.smartcity360.partE_interpreter;

import java.util.Locale;
import java.util.Objects;

/**
 * TerminalExpression in the GoF Interpreter Pattern.
 * Evaluates atomic comparison conditions between a live context variable
 * (retrieved from sensor attributes) and a target value using standard operators
 * (==, !=, >, <, >=, <=).
 */
public class ComparisonExpression implements Expression {

    private final String variableName;
    private final String operator;
    private final String rawTargetValue;

    public ComparisonExpression(String variableName, String operator, String rawTargetValue) {
        this.variableName = Objects.requireNonNull(variableName, "variableName cannot be null").trim();
        this.operator = Objects.requireNonNull(operator, "operator cannot be null").trim();
        this.rawTargetValue = Objects.requireNonNull(rawTargetValue, "rawTargetValue cannot be null").trim();
    }

    @Override
    public boolean interpret(PolicyContext context) {
        Object actualObj = context != null ? context.getAttribute(variableName) : null;
        String cleanTarget = stripQuotes(rawTargetValue);
        boolean result;

        if (actualObj == null) {
            result = evaluateNull(operator, cleanTarget);
        } else {
            Double actualNum = tryParseDouble(actualObj.toString());
            Double targetNum = tryParseDouble(cleanTarget);

            if (actualNum != null && targetNum != null) {
                result = compareNumeric(actualNum, operator, targetNum);
            } else {
                result = compareString(actualObj.toString().trim(), operator, cleanTarget);
            }
        }

        if (context != null) {
            context.addTrace(String.format(Locale.US,
                    "[Terminal] %s (%s) %s %s -> %b",
                    variableName, String.valueOf(actualObj), operator, rawTargetValue, result));
        }

        return result;
    }

    @Override
    public String toASTString(int indent) {
        return "  ".repeat(indent) + String.format("[Terminal] %s %s %s", variableName, operator, rawTargetValue);
    }

    private boolean evaluateNull(String op, String target) {
        boolean targetIsNull = "null".equalsIgnoreCase(target);
        if ("==".equals(op)) {
            return targetIsNull;
        } else if ("!=".equals(op)) {
            return !targetIsNull;
        }
        return false;
    }

    private boolean compareNumeric(double actual, String op, double target) {
        return switch (op) {
            case "==" -> Math.abs(actual - target) < 1e-9;
            case "!=" -> Math.abs(actual - target) >= 1e-9;
            case ">" -> actual > target;
            case "<" -> actual < target;
            case ">=" -> actual >= target;
            case "<=" -> actual <= target;
            default -> throw new UnsupportedOperationException("Unknown operator: " + op);
        };
    }

    private boolean compareString(String actual, String op, String target) {
        int cmp = actual.compareToIgnoreCase(target);
        return switch (op) {
            case "==" -> cmp == 0;
            case "!=" -> cmp != 0;
            case ">" -> cmp > 0;
            case "<" -> cmp < 0;
            case ">=" -> cmp >= 0;
            case "<=" -> cmp <= 0;
            default -> throw new UnsupportedOperationException("Unknown operator: " + op);
        };
    }

    private Double tryParseDouble(String val) {
        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String stripQuotes(String val) {
        if (val.length() >= 2 &&
                ((val.startsWith("'") && val.endsWith("'")) ||
                 (val.startsWith("\"") && val.endsWith("\"")))) {
            return val.substring(1, val.length() - 1);
        }
        return val;
    }

    public String getVariableName() {
        return variableName;
    }

    public String getOperator() {
        return operator;
    }

    public String getRawTargetValue() {
        return rawTargetValue;
    }
}
