package structs;

public abstract class RunStruct extends Struct {
	public int finEstructura;
	public RunStruct(int tipo){
		super(tipo);
	}
	public String toString(){
		switch(this.estructura){
			case Struct.ESTRUCTURA_INSTRUCCION:
				return ">instruccion";
			case Struct.ESTRUCTURA_INSTRUCCION_DEFINIDA:
				return ">definida";
			case Struct.ESTRUCTURA_MIENTRAS:
				return ">mientras";
			case Struct.ESTRUCTURA_REPITE:
				return ">repite";
			case Struct.ESTRUCTURA_SI:
				return ">si";
			case Struct.ESTRUCTURA_SINO:
				return ">sino";
			default:
				return "";
		}
	}
}
