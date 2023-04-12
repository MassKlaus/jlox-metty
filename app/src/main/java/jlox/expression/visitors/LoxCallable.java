package jlox.expression.visitors;

import java.util.List;

public interface LoxCallable {
    int arity();
    void call(Interpreter interpreter, List<Object> arguments);
  }
