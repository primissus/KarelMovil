package poodleDeveloper.karel.fragments;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import poodleDeveloper.karel.KWorldGraphics;
import poodleDeveloper.karel.R;
import poodleDeveloper.karel.data.karelmovil.KGrammar;
import poodleDeveloper.karel.data.karelmovil.KarelException;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;

public class KEditorFragment extends SherlockFragment{

	private String test = "iniciar-programa\ninicia-ejecucion\navanza;\ntermina-ejecucion\nfinalizar-programa";
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
		
		final EditText textEdit = (EditText)view.findViewById(R.id.editor);
		textEdit.setTypeface(Typeface.MONOSPACE);
		textEdit.setText(test);
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
				String codigo = textEdit.getText().toString();
				InputStream is = new ByteArrayInputStream(codigo.getBytes());
				InputStreamReader isr = new InputStreamReader(is);
				try{
					KGrammar grammar = new KGrammar(new BufferedReader(isr), false);
					grammar.verificar_sintaxis();
					startActivity(new Intent(getActivity(),KWorldGraphics.class));
				}catch(KarelException e){
					Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
				}
			}
		});
		return view;
	}
}
