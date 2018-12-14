package poodleDeveloper.karel.data.karelmovil;

import java.util.HashSet;

public class KCasilla {
    public int fila;
    public int columna;
    public int zumbadores;
    public HashSet<Integer> paredes;

    public KCasilla(int fila, int columna){
        this.fila = fila;
        this.columna = columna;
        this.zumbadores = 0;
        this.paredes = new HashSet<Integer>();
    }
    public KCasilla(KPosition posicion){
        this.fila = posicion.fila;
        this.columna = posicion.columna;
        this.zumbadores = 0;
        this.paredes = new HashSet<Integer>();
    }
}
