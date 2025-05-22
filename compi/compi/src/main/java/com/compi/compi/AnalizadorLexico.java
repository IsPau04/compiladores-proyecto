package com.compi.compi;
import java.util.*;
import java.util.regex.*;

public class AnalizadorLexico {

    private String codigoFuente;
    private List<Token> tokens = new ArrayList<>();
    private Map<String, Simbolo> tablaSimbolos = new HashMap<>();
    private List<String> errores = new ArrayList<>();

    // Expresiones regulares para los tokens
    private final Pattern patronIdentificador = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");
    private final Pattern patronNumeroEntero = Pattern.compile("-?\\d+"); // Números enteros con signo negativo
    private final Pattern patronNumeroReal = Pattern.compile("-?\\d+\\.\\d+"); // Números reales con signo negativo
    private final Pattern patronOperadorAritmetico = Pattern.compile("[+\\-*/^#]");
    private final Pattern patronOperadorRelacional = Pattern.compile("==|>=|<=|!=|<|>");
    private final Pattern patronCorchete = Pattern.compile("[\\[\\]]");
    private final Pattern patronOperadorAsignacion = Pattern.compile("="); // Operador de asignación
    private final Pattern patronOperadorLogico = Pattern.compile("\\|\\||&&|!");
    private final Pattern patronSignosEspeciales = Pattern.compile("[(){},;]");
    private final Pattern patronComentarioUnaLinea = Pattern.compile("//.*");
    private final Pattern patronComentarioVariasLineas = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
    private final Pattern patronCadena = Pattern.compile("\"[^\"]*\""); // Nueva: cadenas de texto
    private final Pattern patronChar = Pattern.compile("'[^']'"); // Caracteres entre comillas simples
    private final Pattern patronAutoIncremento = Pattern.compile("\\+\\+");
    private final Pattern patronAutoDecremento = Pattern.compile("--");


    // Palabras reservadas del lenguaje (en inglés)
    private final Set<String> palabrasReservadas = new HashSet<>(Arrays.asList(
            "int", "double", "boolean", "do","true","false","char", "string", "if", "else", "for", "while", "print", "println", "entero", "real", "booleano","caracter","cadena","longitud","acadena","escribirlinea","escribir"
    ));
    

    public AnalizadorLexico(String codigoFuente) {
        this.codigoFuente = codigoFuente.toLowerCase(); // Convertir a minúsculas
    }

    public void analizar() {
        // Eliminar comentarios de varias líneas (/* ... */) del código fuente
        String codigoSinComentarios = eliminarComentariosVariasLineas(codigoFuente);
    
        // Dividir el código en líneas
        String[] lineas = codigoSinComentarios.split("\n");
        for (int i = 0; i < lineas.length; i++) {
            String linea = lineas[i].trim();
    
            // Ignorar comentarios de una línea (//)
            Matcher matcherComentarioUnaLinea = patronComentarioUnaLinea.matcher(linea);
            if (matcherComentarioUnaLinea.find()) {
                linea = linea.substring(0, matcherComentarioUnaLinea.start()).trim();
            }
    
            // Procesar la línea
            procesarLinea(linea, i + 1);
        }
    }
    
    private String eliminarComentariosVariasLineas(String codigo) {
        // Expresión regular para eliminar comentarios de varias líneas (/* ... */)
        // Eliminar comentarios de varias líneas
    Pattern patronComentarioVariasLineas = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
    codigo = patronComentarioVariasLineas.matcher(codigo).replaceAll("");

    // Eliminar comentarios de una sola línea
    Pattern patronComentarioUnaLinea = Pattern.compile("//.*$", Pattern.MULTILINE);
    codigo = patronComentarioUnaLinea.matcher(codigo).replaceAll("");

    return codigo;
    }

    private void procesarLinea(String linea, int numeroLinea) {
        int columna = 1;
        while (!linea.isEmpty()) {
            // Ignorar espacios en blanco
            if (linea.charAt(0) == ' ') {
                linea = linea.substring(1).trim();
                columna++;
                continue;
            }
            
    
            // Detectar comentarios de varias líneas (/* ... */)
            if (linea.startsWith("/*")) {
                int finComentario = linea.indexOf("*/");
                if (finComentario == -1) {
                    // Si no se encuentra el cierre del comentario, es un error
                    errores.add("Error léxico en línea " + numeroLinea + ", columna " + columna + ": comentario de varias líneas no cerrado");
                    return; // Salir del método porque no hay más que procesar en esta línea
                } else {
                    // Ignorar el comentario y continuar con el resto de la línea
                    linea = linea.substring(finComentario + 2).trim();
                    columna += finComentario + 2;
                    continue;
                }
            }
    
            // Identificar tokens
            boolean tokenEncontrado = false;
    
            // AutoIncremento (++)
            Matcher matcherAutoIncremento = patronAutoIncremento.matcher(linea);
            if (!tokenEncontrado && matcherAutoIncremento.lookingAt()) {
                String token = matcherAutoIncremento.group();
                tokens.add(new Token("OP_AUTOINCREMENTO", token, numeroLinea, columna));
                linea = linea.substring(token.length()).trim();
                columna += token.length();
                tokenEncontrado = true;
            }
    
            // AutoDecremento (--)
            Matcher matcherAutoDecremento = patronAutoDecremento.matcher(linea);
            if (!tokenEncontrado && matcherAutoDecremento.lookingAt()) {
                String token = matcherAutoDecremento.group();
                tokens.add(new Token("OP_AUTODECREMENTO", token, numeroLinea, columna));
                linea = linea.substring(token.length()).trim();
                columna += token.length();
                tokenEncontrado = true;
            }
    
            // Cadenas de texto
            Matcher matcherCadena = patronCadena.matcher(linea);
            if (!tokenEncontrado && matcherCadena.lookingAt()) {
                String token = matcherCadena.group();
                tokens.add(new Token("CADENA", token, numeroLinea, columna));
                linea = linea.substring(token.length()).trim();
                columna += token.length();
                tokenEncontrado = true;
            }
                        // Corchetes [ y ]
            Matcher matcherCorchete = patronCorchete.matcher(linea);
            if (!tokenEncontrado && matcherCorchete.lookingAt()) {
                String token = matcherCorchete.group();
                tokens.add(new Token("CORCHETE", token, numeroLinea, columna));
                linea = linea.substring(token.length()).trim();
                columna += token.length();
                tokenEncontrado = true;
            }

    
            // Caracteres
            Matcher matcherChar = patronChar.matcher(linea);
            if (!tokenEncontrado && matcherChar.lookingAt()) {
                String token = matcherChar.group();
                tokens.add(new Token("CHAR", token, numeroLinea, columna));
                linea = linea.substring(token.length()).trim();
                columna += token.length();
                tokenEncontrado = true;
            }
    
            // Palabras reservadas e identificadores
            Matcher matcherIdentificador = patronIdentificador.matcher(linea);
            if (!tokenEncontrado && matcherIdentificador.lookingAt()) {
                String token = matcherIdentificador.group();
                if (palabrasReservadas.contains(token)) {
                    tokens.add(new Token("PALABRA_RESERVADA", token, numeroLinea, columna));
                } else {
                    tokens.add(new Token("ID", token, numeroLinea, columna));
                    tablaSimbolos.put(token, new Simbolo(token, "ID", numeroLinea, columna));
                }
                linea = linea.substring(token.length()).trim();
                columna += token.length();
                tokenEncontrado = true;
            }
    
            // Números enteros y reales
            Matcher matcherNumeroReal = patronNumeroReal.matcher(linea);
            if (!tokenEncontrado && matcherNumeroReal.lookingAt()) {
                String token = matcherNumeroReal.group();
                tokens.add(new Token("NUM", token, numeroLinea, columna));
                linea = linea.substring(token.length()).trim();
                columna += token.length();
                tokenEncontrado = true;
            }
    
            Matcher matcherNumeroEntero = patronNumeroEntero.matcher(linea);
            if (!tokenEncontrado && matcherNumeroEntero.lookingAt()) {
                String token = matcherNumeroEntero.group();
                tokens.add(new Token("NUM", token, numeroLinea, columna));
                linea = linea.substring(token.length()).trim();
                columna += token.length();
                tokenEncontrado = true;
            }
    
            // Operadores aritméticos
            Matcher matcherOperadorAritmetico = patronOperadorAritmetico.matcher(linea);
            if (!tokenEncontrado && matcherOperadorAritmetico.lookingAt()) {
                String token = matcherOperadorAritmetico.group();
                tokens.add(new Token("OP_ARITMETICO", token, numeroLinea, columna));
                linea = linea.substring(token.length()).trim();
                columna += token.length();
                tokenEncontrado = true;
            }
    
            // Operadores relacionales
            Matcher matcherOperadorRelacional = patronOperadorRelacional.matcher(linea);
            if (!tokenEncontrado && matcherOperadorRelacional.lookingAt()) {
                String token = matcherOperadorRelacional.group();
                tokens.add(new Token("OP_RELACIONAL", token, numeroLinea, columna));
                linea = linea.substring(token.length()).trim();
                columna += token.length();
                tokenEncontrado = true;
            }
    
            // Operador de asignación
            Matcher matcherOperadorAsignacion = patronOperadorAsignacion.matcher(linea);
            if (!tokenEncontrado && matcherOperadorAsignacion.lookingAt()) {
                String token = matcherOperadorAsignacion.group();
                tokens.add(new Token("OP_ASIGNACION", token, numeroLinea, columna));
                linea = linea.substring(token.length()).trim();
                columna += token.length();
                tokenEncontrado = true;
            }
    
            // Operadores lógicos
            Matcher matcherOperadorLogico = patronOperadorLogico.matcher(linea);
            if (!tokenEncontrado && matcherOperadorLogico.lookingAt()) {
                String token = matcherOperadorLogico.group();
                tokens.add(new Token("OP_LOGICO", token, numeroLinea, columna));
                linea = linea.substring(token.length()).trim();
                columna += token.length();
                tokenEncontrado = true;
            }
    
            // Signos especiales
            Matcher matcherSignosEspeciales = patronSignosEspeciales.matcher(linea);
            if (!tokenEncontrado && matcherSignosEspeciales.lookingAt()) {
                String token = matcherSignosEspeciales.group();
                tokens.add(new Token("SIGNO_ESPECIAL", token, numeroLinea, columna));
                linea = linea.substring(token.length()).trim();
                columna += token.length();
                tokenEncontrado = true;
            }
    
            // Si no se encontró ningún token válido, es un error léxico
            if (!tokenEncontrado) {
                errores.add("Error léxico en línea " + numeroLinea + ", columna " + columna + ": carácter inválido '" + linea.charAt(0) + "'");
                linea = linea.substring(1).trim();
                columna++;
            }
        }
    }
    public List<Token> getTokens() {
        return tokens;
    }

    public Map<String, Simbolo> getTablaSimbolos() {
        return tablaSimbolos;
    }

    public List<String> getErrores() {
        return errores;
    }

    public static void main(String[] args) {
        // Código fuente que se va a analizar
        String codigoFuente = "int contador = -10;\n" +
                              "double pi = 3.14;\n" +
                              "if (contador > 5) {\n" +
                              "    println(\"El contador es mayor que 5\");\n" +
                              "}";

        // Crear una instancia del analizador léxico
        AnalizadorLexico analizador = new AnalizadorLexico(codigoFuente);

        // Ejecutar el análisis
        analizador.analizar();

        // Mostrar los resultados
        System.out.println("Tokens:");
        for (Token token : analizador.getTokens()) {
            System.out.println(token);
        }

        System.out.println("\nTabla de símbolos:");
        for (Map.Entry<String, Simbolo> entry : analizador.getTablaSimbolos().entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

        System.out.println("\nErrores:");
        for (String error : analizador.getErrores()) {
            System.out.println(error);
        }
    }
    public String getCodigoSinComentarios() {
    return eliminarComentariosVariasLineas(codigoFuente);
}

}

class Token {
    private String tipo;
    private String valor;
    private int linea;
    private int columna;

    public Token(String tipo, String valor, int linea, int columna) {
        this.tipo = tipo;
        this.valor = valor;
        this.linea = linea;
        this.columna = columna;
    }

    // Getters y Setters
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }


    public int getLinea() {
        return linea;
    }

    public void setLinea(int linea) {
        this.linea = linea;
    }

    public int getColumna() {
        return columna;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }

    @Override
    public String toString() {
        return "Token{" +
                "tipo='" + tipo + '\'' +
                ", valor='" + valor + '\'' +
                ", linea=" + linea +
                ", columna=" + columna +
                '}';
    }
}

class Simbolo {
    private String nombre;
    private String tipo;
    private int linea;
    private int columna;

    public Simbolo(String nombre, String tipo, int linea, int columna) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.linea = linea;
        this.columna = columna;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getLinea() {
        return linea;
    }

    public void setLinea(int linea) {
        this.linea = linea;
    }

    public int getColumna() {
        return columna;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }

    @Override
    public String toString() {
        return "Simbolo{" +
                "nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +
                ", linea=" + linea +
                ", columna=" + columna +
                '}';
    }
}
