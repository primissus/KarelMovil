package karelmovil;

import java.util.ArrayList;

public class KCasilla {
	public int fila;
	public int columna;
	public int zumbadores;
	public ArrayList<Integer> paredes;
	
	public KCasilla(int fila, int columna){
		this.fila = fila;
		this.columna = columna;
		this.zumbadores = 0;
		this.paredes = new ArrayList<Integer>();
	}
	public KCasilla(KPosition posicion){
		this.fila = posicion.fila;
		this.columna = posicion.columna;
		this.zumbadores = 0;
		this.paredes = new ArrayList<Integer>();
	}
}
