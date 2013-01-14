package poodleDeveloper.karel.fragments;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import poodleDeveloper.karel.Exchanger;
import poodleDeveloper.karel.KWorldGraphics;
import poodleDeveloper.karel.R;
import poodleDeveloper.karel.data.grammar.Ejecutable;
import poodleDeveloper.karel.data.karelmovil.KGrammar;
import poodleDeveloper.karel.data.karelmovil.KRunner;
import poodleDeveloper.karel.data.karelmovil.KarelException;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;

public class KEditorFragment extends SherlockFragment implements View.OnClickListener{

	private String test = "iniciar-programa\ninicia-ejecucion\navanza;\navanza;\navanza;\nrepetir 3 veces gira-izquierda;\navanza;\navanza;\ntermina-ejecucion\nfinalizar-programa";
	private EditText textEdit;
	private ImageView newCode, openCode, saveCode, run;
	private Button tab, semiColon, dash;
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
		
		textEdit = (EditText)view.findViewById(R.id.editor);
		textEdit.setTypeface(Typeface.MONOSPACE);
		textEdit.setText(test);
		newCode = (ImageView)view.findViewById(R.id.openCode);
		newCode.setOnClickListener(this);
		openCode = (ImageView)view.findViewById(R.id.openCode);
		openCode.setOnClickListener(this);
		saveCode = (ImageView)view.findViewById(R.id.saveCode);
		saveCode.setOnClickListener(this);
		run = (ImageView)view.findViewById(R.id.run);
		run.setOnClickListener(this);
		tab = (Button)view.findViewById(R.id.tab);
		tab.setOnClickListener(this);
		semiColon = (Button)view.findViewById(R.id.semiColon);
		semiColon.setOnClickListener(this);
		dash = (Button)view.findViewById(R.id.dash);
		dash.setOnClickListener(this);
		return view;
	}

	public void writing(String item, int offset){
		int cursor = textEdit.getSelectionStart();
		CharSequence text = textEdit.getText().toString();
		CharSequence prevText = text.subSequence(0, cursor);
		CharSequence nextText = text.subSequence(cursor, text.length());
		textEdit.setText(prevText+item+nextText);
		textEdit.setSelection(cursor+offset);
	}
	
	public void addNewCode(){
		
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.newCode:
			Toast.makeText(getActivity(), "Nuevo", Toast.LENGTH_SHORT).show();
			addNewCode();
			break;
		case R.id.openCode:
			Toast.makeText(getActivity(), "Abrir", Toast.LENGTH_SHORT).show();
			break;
		case R.id.saveCode:
			Toast.makeText(getActivity(), "Guardar", Toast.LENGTH_SHORT).show();
			break;
		case R.id.run:
			String codigo = textEdit.getText().toString();
			InputStream is = new ByteArrayInputStream(codigo.getBytes());
			InputStreamReader isr = new InputStreamReader(is);
			try{
				Exchanger.kworld.limpiar();
				KGrammar grammar = new KGrammar(new BufferedReader(isr), true, false);
				grammar.verificar_sintaxis();
				Ejecutable exe = grammar.expandir_arbol();
				Exchanger.krunner = new KRunner(exe, Exchanger.kworld);
				startActivity(new Intent(getActivity(),KWorldGraphics.class));
			}catch(KarelException e){
				Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.tab:
			writing("   ", 3);
			break;
		case R.id.semiColon:
			writing(";", 1);
			break;
		case R.id.dash:
			writing("-", 1);
			break;
		}
	}
}
