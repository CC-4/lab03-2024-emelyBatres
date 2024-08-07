/*
    Laboratorio No. 3 - Recursive Descent Parsing
    CC4 - Compiladores

    Clase que representa el parser

    Actualizado: agosto de 2021, Luis Cu
*/

import java.util.LinkedList;
import java.util.Stack;

public class Parser {

    // Puntero next que apunta al siguiente token
    private int next;
    // Stacks para evaluar en el momento
    private Stack<Double> operandos;
    private Stack<Token> operadores;
    // LinkedList de tokens
    private LinkedList<Token> tokens;

    // Funcion que manda a llamar main para parsear la expresion
    public boolean parse(LinkedList<Token> tokens) {
        this.tokens = tokens;
        this.next = 0;
        this.operandos = new Stack<Double>();
        this.operadores = new Stack<Token>();

        // Recursive Descent Parser
        // Imprime si el input fue aceptado
        //System.out.println("Aceptada? " + S());

        // Shunting Yard Algorithm
        // Imprime el resultado de operar el input
        //System.out.println("Resultado: " + this.operandos.peek());

        // Verifica si terminamos de consumir el input
        /*if(this.next != this.tokens.size()) {
            return false;
        }
        return true;*/
        
        boolean accepted = S();

        if (accepted && this.next == this.tokens.size()) {
            // Solo imprimir el resultado si la expresión es aceptada y procesada completamente
            if (!this.operandos.isEmpty()) {
                System.out.println(this.operandos.peek());
            }
        }
        return accepted;
    }

    // Verifica que el id sea igual que el id del token al que apunta next
    // Si si avanza el puntero es decir lo consume.
    private boolean term(int id) {
        if (this.next < this.tokens.size() && this.tokens.get(this.next).equals(id)) {
            Token token = this.tokens.get(this.next);

            if (id == Token.NUMBER) {
                // Encontramos un número, guardamos en el stack de operandos
                operandos.push(token.getVal());

            } else if (id == Token.UNARY) {
                this.next++;
                if (this.next < this.tokens.size() && term(Token.NUMBER)) {
                    operandos.push(-(operandos.pop()));
                } else {
                    return false;
                }

            } else if (id == Token.SEMI) {
                // Encontramos un punto y coma, operamos todo lo que quedó pendiente
                while (!this.operadores.isEmpty()) {
                    popOp();
                }

            } else if (id == Token.LPAREN) {
                this.operadores.push(token);

            } else if (id == Token.RPAREN) {
                while (!this.operadores.isEmpty() && this.operadores.peek().getId() != Token.LPAREN) {
                    popOp();
                }
                if (!this.operadores.isEmpty() && this.operadores.peek().getId() == Token.LPAREN) {
                    this.operadores.pop();
                } else {
                    return false;
                }

            } else {
                // Encontramos algún otro token, es decir un operador binario
                pushOp(token);
            }

            this.next++;
            return true;
        }
        return false;
    }

    // Funcion que verifica la precedencia de un operador
    private int pre(Token op) {

        /* el numero de operacion mas alto es el primero que debe operarse*/

        switch(op.getId()) {
        	case Token.PLUS:
        		return 1;
            case Token.MINUS:
                return 1;
        	case Token.MULT:
        		return 2;
            case Token.DIV:
                return 2;
            case Token.MOD:
                return 2;
            case Token.EXP:
                return 3;
            case Token.UNARY:
                return 4;
            case Token.LPAREN:
                return 0;
            case Token.RPAREN:
                return 0;
        	default:
        		return -1;
        }
    }

    private void popOp() {
        Token op = this.operadores.pop();
        double a = this.operandos.pop();
        double b = this.operandos.pop();
        /* Esta es la parte operativa. Saca con push a los operandos y los opera */

        if (op.equals(Token.PLUS)) {
        	// print para debug, quitarlo al terminar
        	//System.out.println("suma " + a + " + " + b);
        	this.operandos.push(a + b);

        } else if (op.equals(Token.MINUS)) {
            this.operandos.push(a - b);

        } else if (op.equals(Token.MULT)) {
        	this.operandos.push(a * b);

        } else if (op.equals(Token.DIV)) {
            this.operandos.push(a / b);

        } else if (op.equals(Token.MOD)) {
            this.operandos.push(a % b);

        } else if (op.equals(Token.EXP)) {
            this.operandos.push(Math.pow(a, b));

        } else if (op.equals(Token.UNARY)) {
            this.operandos.push(-a);
        }

    }

    private void pushOp(Token op) {
        /* Casi todo el codigo para esta seccion se vera en clase */

        if(this.operadores.empty()) {
            // Si no hay operandos automaticamente ingresamos op al stack
            this.operadores.push(op);

        } else {
            // Si si hay operandos:
    		// Obtenemos la precedencia de op
            int preceOp = pre(op);

            // Obtenemos la precedencia de quien ya estaba en el stack
            int preceinStack = pre(this.operadores.peek());

            // Comparamos las precedencias y decidimos si hay que operar
            // Es posible que necesitemos un ciclo aqui, una vez tengamos varios niveles de precedencia
            while (preceOp <= preceinStack && !this.operadores.isEmpty()) {
                popOp();
            } 

        	// Al terminar operaciones pendientes, guardamos op en stack
            this.operadores.push(op);
        }
    }

    private boolean S() {
        return E() && term(Token.SEMI);
    }

    private boolean E() {
        //return term(Token.NUMBER) && F();
        if (term(Token.NUMBER)) {
            return F();
        } else if (term(Token.LPAREN)) {
            if (E() && term(Token.RPAREN)) {
                return F();
            }
            return false;
        }
        return false;
    }

    private boolean F() {

        if (term(Token.PLUS) || term(Token.MINUS) || term(Token.MULT) || term(Token.DIV) || term(Token.MOD) || term(Token.EXP)) {
            if (E()) {
                return F();
            }
            return false;
        }
        return true;
    }

}
