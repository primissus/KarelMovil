package poodleDeveloper.karel.data;

import poodleDeveloper.karel.KWorld;

public class Karel {
	public int fila;
	public int columna;
	public int orientacion;
	public int mochila;
	
	public Karel(int fila, int columna, int orientacion, int mochila){
		this.fila = fila;
		this.columna = columna;
		this.orientacion = orientacion;
		this.mochila = mochila;
	}
	
	public Karel(){
		fila = columna = 1;
		mochila = 0;
		orientacion = KWorld.NORTE;
	}
}
