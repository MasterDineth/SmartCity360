package com.urbantech.smartcity360.partE_interpreter;

/**
 * Abstract Expression Interface in the GoF Interpreter Pattern.
 * Serves as the base node for the Abstract Syntax Tree (AST) representing
 * parsed municipal policy rules.
 */
public interface Expression {

    /**
     * Interprets the expression against the provided sensor and event context.
     *
     * @param context Sensor readings and execution trace container
     * @return True if the policy rule/condition evaluates to true (e.g. violation), false otherwise
     */
    boolean interpret(PolicyContext context);

    /**
     * Returns a human-readable hierarchical representation of this AST node and its sub-trees.
     *
     * @param indent Current indentation level for tree formatting
     * @return Formatted multi-line AST string
     */
    String toASTString(int indent);
}
