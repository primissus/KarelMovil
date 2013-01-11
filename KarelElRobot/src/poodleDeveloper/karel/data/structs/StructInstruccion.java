package structs;

public class StructInstruccion extends Struct {
	public String nombre; //Solo para ESTRUCTURA_INSTRUCCION_DEFINIDA
	public StructInstruccion(String nombre){
		super(Struct.ESTRUCTURA_INSTRUCCION);
		this.nombre = nombre;
	}
	public StructInstruccion(){
		super(Struct.ESTRUCTURA_INSTRUCCION);
	}
}
