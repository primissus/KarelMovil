package structs;

import grammar.LogicO;

public class RStructMientras extends RStructBucle {
	public LogicO argumentoLogico;
	public RStructMientras(LogicO args, int id){
		super(Struct.ESTRUCTURA_MIENTRAS);
		this.argumentoLogico = args;
		this.id = id;
	}
}
