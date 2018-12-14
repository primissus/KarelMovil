package poodleDeveloper.karel.Activities;

import java.io.File;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;

import poodleDeveloper.karel.R;
import poodleDeveloper.karel.data.karelmovil.KWorld;
import poodleDeveloper.tools.Exchanger;
import android.os.Bundle;
import android.os.Environment;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Toast;

public class Splash extends SherlockActivity {

	protected boolean _active = true;
    protected int _splashTime = 3000;
    private Context context = this;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        	requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }catch(Exception e){
        	
        }
        setContentView(R.layout.activity_splash); 
        //setTitle("");
        Thread splashTread = new Thread() {
			@Override
            public void run() {
                try {
                    int waited = 0;
                    while(_active && (waited < _splashTime)) {
                        sleep(100);
                        if(_active) {
                            waited += 100;
                        }
                    }
                } catch(InterruptedException e) {
                    // do nothing
                } finally {
                    finish();
                    if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                        Log.d("Karel el Robot", "No SDCARD");
                    }else{
                        File root_path = new File(Exchanger.ROOT_PATH);
                        File worlds_path = new File(Exchanger.WORLD_PATH);
                        File codes_path = new File(Exchanger.CODE_PATH);
                        File karelapan_path = new File(Exchanger.KARELAPAN_PATH);
                        if(!root_path.exists()){
                        	showMessage("El directorio raíz de Karel fue eliminado, se creará nuevamente");
                        	root_path.mkdirs();
                        	worlds_path.mkdirs();
                        	codes_path.mkdirs();
                        }else{ 
                        	if(!worlds_path.exists()){
                        		worlds_path.mkdirs();
                        		showMessage("El directorio de mundos de Karel fue eliminado, se creará nuevamente");
                        	}
                        	if(!codes_path.exists()){
                        		codes_path.mkdirs();
                        		showMessage("El directorio de códigos de Karel fue eliminado, se creará nuevamente");
                        	}
                        	if(!karelapan_path.exists()){
                        		karelapan_path.mkdirs();
                        		showMessage("El directorio de Karelapan de Karel fue eliminado, se creará nuevamente");
                        	}
                        }
                    }
                    Exchanger.kworld = new KWorld();
                    startActivity(new Intent(context, KEditor.class));
                    this.interrupt();
                }
            }
        };
        splashTread.start();
    }
    
	
	public void showMessage(final String str){
		final SherlockActivity activity = (SherlockActivity)context;
    	activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(context, str, Toast.LENGTH_LONG).show();
			}
		});
	}
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            _active = false;
        }
        return true;
    }
}
