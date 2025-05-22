grammar MiGramatica;

options { visitor=true; }

inicio: sentencia+ EOF;

sentencia
    : ID '<-' expresion ';'             #asignacion
    ;

expresion
    : expresion '+' termino             #suma
    | expresion '-' termino             #resta
    | termino                           #toTermino
    ;

termino
    : termino '*' potencia              #multiplicacion
    | termino '/' potencia              #division
    | potencia                          #toPotencia
    ;

potencia
    : factor '^' potencia               #operacionpotencia
    | factor                            #toFactor
    ;

factor
    : CORCHETE_IZQ expresion CORCHETE_DER   #agrupacion
    | ID                                    #id
    | NUMERO                                #numero
    ;


ID: [a-zA-Z_][a-zA-Z_0-9]*;
NUMERO: [0-9]+ ('.' [0-9]+)?;
WS: [ \t\r\n]+ -> skip;
CORCHETE_IZQ : '[' ;
CORCHETE_DER : ']' ;
