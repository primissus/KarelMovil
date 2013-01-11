package poodleDeveloper.karel;

import poodleDeveloper.karel.fragments.KEditorFragment;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class KEditor extends SherlockFragmentActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.keditor_layout);
		
		KEditorFragment fragment = (KEditorFragment)getSupportFragmentManager().findFragmentById(R.id.keditor);
	}
}
