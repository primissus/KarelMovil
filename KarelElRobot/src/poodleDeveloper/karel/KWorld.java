package poodleDeveloper.karel;


import com.actionbarsherlock.app.SherlockActivity;

import poodleDeveloper.karel.data.karelmovil.KCasilla;
import poodleDeveloper.karel.data.karelmovil.KPosition;
import poodleDeveloper.karel.data.karelmovil.KRunner;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class KWorld extends SurfaceView implements SurfaceHolder.Callback{

	class Zumbador{
		public int columna;
		public int fila;
		public int radio;
		public String numero;
		public Zumbador(int x, int y, int radio, String numero){
			this.fila = x;
			this.columna = y;
			this.radio = radio;
			this.numero = numero;
		}
	}
	
	private static int TAM_CAS;	
	private static int FREE_SPACE; 
	private static int MAX_SCREEN_X;
	private static int MAX_SCREEN_Y;
	private static int MIN_SCREEN_X;
	private static int MIN_SCREEN_Y;
	private KThread thread;
	private Bitmap world,karelN,karelE,karelS,karelO;
	private Point size;
	private int firstX,firstY,lastX,lastY;
	private static boolean estoyArrastrando = false, addingBeeper = false, addingWall = false, deleting = false;	
	final Handler handler = new Handler();
	private static Context context;
	private Thread t;
	private static int NUM_BEEPERS;
	private static int NUMBER_ITEMS;
	private static Button beeper, del;
	
	@SuppressWarnings("static-access")
	public KWorld(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFocusable(true);
		this.context = context; 
		this.init(context);	
	}
	
	@SuppressLint("NewApi")
	public void init(Context context) {
		loadItems(context);	
		Exchanger.krunner.step_run();  //KRunner ya fue inicializado previamente, acomodamos las variables
		//kThread();  
		NUM_BEEPERS = 0;
		NUMBER_ITEMS = 0;
	}
	
	public static void loadButtons(Button addBeeper, Button addWall, Button delete){
		beeper = addBeeper;
		del = delete;
		beeper.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!deleting){
					final EditText input = new EditText(context);
					input.setInputType(InputType.TYPE_CLASS_PHONE);
					new AlertDialog.Builder(context).setTitle("Agregar Zumbador").setMessage("¿Cuantos zumbadores deseas agregar? (-1 = Infinitos)")
					    .setView(input).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					         public void onClick(DialogInterface dialog, int whichButton) {				
						        	if(!input.getText().toString().equals("")){
						        		int numerBeepers = Integer.parseInt(input.getText().toString());
							            if(numerBeepers > 99)
							            	Toast.makeText(context, "El número máximo de zumbadores es 99, vuelve a intentarlo", Toast.LENGTH_SHORT).show();
							            else{
							            	Toast.makeText(context, "Pulsa la casilla donde quieres colocar los zumbadores", Toast.LENGTH_SHORT).show();
							            	addingBeeper = true;
							            	NUM_BEEPERS = numerBeepers;
							            }
						        	}
					         }
					    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					         public void onClick(DialogInterface dialog, int whichButton) {}
					    }).show();
				}else{
					Toast.makeText(context, "Modo de borrado activado, pulse el icono para desactivarlo", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		del.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP)
					if(NUMBER_ITEMS > 0)
						if(!deleting){
							deleting = true;
							del.setPressed(true);
							Toast.makeText(context, "Seleccione un ítem para borrarlo", Toast.LENGTH_SHORT).show();
						}else{
							deleting = false;
							del.setPressed(false);
						}
					else{
						Toast.makeText(context, "No hay movimientos que deshacer", Toast.LENGTH_SHORT).show();
					}
				return true;
			}
		});
	}
	
	protected void kThread(){
		/** Hilo de ejecución de karel*/
		t = new Thread(){
			public void run(){
				int estado;
				while((estado = Exchanger.krunner.step()) == KRunner.ESTADO_OK){
					try{ 
						invalidate();
						Thread.sleep(500); 
					}catch(Exception e){
						e.getMessage();
					} 
				}
				final SherlockActivity activity = (SherlockActivity)context;
				if(estado == KRunner.ESTADO_ERROR){
					/** Mostramos en el hilo de la principal de la UI el mensaje*/
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(activity, Exchanger.krunner.mensaje, Toast.LENGTH_SHORT).show();
						} 
					});
				} 
				else if(estado == KRunner.ESTADO_TERMINADO){
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(activity, "FELICIDADES, Karel llegó a su destino", Toast.LENGTH_SHORT).show();
						}
					});
				} 
			}
		};
		t.start();
	}
	
	
	
	
	@SuppressLint("DrawAllocation") @Override
	public void onDraw(Canvas canvas){ 
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setAntiAlias(true);
		/** Pintamos las casillas */
		for(float i = 0; i < size.x+TAM_CAS; i+=TAM_CAS)
			for(float j = size.y-TAM_CAS; j > -TAM_CAS ; j-=TAM_CAS)
					canvas.drawBitmap(world, i, j, paint);
		
		/** En caso de haber zumbadores los agregamos*/
		int xB;
		int yB;
		paint.setTextSize(TAM_CAS/3);
		for(KCasilla casilla : Exchanger.kworld.casillas.values()){
			if(casilla.fila < MAX_SCREEN_Y+1 && casilla.fila >= MIN_SCREEN_Y && casilla.columna < MAX_SCREEN_X+2 && casilla.columna >= MIN_SCREEN_X){
				xB = (casilla.columna-MIN_SCREEN_X)*TAM_CAS;
				yB = (((((int)(size.y/TAM_CAS))-(casilla.fila-MIN_SCREEN_Y))-1)*TAM_CAS)+FREE_SPACE;
				if(casilla.zumbadores != 0){
					 paint.setStrokeWidth(28);
					 paint.setColor(Color.GREEN); 
					 canvas.drawCircle(xB+(TAM_CAS/2), yB+(TAM_CAS/2), (TAM_CAS-50)/2, paint);
					 paint.setStrokeWidth(1);
					 paint.setColor(Color.BLACK);
					 if(casilla.zumbadores > 9)
						 canvas.drawText(String.valueOf(casilla.zumbadores), xB+(TAM_CAS/3), yB+(TAM_CAS-(TAM_CAS/2))+5, paint);
					 else
						 canvas.drawText(String.valueOf(casilla.zumbadores), xB+(TAM_CAS/3)+5, yB+(TAM_CAS-(TAM_CAS/2))+5, paint);
				}
			}
		}
		paint.setStrokeWidth(1);
		/** Sólo si Karel se encuentra dentro de los límites virtuales del mundo, lo pintamos*/
		if(Exchanger.kworld.karel.posicion.fila < MAX_SCREEN_Y+1 && Exchanger.kworld.karel.posicion.fila >= MIN_SCREEN_Y && 
				Exchanger.kworld.karel.posicion.columna < MAX_SCREEN_X+2 && Exchanger.kworld.karel.posicion.columna >= MIN_SCREEN_X){
			
			int x = (Exchanger.kworld.karel.posicion.columna-MIN_SCREEN_X)*TAM_CAS;
			int y = (((((int)(size.y/TAM_CAS))-(Exchanger.kworld.karel.posicion.fila-MIN_SCREEN_Y))-1)*TAM_CAS)+FREE_SPACE;
					
			switch (Exchanger.kworld.karel.orientacion) {
			case poodleDeveloper.karel.data.karelmovil.KWorld.NORTE:
				canvas.drawBitmap(karelN,x,y,paint);
				break;
			case poodleDeveloper.karel.data.karelmovil.KWorld.ESTE:
				canvas.drawBitmap(karelE,x,y,paint);
				break; 
			case poodleDeveloper.karel.data.karelmovil.KWorld.SUR:
				canvas.drawBitmap(karelS,x,y,paint);
				break;
			case poodleDeveloper.karel.data.karelmovil.KWorld.OESTE:
				canvas.drawBitmap(karelO,x,y,paint);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		/** Hilo de ejecución de la vista del mundo*/
		thread = new KThread(getHolder(), this);
		thread.setRunning(true);
		thread.start();
		kThread();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		thread.setRunning(false);
		while(retry){
			try{
				/** Detenemos ambos hilos al cerrar la vista*/
				thread.join();
				t.join();
				retry = false;
			}catch(InterruptedException e){
				
			}
		}
		
	}
	
	/** Scroll del mundo*/
	public boolean onTouchEvent(MotionEvent event){
		int evento = event.getAction();
		switch (evento) {
		case MotionEvent.ACTION_DOWN:
			if(deleting){
				;
			}else if(addingBeeper){ 
				int columna = ((int)event.getX()/TAM_CAS)+MIN_SCREEN_X;
				int fila = (MAX_SCREEN_Y - (((int)event.getY())+FREE_SPACE)/TAM_CAS);
				Exchanger.kworld.pon_zumbadores(new KPosition(fila, columna), NUM_BEEPERS);
				NUMBER_ITEMS++;
			}
			estoyArrastrando = true;
			/** Obtenemos el primer punto donde hicimos click*/
			firstX = (int)event.getX();
			firstY = (int)event.getY();
			lastX = firstX;
			lastY = firstY;
			break;
		case MotionEvent.ACTION_MOVE:
			if(estoyArrastrando){
				/** Sumamos la cantidad de pixeles arrastrados hasta obtener una cantidad considerable para mover a Karel*/
				int offsetX = (int)event.getX();
				int offsetY = (int)event.getY();
				lastX+=(offsetX-lastX);
				lastY+=(offsetY-lastY);
				if(Math.abs(lastX-firstX) >= TAM_CAS/2){ // la última expresión denota la suavidad del scroll
					/** Una vez acumulados los pixeles que queremos, movemos el mundo virtual*/
					if(lastX > firstX){
						MIN_SCREEN_X-=1;
						MAX_SCREEN_X-=1;
					}else{
						MIN_SCREEN_X+=1;
						MAX_SCREEN_X+=1;
					}
					firstX = lastX;
					firstY = lastY;
				}
				if(Math.abs(lastY-firstY) >= TAM_CAS/2){
					if(lastY > firstY){
						MIN_SCREEN_Y+=1;
						MAX_SCREEN_Y+=1;
					}else{
						MIN_SCREEN_Y-=1;
						MAX_SCREEN_Y-=1;
					}
					firstX = lastX;
					firstY = lastY;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			estoyArrastrando = false;
			addingBeeper = false;
			if(deleting){
				int columna = ((int)event.getX()/TAM_CAS)+MIN_SCREEN_X;
				int fila = (MAX_SCREEN_Y - (((int)event.getY())+FREE_SPACE)/TAM_CAS);
				/** Buscamos las coordenadas obtenidas en todas las casillas para eliminar el item*/
				for(KCasilla casilla: Exchanger.kworld.casillas.values())
					if(casilla.fila == fila && casilla.columna == columna)
						casilla.zumbadores = 0;
			}
			break;
		default:
			break;
		}
		invalidate();
		return true;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private static Point getDisplaySize(final Display display) {
		/** Obtenemos el tamaño de la pantalla para viejos y nuevos dispositivos*/
	    final Point point = new Point();
	    try {
	        display.getSize(point);
	    } catch (java.lang.NoSuchMethodError ignore) {
	        point.x = display.getWidth();
	        point.y = display.getHeight(); 
	    }
	    return point;
	}

	private void loadItems(Context context){
		/** Cargamos los recursos y los valores iniciales*/
		WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		
		world = BitmapFactory.decodeResource(getResources(), R.drawable.bkworld);
		karelN = BitmapFactory.decodeResource(getResources(), R.drawable.knorte);
		karelE = BitmapFactory.decodeResource(getResources(), R.drawable.keste);
		karelS = BitmapFactory.decodeResource(getResources(), R.drawable.ksur);
		karelO = BitmapFactory.decodeResource(getResources(), R.drawable.koeste);
		getHolder().addCallback(this);
		TAM_CAS = world.getWidth();
		size = getDisplaySize(display);
		MAX_SCREEN_X = (int)(size.x/TAM_CAS);
		MAX_SCREEN_Y = (int)(size.y/TAM_CAS);
		MIN_SCREEN_X = 1;
		MIN_SCREEN_Y = 1;
		FREE_SPACE = size.y-(((int)(size.y/TAM_CAS))*TAM_CAS);
	}
	
}
