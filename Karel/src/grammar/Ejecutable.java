package grammar;

import java.util.HashMap;
import java.util.LinkedList;

import structs.RunStruct;

public class Ejecutable{
    public HashMap<String, Integer> indiceFunciones; //Indica las posiciones de las funciones en la lista
    public LinkedList<RunStruct> lista; //cinta de la máquina de turing
    public int main; // posicion de la primera instruccion a ejecutar
    public Ejecutable(){
        this.indiceFunciones = new HashMap<String, Integer>();
        this.lista = new LinkedList<RunStruct>();
        this.main = 0;
    }
}
