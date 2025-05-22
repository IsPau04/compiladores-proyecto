package com.compi.compi;

import org.antlr.v4.runtime.*;

import java.util.ArrayList;
import java.util.List;

public class SyntaxErrorListener extends BaseErrorListener {
    private final List<String> errors = new ArrayList<>();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                            int line, int charPositionInLine, String msg, RecognitionException e) {
        errors.add("Línea " + line + ":" + (charPositionInLine + 1) + " - " + msg);
    }

    public List<String> getErrors() {
        return errors;
    }
}
