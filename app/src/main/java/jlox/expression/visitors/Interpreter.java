package jlox.expression.visitors;

import java.util.ArrayList;
import java.util.List;

import jlox.Jlox;
import jlox.expression.Expr;
import jlox.expression.Stmt;
import jlox.expression.Stmt.Block;
import jlox.expression.Stmt.Expression;
import jlox.expression.Stmt.Function;
import jlox.expression.Stmt.IF;
import jlox.expression.Stmt.Return;
import jlox.expression.Stmt.Var;
import jlox.expression.Stmt.While;
import jlox.expression.environement.Environment;
import jlox.expression.Expr.*;
import jlox.lexer.Token;
import jlox.lexer.TokenType;

public class Interpreter implements Expr.Visitor<Object>,
        Stmt.Visitor<Void> {

    final Environment globals = new Environment();
    private Environment environment = globals;

    private Object returnedValue = null;
    private boolean returned = false;

    public Interpreter() {
        super();
        globals.define("clock", new LoxCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public void call(Interpreter interpreter,
                    List<Object> arguments) {
                returnedValue((double) System.currentTimeMillis() / 1000.0);
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        });
    }

    @Override
    public Object visitBinaryExpr(Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double) left - (double) right;
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return (double) left / (double) right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double) left * (double) right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }

                if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }

                throw new RuntimeError(expr.operator,
                        "Operands must be two numbers or two strings.");
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double) left > (double) right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double) left >= (double) right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double) left < (double) right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double) left <= (double) right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
        }

        // Unreachable.
        return null;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null)
            return true;
        if (a == null || b == null)
            return false;

        return a.equals(b);
    }

    @Override
    public Object visitGroupingExpr(Grouping expr) {
        return evaluate(expr.expression);
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);

    }

    private boolean isTruthy(Object object) {
        if (object == null)
            return false;
        if (object instanceof Boolean)
            return (boolean) object;
        return true;
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double) right;
        }

        return null;
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double)
            return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator,
            Object left, Object right) {
        if (left instanceof Double && right instanceof Double)
            return;

        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    public static class RuntimeError extends RuntimeException {
        public final Token token;

        public RuntimeError(Token token, String message) {
            super(message);
            this.token = token;
        }
    }

    public void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Jlox.runtimeError(error);
        }
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    private String stringify(Object object) {
        if (object == null)
            return "nil";

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }

    @Override
    public Void visitExpressionStmt(Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitVarStmt(Var stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }

        environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Object visitVariableExpr(Variable expr) {
        return environment.get(expr.name);
    }

    @Override
    public Object visitAssignExpr(Assign expr) {
        Object value = evaluate(expr.value);
        environment.assign(expr.name, value);
        return value;
    }

    void executeBlock(List<Stmt> statements,
            Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Stmt statement : statements) {
                if (returned) {
                    break;
                }
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    @Override
    public Void visitBlockStmt(Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    @Override
    public Void visitIFStmt(IF expr) {
        if (isTruthy(evaluate(expr.expression))) {
            execute(expr.trueBranch);
        } else if (expr.falseBranch != null) {
            execute(expr.falseBranch);
        }

        return null;
    }

    @Override
    public Object visitLogicalExpr(Logical expr) {
        Object left = evaluate(expr.left);

        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left))
                return left;
        } else {
            if (!isTruthy(left))
                return left;
        }

        return evaluate(expr.right);
    }

    @Override
    public Void visitWhileStmt(While expr) {
        while (isTruthy(evaluate(expr.condition))) {
            execute(expr.body);
        }

        return null;
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);

        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments) {
            arguments.add(evaluate(argument));
        }

        if (!(callee instanceof LoxCallable)) {
            throw new RuntimeError(expr.paren,
                    "Can only call functions and classes.");
        }

        LoxCallable function = (LoxCallable) callee;
        function.call(this, arguments);

        return consumeReturnedValue();
    }

    @Override
    public Void visitFunctionStmt(Function stmt) {
        LoxFunction function = new LoxFunction(stmt, environment);
        environment.define(stmt.name.lexeme, function);
        return null;
    }

    @Override
    public Void visitReturnStmt(jlox.expression.Stmt.Return stmt) {
        Object value = null;
        if (stmt.value != null)
            value = evaluate(stmt.value);

        returnedValue(value);

        return null;
    }

    private void returnedValue(Object value) {
        returnedValue = value;
        returned = true;
    }

    private Object consumeReturnedValue() {
        var result = returnedValue;
        returned = false;
        returnedValue = null;
        return result;
    }
}
