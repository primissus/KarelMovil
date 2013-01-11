package poodleDeveloper.karel.fragments;

import poodleDeveloper.karel.KWorld;
import poodleDeveloper.karel.R;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;

public class KEditorFragment extends SherlockFragment{

	public KEditorFragment(){
		;
	}
	
	public void onAttach(SherlockActivity activity) {
		super.onAttach(activity);
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);
	}
	
	public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstance){
		View view = inflater.inflate(R.layout.keditor_fragment, container,false);
		
		final EditText codigo = (EditText)view.findViewById(R.id.editor);
		codigo.setTypeface(Typeface.MONOSPACE);
		
		Button openWorld = (Button)view.findViewById(R.id.openWorld);
		openWorld.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "Abrir", Toast.LENGTH_SHORT).show();
			}
		});
		Button saveWorld = (Button)view.findViewById(R.id.saveWorld);
		saveWorld.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "Guardar", Toast.LENGTH_SHORT).show();
			}
		});
		Button run = (Button)view.findViewById(R.id.run);
		run.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		return view;
	}
}
