# Jlox

## Introduction
This is my own implementation of the Lox language following the [Crafting Interpreters Book](https://craftinginterpreters.com/), a small change is not using Exceptions for returning results due to exception handling being a slow endevour in Java.

This implementation is only temporary, as I plan to rewrite it in Zig in the future. To learn the language and Accentuate my knowledge of language design.

Image of Lox mascot and logo - Generated through midjourney
![Jlox-Metty Logo](https://github.com/MassKlaus/jlox-metty/blob/master/JLox.png?raw=true)

## Usage

### Running the REPL
To run the REPL, run the following command in the root directory of the project:

```bash
./gradlew run
```

### Running a file
To run a file, run the following command in the root directory of the project:

```bash
./gradlew run --args="path/to/file"
```
