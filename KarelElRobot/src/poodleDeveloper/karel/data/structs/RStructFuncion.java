package poodleDeveloper.karel.data.structs;

import poodleDeveloper.karel.data.grammar.IntExpr;
import java.util.LinkedList;

public class RStructFuncion extends RunStruct {
    public LinkedList<IntExpr> params;
    public String nombre;
    public RStructFuncion(LinkedList<IntExpr> param, String nombre){
        super(Struct.ESTRUCTURA_INSTRUCCION_DEFINIDA);
        this.params = param;
        this.nombre = nombre;
    }
}
