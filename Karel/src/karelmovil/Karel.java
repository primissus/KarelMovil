package karelmovil;

public class Karel {
	public KPosition posicion;
	public int mochila;
	public int orientacion;
	public Karel(int fila, int columna, int mochila, int orientacion){
		this.posicion = new KPosition(fila, columna);
		this.mochila = mochila;
		this.orientacion = orientacion;
	}
	public Karel(){
		this.posicion = new KPosition(1, 1);
		this.mochila = 0;
		this.orientacion = KWorld.NORTE;
	}
}
