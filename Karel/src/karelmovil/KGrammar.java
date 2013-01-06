package karelmovil;

/**
 *
 * @author abraham
 */

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Iterator;
import structs.EndStruct;
import structs.RStructFuncion;
import structs.RStructInstruccion;
import structs.RStructMientras;
import structs.RStructRepite;
import structs.RStructSi;
import structs.RStructSino;
import structs.RunStruct;
import structs.Struct;
import structs.StructFuncion;
import structs.StructInstruccion;
import structs.StructMientras;
import structs.StructRepite;
import structs.StructSi;
import grammar.*;

public class KGrammar {
    /*
    Clase que contiene y conoce la gramatica de karel
    */
    public String instrucciones[] = {"avanza", "gira-izquierda", "coge-zumbador", "deja-zumbador", "apagate", "sal-de-instruccion", "sal-de-bucle"};
    public String condiciones[] = {
        "frente-libre",
        "frente-bloqueado",
        "derecha-libre",
        "derecha-bloqueada",
        "izquierda-libre",
        "izquierda-bloqueada",
        "junto-a-zumbador",
        "no-junto-a-zumbador",
        "algun-zumbador-en-la-mochila",
        "ningun-zumbador-en-la-mochila",
        "orientado-al-norte",
        "no-orientado-al-norte",
        "orientado-al-este",
        "no-orientado-al-este",
        "orientado-al-sur",
        "no-orientado-al-sur",
        "orientado-al-oeste",
        "no-orientado-al-oeste",
        "si-es-cero",
        "verdadero", //Reservadas para futuros usos
        "falso" //reservadas para futuros usos
    };
    public String expresionesEnteras[] = {"sucede", "precede"};
    public String estructuras[] = {"si", "mientras", "repite", "repetir"};
    public String palabrasReservadas[];
    
    public KLexer lexer;
    public KToken token_actual;
    
    public HashMap<String, LinkedList<String>> prototipoFunciones;
    public HashMap<String, LinkedList<IntExpr>> funciones;
    
    public boolean futuro;
    private Arbol arbol;
    private LinkedList<RunStruct> listaPrograma;
    private Ejecutable ejecutable;
    
    public KGrammar(BufferedReader flujo, boolean futuro){
        /* Inicializa la gramatica:
        flujo       indica el torrente de entrada
        archivo     es el nombre del archivo fuente, si existe
        debug       indica si es necesario imprimir mensajes para debug
        gen_arbol   indica si hay que compilar
        futuro      indica si se pueden usar caracteristicas del futuro
                    de Karel como las condiciones "falso" y "verdadero"*/
        
        if (! futuro){
            this.condiciones = new String[]{
                "frente-libre",
                "frente-bloqueado",
                "derecha-libre",
                "derecha-bloqueada",
                "izquierda-libre",
                "izquierda-bloqueada",
                "junto-a-zumbador",
                "no-junto-a-zumbador",
                "algun-zumbador-en-la-mochila",
                "ningun-zumbador-en-la-mochila",
                "orientado-al-norte",
                "no-orientado-al-norte",
                "orientado-al-este",
                "no-orientado-al-este",
                "orientado-al-sur",
                "no-orientado-al-sur",
                "orientado-al-oeste",
                "no-orientado-al-oeste",
                "si-es-cero",
                "verdadero", //Reservadas para futuros usos
                "falso" //reservadas para futuros usos
            };
            this.instrucciones = new String[]{"avanza", "gira-izquierda", "coge-zumbador", "deja-zumbador", "apagate", "sal-de-instruccion", "sal-de-bucle"};
        }
        String palabras_clave[]= {
            "iniciar-programa",
            "inicia-ejecucion",
            "termina-ejecucion",
            "finalizar-programa",
            "no",
            "y",
            "o",
            "define-nueva-instruccion",
            "define-prototipo-instruccion",
            "inicio",
            "fin",
            "hacer",
            "veces",
            "entonces",
            "sino"
        };
        this.palabrasReservadas = new String[palabras_clave.length+this.instrucciones.length+this.condiciones.length+this.expresionesEnteras.length+this.estructuras.length];
        
        int i=0;
        for(int j=0;j<palabras_clave.length;j++){
            this.palabrasReservadas[i] = palabras_clave[j];
            i++;
        }
        for(int j=0;j<this.instrucciones.length;j++){
            this.palabrasReservadas[i] = this.instrucciones[j];
            i++;
        }
        for(int j=0;j<this.condiciones.length;j++){
            this.palabrasReservadas[i] = this.condiciones[j];
            i++;
        }
        for(int j=0;j<this.expresionesEnteras.length;j++){
            this.palabrasReservadas[i] = this.expresionesEnteras[j];
            i++;
        }
        for(int j=0;j<this.estructuras.length;j++){
            this.palabrasReservadas[i] = this.estructuras[j];
            i++;
        }
        
        this.lexer = new KLexer(flujo, false);
        try{
            this.token_actual = this.lexer.get_token();
        } catch(KarelException e){
            System.out.println(e.getMessage());
            System.exit(1);
        }
        
        this.prototipoFunciones = new HashMap<String, LinkedList<String>>();
        this.funciones = new HashMap<String, LinkedList<IntExpr>>();
        this.arbol = new Arbol();
        this.ejecutable = new Ejecutable();
        // Un diccionario que tiene por llaves los nombres de las funciones
        // y que tiene por valores listas con las variables de dichas
        // funciones
        this.listaPrograma = new LinkedList<RunStruct>();
        // Una lista que puede contener el árbol expandido con las instrucciones
        // del programa de forma adecuada
        this.futuro = futuro;
    }

    public static boolean in_array(Object val, Object[] arr){
        /* Indica cuando val se encuentra en arr
         * 
         */
        for(Object elem:arr){
            if(elem.equals(val)){
                return true;
            }
        }
        return false;
    }
    
    public static boolean in_array(char val, char[] arr){
        /* Indica cuando val se encuentra en arr
         * 
         */
        for(char elem:arr){
            if(elem == val)
                return true;
        }
        return false;
    }
    
    public static boolean in_array(int val, int[] arr){
        /* Indica cuando val se encuentra en arr
         * 
         */
        for(int elem:arr){
            if(elem == val)
                return true;
        }
        return false;
    }
    
    public boolean contains(String name, LinkedList<IntExpr> lista){
    	Iterator<IntExpr> iterador = lista.iterator();
    	while(iterador.hasNext()){
    		IntExpr elemento = iterador.next();
    		if(elemento.tipo == IntExpr.VALOR_IDENTIFICADOR){
    			if(elemento.valorIdentificador.equals(name))
    				return true;
    		}
    	}
    	return false;
    }
    
    private boolean avanza_token () throws KarelException{
        /* Avanza un token en el archivo */
        KToken siguiente_token = this.lexer.get_token();

        if (!siguiente_token.token.equals("")){
            this.token_actual = siguiente_token;
            return true;
        }else
            return false;
    }

    private void bloque() throws KarelException{
        /*
        Define un bloque en la sitaxis de karel
        {BLOQUE ::=
                [DeclaracionDeProcedimiento ";" | DeclaracionDeEnlace ";"] ...
                "INICIA-EJECUCION"
                   ExpresionGeneral [";" ExpresionGeneral]...
                "TERMINA-EJECUCION"
        }
        Un bloque se compone de todo el codigo admitido entre iniciar-programa
        y finalizar-programa
        */

        while (this.token_actual.token.equals("define-nueva-instruccion") || this.token_actual.token.equals("define-prototipo-instruccion") || this.token_actual.token.equals("externo")){
            if (this.token_actual.token.equals("define-nueva-instruccion"))
                this.declaracion_de_procedimiento();
            else if (this.token_actual.token.equals("define-prototipo-instruccion"))
                this.declaracion_de_prototipo();
            else //Se trata de una declaracion de enlace
                this.declaracion_de_enlace();
        }
        //Toca verificar que todos los prototipos se hayan definido
        Object llaves_prototipos[] = this.prototipoFunciones.keySet().toArray();
        for(Object llave:llaves_prototipos)
            if(!this.funciones.containsKey((String)llave))
                throw new KarelException("La instrucción "+(String)llave+" tiene prototipo pero no fue definida");
        //Sigue el bloque con la lógica del programa
        if (this.token_actual.token.equals("inicia-ejecucion")){
            this.avanza_token();
            this.arbol.main = this.expresion_general(new LinkedList<IntExpr>(), false, false);
            if (!this.token_actual.token.equals("termina-ejecucion"))
                throw new KarelException("Se esperaba 'termina-ejecucion al final del bloque lógico del programa, encontré"+this.token_actual);
            else
                this.avanza_token();
        }
    }

    private LogicAtomic clausula_atomica(LinkedList<IntExpr> lista_variables) throws KarelException{
        /*
        Define una clausila atomica
        {
        ClausulaAtomica ::=  {
                              "SI-ES-CERO" "(" ExpresionEntera ")" | 
                              FuncionBooleana | 
                              "(" Termino ")" 
                             }{
        }
        */
        LogicAtomic retornar_valor = new LogicAtomic();

        if (this.token_actual.token.equals("si-es-cero")){
            this.avanza_token();
            if (this.token_actual.token.equals("(")){
                this.avanza_token();
                retornar_valor.crearSiEsCero(this.expresion_entera(lista_variables));
                if (this.token_actual.token.equals(")"))
                    this.avanza_token();
                else
                    throw new KarelException("Se esperaba ')'");
            }else
                throw new KarelException("Se esperaba '('");
        }else if (this.token_actual.token.equals("(")){
            this.avanza_token();
            retornar_valor.crearTermino(this.termino(lista_variables));
            if (this.token_actual.token.equals(")"))
                this.avanza_token();
            else
                throw new KarelException("Se esperaba ')'");
        }else{
            retornar_valor.crearExpresionBooleana(this.funcion_booleana());
        }
        
        return retornar_valor;
    }

    private LogicNo clausula_no(LinkedList<IntExpr> lista_variables) throws KarelException{
        /*
        Define una clausula de negacion
        {
            ClausulaNo ::= ["NO"] ClausulaAtomica
        }
        */
        LogicNo retornar_valor = new LogicNo();

        if (this.token_actual.token.equals("no")){
            this.avanza_token();
            retornar_valor.crearNo(this.clausula_atomica(lista_variables));
        }else
            retornar_valor.crear(this.clausula_atomica(lista_variables));

        return retornar_valor;
    }

    private LogicY clausula_y(LinkedList<IntExpr> lista_variables) throws KarelException{
        /*
        Define una clausula conjuntiva
        {
            ClausulaY ::= ClausulaNo ["Y" ClausulaNo]...
        }
        */
        LogicY retornar_valor = new LogicY();
        retornar_valor.argumento.add(this.clausula_no(lista_variables));

        while (this.token_actual.token.equals("y")){
            this.avanza_token();
            retornar_valor.argumento.add(this.clausula_no(lista_variables));
        }

        return retornar_valor;
    }

    private void declaracion_de_procedimiento() throws KarelException{
        /*
        Define una declaracion de procedimiento
        {
            DeclaracionDeProcedimiento ::= "DEFINE-NUEVA-INSTRUCCION" Identificador ["(" Identificador ")"] "COMO"
                                         Expresion
        }
        Aqui se definen las nuevas funciones que extienden el lenguaje
        de Karel, como por ejemplo gira-derecha.
        */

        this.avanza_token();

        boolean requiere_parametros = false; //Indica si la funcion a definir tiene parametros
        String nombre_funcion = "";

        if (KGrammar.in_array(this.token_actual.token, this.palabrasReservadas) || (! this.es_identificador_valido(this.token_actual)))
            throw new KarelException("Se esperaba un nombre de procedimiento válido, '"+this.token_actual+"' no lo es");

        if (this.funciones.containsKey(this.token_actual.token))
            throw new KarelException("Ya se ha definido una funcion con el nombre '"+this.token_actual+"'");
        else{
            this.funciones.put(this.token_actual.token, new LinkedList<IntExpr>());
            nombre_funcion = this.token_actual.token;
        }

        FunctionDef definicion_funcion = new FunctionDef(nombre_funcion);
        this.arbol.funciones.put(nombre_funcion, definicion_funcion);

        this.avanza_token();

        if (this.token_actual.token.equals("como"))
            this.avanza_token();
        else if (this.token_actual.token.equals("(")){
            this.avanza_token();
            requiere_parametros = true;
            while (true){
                if (! this.es_identificador_valido(this.token_actual))
                    throw new KarelException("Se esperaba un nombre de variable, '"+this.token_actual+"' no es válido");
                else{
                    if(this.funciones.get(nombre_funcion).contains(this.token_actual.token))
                        throw new KarelException("La funcion '"+nombre_funcion+"' ya tiene un parámetro con el nombre '"+this.token_actual+"'");
                    else{
                        LinkedList<IntExpr> params = this.funciones.get(nombre_funcion);
                        IntExpr param = new IntExpr();
                        param.crearIdentificador(this.token_actual.token);
                        params.add(param);
                        this.funciones.put(nombre_funcion, params);
                        this.avanza_token();
                    }

                    if (this.token_actual.token.equals(")")){
                        this.lexer.push_token(this.token_actual); //Devolvemos el token a la pila
                        break;
                    }else if (this.token_actual.token.equals(","))
                        this.avanza_token();
                    else
                        throw new KarelException("Se esperaba ',', encontré '"+this.token_actual+"'");
                }
            }
            FunctionDef funcion = this.arbol.funciones.get(nombre_funcion);
            funcion.params = this.funciones.get(nombre_funcion);
            this.arbol.funciones.put(nombre_funcion, funcion);
        }else
            throw new KarelException("Se esperaba la palabra clave 'como' o un parametro");

        if (requiere_parametros){
            this.avanza_token();
            if (!this.token_actual.token.equals(")"))
                throw new KarelException("Se esperaba ')'");
            this.avanza_token();
            if (!this.token_actual.token.equals("como"))
                throw new KarelException("se esperaba la palabra clave 'como'");
            this.avanza_token();
        }

        if (this.prototipoFunciones.containsKey(nombre_funcion)) //Hay que verificar que se defina como se planeó
            if (this.prototipoFunciones.get(nombre_funcion).size() != this.funciones.get(nombre_funcion).size())
                throw new KarelException("La función '"+nombre_funcion+"' no está definida como se planeó en el prototipo, verifica el número de variables");
        
        FunctionDef funcion = this.arbol.funciones.get(nombre_funcion);
        funcion.cola = this.expresion(this.funciones.get(nombre_funcion), true, false);
        this.arbol.funciones.put(nombre_funcion, funcion);

        if (!this.token_actual.token.equals(";"))
            throw new KarelException("Se esperaba ';'");
        else
            this.avanza_token();
    }

    private void declaracion_de_prototipo() throws KarelException{
        /*
        Define una declaracion de prototipo
        {
            DeclaracionDePrototipo ::= "DEFINE-PROTOTIPO-INSTRUCCION" Identificador ["(" Identificador ")"]
        }
        Los prototipos son definiciones de funciones que se hacen previamente
        para poderse utilizar dentro de una función declarada antes.
        */

        boolean requiere_parametros = false;
        String nombre_funcion = "";
        this.avanza_token();

        if (! this.es_identificador_valido(this.token_actual))
            throw new KarelException("Se esperaba un nombre de función, '"+this.token_actual+"' no es válido");
        if (this.prototipoFunciones.containsKey(this.token_actual.token))
            throw new KarelException("Ya se ha definido un prototipo de funcion con el nombre '"+this.token_actual+"'");
        else{
            this.prototipoFunciones.put(this.token_actual.token, new LinkedList<String>());
            nombre_funcion = this.token_actual.token;
        }

        this.avanza_token();

        if (this.token_actual.token.equals(";"))
            this.avanza_token();
        else if (this.token_actual.token.equals("(")){
            this.avanza_token();
            requiere_parametros = true;
            while (true){
                if (!this.es_identificador_valido(this.token_actual))
                    throw new KarelException("Se esperaba un nombre de variable, '"+this.token_actual+"' no es válido");
                else{
                    if(this.prototipoFunciones.get(nombre_funcion).contains(this.token_actual.token))
                        throw new KarelException("El prototipo de función '"+nombre_funcion+"' ya tiene un parámetro con el nombre '"+this.token_actual+"'");
                    else{
                        LinkedList<String> funcion = this.prototipoFunciones.get(nombre_funcion);
                        funcion.add(this.token_actual.token);
                        this.prototipoFunciones.put(nombre_funcion, funcion);
                        this.avanza_token();
                    }

                    if (this.token_actual.token.equals(")")){
                        this.lexer.push_token(this.token_actual); //Devolvemos el token a la pila
                        break;
                    }else if (this.token_actual.token.equals(","))
                        this.avanza_token();
                    else
                        throw new KarelException("Se esperaba ',', encontré '"+this.token_actual+"'");
                }
            }
        }else
            throw new KarelException("Se esperaba ';' o un parámetro");

        if (requiere_parametros){
            this.avanza_token();
            if (!this.token_actual.token.equals(")"))
                throw new KarelException("Se esperaba ')'");
            this.avanza_token();
            if (!this.token_actual.token.equals(";"))
                throw new KarelException("Se esperaba ';'");
            this.avanza_token();
        }
    }

    private void declaracion_de_enlace (){
        /* Se utilizara para tomar funciones de librerias externas,
        aun no implementado*/
    }

    private LinkedList<Struct> expresion(LinkedList<IntExpr> lista_variables, boolean c_funcion, boolean c_bucle) throws KarelException{
        /*
        Define una expresion
        {
        Expresion :: = {
                          "apagate"
                          "gira-izquierda"
                          "avanza"
                          "coge-zumbador"
                          "deja-zumbador"
                          "sal-de-instruccion"
                          ExpresionLlamada
                          ExpresionSi
                          ExpresionRepite
                          ExpresionMientras
                          "inicio"
                              ExpresionGeneral [";" ExpresionGeneral] ...
                          "fin"
                       }{
        }
        Recibe para comprobar una lista con las variables válidas en
        este contexto, tambien comprueba mediante c_funcion si esta en
        un contexto donde es valido el sal-de-instruccion.
        */
        LinkedList<Struct> retornar_valor = new LinkedList<Struct>();

        if(KGrammar.in_array(this.token_actual.token, this.instrucciones)){
            if (this.token_actual.token.equals("sal-de-instruccion")){
                if (c_funcion){
                    StructInstruccion ins = new StructInstruccion(this.token_actual.token);
                    retornar_valor.add(ins);
                    this.avanza_token();
                }else
                    throw new KarelException("No es posible usar 'sal-de-instruccion' fuera de una instruccion :)");
            }else if (this.token_actual.token.equals("sal-de-bucle")){
                if (c_bucle){
                    StructInstruccion ins = new StructInstruccion(this.token_actual.token);
                    retornar_valor.add(ins);
                    this.avanza_token();
                }else
                    throw new KarelException("No es posible usar 'sal-de-bucle' fuera de un bucle :)");
            }else{
                StructInstruccion ins = new StructInstruccion(this.token_actual.token);
                retornar_valor.add(ins);
                this.avanza_token();
            }
        }else if (this.token_actual.token.equals("si"))
            retornar_valor.add(this.expresion_si(lista_variables, c_funcion, c_bucle));
        else if (this.token_actual.token.equals("mientras"))
            retornar_valor.add(this.expresion_mientras(lista_variables, c_funcion));
        else if (this.token_actual.token.equals("repite") || this.token_actual.token.equals("repetir"))
            retornar_valor.add(this.expresion_repite(lista_variables, c_funcion));
        else if (this.token_actual.token.equals("inicio")){
            this.avanza_token();
            retornar_valor = this.expresion_general(lista_variables, c_funcion, c_bucle);
            if (this.token_actual.token.equals("fin"))
                this.avanza_token();
            else
                throw new KarelException("Se esperaba 'fin' para concluir el bloque, encontré '"+this.token_actual+"'");
        }else if (this.es_identificador_valido(this.token_actual)){
            //Se trata de una instrucción creada por el usuario
            if (this.prototipoFunciones.containsKey(this.token_actual.token) || this.funciones.containsKey(this.token_actual.token)){
                String nombre_funcion = this.token_actual.token;
                
                StructFuncion funcion = new StructFuncion(nombre_funcion, new LinkedList<IntExpr>(), new LinkedList<Struct>());
                
                retornar_valor.add(funcion);
                this.avanza_token();
                int num_parametros = 0;
                if (this.token_actual.token.equals("(")){
                    this.avanza_token();
                    while (true){
                        StructFuncion elemento = (StructFuncion)retornar_valor.get(0);
                        elemento.argumentoFuncion.add(this.expresion_entera(lista_variables));
                        retornar_valor.set(0, elemento);
                        num_parametros ++;
                        if (this.token_actual.token.equals(")"))
                            break;
                        else if (this.token_actual.token.equals(","))
                            this.avanza_token();
                        else
                            throw new KarelException("Se esperaba ',', encontré '"+this.token_actual+"'");
                    }
                    if ((! this.futuro) && num_parametros>1)
                        throw new KarelException("No están habilitadas las funciones con varios parámetros, bienvenido al presente");
                    this.avanza_token();
                }
                if (this.prototipoFunciones.containsKey(nombre_funcion)){
                    if (num_parametros != this.prototipoFunciones.get(nombre_funcion).size())
                        throw new KarelException("Estas intentando llamar la funcion '"+nombre_funcion+"' con "+num_parametros+" parámetros, pero así no fue definida");
                } else {
                    if (num_parametros != this.funciones.get(nombre_funcion).size())
                        throw new KarelException("Estas intentando llamar la funcion '"+nombre_funcion+"' con "+num_parametros+" parámetros, pero así no fue definida");
                }
            }else
                throw new KarelException("La instrucción '"+this.token_actual+"' no ha sido previamente definida, pero es utilizada");
        }else
            throw new KarelException("Se esperaba un procedimiento, '"+this.token_actual+"' no es válido");

        return retornar_valor;
    }

    private IntExpr expresion_entera(LinkedList<IntExpr> lista_variables) throws KarelException{
        /*
        Define una expresion numerica entera
        {
            ExpresionEntera ::= { Decimal | Identificador | "PRECEDE" "(" ExpresionEntera ")" | "SUCEDE" "(" ExpresionEntera ")" }{
        }
        */
        IntExpr retornar_valor = new IntExpr();
        //En este punto hay que verificar que se trate de un numero entero
        boolean es_numero = false;
        if (this.es_numero(this.token_actual)){
        	retornar_valor.crearDecimal(Integer.parseInt(this.token_actual.token));
            es_numero = true;
        }else{
            //No era un entero
            if (KGrammar.in_array(this.token_actual, this.expresionesEnteras)){
            	//Precede o sucede
            	if(this.token_actual.token.equals("precede"))
            		retornar_valor.crearPrecede(null);
            	else
            		retornar_valor.crearSucede(null);
                this.avanza_token();
                if (this.token_actual.token.equals("(")){
                    this.avanza_token();
                    //retornar_valor[retornar_valor.keys()[0]] = this.expresion_entera(lista_variables);
                    retornar_valor.argumento = this.expresion_entera(lista_variables);
                    if (this.token_actual.token.equals(")"))
                        this.avanza_token();
                    else
                        throw new KarelException("Se esperaba ')'");
                }else
                    throw new KarelException("Se esperaba '('");
            }else if (this.es_identificador_valido(this.token_actual)){
                //Se trata de una variable definida por el usuario
                if (!this.contains(this.token_actual.token, lista_variables))
                    throw new KarelException("La variable '"+this.token_actual+"' no está definida en este contexto");
                retornar_valor.crearIdentificador(this.token_actual.token);
                this.avanza_token();
            }else
                throw new KarelException("Se esperaba un entero, variable, sucede o predece, '"+this.token_actual+"' no es válido");
        }
        if (es_numero) //Si se pudo convertir, avanzamos
            this.avanza_token();

        return retornar_valor;
    }

    private LinkedList<Struct> expresion_general(LinkedList<IntExpr> lista_variables, boolean c_funcion, boolean c_bucle) throws KarelException{
        /*
        Define una expresion general
        { Expresion | ExpresionVacia }
        Generalmente se trata de una expresión dentro de las etiquetas
        "inicio" y "fin" o entre "inicia-ejecucion" y "termina-ejecucion"
        */
        LinkedList<Struct> retornar_valor = new LinkedList<Struct>(); //Una lista de funciones

        while ((!this.token_actual.token.equals("fin")) && (!this.token_actual.token.equals("termina-ejecucion"))){
            retornar_valor.addAll(this.expresion(lista_variables, c_funcion, c_bucle));
            if ((!this.token_actual.token.equals(";")) && (!this.token_actual.token.equals("fin")) && (!this.token_actual.token.equals("termina-ejecucion")))
                throw new KarelException("Se esperaba ';'");
            else if (this.token_actual.token.equals(";"))
                this.avanza_token();
            else if (this.token_actual.token.equals("fin"))
                throw new KarelException("Se esperaba ';'");
            else if (this.token_actual.token.equals("termina-ejecucion"))
                throw new KarelException("Se esperaba ';'");
        }
        return retornar_valor;
    }

    private Struct expresion_mientras(LinkedList<IntExpr> lista_variables, boolean c_funcion) throws KarelException{
        /*
        Define la expresion del bucle MIENTRAS
        {
        ExpresionMientras ::= "Mientras" Termino "hacer"
                                  Expresion
        }
        */
    	StructMientras retornar_valor = new StructMientras();
        this.avanza_token();

        retornar_valor.argumentoLogico = this.termino(lista_variables);

        if (!this.token_actual.token.equals("hacer"))
            throw new KarelException("Se esperaba 'hacer'");
        this.avanza_token();
        retornar_valor.cola = this.expresion(lista_variables, c_funcion, true);

        return retornar_valor;
    }

    private Struct expresion_repite(LinkedList<IntExpr> lista_variables, boolean c_funcion) throws KarelException{
        /*
        Define la expresion del bucle REPITE
        {
        ExpresionRepite::= "repetir" ExpresionEntera "veces"
                              Expresion
        }
        */
    	StructRepite retornar_valor = new StructRepite();

        this.avanza_token();
        retornar_valor.argumentoEntero = this.expresion_entera(lista_variables);

        if (!this.token_actual.token.equals("veces"))
            throw new KarelException("Se esperaba la palabra 'veces', '"+this.token_actual+"' no es válido");

        this.avanza_token();
        retornar_valor.cola = this.expresion(lista_variables, c_funcion, true);

        return retornar_valor;
    }

    private Struct expresion_si(LinkedList<IntExpr> lista_variables, boolean c_funcion, boolean c_bucle) throws KarelException{
        /*
        Define la expresion del condicional SI
        {
        ExpresionSi ::= "SI" Termino "ENTONCES"
                             Expresion
                        ["SINO"
                               Expresion
                        ]
        }
        */
    	StructSi retornar_valor = new StructSi();

        this.avanza_token();

        retornar_valor.argumentoLogico = this.termino(lista_variables);

        if (!this.token_actual.token.equals("entonces"))
            throw new KarelException("Se esperaba 'entonces'");

        this.avanza_token();

        retornar_valor.cola = this.expresion(lista_variables, c_funcion, c_bucle);

        if (this.token_actual.token.equals("sino")){
            this.avanza_token();
            retornar_valor.colaSino = this.expresion(lista_variables, c_funcion, c_bucle);
        }

        return retornar_valor;
    }

    private String funcion_booleana() throws KarelException{
        /*
        Define una funcion booleana del mundo de karel
        {
        FuncionBooleana ::= {
                               "FRENTE-LIBRE"
                               "FRENTE-BLOQUEADO"
                               "DERECHA-LIBRE"
                               "DERECHA-BLOQUEADA"
                               "IZQUIERAD-LIBRE"
                               "IZQUIERDA-BLOQUEADA"
                               "JUNTO-A-ZUMBADOR"
                               "NO-JUNTO-A-ZUMBADOR"
                               "ALGUN-ZUMBADOR-EN-LA-MOCHILA"
                               "NINGUN-ZUMBADOR-EN-LA-MOCHILA"
                               "ORIENTADO-AL-NORTE"
                               "NO-ORIENTADO-AL-NORTE"
                               "ORIENTADO-AL-ESTE"
                               "NO-ORIENTADO-AL-ESTE"
                               "ORIENTADO-AL-SUR"
                               "NO-ORIENTADO-AL-SUR"
                               "ORIENTADO-AL-OESTE"
                               "NO-ORIENTADO-AL-OESTE"
                               "VERDADERO"
                               "FALSO"
                            }{
        }
        Son las posibles funciones booleanas para Karel
        */
        String retornar_valor = "";

        if (in_array(this.token_actual.token, this.condiciones)){
            retornar_valor = this.token_actual.token;
            this.avanza_token();
        }else
            throw new KarelException("Se esperaba una condición como 'frente-libre', '"+this.token_actual+"' no es una condición");

        return retornar_valor;
    }

    private LogicO termino(LinkedList<IntExpr> lista_variables) throws KarelException{
        /*
        Define un termino
        {
            Termino ::= ClausulaY [ "o" ClausulaY] ...
        }
        Se usan dentro de los condicionales "si" y el bucle "mientras"
        */
        LogicO retornar_valor = new LogicO();
        retornar_valor.argumento.add(this.clausula_y(lista_variables));

        while (this.token_actual.token.equals("o")){
            this.avanza_token();
            retornar_valor.argumento.add(this.clausula_y(lista_variables));
        }

        return retornar_valor;
    }

    public void verificar_sintaxis () throws KarelException{
        /* Verifica que este correcta la gramatica de un programa
        en karel y crea el árbol correspondiente */
        if (this.token_actual.token.equals("iniciar-programa")){
            if (this.avanza_token()){
                this.bloque();
                if (!this.token_actual.token.equals("finalizar-programa"))
                    throw new KarelException("Se esperaba 'finalizar-programa' al final del codigo");
            }else
                throw new KarelException("Codigo mal formado");
        }else
            throw new KarelException("Se esperaba 'iniciar-programa' al inicio del programa");
    }

    public boolean es_identificador_valido(KToken token){
        /* Identifica cuando una cadena es un identificador valido,
        osea que puede ser usado en el nombre de una variable, las
        reglas son:
        * Debe comenzar en una letra
        * Sólo puede tener letras, números, "-" y "_" */
        if(KGrammar.in_array(token.token, this.palabrasReservadas)){
            return false;
        }
        boolean es_valido = true;
        int i = 0;
        for(char caracter:token.token.toCharArray()){
            if (i == 0){
                if (!Character.isLetter(caracter)){
                    //Un identificador válido comienza con una letra
                    es_valido = false;
                    break;
                }
            }else{
                if ((!KGrammar.in_array(caracter, this.lexer.palabras.toCharArray())) && (!KGrammar.in_array(caracter, this.lexer.numeros.toCharArray()))){
                    es_valido = false;
                    break;
                }
            }
            i++;
        }
        return es_valido;
    }

    public boolean es_numero(KToken token){
        /*Determina si un token es un numero*/
        for(char caracter:token.token.toCharArray()){
            if(!KGrammar.in_array(caracter, this.lexer.numeros.toCharArray()))
                return false; //Encontramos algo que no es numero
        }
        return true;
    }

    public Ejecutable expandir_arbol(){
        /*Expande el árbol de instrucciones para ser usado por krunner
        durante la ejecución*/
    	for(Object funcion_o: this.arbol.funciones.keySet().toArray()){
            String funcion = (String)funcion_o;
            
            RStructFuncion def_funcion = new RStructFuncion(this.arbol.funciones.get(funcion).params, funcion);
            
            int posicion_inicio = this.listaPrograma.size();
            this.listaPrograma.add(def_funcion);

            this.ejecutable.indiceFunciones.put(funcion, posicion_inicio);
            this.expandir_arbol_recursivo(this.arbol.funciones.get(funcion).cola);
            
            EndStruct fin = new EndStruct(Struct.ESTRUCTURA_INSTRUCCION_DEFINIDA);
            fin.inicio = posicion_inicio;
            
            this.listaPrograma.add(fin);
        }
        this.ejecutable.main = this.listaPrograma.size();
        this.expandir_arbol_recursivo(this.arbol.main);
        this.listaPrograma.add(new RStructInstruccion("fin")); //Marca de fin del programa
        this.ejecutable.lista = this.listaPrograma;
        return this.ejecutable;
    }
    
    private void expandir_arbol_recursivo(LinkedList<Struct> cola){
        /*Toma un arbol y lo expande*/
        Iterator<Struct> iterador = cola.iterator();
        Struct elemento;
        while(iterador.hasNext()){
            elemento = iterador.next();
            if(elemento.estructura == Struct.ESTRUCTURA_INSTRUCCION){
            	RStructInstruccion instruccion = new RStructInstruccion(((StructInstruccion)elemento).nombre);
            	this.listaPrograma.add(instruccion);
            } else if(elemento.estructura == Struct.ESTRUCTURA_MIENTRAS){
                int posicion_inicio = this.listaPrograma.size();
                RStructMientras estructura = new RStructMientras(((StructMientras)elemento).argumentoLogico, posicion_inicio);
                
                this.listaPrograma.add(estructura);
                this.expandir_arbol_recursivo(((StructMientras)elemento).cola);
                
                int posicion_fin = this.listaPrograma.size();
                
                EndStruct fin = new EndStruct(Struct.ESTRUCTURA_MIENTRAS);
                fin.inicio = posicion_inicio;
                this.listaPrograma.add(fin);
                
                estructura.finEstructura = posicion_fin;
                this.listaPrograma.set(posicion_inicio, estructura);
            } else if(elemento.estructura == Struct.ESTRUCTURA_REPITE){
            	int posicion_inicio = this.listaPrograma.size();
                RStructRepite estructura = new RStructRepite(((StructRepite)elemento).argumentoEntero, posicion_inicio);
                
                this.listaPrograma.add(estructura);
                this.expandir_arbol_recursivo(((StructRepite)elemento).cola);
                
                int posicion_fin = this.listaPrograma.size();
                
                EndStruct fin = new EndStruct(Struct.ESTRUCTURA_REPITE);
                fin.inicio = posicion_inicio;
                this.listaPrograma.add(fin);
                
                estructura.finEstructura = posicion_fin;
                this.listaPrograma.set(posicion_inicio, estructura);
            } else if (elemento.estructura == Struct.ESTRUCTURA_SI){
                int posicion_inicio = this.listaPrograma.size();
                RStructSi estructura = new RStructSi(((StructSi)elemento).argumentoLogico);

                this.listaPrograma.add(estructura);
                this.expandir_arbol_recursivo(((StructSi)elemento).cola);
                int posicion_fin = this.listaPrograma.size();
                
                EndStruct fin_def = new EndStruct(Struct.ESTRUCTURA_SI);
                fin_def.inicio = posicion_inicio;
                fin_def.finEstructura = posicion_fin+1;
                this.listaPrograma.add(fin_def);
                
                estructura.finEstructura = posicion_fin;
                this.listaPrograma.set(posicion_inicio, estructura);
                if (((StructSi)elemento).tieneSino){
                    RStructSino nueva_estructura = new RStructSino();
                    
                    this.listaPrograma.add(nueva_estructura);
                    this.expandir_arbol_recursivo(((StructSi)elemento).colaSino);
                    int fin_sino = this.listaPrograma.size();
                    
                    EndStruct fin = new EndStruct(Struct.ESTRUCTURA_SINO);
                    this.listaPrograma.add(fin);
                    
                    fin_def.finEstructura = fin_sino;
                    this.listaPrograma.set(posicion_fin, fin_def);
                }
            }else{//Se trata de la llamada a una función
                RStructFuncion element = new RStructFuncion(((StructFuncion)elemento).argumentoFuncion, ((StructFuncion)elemento).nombre);
                this.listaPrograma.add(element);
            }
        }
    }
}
