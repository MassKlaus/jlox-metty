package jlox.expression.environement;

import java.util.HashMap;
import java.util.Map;
import jlox.expression.visitors.Interpreter;
import jlox.expression.visitors.Interpreter.RuntimeError;
import jlox.lexer.Token;

public class Environment {
  private final Map<String, Object> values = new HashMap<>();
  public final Environment enclosing;

  public Environment() {
    enclosing = null;
  }

  public Environment(Environment enclosing) {
    this.enclosing = enclosing;
  }

  public void define(String name, Object value) {
    values.put(name, value);

  }

  public Object get(Token name) throws Interpreter.RuntimeError {
    if (values.containsKey(name.lexeme)) {
      return values.get(name.lexeme);
    }

    if (enclosing != null)
      return enclosing.get(name);

    throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
  }

  public void assign(Token name, Object value) {
    if (values.containsKey(name.lexeme)) {
      values.put(name.lexeme, value);
      return;
    }

    if (enclosing != null) {
      enclosing.assign(name, value);
      return;
    }

    throw new RuntimeError(name,
        "Undefined variable '" + name.lexeme + "'.");
  }
}
