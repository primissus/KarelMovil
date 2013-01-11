package poodleDeveloper.karel;

import poodleDeveloper.karel.data.karelmovil.KWorld;

import com.actionbarsherlock.app.SherlockActivity;

import android.content.Intent;
import android.os.Bundle;

public class Main extends SherlockActivity {

	public static KWorld kworld;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kworld = new KWorld();
        startActivity(new Intent(this, KEditor.class));
    }
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    }
}
