package poodleDeveloper.karel;

import poodleDeveloper.karel.fragments.KEditorFragment;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

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
	
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		this.getSupportMenuInflater().inflate(R.menu.editor_menu, menu);
	    return true;
	} 
	
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		switch(item.getItemId()){
		case R.id.help_item:
			Toast.makeText(getApplicationContext(), "Ayuda", Toast.LENGTH_SHORT).show();
			break;
		case R.id.about_item:
			Toast.makeText(getApplicationContext(), "Acerda de", Toast.LENGTH_SHORT).show();
			break;
		}
		return true;
	}
}
