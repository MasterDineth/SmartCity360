package com.urbantech.smartcity360.partE_interpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Domain-Specific Language (DSL) Parser for SmartCity360 Operational Policies.
 * Uses recursive descent parsing to construct an Abstract Syntax Tree (AST) composed of
 * GoF Interpreter Pattern nodes (ComparisonExpression, AndExpression, OrExpression, NotExpression).
 *
 * Supported Grammar:
 *   Expression -> OrExpr
 *   OrExpr     -> AndExpr ( "OR" AndExpr )*
 *   AndExpr    -> NotExpr ( "AND" NotExpr )*
 *   NotExpr    -> "NOT" NotExpr | Primary
 *   Primary    -> "(" Expression ")" | Comparison
 *   Comparison -> IDENTIFIER OPERATOR VALUE
 *   OPERATOR   -> "==" | "!=" | ">=" | "<=" | ">" | "<"
 *   VALUE      -> STRING_LITERAL | NUMBER | IDENTIFIER
 */
public class PolicyDSLParser {

    private enum TokenType {
        IDENTIFIER,
        LITERAL,
        OPERATOR,
        AND,
        OR,
        NOT,
        LPAREN,
        RPAREN,
        EOF
    }

    private static class Token {
        final TokenType type;
        final String text;
        final int position;

        Token(TokenType type, String text, int position) {
            this.type = type;
            this.text = text;
            this.position = position;
        }

        @Override
        public String toString() {
            return type + "('" + text + "' at pos " + position + ")";
        }
    }

    /**
     * Parses a policy rule DSL string into an executable Expression AST.
     *
     * @param dslRule The raw rule string (e.g. "ZONE == 'RESIDENTIAL' AND DECIBELS > 55")
     * @return Root Expression node of the generated AST
     * @throws IllegalArgumentException If the DSL rule is blank or contains syntax errors
     */
    public Expression parse(String dslRule) {
        if (dslRule == null || dslRule.trim().isEmpty()) {
            throw new IllegalArgumentException("Policy DSL rule cannot be null or empty.");
        }

        List<Token> tokens = tokenize(dslRule);
        ParserState state = new ParserState(tokens, dslRule);
        Expression ast = state.parseExpression();

        if (state.peek().type != TokenType.EOF) {
            Token unexpected = state.peek();
            throw new IllegalArgumentException(String.format(
                    "Syntax Error in DSL: Unexpected token '%s' at position %d in rule: \"%s\"",
                    unexpected.text, unexpected.position, dslRule));
        }

        return ast;
    }

    private List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        int i = 0;
        int n = input.length();

        while (i < n) {
            char c = input.charAt(i);

            // 1. Skip whitespace
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            // 2. Parentheses
            if (c == '(') {
                tokens.add(new Token(TokenType.LPAREN, "(", i));
                i++;
                continue;
            }
            if (c == ')') {
                tokens.add(new Token(TokenType.RPAREN, ")", i));
                i++;
                continue;
            }

            // 3. Quoted string literals ('...' or "...")
            if (c == '\'' || c == '"') {
                char quote = c;
                int start = i;
                i++;
                StringBuilder sb = new StringBuilder();
                while (i < n && input.charAt(i) != quote) {
                    sb.append(input.charAt(i));
                    i++;
                }
                if (i >= n) {
                    throw new IllegalArgumentException(String.format(
                            "Unterminated string literal starting at position %d in rule: \"%s\"", start, input));
                }
                i++; // Skip closing quote
                tokens.add(new Token(TokenType.LITERAL, "'" + sb.toString() + "'", start));
                continue;
            }

            // 4. Comparison Operators (multi-character first)
            if (i + 1 < n) {
                String twoChar = input.substring(i, i + 2);
                if (twoChar.equals("==") || twoChar.equals("!=") || twoChar.equals(">=") || twoChar.equals("<=")) {
                    tokens.add(new Token(TokenType.OPERATOR, twoChar, i));
                    i += 2;
                    continue;
                }
            }

            if (c == '>' || c == '<') {
                tokens.add(new Token(TokenType.OPERATOR, String.valueOf(c), i));
                i++;
                continue;
            }

            // 5. Numbers (positive or negative)
            if (Character.isDigit(c) || ((c == '+' || c == '-') && i + 1 < n && Character.isDigit(input.charAt(i + 1)))) {
                int start = i;
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                i++;
                boolean hasDot = false;
                while (i < n && (Character.isDigit(input.charAt(i)) || input.charAt(i) == '.')) {
                    if (input.charAt(i) == '.') {
                        if (hasDot) break;
                        hasDot = true;
                    }
                    sb.append(input.charAt(i));
                    i++;
                }
                tokens.add(new Token(TokenType.LITERAL, sb.toString(), start));
                continue;
            }

            // 6. Keywords and Identifiers
            if (Character.isLetter(c) || c == '_') {
                int start = i;
                StringBuilder sb = new StringBuilder();
                while (i < n && (Character.isLetterOrDigit(input.charAt(i)) || input.charAt(i) == '_' || input.charAt(i) == '-')) {
                    sb.append(input.charAt(i));
                    i++;
                }
                String word = sb.toString();
                String upper = word.toUpperCase();
                if ("AND".equals(upper)) {
                    tokens.add(new Token(TokenType.AND, word, start));
                } else if ("OR".equals(upper)) {
                    tokens.add(new Token(TokenType.OR, word, start));
                } else if ("NOT".equals(upper)) {
                    tokens.add(new Token(TokenType.NOT, word, start));
                } else {
                    tokens.add(new Token(TokenType.IDENTIFIER, word, start));
                }
                continue;
            }

            throw new IllegalArgumentException(String.format(
                    "Unexpected character '%c' at position %d in rule: \"%s\"", c, i, input));
        }

        tokens.add(new Token(TokenType.EOF, "", n));
        return tokens;
    }

    private static class ParserState {
        private final List<Token> tokens;
        private final String originalRule;
        private int current = 0;

        ParserState(List<Token> tokens, String originalRule) {
            this.tokens = tokens;
            this.originalRule = originalRule;
        }

        Token peek() {
            return tokens.get(current);
        }

        boolean isAtEnd() {
            return peek().type == TokenType.EOF;
        }

        Token advance() {
            if (!isAtEnd()) current++;
            return tokens.get(current - 1);
        }

        boolean check(TokenType type) {
            if (isAtEnd()) return false;
            return peek().type == type;
        }

        boolean match(TokenType... types) {
            for (TokenType type : types) {
                if (check(type)) {
                    advance();
                    return true;
                }
            }
            return false;
        }

        Token consume(TokenType type, String errorMessage) {
            if (check(type)) return advance();
            Token currentToken = peek();
            throw new IllegalArgumentException(String.format(
                    "Syntax Error: %s at position %d (got '%s') in rule: \"%s\"",
                    errorMessage, currentToken.position, currentToken.text, originalRule));
        }

        // Expression -> OrExpr
        Expression parseExpression() {
            return parseOr();
        }

        // OrExpr -> AndExpr ( "OR" AndExpr )*
        Expression parseOr() {
            Expression expr = parseAnd();
            while (match(TokenType.OR)) {
                Expression right = parseAnd();
                expr = new OrExpression(expr, right);
            }
            return expr;
        }

        // AndExpr -> NotExpr ( "AND" NotExpr )*
        Expression parseAnd() {
            Expression expr = parseNot();
            while (match(TokenType.AND)) {
                Expression right = parseNot();
                expr = new AndExpression(expr, right);
            }
            return expr;
        }

        // NotExpr -> "NOT" NotExpr | Primary
        Expression parseNot() {
            if (match(TokenType.NOT)) {
                Expression inner = parseNot();
                return new NotExpression(inner);
            }
            return parsePrimary();
        }

        // Primary -> "(" Expression ")" | Comparison
        Expression parsePrimary() {
            if (match(TokenType.LPAREN)) {
                Expression expr = parseExpression();
                consume(TokenType.RPAREN, "Expected ')' to close parenthesized expression");
                return expr;
            }
            return parseComparison();
        }

        // Comparison -> IDENTIFIER OPERATOR (LITERAL | IDENTIFIER)
        Expression parseComparison() {
            Token varToken = consume(TokenType.IDENTIFIER, "Expected variable/attribute name in policy rule");
            
            if (!check(TokenType.OPERATOR)) {
                Token currentToken = peek();
                throw new IllegalArgumentException(String.format(
                        "Expected comparison operator (==, !=, >, <, >=, <=) after '%s' at position %d in rule: \"%s\"",
                        varToken.text, currentToken.position, originalRule));
            }
            Token opToken = advance();

            // The target value can be a quoted string, number, or bare identifier literal
            Token valToken;
            if (check(TokenType.LITERAL) || check(TokenType.IDENTIFIER)) {
                valToken = advance();
            } else {
                Token currentToken = peek();
                throw new IllegalArgumentException(String.format(
                        "Expected target value after '%s' at position %d in rule: \"%s\"",
                        opToken.text, currentToken.position, originalRule));
            }

            return new ComparisonExpression(varToken.text, opToken.text, valToken.text);
        }
    }
}
