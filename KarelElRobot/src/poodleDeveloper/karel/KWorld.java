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
		maxScreenXY.set(size.x/36, size.y/36-2);
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
	}
	@Override
	public void onDraw(Canvas canvas){ 
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		paint.setAntiAlias(true);
		
		for(float i = size.y; i > -36; i-=36)
			for(float j = 0; j < size.x+36; j+=36)
					canvas.drawBitmap(world, j, i, paint);
		
		for(KCasilla c: casillas){ 
			if(c.getFila() < maxScreenXY.y && c.getFila() > minScreenXY.y && c.getColumna() < maxScreenXY.x && c.getColumna() > minScreenXY.x){
				if(c.getParedes().length > 0){
					paint.setColor(Color.BLACK);
					paint.setStrokeWidth(6);
					for(String p : c.getParedes())
						if(p.equals("norte")){ 
							canvas.drawLine((c.getColumna()%(maxScreenXY.x-minScreenXY.x)-1)*36, 
											(maxScreenXY.y-c.getFila()%(maxScreenXY.y-minScreenXY.y))*36+76, 
											(c.getColumna()%(maxScreenXY.x-minScreenXY.x))*36, 
											(maxScreenXY.y-c.getFila()%(maxScreenXY.y-minScreenXY.y))*36+76,
											paint); 
						}else if(p.equals("este")){
							canvas.drawLine((c.getColumna()%(maxScreenXY.x-minScreenXY.x))*36+4,
											(maxScreenXY.y-c.getFila()%(maxScreenXY.y-minScreenXY.y))*36+76,
											(c.getColumna()%(maxScreenXY.x-minScreenXY.x))*36+4,
											(maxScreenXY.y-c.getFila()%(maxScreenXY.y-minScreenXY.y))*36+76+36,
											paint);
						}
				}
				if(c.getZumbadores() > 0 ){
					paint.setColor(Color.GREEN);
					paint.setStrokeWidth(5);
					canvas.drawCircle((c.getColumna()%(maxScreenXY.x-minScreenXY.x)-1)*36+22,
									 (maxScreenXY.y-c.getFila()%(maxScreenXY.y-minScreenXY.y))*36+76+19,
									 12, paint);
					paint.setColor(Color.DKGRAY);
					paint.setStrokeWidth(1);
					if(c.getZumbadores() > 9)
						canvas.drawText(String.valueOf(c.getZumbadores()),
							(c.getColumna()%(maxScreenXY.x-minScreenXY.x)-1)*36+16,
							(maxScreenXY.y-c.getFila()%(maxScreenXY.y-minScreenXY.y))*36+76+23, paint);
					else
						canvas.drawText(String.valueOf(c.getZumbadores()),
								(c.getColumna()%(maxScreenXY.x-minScreenXY.x)-1)*36+19,
								(maxScreenXY.y-c.getFila()%(maxScreenXY.y-minScreenXY.y))*36+76+23, paint);
				}else if(c.getZumbadores() == -1){
					paint.setColor(Color.GREEN);
					paint.setStrokeWidth(5);
					canvas.drawCircle((c.getColumna()%(maxScreenXY.x-minScreenXY.x)-1)*36+22,
									 (maxScreenXY.y-c.getFila()%(maxScreenXY.y-minScreenXY.y))*36+76+19,
									 12, paint);
					paint.setColor(Color.DKGRAY);
					paint.setStrokeWidth(1);
					canvas.drawText("-1",
							(c.getColumna()%(maxScreenXY.x-minScreenXY.x)-1)*36+17,
							(maxScreenXY.y-c.getFila()%(maxScreenXY.y-minScreenXY.y))*36+76+23, paint);
				}
			}
		}
		
		switch (karel.orientacion) {
		case NORTE:
			canvas.drawBitmap(karelN,
					(karel.columna%(maxScreenXY.x-minScreenXY.x)-1)*36+2,
					(maxScreenXY.y-karel.fila%(maxScreenXY.y-minScreenXY.y))*36+78,
					paint);
			break;
		case ESTE:
			canvas.drawBitmap(karelE,
					(karel.columna%(maxScreenXY.x-minScreenXY.x)-1)*36+2,
					(maxScreenXY.y-karel.fila%(maxScreenXY.y-minScreenXY.y))*36+78,
					paint);
			break;
		case SUR:
			canvas.drawBitmap(karelS,
					(karel.columna%(maxScreenXY.x-minScreenXY.x)-1)*36+2,
					(maxScreenXY.y-karel.fila%(maxScreenXY.y-minScreenXY.y))*36+78,
					paint);
			break;
		case OESTE:
			canvas.drawBitmap(karelO,
					(karel.columna%(maxScreenXY.x-minScreenXY.x)-1)*36+2,
					(maxScreenXY.y-karel.fila%(maxScreenXY.y-minScreenXY.y))*36+78,
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
