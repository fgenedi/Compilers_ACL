/**
 * Write your info here
 *
 * @name Farida El Genedi
 * @id 46-2291
 * @labNumber 10
 */

grammar Task8;

/**
 * This rule is to check your grammar using "ANTLR Preview"
 */
test: /* (Rule1 | Rule2 | ... | RuleN)+ */ EOF; //Replace the non-fragment lexer rules here

IF options{caseInsensitive =true;}:'if';
ELSE options{caseInsensitive =true;}:'else';
COMP : '>=' | '<=' | '==' | '!=' | '>' | '<';
LP : '(';
RP : ')';
LIT: '"' ( ESC | ~["\\\r\n] )* '"';
fragment ESC : '\\' [\\"bfnrt];
ID: [a-zA-Z_][a-zA-Z0-9_]*;
NUM: [0-9]+ ('.' [0-9]+)? (EXPONENT [+-]? [0-9]+)?;
fragment EXPONENT options{caseInsensitive =true;}:'e';
WS: [ \r\t\n]+ -> skip;