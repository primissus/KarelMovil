package structs;

import java.util.LinkedList;
import grammar.IntExpr;

public class StructRepite extends Struct {
	public IntExpr argumentoEntero; //Solo para repite
	public LinkedList<Struct> cola;
	public StructRepite(IntExpr args, LinkedList<Struct> cola){
		super(Struct.ESTRUCTURA_REPITE);
		this.argumentoEntero = args;
        this.cola = cola;
	}
	public StructRepite(){
		super(Struct.ESTRUCTURA_REPITE);
	}
}
