package poodleDeveloper.karel.data.grammar;

public class LogicNo{
    public boolean valor = true;
    public LogicAtomic clausula;
    public void crear(LogicAtomic clau){
        this.clausula = clau;
    }
    public void crearNo(LogicAtomic clau){
        this.clausula = clau;
        this.valor = false;
    }
}