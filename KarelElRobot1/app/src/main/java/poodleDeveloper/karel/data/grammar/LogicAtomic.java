package poodleDeveloper.karel.data.grammar;

public class LogicAtomic{
    public static final int EXPRESION_ENTERA=0x1; //si-es-cero
    public static final int EXPRESION_BOOLEANA=0x2; //expresion normal
    public static final int EXPRESION_TERMINO=0x3; //un termino anidado
    
    public IntExpr argumentoEntero;
    public String funcionBooleana;
    public LogicO termino;
    public int tipo;
    
    public void crearSiEsCero(IntExpr arg){
        this.tipo = LogicAtomic.EXPRESION_ENTERA;
        this.argumentoEntero = arg;
    }
    public void crearExpresionBooleana(String func){
        this.tipo = LogicAtomic.EXPRESION_BOOLEANA;
        this.funcionBooleana = func;
    }
    public void crearTermino(LogicO termino){
        this.tipo = LogicAtomic.EXPRESION_TERMINO;
        this.termino = termino;
    }
}
