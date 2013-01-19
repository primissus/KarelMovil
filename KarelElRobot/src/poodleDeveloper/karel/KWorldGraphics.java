package poodleDeveloper.karel;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;

public class KWorldGraphics extends SherlockActivity{

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		try{
        	requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }catch(Exception e){
        	
        }
		setContentView(R.layout.kworld_layout);
		Button addBeeper = (Button)findViewById(R.id.addBeeper);
		Button addWall = (Button)findViewById(R.id.addWall);
		Button deleteBeeper = (Button)findViewById(R.id.deleteBeeper);
		Button deleteWall = (Button)findViewById(R.id.deleteWall);
		KWorld.loadButtons(addBeeper, addWall, deleteBeeper, deleteWall);
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
