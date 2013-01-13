package poodleDeveloper.karel;

import poodleDeveloper.karel.fragments.KEditorFragment;
import android.os.Bundle;
import android.view.WindowManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;

public class KEditor extends SherlockFragmentActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		try{
        	requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }catch(Exception e){
        	
        }
		setContentView(R.layout.keditor_layout);
		
		@SuppressWarnings("unused")
		KEditorFragment fragment = (KEditorFragment)getSupportFragmentManager().findFragmentById(R.id.keditor);
	}
}
