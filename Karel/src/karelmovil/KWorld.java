package karelmovil;

import java.util.ArrayList;
import java.util.HashMap;
import json.JSONArray;
import json.JSONException;
import json.JSONObject;

public class KWorld {
	/* Representa el mundo de karel
	 * 
	 */
	public static final int NORTE=0x1;
	public static final int ESTE=0x2;
	public static final int SUR=0x3;
	public static final int OESTE=0x4;
	
	public Karel karel;
	private int filas;
	private int columnas;
	public HashMap<String, KCasilla> casillas;
	
	public int contrario (int cardinal){
	    /* Suena ridículo, pero obtiene el punto cardinal contrario al
	    dado. */
	    switch(cardinal){
	    	case KWorld.NORTE: return KWorld.SUR;
	    	case KWorld.SUR: return KWorld.NORTE;
	    	case KWorld.ESTE: return KWorld.OESTE;
	    	case KWorld.OESTE: return KWorld.ESTE;
	    	default: return 0;
	    }
	}
	
	public KPosition obten_casilla_avance (KPosition casilla, int direccion){
	    /* Obtiene una casilla contigua dada una casilla de inicio y
	    una direccion de avance*/
		switch(direccion){
			case KWorld.NORTE: return new KPosition(casilla.fila+1, casilla.columna);
			case KWorld.SUR: return new KPosition(casilla.fila-1, casilla.columna);
			case KWorld.ESTE: return new KPosition(casilla.fila, casilla.columna+1);
			case KWorld.OESTE: return new KPosition(casilla.fila, casilla.columna-1);
			default: return new KPosition(0, 0);
		}
	}
	
	public int rotado (int cardinal){
	    /* Obtiene la orientación resultado de un gira-izquierda en
	    Karel */
	    switch(cardinal){
	    	case KWorld.NORTE: return KWorld.OESTE;
	    	case KWorld.OESTE: return KWorld.SUR;
	    	case KWorld.SUR: return KWorld.ESTE;
	    	case KWorld.ESTE: return KWorld.NORTE;
	    	default: return 0;
	    }
	}

    public KWorld(int filas, int columnas, Karel karel, HashMap<String, KCasilla> casillas){
    	this.karel = karel;
    	this.filas = filas;
    	this.columnas = columnas;
    	this.casillas = casillas;
    }
    
    public KWorld(){
    	this.karel = new Karel();
    	this.filas = 100;
    	this.columnas = 100;
    	this.casillas = new HashMap<String, KCasilla>();
    }
    
    public void conmuta_pared (KPosition coordenadas, int orientacion){
        /* Agrega una pared al mundo, si es que está permitido, el
        atributo "coordenadas" es una tupla con la fila y columna de la
        casilla afectada, orientacion es una cadena que indica si se pone
        arriba, abajo, a la izquierda o a la derecha. */
    	if(0<coordenadas.fila && coordenadas.fila<this.filas+1 && 0<coordenadas.columna && coordenadas.columna<this.columnas+1){
            boolean agregar = true; //Indica si agregamos o quitamos la pared
            if(this.casillas.containsKey(coordenadas.toString())){
                //Puede existir una pared
            	KCasilla casilla = this.casillas.get(coordenadas.toString());
            	if(this.casillas.get(coordenadas.toString()).paredes.contains(orientacion)){
                    //Ya existe la pared, la quitamos
                    casilla.paredes.remove(orientacion);
                    this.casillas.put(coordenadas.toString(), casilla);
                    agregar = false;
            	} else{ //no existe la pared, la agregamos
                    casilla.paredes.add(orientacion);
                    this.casillas.put(coordenadas.toString(), casilla);
            	}
            } else{
                //No existe el indice, tampoco la pared, asi que se agrega
            	KCasilla nueva_casilla = new KCasilla(coordenadas);
            	nueva_casilla.paredes.add(orientacion);
            	this.casillas.put(coordenadas.toString(), nueva_casilla);
            }
            //Debemos conmutar la pared en la casilla opuesta
            KPosition casilla_opuesta = this.obten_casilla_avance(coordenadas, orientacion);
            int posicion_opuesta = this.contrario(orientacion);
            if (0<casilla_opuesta.fila && casilla_opuesta.fila < this.filas+1 && 0<casilla_opuesta.columna && casilla_opuesta.columna<this.columnas+1){
                //no es una casilla en los bordes
                if (agregar){
                    //Agregamos una pared
                	if(this.casillas.containsKey(casilla_opuesta.toString())){
                        //Del otro lado si existe registro
                		KCasilla casilla = this.casillas.get(casilla_opuesta.toString());
                		casilla.paredes.add(posicion_opuesta);
                		this.casillas.put(casilla_opuesta.toString(), casilla);
                	} else {
                        //Tampoco hay registro del otro lado
                		KCasilla nueva_casilla = new KCasilla(casilla_opuesta);
                		nueva_casilla.paredes.add(posicion_opuesta);
                		this.casillas.put(casilla_opuesta.toString(), nueva_casilla);
                	}
                } else{
                    //quitamos una pared, asumimos que existe el registro
                    //del lado opuesto
                	KCasilla casilla = this.casillas.get(casilla_opuesta.toString());
                	casilla.paredes.remove(casilla_opuesta.toString());
                	this.casillas.put(casilla_opuesta.toString(), casilla);
                }
            }
            //Operaciones de limpieza para ahorrar memoria
            if (! (this.casillas.get(coordenadas.toString()).paredes.size()>0 || this.casillas.get(coordenadas.toString()).zumbadores>0))
            	this.casillas.remove(coordenadas.toString());
            if (! (this.casillas.get(casilla_opuesta.toString()).paredes.size()>0 || this.casillas.get(casilla_opuesta.toString()).zumbadores>0))
            	this.casillas.remove(casilla_opuesta.toString());
    	}
    }

    public void pon_zumbadores (KPosition posicion, int cantidad){
        /* Agrega zumbadores al mundo en la posicion dada */
        if (0<posicion.fila && posicion.fila <(this.filas+1) && 0<posicion.columna && posicion.columna<(this.columnas+1)) {
            if (this.casillas.containsKey(posicion.toString())){
                KCasilla casilla = this.casillas.get(posicion.toString());
                casilla.zumbadores = cantidad;
                this.casillas.put(posicion.toString(), casilla);
            } else {
            	KCasilla casilla = new KCasilla(posicion);
            	casilla.zumbadores = cantidad;
            	this.casillas.put(posicion.toString(), casilla);
            }
            //Limpiamos la memoria si es necesario
            if (! (this.casillas.get(posicion.toString()).paredes.size()>0 || this.casillas.get(posicion.toString()).zumbadores>0))
            	this.casillas.remove(posicion.toString());
        }
    }

    public boolean avanza (){
        /* Determina si puede karel avanzar desde la posición en la que
        se encuentra, de ser posible avanza. Si el parámetro test es
        verdadero solo ensaya. */
        //Determino primero si está en los bordes
        if (this.frente_libre()){
            this.karel.posicion = obten_casilla_avance(this.karel.posicion, this.karel.orientacion);
            return true;
        }else
            return false;
    }

    public void gira_izquierda (){
        /* Gira a Karel 90° a la izquierda, obteniendo una nueva
        orientación. Si el parámetro test es verdadero solo ensaya*/
        this.karel.orientacion = this.rotado(this.karel.orientacion);
    }

    public boolean coge_zumbador (){
        /* Determina si Karel puede coger un zumbador, si es posible lo
        toma, devuelve Falso si no lo logra. Si el parámetro test es
        verdadero solo ensaya. */
        KPosition posicion = this.karel.posicion;
        if (this.junto_a_zumbador()){
            if (this.casillas.get(posicion.toString()).zumbadores == -1){
                if (this.karel.mochila != -1)
                    this.karel.mochila += 1;
            } else if (this.casillas.get(posicion.toString()).zumbadores>0){
                if (this.karel.mochila != -1)
                    this.karel.mochila += 1;
                KCasilla casilla = this.casillas.get(posicion.toString());
                casilla.zumbadores -= 1;
                this.casillas.put(posicion.toString(), casilla);
            }
            //Limpiamos la memoria si es necesario
            if (! (this.casillas.get(posicion.toString()).paredes.size()>0 || this.casillas.get(posicion.toString()).zumbadores>0))
                this.casillas.remove(posicion.toString());
            return true;
        }else
            return false;
    }

    public boolean deja_zumbador (){
        /* Determina si Karel puede dejar un zumbador en la casilla
        actual, si es posible lo deja. Si el parámetro test es verdadero
        solo ensaya  */
        KPosition posicion = this.karel.posicion;
        if (this.algun_zumbador_en_la_mochila()){
            if( this.casillas.containsKey(posicion.toString())){
                if (this.casillas.get(posicion.toString()).zumbadores != -1){
                    this.casillas.get(posicion.toString()).zumbadores += 1;
                }
            }else{
            	KCasilla casilla = new KCasilla(posicion);
            	casilla.zumbadores = 1;
            	this.casillas.put(posicion.toString(), casilla);
            }
            if (this.karel.mochila != -1)
                this.karel.mochila -= 1;
            return true;
        }else
            return false;
    }

    public boolean frente_libre(){
        /* Determina si Karel tiene el frente libre */
        int direccion = this.karel.orientacion;
        KPosition posicion = this.karel.posicion;
        switch(direccion){
        	case KWorld.NORTE:
        		if (posicion.fila == this.filas) return false;
        		break;
        	case KWorld.SUR:
        		if(posicion.fila == 1) return false;
        		break;
        	case KWorld.ESTE:
        		if(posicion.columna == this.columnas) return false;
        		break;
        	case KWorld.OESTE:
        		if(posicion.columna == 1) return false;
        		break;
        }
        if (! this.casillas.containsKey(posicion.toString())){
            return true; //No hay un registro para esta casilla, no hay paredes
        } else {
            if (this.casillas.get(posicion.toString()).paredes.contains(direccion)){
                return false;
            } else {
                return true;
            }
        }
    }

    public boolean izquierda_libre (){
        /* Determina si Karel tiene la izquierda libre */
        int direccion = this.karel.orientacion;
        KPosition posicion = this.karel.posicion;
        
        switch(direccion){
	    	case KWorld.NORTE: if (posicion.columna == 1) return false;
	    	case KWorld.SUR: if(posicion.columna == this.columnas) return false;
	    	case KWorld.ESTE: if(posicion.fila == this.filas) return false;
	    	case KWorld.OESTE: if(posicion.fila == 1) return false;
	    }
        
        if (! this.casillas.containsKey(posicion.toString()))
            return true; //No hay un registro para esta casilla, no hay paredes
        else{
            if (this.casillas.get(posicion.toString()).paredes.contains(rotado(direccion)))
                return false;
            else
                return true;
        }
	}

    public boolean derecha_libre (){
        /* Determina si Karel tiene la derecha libre */
        int direccion = this.karel.orientacion;
        KPosition posicion = this.karel.posicion;
        
        switch(direccion){
	    	case KWorld.NORTE: if (posicion.columna == this.columnas) return false;
	    	case KWorld.SUR: if(posicion.columna == 1) return false;
	    	case KWorld.ESTE: if(posicion.fila == 1) return false;
	    	case KWorld.OESTE: if(posicion.fila == this.filas) return false;
	    }
        
        if (! this.casillas.containsKey(posicion.toString()))
            return true; //No hay un registro para esta casilla, no hay paredes extra
        else{
            if (this.casillas.get(posicion.toString()).paredes.contains(rotado(rotado(rotado(direccion)))))
                return false;
            else
                return true;
        }
    }

    public boolean junto_a_zumbador (){
        /* Determina si Karel esta junto a un zumbador. */
        if (this.casillas.containsKey(this.karel.posicion.toString())){
            if (this.casillas.get(this.karel.posicion.toString()).zumbadores == -1)
                return true;
            else if (this.casillas.get(this.karel.posicion.toString()).zumbadores > 0)
                return true;
            else
                return false;
        }else
            return false;
    }

    public boolean orientado_al(int direccion){
        /* Determina si karel esta orientado al norte */
        if (this.karel.orientacion == direccion)
            return true;
        else
            return false;
    }

    public boolean algun_zumbador_en_la_mochila(){
        /* Determina si karel tiene algun zumbador en la mochila */
        if (this.karel.mochila > 0)
            return true;
        else
            return false;
    }

    public static String convierteOrientacion(int orientacion){
    	switch(orientacion){
    		case KWorld.NORTE: return "norte";
    		case KWorld.ESTE: return "este";
    		case KWorld.SUR: return "sur";
    		case KWorld.OESTE: return "oeste";
    		default: return "";
    	}
    }
    
    public static int convierteOrientacion(String orientacion){
    	if(orientacion.equals("norte"))
    		return KWorld.NORTE;
    	else if(orientacion.equals("este"))
    		return KWorld.ESTE;
    	else if(orientacion.equals("sur"))
    		return KWorld.SUR;
    	else
    		return KWorld.OESTE;
    }
    
    public static ArrayList<String> convierteParedes(ArrayList<Integer> original){
    	ArrayList<String> paredes = new ArrayList<String>();
    	for(int i:original){
    		paredes.add(KWorld.convierteOrientacion(i));
    	}
    	return paredes;
    }

    public static ArrayList<Integer> convierteParedes(JSONArray original) throws JSONException{
    	ArrayList<Integer> paredes = new ArrayList<Integer>();
    	
    	for(int i=0; i<original.length(); i++){
    		paredes.add(KWorld.convierteOrientacion(original.getString(i)));
    	}
    	
    	return paredes;
    }
    
    public JSONObject exporta_mundo () throws JSONException{
        /* Exporta las condiciones actuales del mundo usando algun
        lenguaje de marcado */
    	JSONObject mundo = new JSONObject();
    	
    	JSONObject karel = new JSONObject();
    	ArrayList<Integer> posicion = new ArrayList<Integer>();
    	posicion.add(this.karel.posicion.fila);
    	posicion.add(this.karel.posicion.columna);
    	karel.put("posicion", posicion);
    	karel.put("orientacion", KWorld.convierteOrientacion(this.karel.orientacion));
    	karel.put("mochila", this.karel.mochila);
    	
    	JSONObject dimensiones = new JSONObject();
    	dimensiones.put("filas", this.filas);
    	dimensiones.put("columnas", this.columnas);
    	
    	ArrayList<JSONObject> casillas = new ArrayList<JSONObject>();
    	
    	for(String llave:this.casillas.keySet()){
    		KCasilla valor = this.casillas.get(llave);
    		JSONObject casilla = new JSONObject();
    		casilla.put("fila", valor.fila);
    		casilla.put("columna", valor.columna);
    		casilla.put("zumbadores", this.casillas.get(llave).zumbadores);
    		casilla.put("paredes", KWorld.convierteParedes(this.casillas.get(llave).paredes));
    		
    		casillas.add(casilla);
    	}
        mundo.put("karel", karel);
        mundo.put("dimensiones", dimensiones);
        mundo.put("casillas", new JSONArray(casillas));
        
        return mundo;
    }
   
    public void carga_casillas (JSONArray casillas) throws JSONException{
        /* Carga las casillas de un diccionario dado. */
        this.casillas.clear();
        for(int i=0;i<casillas.length(); i++){
        	JSONObject casillaObj = casillas.getJSONObject(i);
        	
        	KPosition llave = new KPosition(casillaObj.getInt("fila"), casillaObj.getInt("columna"));
        	KCasilla casilla = new KCasilla(llave);
        	casilla.zumbadores = casillaObj.getInt("zumbadores");
        	casilla.paredes = KWorld.convierteParedes(casillaObj.getJSONArray("paredes"));
        	
        	this.casillas.put(llave.toString(), casilla);
        }
    }

    public void cargaJSON (JSONObject mundo) throws JSONException{
        /* Carga el contenido de un archivo con la configuración del
        mundo. Archivo debe ser una estancia de "file" o de un objeto
        con metodo "read()"*/
        //Lo cargamos al interior
    	this.karel.posicion = new KPosition(mundo.getJSONObject("karel").getJSONArray("posicion").getInt(0), mundo.getJSONObject("karel").getJSONArray("posicion").getInt(1));
    	this.karel.orientacion = KWorld.convierteOrientacion(mundo.getJSONObject("karel").getString("orientacion"));
    	this.karel.mochila = mundo.getJSONObject("karel").getInt("mochila");
    	
    	this.filas = mundo.getJSONObject("dimensiones").getInt("filas");
    	this.columnas = mundo.getJSONObject("dimensiones").getInt("columnas");
    	
        this.carga_casillas(mundo.getJSONArray("casillas"));
    }

    public void limpiar (){
        /* Limpia el mundo y lo lleva a un estado inicial */
    	this.karel = new Karel();
    	this.casillas.clear();
    }
}
