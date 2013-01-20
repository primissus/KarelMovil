package poodleDeveloper.karel.fragments;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import poodleDeveloper.karel.Exchanger;
import poodleDeveloper.karel.FilePickerActivity;
import poodleDeveloper.karel.KWorldGraphics;
import poodleDeveloper.karel.R;
import poodleDeveloper.karel.data.grammar.Ejecutable;
import poodleDeveloper.karel.data.karelmovil.KGrammar;
import poodleDeveloper.karel.data.karelmovil.KRunner;
import poodleDeveloper.karel.data.karelmovil.KarelException;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

	private static final int REQUEST_PICK_FILE = 1;
	
	//private String test = "iniciar-programa\ninicia-ejecucion\navanza;\navanza;\navanza;\nrepetir 3 veces gira-izquierda;\navanza;\navanza;\napagate;\ntermina-ejecucion\nfinalizar-programa";
	private EditText textEdit;
	private ImageView newCode, openCode, saveCode, run , world;
	private Button tab, semiColon, dash;
	private boolean newCodeOn = false, EXISTING_CODE = false;
	
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
		//textEdit.setText(test);
		textEdit.setEnabled(false);
		textEdit.setHint("Pulsa nuevo para empezar");
		textEdit.setTextSize(13);
		newCode = (ImageView)view.findViewById(R.id.newCode);
		newCode.setOnClickListener(this);
		openCode = (ImageView)view.findViewById(R.id.openCode); 
		openCode.setOnClickListener(this);
		saveCode = (ImageView)view.findViewById(R.id.saveCode);
		saveCode.setOnClickListener(this);
		run = (ImageView)view.findViewById(R.id.run);
		world = (ImageView)view.findViewById(R.id.switch_world);
		world.setOnClickListener(this);
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
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.newCode:
			newFile();
			break;
		case R.id.openCode:
			openFile();
			if(!textEdit.isEnabled())
				textEdit.setEnabled(true);
			break; 
		case R.id.saveCode:
			saveFile();
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
				Exchanger.SUCESS_EXECUTED = false;
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
		case R.id.switch_world:
			startActivity(new Intent(getActivity(),KWorldGraphics.class));
			break;
		}
	}
	
	public void newFile(){
		if(!newCodeOn && Exchanger.code == null){
			newCodeOn = true;
			textEdit.setEnabled(true);
			EXISTING_CODE = false;
			textEdit.setHint("");
			textEdit.setText("");
		}else{
			new AlertDialog.Builder(getActivity()).setTitle("KarelTheRobot").setMessage("¿Deseas guardar el archivo actual?")
		    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
		         public void onClick(DialogInterface dialog, int whichButton) {
		        	 saveFile();
		        	 newCodeOn = true;
		        	 EXISTING_CODE = false;
		        	 textEdit.setText("");
		         }
		    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
		         public void onClick(DialogInterface dialog, int whichButton) {
		        	 newCodeOn = true;
		        	 EXISTING_CODE = false;
		        	 textEdit.setText("");
		         }
		    }).show();
		}
	}
	
	public void openFile(){
		if(!newCodeOn && textEdit.getText().toString().equals("")){
			Intent intent = new Intent(getActivity(), FilePickerActivity.class);
			intent.putExtra(FilePickerActivity.EXTRA_FILE_PATH, Exchanger.CODE_PATH);
			getActivity().startActivityForResult(intent, REQUEST_PICK_FILE);
		}else{
			new AlertDialog.Builder(getActivity()).setTitle("KarelTheRobot").setMessage("¿Deseas guardar el archivo actual?")
		    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
		         public void onClick(DialogInterface dialog, int whichButton) {
		        	 saveFile();
		         }
		    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
		         public void onClick(DialogInterface dialog, int whichButton) {
		        	Intent intent = new Intent(getActivity(), FilePickerActivity.class);
		 			intent.putExtra(FilePickerActivity.EXTRA_FILE_PATH, Exchanger.CODE_PATH);
		 			getActivity().startActivityForResult(intent, REQUEST_PICK_FILE);
		         }
		    }).show();
		}
		EXISTING_CODE = true;
	}
	
	public void loadFile() throws IOException{
			FileReader fr = null;
			BufferedReader br = null;
			try {
				fr = new FileReader(Exchanger.code);
				br = new BufferedReader(fr);
				String aux;
				StringBuilder sb = new StringBuilder();
				while((aux = br.readLine())!= null){
					sb.append(aux+"\n");
				}
				String code = new String(sb);
				textEdit.setText(code);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			EXISTING_CODE = true;
	}
	
	private String file_name = "";
	
	public void saveFile(){
		if(!EXISTING_CODE){
			final EditText input = new EditText(getActivity());
			new AlertDialog.Builder(getActivity()).setTitle("KarelTheRobot").setMessage("Escribe el nombre del archivo").setView(input)
		    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		         public void onClick(DialogInterface dialog, int whichButton) {
		        	 file_name = Exchanger.CODE_PATH+File.separator+input.getText().toString()+".karel";
		         }
		    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		         public void onClick(DialogInterface dialog, int whichButton) {}
		    }).show();
		}else{
			file_name = Exchanger.code.toString();
		}
		try {
			FileWriter fw = new FileWriter(new File(file_name));
			PrintWriter pw = new PrintWriter(fw);
			pw.println(textEdit.getText().toString());
			fw.close();
			Toast.makeText(getActivity(), "Se guardo el archivo como: "+file_name, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		newCodeOn = false;
	}
	
	
}
