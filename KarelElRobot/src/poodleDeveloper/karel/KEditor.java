package poodleDeveloper.karel;

import java.io.File;
import java.io.IOException;

import poodleDeveloper.karel.fragments.KEditorFragment;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;

public class KEditor extends SherlockFragmentActivity{

	private static final int REQUEST_PICK_FILE = 1;
	private KEditorFragment fragment;
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
		fragment = (KEditorFragment)getSupportFragmentManager().findFragmentById(R.id.keditor);
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
			dialog();
			break;
		}
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
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			switch(requestCode) {
			case REQUEST_PICK_FILE:
				if(data.hasExtra(FilePickerActivity.EXTRA_FILE_PATH)) {
					Exchanger.code = new File(data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH));
					try {
						fragment.loadFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
