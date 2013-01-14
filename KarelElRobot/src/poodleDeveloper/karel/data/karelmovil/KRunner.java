package poodleDeveloper.karel.data.karelmovil;

import java.util.HashMap;
import java.util.LinkedList;

import poodleDeveloper.karel.data.structs.*;
import poodleDeveloper.karel.data.grammar.*;

class Nota{ //Se usa para guardar un estado del programa en la pila
        public int posicion;
        public HashMap<String, Integer> diccionarioVariables;
        public Nota(int pos, HashMap<String, Integer> varDict){
                this.posicion = pos;
                this.diccionarioVariables = varDict;
        }
}

class PilaEstructuras extends LinkedList<RStructBucle>{
        private static final long serialVersionUID = 1L;
        public boolean enTope(int id){
                if(this.size() == 0)
                        return false;
                RStructBucle ultimoValor = this.pop();
                this.push(ultimoValor);
                if(ultimoValor.id == id)
                        return true;
                return false;
        }
}

public class KRunner {
        public static final int ESTADO_OK=0x1;
        public static final int ESTADO_ERROR=0x2;
        public static final int ESTADO_TERMINADO=0x3;

        private Ejecutable ejecutable;
        private KWorld mundo;

        public KWorld getMundo() {
                return mundo;
        }

        public void setMundo(KWorld mundo) {
                this.mundo = mundo;
        }

        public int limiteRecursion=65000;
        public int limiteIteracion=65000;
        public int limiteEjecucion=200000;

        private int indice;
        private int ejecucion=0;
        private HashMap<String, Integer> diccionario_variables;
        public boolean corriendo=true;

        private LinkedList<Nota> pilaFunciones;
        private PilaEstructuras pilaEstructuras;
        public int estado;
        public String mensaje;
        public KRunner(Ejecutable programa_compilado, KWorld mundo, int limite_recursion, int limiteIteracion, int limite_ejecucion){
                /* Inicializa el ejecutor dados un codigo fuente compilado y un
                mundo, tambien establece el limite para la recursion sobre una
                funcion antes de botar un error stack_overflow.*/
                this.ejecutable = programa_compilado;
                this.mundo = mundo;
                this.limiteRecursion = limite_recursion;
                this.limiteIteracion = limiteIteracion;
                this.limiteEjecucion = limite_ejecucion;
                this.pilaFunciones = new LinkedList<Nota>(); //La pila de llamadas a funciones
                this.pilaEstructuras = new PilaEstructuras(); //pila de llamadas a estructuras
                this.estado = KRunner.ESTADO_OK; //El estado en que se encuentra
                this.mensaje = ""; //Mensaje con que termina la ejecucion
        }

        public KRunner(Ejecutable programa_compilado, KWorld mundo){
                /* Inicializa el ejecutor dados un codigo fuente compilado y un
        mundo, tambien establece el limite para la recursion sobre una
        funcion antes de botar un error stack_overflow.*/
        this.ejecutable = programa_compilado;
        this.mundo = mundo;
        this.pilaFunciones = new LinkedList<Nota>(); //La pila de llamadas a funciones
        this.pilaEstructuras = new PilaEstructuras(); //pila de llamadas a estructuras
        this.estado = KRunner.ESTADO_OK; //El estado en que se encuentra
        this.mensaje = ""; //Mensaje con que termina la ejecucion
        }

        private boolean terminoLogico(LogicO args, HashMap<String, Integer> vars){
                /* Obtiene el resultado de la evaluacion de un termino logico "o"
        para el punto en que se encuentre Karel al momento de la llamada,
        recibe una lista con los terminos a evaluar
        */
        for (LogicY termino:args.argumento){
            if (this.clausula_y(termino, vars)){
                return true;
            }
        }
        return false;
        }

        private boolean clausula_y(LogicY expresion, HashMap<String, Integer> vars) {
                /* Obtiene el resultado de una comparación "y" entre terminos
        logicos */
        for (LogicNo termino:expresion.argumento){
            if (!this.clausula_no(termino, vars)){
                return false; //El resultado de una evaluacion "y" es falso si uno de los terminos es falso
            }
        }
        return true;
        }

        private boolean clausula_no(LogicNo termino, HashMap<String, Integer> vars) {
                /* Obtiene el resultado de una negacion "no" o de un termino
        logico */
                if(termino.valor){
                        return clausula_atomica(termino.clausula, vars);
                } else {
                        return ! clausula_atomica(termino.clausula, vars);
                }
        }

        private boolean clausula_atomica(LogicAtomic clausula,
                        HashMap<String, Integer> vars) {
                switch(clausula.tipo){
                        case LogicAtomic.EXPRESION_BOOLEANA:
                                if (clausula.funcionBooleana.equals("verdadero"))
                        return true;
                    else if(clausula.funcionBooleana.equals("falso"))
                        return false;
                    else if(clausula.funcionBooleana.equals("frente-libre")){
                        return this.mundo.frente_libre();
                    } else if(clausula.funcionBooleana.equals("frente-bloqueado"))
                        return ! this.mundo.frente_libre();
                    else if(clausula.funcionBooleana.equals("izquierda-libre"))
                        return this.mundo.izquierda_libre();
                    else if(clausula.funcionBooleana.equals("izquierda-bloqueada"))
                        return ! this.mundo.izquierda_libre();
                    else if(clausula.funcionBooleana.equals("derecha-libre"))
                        return this.mundo.derecha_libre();
                    else if(clausula.funcionBooleana.equals("derecha-bloqueada"))
                        return ! this.mundo.derecha_libre();
                    else if(clausula.funcionBooleana.equals("junto-a-zumbador"))
                        return this.mundo.junto_a_zumbador();
                    else if(clausula.funcionBooleana.equals("no-junto-a-zumbador"))
                        return ! this.mundo.junto_a_zumbador();
                    else if(clausula.funcionBooleana.equals("algun-zumbador-en-la-mochila"))
                        return this.mundo.algun_zumbador_en_la_mochila();
                    else if(clausula.funcionBooleana.equals("ningun-zumbador-en-la-mochila"))
                        return ! this.mundo.algun_zumbador_en_la_mochila();
                    else if(clausula.funcionBooleana.startsWith("orientado-al-")){
                        return this.mundo.orientado_al(KWorld.convierteOrientacion(clausula.funcionBooleana.substring(13,clausula.funcionBooleana.length())));
                    } else if(clausula.funcionBooleana.startsWith("no-orientado-al-")){
                        return ! this.mundo.orientado_al(KWorld.convierteOrientacion(clausula.funcionBooleana.substring(16, clausula.funcionBooleana.length())));
                    }
                        case LogicAtomic.EXPRESION_ENTERA:
                                return this.expresionEntera(clausula.argumentoEntero, vars)==0;
                        case LogicAtomic.EXPRESION_TERMINO:
                                return this.terminoLogico(clausula.termino, vars);
                }
                return false;
        }

        private int expresionEntera(IntExpr args, HashMap<String, Integer> vars){
                /* Obtiene el resultado de una evaluacion entera y lo devuelve
        */
        if(args.tipo == IntExpr.VALOR_PRECEDE){
                return this.expresionEntera(args.argumento, vars)-1;
        } else if(args.tipo == IntExpr.VALOR_SUCEDE){
                return this.expresionEntera(args.argumento, vars)+1;
        } else if(args.tipo == IntExpr.VALOR_DECIMAL){
                return args.valorDecimal;
        } else {
                return vars.get(args.valorIdentificador);
        }
        }

        public void run(){
        /* Ejecuta el codigo compilado de Karel en el mundo
        proporcionado, comenzando por el bloque "main" o estructura
        principal. */
                this.step_run();
        while(this.corriendo){
                this.step();
        }
        }

        public void step_run(){
                /* Prepara las cosas para un step-run de karel */
                this.indice = this.ejecutable.main; //El cabezal de esta máquina de turing
        this.ejecucion = 0;
        this.diccionario_variables = new HashMap<String, Integer>();
        }

        public int step(){
                try{
                        if (this.corriendo){
                if (ejecucion >= this.limiteEjecucion){
                    throw new KarelException("HanoiTowerException: Tu programa nunca termina ¿Usaste 'apagate'?");
                }
                //Hay que ejecutar la función en turno en el índice actual
                RunStruct instruccion = this.ejecutable.lista.get(indice); //TODO cuidar esta líneas
                if(EndStruct.class.isInstance(instruccion)){
                        EndStruct fin = (EndStruct)instruccion;
                        int bucles[] = {Struct.ESTRUCTURA_MIENTRAS, Struct.ESTRUCTURA_REPITE};
                        if(KGrammar.in_array(fin.estructura, bucles)){
                                indice = fin.inicio;
                        } else if (fin.estructura == Struct.ESTRUCTURA_SI){
                        indice = fin.finEstructura;
                        } else if (instruccion.estructura == Struct.ESTRUCTURA_SINO){
                        indice ++;
                        } else{//fin de una funcion
                        Nota nota = this.pilaFunciones.pop(); //Obtenemos la nota de donde nos hemos quedado
                        indice = nota.posicion+1;
                        diccionario_variables = nota.diccionarioVariables;
                    }
                } else if(instruccion.estructura == Struct.ESTRUCTURA_INSTRUCCION){
                        //Es una instruccion predefinida de Karel
                        RStructInstruccion instruccionPredefinida = (RStructInstruccion)instruccion;
                    if (instruccionPredefinida.instruccion.equals("avanza")){
                        if (! this.mundo.avanza())
                            throw new KarelException("Karel se ha estrellado con una pared!");
                        indice ++;
                    } else if (instruccionPredefinida.instruccion.equals("gira-izquierda")){
                        this.mundo.gira_izquierda();
                        indice ++;
                    } else if (instruccionPredefinida.instruccion.equals("coge-zumbador")){
                        if (! this.mundo.coge_zumbador())
                                throw new KarelException("Karel quizo coger un zumbador pero no habia en su posicion");
                        indice ++;
                    } else if (instruccionPredefinida.instruccion.equals("deja-zumbador")){
                        if (! this.mundo.deja_zumbador())
                            throw new KarelException("Karel quizo dejar un zumbador pero su mochila estaba vacia");
                        indice ++;
                    } else if (instruccionPredefinida.instruccion.equals("apagate")){
                        this.corriendo = false; //Fin de la ejecución
                        this.estado = KRunner.ESTADO_OK;
                        this.mensaje = "Ejecucion terminada";
                        return KRunner.ESTADO_TERMINADO;
                    } else if (instruccionPredefinida.instruccion.equals("sal-de-instruccion")){
                        Nota nota = this.pilaFunciones.pop();//Obtenemos la nota de donde nos hemos quedado
                        indice = nota.posicion+1;
                        diccionario_variables = nota.diccionarioVariables;
                    } else if (instruccionPredefinida.instruccion.equals("sal-de-bucle")){
                        RStructBucle bucle = this.pilaEstructuras.pop();
                        indice = bucle.finEstructura+1;
                    } else if(instruccionPredefinida.instruccion.equals("continua-bucle")){
                        RStructBucle bucle = (RStructBucle)this.pilaEstructuras.getLast();
                        indice = ((EndStruct)this.ejecutable.lista.get(bucle.finEstructura)).inicio;
                    } else { //FIN
                        throw new KarelException("HanoiTowerException: Tu programa excede el límite de ejecución ¿Usaste 'apagate'?");
                    }
                    ejecucion ++;
                } else if (instruccion.estructura == Struct.ESTRUCTURA_SI){ //Se trata de una estructura de control o una funcion definida
                        RStructSi si = (RStructSi)instruccion;
                    if (this.terminoLogico(si.argumentoLogico, diccionario_variables)){
                        indice ++; //Avanzamos a la siguiente posicion en la cinta
                    } else {//nos saltamos el si, vamos a la siguiente casilla, que debe ser un sino o la siguiente instruccion
                        indice = si.finEstructura+1;
                    }
                    ejecucion ++;
                } else if (instruccion.estructura == Struct.ESTRUCTURA_SINO){ //Llegamos a un sino, procedemos, no hay de otra
                    indice ++;
                    ejecucion ++;
                } else if (instruccion.estructura == Struct.ESTRUCTURA_REPITE){
                        RStructRepite repite = (RStructRepite)instruccion;
                    if (!this.pilaEstructuras.enTope(repite.id)){//Se está llegando a la estructura por primera vez
                        int argumento = this.expresionEntera(repite.argumento, diccionario_variables);
                        if (argumento < 0)
                            throw new KarelException("WeirdNumberException: Estás intentando que karel repita un número negativo de veces");
                        repite.argumento = new IntExpr(argumento);
                        this.pilaEstructuras.push(repite);
                    }
                    if (((RStructRepite)this.pilaEstructuras.getLast()).argumento.valorDecimal>0){
                        if (this.pilaEstructuras.getLast().cuenta == this.limiteIteracion)
                            throw new KarelException("LoopLimitExceded: hay un bucle que se cicla");
                        indice ++;
                        this.pilaEstructuras.getLast().cuenta ++;
                        ((RStructRepite)this.pilaEstructuras.getLast()).argumento.valorDecimal --;
                    }else{//nos vamos al final y extraemos el repite de la pila
                        indice = this.pilaEstructuras.getLast().finEstructura+1;
                        this.pilaEstructuras.pop();
                    }
                    ejecucion ++;
                } else if (instruccion.estructura == Struct.ESTRUCTURA_MIENTRAS){
                        RStructMientras mientras = (RStructMientras)instruccion;
                    if (!this.pilaEstructuras.enTope(mientras.id)){//Se está llegando a la estructura al menos por segunda vez
                        this.pilaEstructuras.push(mientras);
                    }
                    if (this.terminoLogico(mientras.argumentoLogico, diccionario_variables)){//Se cumple la condición del mientras
                        if (this.pilaEstructuras.getLast().cuenta >= this.limiteIteracion)
                            throw new KarelException("LoopLimitExceded: hay un bucle que se cicla");
                        indice ++;
                        this.pilaEstructuras.getLast().cuenta ++;
                    } else { //nos vamos al final
                        indice = this.pilaEstructuras.pop().finEstructura+1;
                    }
                    ejecucion ++;
                } else { //Se trata la llamada a una función
                    if (this.pilaFunciones.size() >= this.limiteRecursion)
                        throw new KarelException("StackOverflow: Karel ha excedido el límite de recursión");
                    RStructFuncion funcion = (RStructFuncion)instruccion;
                    //Hay que guardar la posición actual y el diccionario de variables en uso
                    this.pilaFunciones.push(new Nota(indice, diccionario_variables));
                    // Lo que prosigue es ir a la definición de la función
                    indice = this.ejecutable.indiceFunciones.get(funcion.nombre)+1;
                    // recalcular el diccionario de variables
                    LinkedList<IntExpr> valores = new LinkedList<IntExpr>();
                    for (IntExpr i:funcion.params)
                        valores.push(new IntExpr(this.expresionEntera(i, diccionario_variables)));
                    diccionario_variables = merge(
                        ((RStructFuncion)this.ejecutable.lista.get(indice-1)).params,
                        valores
                    );
                    ejecucion ++;
                }
            } else {
                return KRunner.ESTADO_TERMINADO;
            }
                } catch(KarelException e){
                        this.corriendo = false;
                        this.estado = KRunner.ESTADO_ERROR;
                        this.mensaje = e.getMessage();
                        return KRunner.ESTADO_ERROR;
                }
                return KRunner.ESTADO_OK;
        }

        private HashMap<String, Integer> merge(LinkedList<IntExpr> nombres, LinkedList<IntExpr> valores) {
                HashMap<String, Integer> diccionario_variables = new HashMap<String, Integer>();

                for(int i=0;i<nombres.size();i++)
                        diccionario_variables.put(nombres.get(i).valorIdentificador, valores.get(i).valorDecimal);

                return diccionario_variables;
        }
}
