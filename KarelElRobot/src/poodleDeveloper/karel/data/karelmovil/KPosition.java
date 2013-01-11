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
	public boolean equals(Object k){
		if(!KPosition.class.isInstance(k))
			return false;
		KPosition m = (KPosition)k;
		return (this.fila == m.fila) && (this.columna == m.columna);
	}
}
