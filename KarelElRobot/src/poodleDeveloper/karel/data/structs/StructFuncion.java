package poodleDeveloper.karel.data.structs;

import poodleDeveloper.karel.data.grammar.IntExpr;
import java.util.LinkedList;

public class StructFuncion extends Struct {
    public String nombre;
    public LinkedList<Struct> cola;
    public LinkedList<IntExpr> argumentoFuncion; //Para funciones
    public StructFuncion(String nombre, LinkedList<IntExpr> args, LinkedList<Struct> cola){
        super(Struct.ESTRUCTURA_INSTRUCCION_DEFINIDA);
        this.argumentoFuncion = args;
        this.nombre = nombre;
        this.cola = cola;
    }
    public StructFuncion(){
        super(Struct.ESTRUCTURA_INSTRUCCION_DEFINIDA);
    }
}
