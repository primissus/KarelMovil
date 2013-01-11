package grammar;

import java.util.LinkedList;

import structs.Struct;

public class FunctionDef {
	public String nombre;
    public LinkedList<Struct> cola; //Instrucciones que le pertenecen a esta función
    public LinkedList<IntExpr> params; //Parametros de la funcion
    public FunctionDef(String nombre, LinkedList<Struct> cola, LinkedList<IntExpr> params){
        this.nombre = nombre;
        this.cola = cola;
        this.params = params;
    }
    public FunctionDef(String nombre){
        this.nombre = nombre;
        this.cola = new LinkedList<Struct>();
        this.params = new LinkedList<IntExpr>();
    }
}
