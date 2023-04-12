package jlox.expression.visitors;

import java.util.List;

import jlox.expression.Stmt;
import jlox.expression.environement.Environment;

public class LoxFunction implements LoxCallable {
    private final Stmt.Function declaration;
    private final Environment closure;

    public LoxFunction(Stmt.Function declaration, Environment closure) {
        this.declaration = declaration;
        this.closure = closure;
    }

    @Override
    public int arity() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'arity'");
    }

    @Override
    public void call(Interpreter interpreter,
            List<Object> arguments) {
        Environment environment = new Environment(closure);
        for (int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).lexeme,
                    arguments.get(i));
        }

        interpreter.executeBlock(declaration.body, environment);
    }
}
