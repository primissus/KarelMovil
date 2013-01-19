/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package karelmovil;

/**
 *
 * @author abraham
 */

import grammar.Ejecutable;

import java.io.*;

import json.JSONException;
import json.JSONObject;

public class KarelMovil {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        String nombre_archivo = "/home/abraham/codigo.karel";
//        File archivo = new File(nombre_archivo);
//        KGrammar k;
//        try{
//            FileReader fr = new FileReader(archivo);
//            BufferedReader br_ = new BufferedReader(fr);
//            k = new KGrammar(br_, true, true);
//            try{
//                k.verificar_sintaxis();
//                System.out.println("Sintaxis correcta");
//                Ejecutable exe = k.expandir_arbol();
//                
//                KWorld mundo = new KWorld();
//                
//                KRunner runner = new KRunner(exe, mundo, 200, 7, 200);
//                runner.run();
//                if(runner.estado == KRunner.ESTADO_OK){
//                	System.out.println("Programa ejecutado");
//                } else {
//                	System.out.println("ERROR: "+runner.mensaje);
//                }
//                try{
//                	KWorld w = runner.getMundo();
//                	JSONObject cosa = w.exporta_mundo();
//                	String res = cosa.toString();
//                	
//                	File f = new File("/home/abraham/resultado.json");
//                	FileWriter fw = new FileWriter(f);
//                	fw.write(res);
//                	fw.close();
//                	System.out.println("Mundo exportado");
//                	System.out.println("Karel está en la "+w.karel.posicion.fila+","+w.karel.posicion.columna);
//                } catch (JSONException e){
//                	System.out.println(e.getMessage());
//                } catch (IOException e){
//                	System.out.println(e.getMessage());
//                }
//            }catch(KarelException e){
//                System.out.println(e.getMessage()+" en la línea "+k.token_actual.linea+" columna "+k.token_actual.columna);
//            }
//        } catch(FileNotFoundException e){
//            System.out.println("Archivo no encontrado");
//        }
        KWorld mundo = new KWorld();
        mundo.conmuta_pared(new KPosition(2, 3), KWorld.NORTE);
        mundo.conmuta_pared(new KPosition(2, 3), KWorld.NORTE);
        mundo.karel.posicion.fila = 2;
        mundo.karel.posicion.columna = 3;
        System.out.println(mundo.avanza());
        System.out.println("hola");
    }
}
