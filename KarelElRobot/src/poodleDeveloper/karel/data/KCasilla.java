package poodleDeveloper.karel.data;


public class KCasilla{ 
	private int fila;
	private int columna;
	private int zumbadores;
	private String[] paredes;
	
	public int getFila() {
		return fila;
	}
	public void setFila(int fila) {
		this.fila = fila;
	}
	public int getColumna() {
		return columna;
	}
	public void setColumna(int columna) {
		this.columna = columna;
	}
	public int getZumbadores() {
		return zumbadores;
	}
	public void setZumbadores(int zumbadores) {
		this.zumbadores = zumbadores;
	}
	public String[] getParedes() {
		return paredes;
	}
	public void setParedes(String paredes[]) {
		this.paredes = paredes;
	}

}
