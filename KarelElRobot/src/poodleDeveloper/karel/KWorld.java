package poodleDeveloper.karel;

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
	private Point maxScreenXY;
	private Point minScreenXY;
	private int firstX,firstY,lastX,lastY;
	private boolean estoyArrastrando = false;	
	final Handler handler = new Handler();
	private Context context;
	public KWorld(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.init(context);
	}
	
	@SuppressLint("NewApi")
	public void init(Context context) {
		loadItems(context);	
		Exchanger.krunner.step_run();
		kThread();
	}
	
	protected void kThread(){
		Thread t = new Thread(){
			public void run(){
				while(Exchanger.krunner.estado == KRunner.ESTADO_OK){
					try{
						handler.post(runnable);
						Thread.sleep(500);
						if(Exchanger.krunner.estado == KRunner.ESTADO_ERROR){
							Toast.makeText(context, Exchanger.krunner.mensaje, Toast.LENGTH_LONG).show();
							this.interrupt();
						}
						else if(Exchanger.krunner.estado == KRunner.ESTADO_TERMINADO){
							Toast.makeText(context, "Felicidades, ejecución terminada con éxito", Toast.LENGTH_LONG).show();
							this.interrupt();
						}
					}catch(Exception e){
						Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
					}
				}
			}
		};
		t.start();
	}
	
	final Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			Exchanger.krunner.step();
			invalidate();
		}
	};
	
	
	
	@SuppressLint("DrawAllocation") @Override
	public void onDraw(Canvas canvas){ 
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK); 
		paint.setAntiAlias(true);
		
		
		for(float i = 0; i < size.x+TAM_CAS; i+=TAM_CAS)
			for(float j = size.y-TAM_CAS; j > -TAM_CAS ; j-=TAM_CAS)
					canvas.drawBitmap(world, i, j, paint);
/*		int num_filas = size.y/TAM_CAS+1;
		int num_columnas = size.x/TAM_CAS+1;
		for(int i = casilla.fila+num_filas; i >= casilla.fila; i--){
			int fila_actual = 0;
			for(int j = casilla.columna; j <= casilla.columna+num_columnas; j++)
				canvas.drawBitmap(world, (j-casilla.columna)*TAM_CAS-casilla.backX, fila_actual*TAM_CAS+(TAM_CAS-size.y%TAM_CAS)+casilla.forwY,paint);
			fila_actual++;
		}
*/				
		if(Exchanger.kworld.karel.posicion.fila < maxScreenXY.y+1 && Exchanger.kworld.karel.posicion.fila >= minScreenXY.y && 
				Exchanger.kworld.karel.posicion.columna < maxScreenXY.x+1 && Exchanger.kworld.karel.posicion.columna >= minScreenXY.x)
			switch (Exchanger.kworld.karel.orientacion) {
			case poodleDeveloper.karel.data.karelmovil.KWorld.NORTE:
				canvas.drawBitmap(karelN,
						(Exchanger.kworld.karel.posicion.columna-MIN_SCREEN_X)*TAM_CAS,
						(((((int)(size.y/TAM_CAS))-(Exchanger.kworld.karel.posicion.fila-MIN_SCREEN_Y))-1)*TAM_CAS)+FREE_SPACE,
						paint);
				break;
			case poodleDeveloper.karel.data.karelmovil.KWorld.ESTE:
				canvas.drawBitmap(karelE,
						(Exchanger.kworld.karel.posicion.columna-minScreenXY.x)*TAM_CAS,
						(((((int)(size.y/TAM_CAS))-(Exchanger.kworld.karel.posicion.fila-minScreenXY.y))-1)*TAM_CAS)+FREE_SPACE,
						paint);
				break; 
			case poodleDeveloper.karel.data.karelmovil.KWorld.SUR:
				canvas.drawBitmap(karelS,
						(Exchanger.kworld.karel.posicion.columna-minScreenXY.x)*TAM_CAS,
						(((((int)(size.y/TAM_CAS))-(Exchanger.kworld.karel.posicion.fila-minScreenXY.y))-1)*TAM_CAS)+FREE_SPACE,
						paint);
				break;
			case poodleDeveloper.karel.data.karelmovil.KWorld.OESTE:
				canvas.drawBitmap(karelO,
						(Exchanger.kworld.karel.posicion.columna-minScreenXY.x)*TAM_CAS,
						(((((int)(size.y/TAM_CAS))-(Exchanger.kworld.karel.posicion.fila-minScreenXY.y))-1)*TAM_CAS)+FREE_SPACE,
						paint);
				break;
			default:
				break;
			}
		/*for(poodleDeveloper.karel.data.karelmovil.KCasilla c: Exchanger.kworld.casillas.values()){ 
			if(c.fila < maxScreenXY.y+1 && c.fila >= minScreenXY.y && c.columna < maxScreenXY.x+1 && c.columna >= minScreenXY.x){
				if(c.paredes.size() > 0){
					paint.setColor(Color.BLACK);
					paint.setStrokeWidth(6);
					for(int p : c.paredes)
						switch(p){
						case poodleDeveloper.karel.data.karelmovil.KWorld.NORTE: 
							canvas.drawLine((c.columna%(maxScreenXY.x-minScreenXY.x)-1)*54+3, 
											(maxScreenXY.y-c.fila%(maxScreenXY.y-minScreenXY.y))*54+120, 
											(c.columna%(maxScreenXY.x-minScreenXY.x))*54+3, 
											(maxScreenXY.y-c.fila%(maxScreenXY.y-minScreenXY.y))*54+120,
											paint); 
							break;
						case poodleDeveloper.karel.data.karelmovil.KWorld.ESTE:
							canvas.drawLine((c.columna%(maxScreenXY.x-minScreenXY.x))*54+5,
											(maxScreenXY.y-c.fila%(maxScreenXY.y-minScreenXY.y))*54+120,
											(c.columna%(maxScreenXY.x-minScreenXY.x))*54+5,
											(maxScreenXY.y-c.fila%(maxScreenXY.y-minScreenXY.y))*54+120+54,
											paint);
							break;
						}
				}
				if(c.zumbadores > 0 ){
					paint.setColor(Color.GREEN); 
					paint.setStrokeWidth(18);
					canvas.drawCircle((c.columna%(maxScreenXY.x-minScreenXY.x)-1)*54+31,
									 (maxScreenXY.y-c.fila%(maxScreenXY.y-minScreenXY.y))*54+112+34,
									 8, paint);
					paint.setColor(Color.DKGRAY);
					paint.setStrokeWidth(1);
					if(c.zumbadores > 9)
						canvas.drawText(String.valueOf(c.zumbadores),
							(c.columna%(maxScreenXY.x-minScreenXY.x)-1)*54+24,
							(maxScreenXY.y-c.fila%(maxScreenXY.y-minScreenXY.y))*54+112+37, paint);
					else
						canvas.drawText(String.valueOf(c.zumbadores),
								(c.columna%(maxScreenXY.x-minScreenXY.x)-1)*54+27,
								(maxScreenXY.y-c.fila%(maxScreenXY.y-minScreenXY.y))*54+112+37, paint);
				}else if(c.zumbadores == -1){
					paint.setColor(Color.GREEN);
					paint.setStrokeWidth(18);
					canvas.drawCircle((c.columna%(maxScreenXY.x-minScreenXY.x)-1)*54+31,
									 (maxScreenXY.y-c.fila%(maxScreenXY.y-minScreenXY.y))*54+112+34,
									 8, paint);
					paint.setColor(Color.DKGRAY);
					paint.setStrokeWidth(1);
					canvas.drawText("-1",
							(c.columna%(maxScreenXY.x-minScreenXY.x)-1)*54+24,
							(maxScreenXY.y-c.fila%(maxScreenXY.y-minScreenXY.y))*54+112+37, paint);
				}
			}
		}
		//canvas.drawBitmap(karelS,100,25,paint);
		*/
				
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		thread = new KThread(getHolder(), this);
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		thread.setRunning(false);
		while(retry){
			try{
				thread.join();
				retry = false;
			}catch(InterruptedException e){
				
			}
		}
		
	}
	
	public boolean onTouchEvent(MotionEvent event){
		int evento = event.getAction();
		switch (evento) {
		case MotionEvent.ACTION_DOWN:
			estoyArrastrando = true;
			firstX = (int)event.getX();
			firstY = (int)event.getY();
			lastX = firstX;
			lastY = firstY;
			//	 System.out.println(lastX+"  "+lastY);
			System.out.println("Max: "+MAX_SCREEN_X+"x"+MAX_SCREEN_Y+"     Min: "+MIN_SCREEN_X+"x"+MIN_SCREEN_Y);
			break;
		case MotionEvent.ACTION_MOVE:
			if(estoyArrastrando){
				int offsetX = (int)event.getX();
				int offsetY = (int)event.getY();
				lastX+=(offsetX-lastX);
				lastY+=(offsetY-lastY);
				if(Math.abs(lastX-firstX) >= TAM_CAS/2){
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
		WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		
		world = BitmapFactory.decodeResource(getResources(), R.drawable.bkworld);
		karelN = BitmapFactory.decodeResource(getResources(), R.drawable.knorte);
		karelE = BitmapFactory.decodeResource(getResources(), R.drawable.keste);
		karelS = BitmapFactory.decodeResource(getResources(), R.drawable.ksur);
		karelO = BitmapFactory.decodeResource(getResources(), R.drawable.koeste);
		getHolder().addCallback(this);
		TAM_CAS = world.getWidth();
		maxScreenXY = new Point();
		minScreenXY = new Point();
		size = getDisplaySize(display);
		maxScreenXY.set((int)size.x/TAM_CAS, (int)size.y/TAM_CAS);
		MAX_SCREEN_X = (int)(size.x/TAM_CAS);
		MAX_SCREEN_Y = (int)(size.y/TAM_CAS);
		MIN_SCREEN_X = 1;
		MIN_SCREEN_Y = 1;
		minScreenXY.set(1, 1);
		FREE_SPACE = size.y-(((int)(size.y/TAM_CAS))*TAM_CAS);
	}
	
}
