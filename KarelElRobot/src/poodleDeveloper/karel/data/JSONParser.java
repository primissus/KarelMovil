package poodleDeveloper.karel.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockActivity;

import poodleDeveloper.karel.KWorld;
import poodleDeveloper.karel.R;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class JSONParser {
	 
	
	private static final String TAG_CASILLAS = "casillas";
	private static final String TAG_FILA = "fila";
	private static final String TAG_COLUMNA = "columna";
	private static final String TAG_ZUMBADORES = "zumbadores";
	private static final String TAG_PAREDES = "paredes";
	private static final String TAG_KAREL = "karel";
	private static final String TAG_POSICION = "posicion";
	private static final String TAG_ORIENTACION = "orientacion";
	private static final String TAG_MOCHILA = "mochila";
	
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    private JSONArray casillas = null;
    private Karel karel = null;
    private JSONObject jsonKarel = null;
    private Context context;
    
    public void init(String url, Context context) throws InterruptedException, ExecutionException{
    	this.context = context;
    	JSONHandler handler = new JSONHandler();
    	handler.execute(url);
    }
    
    class JSONHandler extends AsyncTask<String,Void,Bundle>{
 
		@Override
		protected Bundle doInBackground(String... params) {
	        try {
	        	File directory = Environment.getExternalStorageDirectory();
	        	System.out.println(directory);
	        	File archivo = new File(directory+"/mundo.json");	        	
	        	FileReader fr = new FileReader(archivo);	        	
	            BufferedReader reader = new BufferedReader(fr);
	            StringBuilder sb = new StringBuilder();
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	                sb.append(line + "\n");
	            }
	            json = sb.toString();
	        } catch (Exception e) {
	            Log.e("Buffer Error", "Error converting result " + e.toString());
	        }
	 
	        
	        Bundle bundle = new Bundle();
	        bundle.putString("JSON",  json);
	        return bundle;
		}
		
		@SuppressLint("NewApi") @Override
		protected void onPostExecute(Bundle result) {
			try {
	            jObj = new JSONObject(result.getString("JSON"));
	        } catch (JSONException e) {
	            Log.e("JSON Parser", "Error parsing data " + e.toString());
	        }
			try {
				jsonKarel = jObj.getJSONObject(TAG_KAREL);
				casillas = jObj.getJSONArray(TAG_CASILLAS);
				ArrayList<KCasilla> arregloCasillas = new ArrayList<KCasilla>();
				karel = new Karel();
				JSONArray posicion = jsonKarel.getJSONArray(TAG_POSICION);
				karel.fila = (Integer)posicion.get(0);
				karel.columna = (Integer)posicion.get(1);
				String orientacion = jsonKarel.getString(TAG_ORIENTACION);
				if(orientacion.equals("norte"))
					karel.orientacion = KWorld.NORTE;
				else if(orientacion.equals("este"))
					karel.orientacion = KWorld.ESTE;
				else if(orientacion.equals("sur"))
					karel.orientacion = KWorld.SUR;
				else if(orientacion.equals("oeste"))
					karel.orientacion = KWorld.OESTE;
				
				karel.mochila = jsonKarel.getInt(TAG_MOCHILA);
				
				for(int i = 0; i < casillas.length(); i++){
					JSONObject c = casillas.getJSONObject(i);
					KCasilla casilla = new KCasilla();
					casilla.setFila(c.getInt(TAG_FILA));
					casilla.setColumna(c.getInt(TAG_COLUMNA));
					casilla.setZumbadores(c.getInt(TAG_ZUMBADORES));
					JSONArray aparedes = c.getJSONArray(TAG_PAREDES);
					String[] paredes = new String[aparedes.length()];
					for(int j = 0; j < aparedes.length() ; j++)
						paredes[j] = aparedes.getString(j);
					casilla.setParedes(paredes);
					arregloCasillas.add(casilla);
				}
				((SherlockActivity)context).setContentView(R.layout.activity_main);
				KWorld k = (KWorld)((SherlockActivity)context).findViewById(R.id.surface);
				k.init(context,arregloCasillas,karel);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
    	
    }
}