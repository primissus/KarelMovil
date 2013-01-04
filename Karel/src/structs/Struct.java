package structs;

public abstract class Struct {
	/* superclase de todas las estructuras de karel
	 * 
	 */
	public static final int ESTRUCTURA_REPITE=0x1;
    public static final int ESTRUCTURA_MIENTRAS=0x2;
    public static final int ESTRUCTURA_SI=0x3;
    public static final int ESTRUCTURA_SINO=0x4;
    public static final int ESTRUCTURA_INSTRUCCION=0x5; //Instrucciones por defecto
    public static final int ESTRUCTURA_INSTRUCCION_DEFINIDA=0x6; //Definidas por el usuario
    
    public int estructura;
    
    public Struct(int tipo){
    	this.estructura = tipo;
    }
}
