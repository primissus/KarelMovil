package karelmovil;

public class KPosition {
	public int fila;
	public int columna;
	
	public KPosition(int fila, int columna){
		this.fila = fila;
		this.columna = columna;
	}
	public String toString(){
		return "("+this.fila+","+this.columna+")";
	}
}
