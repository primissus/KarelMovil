package poodleDeveloper.karel;

import poodleDeveloper.karel.data.karelmovil.KRunner;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class KThread extends Thread{
	private SurfaceHolder sh;
	private KWorld view;
	private boolean run;
	
	public KThread(SurfaceHolder sh, KWorld view){
		this.sh = sh;
		this.view = view;
		run = false;
	}
	
	public void setRunning(boolean run){
		this.run = run;
	}
	
	public void run(){
		Canvas canvas = null;
		while(run){
			canvas = null;
			try{
				canvas = sh.lockCanvas(null);
				synchronized (sh) {
					view.onDraw(canvas);
				}
			}catch(Exception e){
				
			}finally{
			
				if(canvas!=null)
					sh.unlockCanvasAndPost(canvas);
			}
		}
	}
	
}