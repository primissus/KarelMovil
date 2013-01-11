package poodleDeveloper.karel;

import java.util.concurrent.ExecutionException;


import poodleDeveloper.karel.data.karelmovil.KWorld;

import com.actionbarsherlock.app.SherlockActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Main extends SherlockActivity {

	private final static String url = "http://127.0.0.1/mundo.json";
	public static KWorld kworld;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kworld = new KWorld();
        startActivity(new Intent(this, KEditor.class));
        /*
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        JSONParser j = new JSONParser();
		try {
			j.init(url,this);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}*/
    }
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    }
}
