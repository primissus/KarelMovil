package poodleDeveloper.karel;

import com.actionbarsherlock.app.SherlockActivity;

import poodleDeveloper.karel.data.karelmovil.KRunner;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

public class KWorld extends SurfaceView implements SurfaceHolder.Callback{

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
	private boolean estoyArrastrando = false;	
	final Handler handler = new Handler();
	final Context context;
	private Thread t;
	
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
	}
	
	protected void kThread(){
		/** Hilo de ejecución de karel*/
		t = new Thread(){
			public void run(){
				while(Exchanger.krunner.estado == KRunner.ESTADO_OK){
					try{
						Exchanger.krunner.step();
						invalidate();
						Thread.sleep(500); 
						System.out.println(Exchanger.krunner.estado);
					}catch(Exception e){
						e.getMessage();
					}
				}
				final SherlockActivity activity = (SherlockActivity)context;
				if(Exchanger.krunner.estado == KRunner.ESTADO_ERROR){
					/** Mostramos en el hilo de la principal de la UI el mensaje*/
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(activity, Exchanger.krunner.mensaje, Toast.LENGTH_SHORT).show();
							System.out.println(Exchanger.krunner.mensaje);
						} 
					});
				}
				else if(Exchanger.krunner.estado == KRunner.ESTADO_TERMINADO){
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(activity, "FELICIDADES, Karel llegó a su destino", Toast.LENGTH_SHORT).show();
							System.out.println("Karel llegó a la meta");
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
		paint.setColor(Color.BLACK); 
		paint.setAntiAlias(true);
		
		/** Pintamos las casillas */
		for(float i = 0; i < size.x+TAM_CAS; i+=TAM_CAS)
			for(float j = size.y-TAM_CAS; j > -TAM_CAS ; j-=TAM_CAS)
					canvas.drawBitmap(world, i, j, paint);
		
		/** Sólo si Karel se encuentra dentro de los límites virtuales del mundo, lo pintamos*/
		if(Exchanger.kworld.karel.posicion.fila < MAX_SCREEN_Y+1 && Exchanger.kworld.karel.posicion.fila >= MIN_SCREEN_Y && 
				Exchanger.kworld.karel.posicion.columna < MAX_SCREEN_X+1 && Exchanger.kworld.karel.posicion.columna >= MIN_SCREEN_X){
			
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
