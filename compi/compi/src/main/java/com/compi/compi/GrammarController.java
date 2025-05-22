package com.compi.compi;

import com.compi.compi.parser.MiGramaticaCustomVisitor;
import com.compi.parser.MiGramaticaLexer;
import com.compi.parser.MiGramaticaParser;

import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/analizar")
public class GrammarController {

    @PostMapping
    public Map<String, Object> analizar(@RequestBody Map<String, String> request) {
        String input = request.get("codigoFuente");
        Map<String, Object> resultado = new HashMap<>();

        // Análisis léxico personalizado
        AnalizadorLexico analizadorLexico = new AnalizadorLexico(input);
        analizadorLexico.analizar();

        resultado.put("tokens", analizadorLexico.getTokens());
        resultado.put("tablaSimbolos", analizadorLexico.getTablaSimbolos());
        resultado.put("errores", analizadorLexico.getErrores());

        if (!analizadorLexico.getErrores().isEmpty()) {
            resultado.put("erroresSintacticos", List.of());
            resultado.put("resultadosAlgebraicos", List.of());
            return resultado;
        }

        try {
            SyntaxErrorListener errorListener = new SyntaxErrorListener();

            // Usamos el código sin comentarios para pasar a ANTLR
            String codigoLimpio = analizadorLexico.getCodigoSinComentarios();

            CharStream chars = CharStreams.fromString(codigoLimpio);
            MiGramaticaLexer lexer = new MiGramaticaLexer(chars);
            lexer.removeErrorListeners();
            lexer.addErrorListener(errorListener);

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            MiGramaticaParser parser = new MiGramaticaParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(errorListener);

            ParseTree tree = parser.inicio();
            MiGramaticaCustomVisitor visitor = new MiGramaticaCustomVisitor();
            visitor.visit(tree);

            List<String> resultadosFinales = new ArrayList<>();
            boolean huboErrorEvaluacion = false;
            List<String> erroresEvaluacion = new ArrayList<>();

            for (Map.Entry<String, Double> entry : visitor.getResultados().entrySet()) {
                String var = entry.getKey();
                Double valor = entry.getValue();

                if (valor == null || valor.isNaN()) {
                    erroresEvaluacion.add("Error al evaluar la variable '" + var + "': expresión inválida o mal estructurada.");
                    huboErrorEvaluacion = true;
                } else if (valor.isInfinite()) {
                    erroresEvaluacion.add("Error al evaluar '" + var + "': división entre cero.");
                    huboErrorEvaluacion = true;
                } else {
                    resultadosFinales.add(var + " = " + valor);
                }
            }

            resultado.put("erroresSintacticos", errorListener.getErrors());
            resultado.put("resultadosAlgebraicos", huboErrorEvaluacion ? erroresEvaluacion : resultadosFinales);

        } catch (Exception e) {
            resultado.put("erroresSintacticos", List.of("Error de análisis: La expresión contiene un problema grave."));
            resultado.put("resultadosAlgebraicos", List.of());
        }

        return resultado;
    }
}
