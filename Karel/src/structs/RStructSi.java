package structs;

import grammar.LogicO;

public class RStructSi extends RunStruct {
	public LogicO argumentoLogico;
	public boolean tieneSino=false;
	public int posicionSino;
	public RStructSi(LogicO args){
		super(Struct.ESTRUCTURA_SI);
		this.argumentoLogico = args;
	}
	public void setSino(int posicion){
		this.tieneSino = true;
		this.posicionSino = posicion;
	}
}
