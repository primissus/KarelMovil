/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package karelmovil;

/**
 *
 * @author abraham
 */

import java.io.*;

public class KarelMovil {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        
        System.out.println("Escribe la ruta del archivo:");
        String nombre_archivo = "";
        try{
            nombre_archivo = br.readLine();
        } catch(IOException e){
            System.out.println("Un pinchi error raro");
        }
        File archivo = new File(nombre_archivo);
        KGrammar k;
        try{
            FileReader fr = new FileReader(archivo);
            BufferedReader br_ = new BufferedReader(fr);
            k = new KGrammar(br_, true);
            try{
                k.verificar_sintaxis();
                System.out.println("Sintaxis correcta");
                k.expandir_arbol();
            }catch(KarelException e){
                System.out.println(e.getMessage()+" en la línea "+k.token_actual.linea+" columna "+k.token_actual.columna);
            }
        } catch(FileNotFoundException e){
            
        }
    }
}
