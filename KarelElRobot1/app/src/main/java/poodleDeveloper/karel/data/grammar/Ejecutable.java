package poodleDeveloper.karel.data.grammar;

import java.util.HashMap;
import java.util.LinkedList;

import poodleDeveloper.karel.data.structs.EndStruct;
import poodleDeveloper.karel.data.structs.RStructFuncion;
import poodleDeveloper.karel.data.structs.RStructInstruccion;
import poodleDeveloper.karel.data.structs.RStructMientras;
import poodleDeveloper.karel.data.structs.RStructRepite;
import poodleDeveloper.karel.data.structs.RStructSi;
import poodleDeveloper.karel.data.structs.RunStruct;
import poodleDeveloper.karel.data.structs.Struct;

public class Ejecutable{
    public HashMap<String, Integer> indiceFunciones; //Indica las posiciones de las funciones en la lista
    public LinkedList<RunStruct> lista; //cinta de la máquina de turing
    public int main; // posicion de la primera instruccion a ejecutar
    public Ejecutable(){
        this.indiceFunciones = new HashMap<String, Integer>();
        this.lista = new LinkedList<RunStruct>();
        this.main = 0;
    }

    public String toString(){
        /* Ejecuta el codigo compilado de Karel en el mundo
        proporcionado, comenzando por el bloque "main" o estructura
        principal. */
        String result = "";
        for(int i=0;i<this.lista.size();i++){
                RunStruct instruccion=this.lista.get(i);
                if(EndStruct.class.isInstance(instruccion)){
                        EndStruct fin = (EndStruct)instruccion;
                        result += i+">FIN ("+fin.inicio+")\n";
                } else if(instruccion.estructura == Struct.ESTRUCTURA_INSTRUCCION){
                        //Es una instruccion predefinida de Karel
                        RStructInstruccion instruccionPredefinida = (RStructInstruccion)instruccion;
                        result += i+">"+instruccionPredefinida.instruccion+"\n";
                } else if (instruccion.estructura == Struct.ESTRUCTURA_SI){ //Se trata de una estructura de control o una funcion definida
                        RStructSi si = (RStructSi)instruccion;
                    result += i+">SI ("+si.finEstructura+")\n";
                } else if (instruccion.estructura == Struct.ESTRUCTURA_SINO) //Llegamos a un sino, procedemos, no hay de otra
                    result += i+">SINO\n";
                else if (instruccion.estructura == Struct.ESTRUCTURA_REPITE){
                        RStructRepite repite = (RStructRepite)instruccion;
                        result += i+">REPITE "+repite.argumento+"\n";
                } else if (instruccion.estructura == Struct.ESTRUCTURA_MIENTRAS){
                        RStructMientras mientras = (RStructMientras)instruccion;
                        result += i+">MIENTRAS "+mientras.finEstructura+"\n";
                } else { //Se trata la llamada a una función
                    RStructFuncion funcion = (RStructFuncion)instruccion;
                    result += i+">"+funcion.nombre+"\n";
                }
        }
        return result;
    }
}
