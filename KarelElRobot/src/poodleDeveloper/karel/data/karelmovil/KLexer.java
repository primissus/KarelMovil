/*
 * klexer.java
 *
 * Copyright 2012 Abraham Toriz Cruz <a.wonderful.code@gmail.com>
 *
 * This program is free software; you can redistribute it &&/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 *
 *
 */

package karelmovil;

import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.IOException;

public class KLexer {
    /* analizador léxico de karel */
    public final char ESTADO_ESPACIO = ' ';
    public final char ESTADO_PALABRA = 'a';
    public final char ESTADO_COMENTARIO = '#';
    public final char ESTADO_NUMERO = '0';
    public final char ESTADO_SIMBOLO = '+';

    public String nombre_archivo;
    private BufferedReader lector_buffer;
            
    public final String numeros = "0123456789";
    public final String palabras = "abcdfeghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_-";
    public final String simbolos = "(){}*/;,"; //Simbolos permitidos para esta sintaxis
    public final String espacios = " \n\t\r";
    public final String caracteres = this.numeros+this.palabras+this.simbolos+this.espacios;

    private char ultimo_caracter = '\0';
    private char caracter_actual = '\0';
    private char abrir_comentario = '\0'; //Indica cómo fue abierto un comentario

    private LinkedList<KToken> pila_tokens; //Pila de tokens por si me lo devuelven
    private LinkedList<Character> pila_chars; //Pila de caracteres

    public int linea;
    public int columna;
    public String posicion;
    
    private boolean tiene_cambio_de_linea;
    private char estado;
    private boolean debug;
    public String token;
    
    public KLexer(BufferedReader archivo, boolean debug){
        /*Se construye el analizador con el nombre del archivo*/
        //this.archivo = archivo;
        this.lector_buffer = archivo;
        
        this.pila_tokens = new LinkedList<KToken>();
        this.pila_chars = new LinkedList<Character>();
        
        this.linea = 1; //El número de linea
        this.columna = 0; //El número de columna
        this.tiene_cambio_de_linea = false;
        this.token = "";
        this.estado = this.ESTADO_ESPACIO;
        
        this.debug = debug;
        if (this.debug)
            System.out.println("leyendo archivo '"+this.nombre_archivo+"'");
    }
    
    public Character lee_caracter() throws IOException{
        /*Lee un caracter de la fuente o devuelve uno de la pila si no
        está vacía*/
        if(this.pila_chars.size()!=0)
            return (Character)this.pila_chars.pop();
        else{
            this.ultimo_caracter = this.caracter_actual;
            return (char)this.lector_buffer.read();
        }
    }

    public KToken get_token() throws KarelException{
        /*Obtiene el siguiente token. Si la pila tiene tokens le quita
        uno, si no, obtiene el siguiente token del archivo*/
        if (this.pila_tokens.size()>0)
            return (KToken)this.pila_tokens.pop();
        else
            return this.lee_token();
    }

    public void push_token(KToken token){
        /*Empuja un token en la pila*/
        this.pila_tokens.push(token);
    }

    public void push_char(char c){
        /*Pone un caracter en la pila de caracteres*/
        this.pila_chars.push(c);
    }

    public KToken lee_token() throws KarelException{
        /*Lee un token del archivo*/
        while(true){
            try{
                this.caracter_actual = this.lee_caracter();
            } catch (IOException e){
                break;
            }
            this.columna += 1;
            if(this.caracter_actual == '\0' || (int)this.caracter_actual==65535)
                break;
            if(this.tiene_cambio_de_linea){
                this.linea += 1;
                this.columna = 0;
                this.tiene_cambio_de_linea = false;
            }
            if (this.estado == this.ESTADO_COMENTARIO){
                if (this.debug)
                    System.out.println("Encontré '"+this.caracter_actual+"' en estado comentario");
                if (this.simbolos.indexOf(this.caracter_actual) != -1){ //Lo que puede pasar es que sea basura o termine el comentario
                    if (this.caracter_actual == ')' && this.abrir_comentario == '(' && this.ultimo_caracter == '*')
                        this.estado = this.ESTADO_ESPACIO;
                    if (this.caracter_actual == '}' && this.abrir_comentario == '{')
                        this.estado = this.ESTADO_ESPACIO;
                } else if(this.caracter_actual == '\n')
                    this.tiene_cambio_de_linea = true;
            } else if (this.estado == this.ESTADO_ESPACIO){
                if (this.debug)
                    System.out.println("Encontré '" + this.caracter_actual + "' en estado espacio");
                if (! (this.caracteres.indexOf(this.caracter_actual)!=-1))
                    throw new KarelException("Caracter desconocido ("+(int)this.caracter_actual+") en la linea "+this.linea+" columna "+this.columna);
                if (this.numeros.indexOf(this.caracter_actual)!=-1){
                    this.token += this.caracter_actual;
                    this.estado = this.ESTADO_NUMERO;
                }else if (this.palabras.indexOf(this.caracter_actual)!=-1){
                    this.token += this.caracter_actual;
                    this.estado = this.ESTADO_PALABRA;
                }else if (this.simbolos.indexOf(this.caracter_actual)!=-1){
                    this.push_char(this.caracter_actual); //Podria ser algo valido como ();,
                    this.estado = this.ESTADO_SIMBOLO;
                }else if (this.caracter_actual == '\n')
                    this.tiene_cambio_de_linea = true;
            }else if (this.estado == this.ESTADO_NUMERO){
                if (this.debug)
                    System.out.println("Encontré '" + this.caracter_actual + "' en estado número");
                if (!(this.caracteres.indexOf(this.caracter_actual)!=-1))
                    throw new KarelException("Caracter desconocido ("+(int)this.caracter_actual+") en la linea "+this.linea+" columna "+this.columna);
                if (this.numeros.indexOf(this.caracter_actual)!=-1)
                    this.token += this.caracter_actual;
                else if(this.palabras.indexOf(this.caracter_actual)!=-1) //Encontramos una letra en el estado numero, incorrecto
                    throw new KarelException("Este token no parece valido, linea "+this.linea+" columna "+this.columna);
                else if (this.simbolos.indexOf(this.caracter_actual)!=-1){
                    this.estado = this.ESTADO_SIMBOLO;
                    this.push_char(this.caracter_actual);
                    break;
                }else if(this.espacios.indexOf(this.caracter_actual)!=-1){
                    if (this.caracter_actual == '\n')
                        this.tiene_cambio_de_linea = true;
                    this.estado = this.ESTADO_ESPACIO;
                    break; //Terminamos este token
                }
            }else if (this.estado == this.ESTADO_PALABRA){
                if (this.debug)
                    System.out.println("Encontré '"+this.caracter_actual+"' en estado palabra");
                if (! (this.caracteres.indexOf(this.caracter_actual)!=-1))
                    throw new KarelException("Caracter desconocido ("+(int)this.caracter_actual+") en la linea "+this.linea+" columna "+this.columna);
                if (this.palabras.indexOf(this.caracter_actual)!=-1 || this.numeros.indexOf(this.caracter_actual)!=-1)
                    this.token += this.caracter_actual;
                else if (this.simbolos.indexOf(this.caracter_actual)!=-1){
                    this.estado = this.ESTADO_SIMBOLO;
                    this.push_char(this.caracter_actual);
                    break;
                }else if (this.espacios.indexOf(this.caracter_actual)!=-1){
                    if (this.caracter_actual == '\n')
                        this.tiene_cambio_de_linea = true;
                    this.estado = this.ESTADO_ESPACIO;
                    break; //Terminamos este token
                }
            }else if (this.estado == this.ESTADO_SIMBOLO){
                if (this.debug)
                    System.out.println("Encontré '"+this.caracter_actual+"' en estado símbolo");
                if (! (this.caracteres.indexOf(this.caracter_actual)!=-1))
                    throw new KarelException("Caracter desconocido ("+(int)this.caracter_actual+") en la linea "+this.linea+" columna "+this.columna);
                if (this.caracter_actual == '{'){
                    this.abrir_comentario = '{';
                    this.estado = this.ESTADO_COMENTARIO;
                }else if(this.numeros.indexOf(this.caracter_actual)!=-1){
                    this.estado = this.ESTADO_NUMERO;
                    this.push_char(this.caracter_actual);
                    if (this.token.length()>0)
                        break;
                    else
                        continue;
                }else if (this.palabras.indexOf(this.caracter_actual)!=-1){
                    this.estado = this.ESTADO_PALABRA;
                    this.push_char(this.caracter_actual);
                    if (this.token.length()>0)
                        break;
                }else if (this.simbolos.indexOf(this.caracter_actual)!=-1){
                    if (this.ultimo_caracter == '('){
                        if (this.caracter_actual == '*'){
                            this.token = "";
                            this.estado = this.ESTADO_COMENTARIO;
                            this.abrir_comentario = '(';
                            continue;
                        }else{
                            this.push_char(this.caracter_actual);
                            break;
                        }
                    }else if (this.caracter_actual != '('){ //el único símbolo con continuación
                        this.token += this.caracter_actual;
                        break;
                    }else{
                        this.token += this.caracter_actual;
                        continue;
                    }
                }else if (this.espacios.indexOf(this.caracter_actual)!=-1){
                    if (this.caracter_actual == '\n')
                        this.tiene_cambio_de_linea = true;
                    this.estado = this.ESTADO_ESPACIO;
                }
            }
        }
        String tokens = this.token;
        this.token = "";
        return new KToken(tokens, this.linea, this.columna, this.posicion);
    }

    public KToken next() throws StopIterationException,KarelException{
        /*Devuelve un token de la pila si no está vacía o devuelve el
        siguiente token del archivo, esta función sirve al iterador de
        tokens*/
        KToken tokenn = this.get_token();
        if (tokenn.token.equals(""))
            throw new StopIterationException("Se acabaron los tokens");
        return tokenn;
    }
}
