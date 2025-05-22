package com.compi.compi.parser;

import com.compi.parser.MiGramaticaBaseVisitor;
import com.compi.parser.MiGramaticaParser;

import java.util.HashMap;
import java.util.Map;

public class MiGramaticaCustomVisitor extends MiGramaticaBaseVisitor<Double> {

    private final Map<String, Double> resultados = new HashMap<>();

    public Map<String, Double> getResultados() {
        return resultados;
    }

    @Override
    public Double visitInicio(MiGramaticaParser.InicioContext ctx) {
        for (var sentencia : ctx.sentencia()) {
            visit(sentencia);
        }
        return 0.0;
    }

    @Override
    public Double visitAsignacion(MiGramaticaParser.AsignacionContext ctx) {
        String variable = ctx.ID().getText();
        Double valor = visit(ctx.expresion());

        if (valor != null) {
            resultados.put(variable, valor);
        }

        return valor;
    }

    @Override
    public Double visitSuma(MiGramaticaParser.SumaContext ctx) {
        Double izq = visit(ctx.expresion());
        Double der = visit(ctx.termino());
        return (izq != null && der != null) ? izq + der : null;
    }

    @Override
    public Double visitResta(MiGramaticaParser.RestaContext ctx) {
        Double izq = visit(ctx.expresion());
        Double der = visit(ctx.termino());
        return (izq != null && der != null) ? izq - der : null;
    }

    @Override
    public Double visitToTermino(MiGramaticaParser.ToTerminoContext ctx) {
        return visit(ctx.termino());
    }

    @Override
    public Double visitMultiplicacion(MiGramaticaParser.MultiplicacionContext ctx) {
        Double izq = visit(ctx.termino());
        Double der = visit(ctx.potencia());
        return (izq != null && der != null) ? izq * der : null;
    }

    @Override
    public Double visitDivision(MiGramaticaParser.DivisionContext ctx) {
        Double izq = visit(ctx.termino());
        Double der = visit(ctx.potencia());
        if (izq != null && der != null && der != 0) {
            return izq / der;
        } else if (der != null && der == 0) {
            return Double.POSITIVE_INFINITY; // se maneja en el controller
        }
        return null;
    }

    @Override
    public Double visitToPotencia(MiGramaticaParser.ToPotenciaContext ctx) {
        return visit(ctx.potencia());
    }

    @Override
    public Double visitOperacionpotencia(MiGramaticaParser.OperacionpotenciaContext ctx) {
        Double base = visit(ctx.factor());
        Double exponente = visit(ctx.potencia());
        return (base != null && exponente != null) ? Math.pow(base, exponente) : null;
    }

    @Override
    public Double visitToFactor(MiGramaticaParser.ToFactorContext ctx) {
        return visit(ctx.factor());
    }

    @Override
    public Double visitAgrupacion(MiGramaticaParser.AgrupacionContext ctx) {
        return visit(ctx.expresion());
    }

    @Override
    public Double visitNumero(MiGramaticaParser.NumeroContext ctx) {
        try {
            return Double.parseDouble(ctx.NUMERO().getText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public Double visitId(MiGramaticaParser.IdContext ctx) {
        String id = ctx.ID().getText();
        return resultados.get(id); // null si no existe
    }
}
