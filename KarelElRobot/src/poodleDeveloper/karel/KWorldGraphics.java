package poodleDeveloper.karel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;

public class KWorldGraphics extends SherlockActivity{

	private static final int REQUEST_PICK_FILE = 1;
	
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
		Button new_world = (Button)findViewById(R.id.newWorld);
		Button open_world = (Button)findViewById(R.id.openWorld);
		Button save_world = (Button)findViewById(R.id.saveWorld);
		Exchanger.world = new File(Exchanger.WORLD_PATH+File.separator+"mundo.mdo");
		try{
			FileReader fr = new FileReader(Exchanger.world);
			BufferedReader br = new BufferedReader(fr);
			StringBuilder sb = new StringBuilder();
			String cad = "";
			while((cad = br.readLine())!= null){
				sb.append(cad);
			}
			JSONObject json = new JSONObject(cad);
			Exchanger.kworld.cargaJSON(json);
		}catch(Exception e){
			e.getMessage();
		}
		KWorld.loadButtons(addBeeper, addWall, deleteBeeper, deleteWall, new_world, open_world, save_world);
	}
	
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		this.getSupportMenuInflater().inflate(R.menu.editor_menu, menu);
	    return true;
	} 
	
	public void dialog(){
		final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.about_layout);
        dialog.setTitle("Acerca de KarelTheRobot Reloaded");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        TextView text = (TextView) dialog.findViewById(R.id.welcomeText);
        text.setText("Karel v1.0.0 - Kiwi\n\nProgramadores:\nAbraham Toriz Cruz - Núcleo\n@categulario\n\nDaniel García Alvarado - UI\n@houseckleiin\n\n" +
        		"Licencias de terceros:\n\nAction Bar Sherlock\nVersión: 4.2.0\nCopyright (c) 2012\nJake Wharton\n\nJava-JSON\nCopyright (c) 2013\n" +
        		"www.java2s.com");    
        dialog.show();
	  }
	
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		switch(item.getItemId()){
		case R.id.help_item:
			Toast.makeText(getApplicationContext(), "Ayuda", Toast.LENGTH_SHORT).show();
			break;
		case R.id.about_item:
			dialog();
			break;
		}
		return true;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			switch(requestCode) {
			case REQUEST_PICK_FILE:
				if(data.hasExtra(FilePickerActivity.EXTRA_FILE_PATH)) {
					try {
						KWorld.loadFile(data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
