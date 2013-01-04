/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package karelmovil;

/**
 *
 * @author abraham
 */
public class KToken {
    public String token;
    public int linea;
    public int columna;
    public String posicion;
    public KToken(String token, int linea, int columna, String posicion){
        this.linea = linea;
        this.columna = columna;
        this.token = token;
        this.posicion = posicion;
    }
    
    @Override
    public String toString(){
        return this.token;
    }
}
