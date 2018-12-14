package poodleDeveloper.karel.data.structs;

public abstract class RStructBucle extends RunStruct {
    public int id;
    public int cuenta=0; //cuenta de las ejecuciones de este bucle
    public RStructBucle(int tipo){
        super(tipo);
    }
}
