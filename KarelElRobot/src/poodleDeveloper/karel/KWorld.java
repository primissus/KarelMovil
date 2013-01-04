package poodleDeveloper.karel;

import java.util.ArrayList;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;
import net.londatiga.android.QuickAction.OnActionItemClickListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.internal.view.menu.ActionMenuItem;

import poodleDeveloper.karel.data.KCasilla;
import poodleDeveloper.karel.data.Karel;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class KWorld extends SurfaceView implements SurfaceHolder.Callback{

	public KWorld(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public static final int NORTE = 1;
	public static final int ESTE = 2;
	public static final int SUR = 3;
	public static final int OESTE = 4;
	
	private KThread thread;
	private Bitmap world,karelN,karelE,karelS,karelO;
	private Point size;
	
	private Point maxScreenXY;
	private Point minScreenXY;
	private ArrayList<KCasilla> casillas;
	private Karel karel;
	private Context context;
	
	
	@SuppressLint("NewApi")
	public void init(Context context, ArrayList<KCasilla> result, Karel karel) {
		//super(context);
		this.context = context;
		WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		
		world = BitmapFactory.decodeResource(getResources(), R.drawable.kworld);
		karelN = BitmapFactory.decodeResource(getResources(), R.drawable.knorte);
		karelE = BitmapFactory.decodeResource(getResources(), R.drawable.keste);
		karelS = BitmapFactory.decodeResource(getResources(), R.drawable.ksur);
		karelO = BitmapFactory.decodeResource(getResources(), R.drawable.koeste);
		
		getHolder().addCallback(this);
		
		casillas = result;
		this.karel = karel;
		maxScreenXY = new Point();
		minScreenXY = new Point();
		size = getDisplaySize(display);
		maxScreenXY.set(size.x/54, size.y/54-2);
		minScreenXY.set(0, 0);
		
		Toast.makeText(context, maxScreenXY.x+"x"+maxScreenXY.y, Toast.LENGTH_SHORT).show();
		initMenuItems();
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
	
	private void initMenuItems(){
		Button addWall = (Button)((SherlockActivity)context).findViewById(R.id.addWall);
		addWall.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "Toca la pantalla para agregar un muro", Toast.LENGTH_SHORT).show();
			}
		});
		Button addBeeper = (Button)((SherlockActivity)context).findViewById(R.id.addBeeper);
		addBeeper.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "Toca la pantalla para agregar un zumador", Toast.LENGTH_SHORT).show();
			}
		});
		Button openWorld = (Button)((SherlockActivity)context).findViewById(R.id.openWorld);
		openWorld.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
			}
		});
	}
	@Override
	public void onDraw(Canvas canvas){ 
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		paint.setAntiAlias(true);
		
		for(float i = size.y; i > -54; i-=54)
			for(float j = 0; j < size.x+54; j+=54)
					canvas.drawBitmap(world, j, i, paint);
		
		for(KCasilla c: casillas){ 
			if(c.getFila() < maxScreenXY.y && c.getFila() > minScreenXY.y && c.getColumna() < maxScreenXY.x && c.getColumna() > minScreenXY.x){
				if(c.getParedes().length > 0){
					paint.setColor(Color.BLACK);
					paint.setStrokeWidth(6);
					for(String p : c.getParedes())
						if(p.equals("norte")){ 
							canvas.drawLine((c.getColumna()%(maxScreenXY.x-minScreenXY.x)-1)*54+3, 
											(maxScreenXY.y-c.getFila()%(maxScreenXY.y-minScreenXY.y))*54+120, 
											(c.getColumna()%(maxScreenXY.x-minScreenXY.x))*54+3, 
											(maxScreenXY.y-c.getFila()%(maxScreenXY.y-minScreenXY.y))*54+120,
											paint); 
						}else if(p.equals("este")){
							canvas.drawLine((c.getColumna()%(maxScreenXY.x-minScreenXY.x))*54+5,
											(maxScreenXY.y-c.getFila()%(maxScreenXY.y-minScreenXY.y))*54+120,
											(c.getColumna()%(maxScreenXY.x-minScreenXY.x))*54+5,
											(maxScreenXY.y-c.getFila()%(maxScreenXY.y-minScreenXY.y))*54+120+54,
											paint);
						}
				}
				if(c.getZumbadores() > 0 ){
					paint.setColor(Color.GREEN);
					paint.setStrokeWidth(5);
					canvas.drawCircle((c.getColumna()%(maxScreenXY.x-minScreenXY.x)-1)*54+31,
									 (maxScreenXY.y-c.getFila()%(maxScreenXY.y-minScreenXY.y))*54+112+34,
									 16, paint);
					paint.setColor(Color.DKGRAY);
					paint.setStrokeWidth(1);
					if(c.getZumbadores() > 9)
						canvas.drawText(String.valueOf(c.getZumbadores()),
							(c.getColumna()%(maxScreenXY.x-minScreenXY.x)-1)*54+24,
							(maxScreenXY.y-c.getFila()%(maxScreenXY.y-minScreenXY.y))*54+112+37, paint);
					else
						canvas.drawText(String.valueOf(c.getZumbadores()),
								(c.getColumna()%(maxScreenXY.x-minScreenXY.x)-1)*54+27,
								(maxScreenXY.y-c.getFila()%(maxScreenXY.y-minScreenXY.y))*54+112+37, paint);
				}else if(c.getZumbadores() == -1){
					paint.setColor(Color.GREEN);
					paint.setStrokeWidth(5);
					canvas.drawCircle((c.getColumna()%(maxScreenXY.x-minScreenXY.x)-1)*54+31,
									 (maxScreenXY.y-c.getFila()%(maxScreenXY.y-minScreenXY.y))*54+112+34,
									 16, paint);
					paint.setColor(Color.DKGRAY);
					paint.setStrokeWidth(1);
					canvas.drawText("-1",
							(c.getColumna()%(maxScreenXY.x-minScreenXY.x)-1)*54+24,
							(maxScreenXY.y-c.getFila()%(maxScreenXY.y-minScreenXY.y))*54+112+37, paint);
				}
			}
		}
		
		switch (karel.orientacion) {
		case NORTE:
			canvas.drawBitmap(karelN,
					(karel.columna%(maxScreenXY.x-minScreenXY.x)-1)*54+8,
					(maxScreenXY.y-karel.fila%(maxScreenXY.y-minScreenXY.y))*54+122,
					paint);
			break;
		case ESTE:
			canvas.drawBitmap(karelE,
					(karel.columna%(maxScreenXY.x-minScreenXY.x)-1)*54+8,
					(maxScreenXY.y-karel.fila%(maxScreenXY.y-minScreenXY.y))*54+122,
					paint);
			break;
		case SUR:
			canvas.drawBitmap(karelS,
					(karel.columna%(maxScreenXY.x-minScreenXY.x)-1)*54+8,
					(maxScreenXY.y-karel.fila%(maxScreenXY.y-minScreenXY.y))*54+122,
					paint);
			break;
		case OESTE:
			canvas.drawBitmap(karelO,
					(karel.columna%(maxScreenXY.x-minScreenXY.x)-1)*54+8,
					(maxScreenXY.y-karel.fila%(maxScreenXY.y-minScreenXY.y))*54+1122,
					paint);
			break;
		default:
			break;
		}
				
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub 
		
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

}
