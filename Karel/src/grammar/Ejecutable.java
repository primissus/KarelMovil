package grammar;

import java.util.HashMap;
import java.util.LinkedList;


import structs.EndStruct;
import structs.RStructFuncion;
import structs.RStructInstruccion;
import structs.RStructMientras;
import structs.RStructRepite;
import structs.RStructSi;
import structs.RunStruct;
import structs.Struct;

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
	        	result += "FIN "+fin.estructura+" >"+fin.inicio+"\n";
	        } else if(instruccion.estructura == Struct.ESTRUCTURA_INSTRUCCION){
	        	//Es una instruccion predefinida de Karel
	        	RStructInstruccion instruccionPredefinida = (RStructInstruccion)instruccion;
	        	result += ">"+instruccionPredefinida.instruccion+"\n";
	        }else if (instruccion.estructura == Struct.ESTRUCTURA_SI){ //Se trata de una estructura de control o una funcion definida
	        	RStructSi si = (RStructSi)instruccion;
	            result += "SI "+si.argumentoLogico+"\n";
	        } else if (instruccion.estructura == Struct.ESTRUCTURA_SINO) //Llegamos a un sino, procedemos, no hay de otra
	            result += "SINO\n";
	        else if (instruccion.estructura == Struct.ESTRUCTURA_REPITE){
	        	RStructRepite repite = (RStructRepite)instruccion;
	        	result += "REPITE "+repite.argumento+"\n";
	        } else if (instruccion.estructura == Struct.ESTRUCTURA_MIENTRAS){
	        	RStructMientras mientras = (RStructMientras)instruccion;
	        	result += "MIENTRAS "+mientras.argumentoLogico+"\n";
	        } else { //Se trata la llamada a una función
	            RStructFuncion funcion = (RStructFuncion)instruccion;
	            result += ">"+funcion.nombre+"\n";
	        }
    	}
    	return result;
    }
}
