package jlox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import jlox.expression.parser.Parser;
import jlox.expression.visitors.Interpreter;
import jlox.expression.visitors.Interpreter.RuntimeError;
import jlox.lexer.Scanner;
import jlox.lexer.Token;
import jlox.lexer.TokenType;

public class Jlox {
    private static final Interpreter interpreter = new Interpreter();
    static Boolean hadError = false;
    static Boolean hadRunTimeError = false;

    public static void main(String[] args) throws IOException {

        if (args.length > 1) {
            System.out.println("Error: Cannot give more than one argument;");
            System.out.println("Usage: Jlox [script file]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    static void runFile(String file) throws IOException {
        String source = Files.readString(Paths.get(file), Charset.defaultCharset());
        run(source);
        if (hadError) {
            System.exit(65);
        }
        if (hadRunTimeError)
            System.exit(70);
    }

    static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while (true) {
            System.out.printf("> ");
            String line = reader.readLine();

            if (line == null) {
                break;
            }

            run(line);
            hadError = false;
        }
    }

    static void run(String input) {
        Scanner scanner = new Scanner(input);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        var statments = parser.parse();

        // Stop if there was a syntax error.
        if (hadError)
            return;

        interpreter.interpret(statments);
    }

    public static void error(int line, int col, String message) {
        report(line, col, "", message);
    }

    public static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, token.col, " at end", message);
        } else {
            report(token.line, token.col, " at '" + token.lexeme + "'", message);
        }
    }

    static void report(int line, int col, String where, String message) {
        System.err.println(
                "[line " + line + " col " + col + "] Error" + where + ": " + message);
        hadError = true;
    }

    public static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() +
                "\n[line " + error.token.line + "]");
        hadRunTimeError = true;
    }
}
