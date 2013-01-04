package structs;

import grammar.LogicO;
import java.util.LinkedList;

public class StructSi extends Struct {
    public LogicO argumentoLogico; //Para si y mientras
    public LinkedList<Struct> cola;
    public LinkedList<Struct> colaSino;
    public boolean tieneSino; //Para si
    
    public StructSi(LogicO arg, LinkedList<Struct> cola){
    	super(Struct.ESTRUCTURA_SI);
        this.argumentoLogico = arg;
        this.cola = cola;
        this.tieneSino = false;
    }
    public StructSi(){
    	super(Struct.ESTRUCTURA_SI);
    }
}
