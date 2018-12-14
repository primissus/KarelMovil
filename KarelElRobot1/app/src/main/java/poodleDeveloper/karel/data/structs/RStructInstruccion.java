package poodleDeveloper.karel.data.structs;

public class RStructInstruccion extends RunStruct {
    public String instruccion;
    public RStructInstruccion(String nombre){
        super(Struct.ESTRUCTURA_INSTRUCCION);
        this.instruccion = nombre;
    }
}
