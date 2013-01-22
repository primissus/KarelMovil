package poodleDeveloper.karel;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import poodleDeveloper.karel.data.karelmovil.KCasilla;
import poodleDeveloper.karel.data.karelmovil.KPosition;
import poodleDeveloper.karel.data.karelmovil.KRunner;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
	
	private static final int REQUEST_PICK_FILE = 1;
	private static int TAM_CAS;	
	private static int FREE_SPACE; 
	private static int MAX_SCREEN_X;
	private static int MAX_SCREEN_Y;
	private static int MIN_SCREEN_X;
	private static int MIN_SCREEN_Y;
	private static int WALL_AREA;
	private KThread thread;
	private Bitmap world,karelN,karelE,karelS,karelO;
	private Point size;
	private int firstX,firstY,lastX,lastY;
	private static boolean estoyArrastrando = false, addingBeeper = false, addingWall = false, deletingBeeper = false, deletingWall = false;	
	final Handler handler = new Handler();
	private static Context context;
	private Thread t;
	private static int NUM_BEEPERS;
	private static int NUMBER_ITEMS;
	private static Button beeper, delB, delW, wall;
	private static boolean newWorldOn = false, EXISTING_WORLD = false;
	private static String file_name = "";
	
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
		if(Exchanger.krunner != null){
			Exchanger.krunner.step_run();  //KRunner ya fue inicializado previamente, acomodamos las variables
			kThread();
		}
		//kThread();  
		NUM_BEEPERS = 0;
		NUMBER_ITEMS = 0;
	}
	
	public static void loadButtons(Button addBeeper, Button addWall, Button deleteBeeper, Button deleteWall, Button new_world, Button open_world, Button save_world){
		beeper = addBeeper;
		delB = deleteBeeper;
		delW = deleteWall;
		wall = addWall;
		beeper.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!deletingBeeper && !deletingWall){
					if(addingWall){
						addingWall = false;
						wall.setPressed(false);
					}
					final EditText input = new EditText(context);
					input.setInputType(InputType.TYPE_CLASS_PHONE);
					new AlertDialog.Builder(context).setTitle("Agregar Zumbador").setMessage("¿Cuantos zumbadores deseas agregar? (-1 = Infinitos)")
					    .setView(input).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					         public void onClick(DialogInterface dialog, int whichButton) {				
						        	if(!input.getText().toString().equals("")){
						        		int numberBeepers = Integer.parseInt(input.getText().toString());
						        		System.out.println(numberBeepers);
							            if(numberBeepers > 99)
							            	Toast.makeText(context, "El número máximo de zumbadores es 99, vuelve a intentarlo", Toast.LENGTH_SHORT).show();
							            else{
							            	Toast.makeText(context, "Pulsa la casilla donde quieres colocar los zumbadores", Toast.LENGTH_SHORT).show();
							            	addingBeeper = true;
							            	NUM_BEEPERS = numberBeepers;
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
		wall.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP){
					if(!deletingBeeper && !deletingWall){
						if(!addingWall){
							addingWall = true;
							wall.setPressed(true);
							Toast.makeText(context, "Toca cerca de los bordes de las casillas para agregar un muro", Toast.LENGTH_SHORT).show();
						}else{
							addingWall = false;
							wall.setPressed(false);
						}
					}else{
						Toast.makeText(context, "Modo de borrado activado, pulse el icono para desactivarlo", Toast.LENGTH_SHORT).show();
					}
				}
				return true;
			}
		});
		delB.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP)
					if(!addingWall)
						if(NUMBER_ITEMS > 0)
							if(!deletingBeeper){
								if(deletingWall){
									delW.setPressed(false);
									deletingWall = false;
								}
								deletingBeeper = true;
								delB.setPressed(true);
							}else{
								deletingBeeper = false;
								delB.setPressed(false);
							}
						else
							Toast.makeText(context, "No hay movimientos que deshacer", Toast.LENGTH_SHORT).show();
					else
						Toast.makeText(context, "Modo de agregado activado, pulse el icono para desactivarlo", Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		delW.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP)
					if(!addingWall)
						if(NUMBER_ITEMS > 0)
							if(!deletingWall){
								if(deletingBeeper){
									deletingBeeper = false;
									delB.setPressed(false);
								}
								deletingWall = true;
								delW.setPressed(true);
							}else{
								deletingWall = false;
								delW.setPressed(false);
							}
						else
							Toast.makeText(context, "No hay movimientos que deshacer", Toast.LENGTH_SHORT).show();
					else
						Toast.makeText(context, "Modo de agregado activado, pulse el icono para desactivarlo", Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		new_world.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!newWorldOn){
					newWorldOn = true;
					Exchanger.kworld.limpiar();
					EXISTING_WORLD = false;
				}else{
					new AlertDialog.Builder(context).setTitle("KarelTheRobot").setMessage("¿Deseas guardar el mundo actual?")
				    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
				         public void onClick(DialogInterface dialog, int whichButton) {
				        	 saveFile();
				        	 newWorldOn = true;
				        	 EXISTING_WORLD = false;
				        	 Exchanger.kworld.limpiar();
				         }
				    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
				         public void onClick(DialogInterface dialog, int whichButton) {
				        	 newWorldOn = true;
				        	 EXISTING_WORLD = false;
				        	 Exchanger.kworld.limpiar();
				         }
				    }).show();
				}
			}
		});
		open_world.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!newWorldOn){
					Intent intent = new Intent(context, FilePickerActivity.class);
					intent.putExtra(FilePickerActivity.EXTRA_FILE_PATH, Exchanger.WORLD_PATH);
					((SherlockActivity)context).startActivityForResult(intent, REQUEST_PICK_FILE);
				}else{
					new AlertDialog.Builder(context).setTitle("KarelTheRobot").setMessage("¿Deseas guardar el mundo actual?")
				    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
				         public void onClick(DialogInterface dialog, int whichButton) {
				        	 saveFile();
				        	 Intent intent = new Intent(context, FilePickerActivity.class);
					 		 intent.putExtra(FilePickerActivity.EXTRA_FILE_PATH, Exchanger.WORLD_PATH);
					 		 ((SherlockActivity)context).startActivityForResult(intent, REQUEST_PICK_FILE);
				         }
				    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
				         public void onClick(DialogInterface dialog, int whichButton) {
				        	Intent intent = new Intent(context, FilePickerActivity.class);
				 			intent.putExtra(FilePickerActivity.EXTRA_FILE_PATH, Exchanger.WORLD_PATH);
				 			((SherlockActivity)context).startActivityForResult(intent, REQUEST_PICK_FILE);
				         }
				    }).show();
				}
				EXISTING_WORLD = true;
			}
		});
		save_world.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveFile();
			}
		});
	}
	
	private static void saveFile(){
			if(!EXISTING_WORLD){
				final EditText input = new EditText(context);
				new AlertDialog.Builder(context).setTitle("KarelTheRobot").setMessage("Escribe el nombre del archivo").setView(input)
			    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			         public void onClick(DialogInterface dialog, int whichButton) {
			        	 file_name = Exchanger.WORLD_PATH+File.separator+input.getText().toString()+".mdo";
			        	 perform();
			         }
			    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			         public void onClick(DialogInterface dialog, int whichButton) {}
			    }).show();
			}else{
				file_name = Exchanger.world.toString();
				perform();
			}
			
	}
	
	public static void perform(){
		try {
			poodleDeveloper.karel.data.karelmovil.KWorld w = Exchanger.kworld;
        	JSONObject cosa = w.exporta_mundo();
        	String res = cosa.toString();
        	File f = new File(file_name);
        	FileWriter fw = new FileWriter(f);
        	fw.write(res);
        	fw.close();
			Toast.makeText(context, "Se guardo el archivo como: "+file_name, Toast.LENGTH_SHORT).show();
			newWorldOn = false;
			EXISTING_WORLD = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public static void loadFile(String file){
		try{
			FileReader fr = new FileReader(new File(file));
			BufferedReader br = new BufferedReader(fr);
			StringBuilder sb = new StringBuilder();
			String cad = "";
			while((cad = br.readLine())!= null){
				sb.append(cad);
			}
			JSONObject json = new JSONObject(new String(sb));
			Exchanger.kworld.limpiar();
			Exchanger.kworld.cargaJSON(json);
			EXISTING_WORLD = true;
		}catch(JSONException e){
			e.getMessage();
			e.getCause();
			e.printStackTrace();
		}catch(Exception ex){
			ex.getMessage();
		}
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
				else if(estado == KRunner.ESTADO_TERMINADO && !Exchanger.SUCESS_EXECUTED){
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(activity, "FELICIDADES, Karel llegó a su destino", Toast.LENGTH_SHORT).show();
							Exchanger.SUCESS_EXECUTED = true;
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
				if(casilla.paredes.size() > 0){
					paint.setStrokeWidth(6);
					paint.setColor(Color.BLACK);
					for(int pared : casilla.paredes){
						if(pared == poodleDeveloper.karel.data.karelmovil.KWorld.NORTE)
							canvas.drawLine(xB, yB, xB+TAM_CAS, yB, paint);
						else if(pared == poodleDeveloper.karel.data.karelmovil.KWorld.ESTE)
							canvas.drawLine(xB+TAM_CAS, yB, xB+TAM_CAS, yB+TAM_CAS, paint);
						else if(pared == poodleDeveloper.karel.data.karelmovil.KWorld.SUR)
							canvas.drawLine(xB, yB+TAM_CAS, xB+TAM_CAS, yB+TAM_CAS, paint);
						else if(pared == poodleDeveloper.karel.data.karelmovil.KWorld.OESTE)
							canvas.drawLine(xB, yB, xB, yB+TAM_CAS, paint);
					}
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
		//kThread();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		thread.setRunning(false);
		while(retry){
			try{
				/** Detenemos ambos hilos al cerrar la vista*/
				thread.join();
				if(t!=null)
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
			if(deletingBeeper){
				int columna = ((int)event.getX()/TAM_CAS)+MIN_SCREEN_X;
				int fila = (MAX_SCREEN_Y - (((int)event.getY())+FREE_SPACE)/TAM_CAS);
				/** Buscamos las coordenadas obtenidas en todas las casillas para eliminar el item*/
				for(KCasilla casilla: Exchanger.kworld.casillas.values())
					if(casilla.fila == fila && casilla.columna == columna && casilla.zumbadores != 0){
						casilla.zumbadores = 0;
						NUMBER_ITEMS--;
						break;
					}
					else
						Toast.makeText(context, "No hay zumbadores en esa casilla", Toast.LENGTH_SHORT).show();
			}/*else if(deletingWall){
				double lastX = event.getX();
				double lastY = event.getY();
				int x = ((int)event.getX()/TAM_CAS)+MIN_SCREEN_X;
				int y = (MAX_SCREEN_Y - (((int)event.getY())+FREE_SPACE)/TAM_CAS);
				int columna = ((int)lastX/TAM_CAS)+1;
				int fila = ((int)(size.y-(event.getY()+FREE_SPACE)))/TAM_CAS+1;
				int toDelete;	
				if( lastX > (columna-1)*TAM_CAS && lastX < (columna-1)*TAM_CAS+WALL_AREA){
					toDelete = poodleDeveloper.karel.data.karelmovil.KWorld.OESTE;
				}else if(lastX > (columna*TAM_CAS-WALL_AREA) && lastX < (columna*TAM_CAS)){
					toDelete = poodleDeveloper.karel.data.karelmovil.KWorld.ESTE;
				}else if(lastY < size.y-(fila*TAM_CAS-WALL_AREA) && lastY > size.y-(fila*TAM_CAS)){
					toDelete = poodleDeveloper.karel.data.karelmovil.KWorld.NORTE;
				}else if(lastY > size.y-(fila-1)*TAM_CAS-WALL_AREA && lastY < size.y-(fila-1)*TAM_CAS){
					toDelete = poodleDeveloper.karel.data.karelmovil.KWorld.SUR;
				}
				NUMBER_ITEMS--;
			}*/
			else if(addingBeeper){
				int columna = ((int)event.getX()/TAM_CAS)+MIN_SCREEN_X;
				int fila = (MAX_SCREEN_Y - (((int)event.getY())+FREE_SPACE)/TAM_CAS);
				Exchanger.kworld.pon_zumbadores(new KPosition(fila, columna), NUM_BEEPERS);
				NUMBER_ITEMS++;
			}else if(addingWall || deletingWall){
				/** Obtenemos el evento*/
				double lastX = event.getX();
				double lastY = event.getY();
				/** Obtenemos las coordenadas en el mundo virtual de Karel*/
				int x = ((int)event.getX()/TAM_CAS)+MIN_SCREEN_X;
				int y = (MAX_SCREEN_Y - (((int)event.getY())+FREE_SPACE)/TAM_CAS);
				/** Obtenemos las coordenadas en la pantalla*/
				int columna = ((int)lastX/TAM_CAS)+1;
				int fila = ((int)(size.y-(event.getY()+FREE_SPACE)))/TAM_CAS+1;
				if( lastX > (columna-1)*TAM_CAS && lastX < (columna-1)*TAM_CAS+WALL_AREA){
					Exchanger.kworld.conmuta_pared(new KPosition(y, x), poodleDeveloper.karel.data.karelmovil.KWorld.OESTE);
				}else if(lastX > (columna*TAM_CAS-WALL_AREA) && lastX < (columna*TAM_CAS)){
					Exchanger.kworld.conmuta_pared(new KPosition(y, x), poodleDeveloper.karel.data.karelmovil.KWorld.ESTE); 
				}else if(lastY < size.y-(fila*TAM_CAS-WALL_AREA) && lastY > size.y-(fila*TAM_CAS)){
						Exchanger.kworld.conmuta_pared(new KPosition(y, x), poodleDeveloper.karel.data.karelmovil.KWorld.NORTE);
				}else if(lastY > size.y-(fila-1)*TAM_CAS-WALL_AREA && lastY < size.y-(fila-1)*TAM_CAS){
						Exchanger.kworld.conmuta_pared(new KPosition(y, x), poodleDeveloper.karel.data.karelmovil.KWorld.SUR);
				}
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
		WALL_AREA = TAM_CAS/4;
		size = getDisplaySize(display);
		MAX_SCREEN_X = (int)(size.x/TAM_CAS);
		MAX_SCREEN_Y = (int)(size.y/TAM_CAS);
		MIN_SCREEN_X = 1;
		MIN_SCREEN_Y = 1;
		FREE_SPACE = size.y-(((int)(size.y/TAM_CAS))*TAM_CAS);
	}
	
}
