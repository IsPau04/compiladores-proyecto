/*package com.compi.compi;

import com.compi.compi.parser.MiGramaticaCustomVisitor;
import com.compi.parser.MiGramaticaLexer;
import com.compi.parser.MiGramaticaParser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class AnalizadorController {

    public AnalizadorController() {
        System.out.println("AnalizadorController creado!");
    }

    @GetMapping("/test")
    public String test() {
        return "¡El controlador funciona!";
    }

    @PostMapping("/analizar")
    public Map<String, Object> analizar(@RequestBody Map<String, String> request) {
        String input = request.get("codigoFuente");

        Map<String, Object> resultado = new HashMap<>();

        // Análisis léxico
        AnalizadorLexico analizador = new AnalizadorLexico(input);
        analizador.analizar();

        resultado.put("tokens", analizador.getTokens());
        resultado.put("tablaSimbolos", analizador.getTablaSimbolos());
        resultado.put("errores", analizador.getErrores());

        // Análisis sintáctico con ANTLR
        try {
            CharStream chars = CharStreams.fromString(input);
            MiGramaticaLexer lexer = new MiGramaticaLexer(chars);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            MiGramaticaParser parser = new MiGramaticaParser(tokens);

            SyntaxErrorListener errorListener = new SyntaxErrorListener();
            parser.removeErrorListeners();
            parser.addErrorListener(errorListener);

            ParseTree tree = parser.inicio();
            MiGramaticaCustomVisitor visitor = new MiGramaticaCustomVisitor();
            visitor.visit(tree);

            // Recoger resultados algebraicos del visitor
            List<String> resultadosFinales = new ArrayList<>();
            for (Map.Entry<String, Double> entry : visitor.getResultados().entrySet()) {
                resultadosFinales.add(entry.getKey() + " = " + entry.getValue());
            }

            resultado.put("erroresSintacticos", errorListener.getErrors());
            resultado.put("resultadosAlgebraicos", resultadosFinales);

        } catch (Exception e) {
            resultado.put("erroresSintacticos", List.of("Error de análisis: " + e.getMessage()));
            resultado.put("resultadosAlgebraicos", List.of());
        }

        return resultado;
    }
}*/
