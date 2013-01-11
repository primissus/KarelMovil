package structs;

import java.util.LinkedList;
import grammar.LogicO;

public class StructMientras extends Struct {
	public LogicO argumentoLogico; //Para si y mientras
	public LinkedList<Struct> cola;
	public StructMientras(LogicO arg, LinkedList<Struct> cola){
		super(Struct.ESTRUCTURA_MIENTRAS);
		this.argumentoLogico = arg;
        this.cola = cola;
	}
	public StructMientras(){
		super(Struct.ESTRUCTURA_MIENTRAS);
	}
}
