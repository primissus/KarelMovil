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
import java.net.URL;

import org.json.JSONObject;

import poodleDeveloper.karel.R;
import poodleDeveloper.karel.Activities.KWorldGraphics;
import poodleDeveloper.karel.data.grammar.Ejecutable;
import poodleDeveloper.karel.data.karelmovil.KGrammar;
import poodleDeveloper.karel.data.karelmovil.KRunner;
import poodleDeveloper.karel.data.karelmovil.KarelException;
import poodleDeveloper.tools.Exchanger;
import poodleDeveloper.tools.FilePickerActivity;
import poodleDeveloper.tools.Karelapan;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;

public class KEditorFragment extends SherlockFragment implements View.OnClickListener {

	private static final int REQUEST_PICK_FILE = 1;
	private static final int KARELAPAN_PICK_FILE = 2;
	private static final int KARELAPAN_DOWNLOAD_FILE = 3;
	
	private EditText textEdit;
	private ImageView newCode, openCode,saveCode, run , world;
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
		/*openKarelapanCode = (ImageView)view.findViewById(R.id.karelapanCode); 
		openKarelapanCode.setOnClickListener(this);*/
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
		/*case R.id.karelapanCode:
			if(newCodeOn || EXISTING_CODE)
				Toast.makeText(getActivity(), "Guarda tus avances antes de descargar un nuevo mundo", Toast.LENGTH_SHORT).show();
			else
				karelapanDialog();
			break;*/
		case R.id.saveCode:
			saveFile();
			break;
		case R.id.run:
			String codigo = textEdit.getText().toString();
			InputStream is = new ByteArrayInputStream(codigo.getBytes());
			InputStreamReader isr = new InputStreamReader(is); 
			try{
				KGrammar grammar = new KGrammar(new BufferedReader(isr), true, false);
				grammar.verificar_sintaxis();
				Ejecutable exe = grammar.expandir_arbol();
				Exchanger.krunner = new KRunner(exe, Exchanger.kworld);
				Exchanger.SUCESS_EXECUTED = false;
				Exchanger.kworld.karel.posicion = poodleDeveloper.karel.fragments.KWorld.prevPosition;
				Exchanger.kworld.karel.orientacion = poodleDeveloper.karel.fragments.KWorld.prevOrientation;
				startActivity(new Intent(getActivity(),KWorldGraphics.class));
			}catch(KarelException e){
				Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.tab:
			writing("    ", 4);
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
	
	public void karelapanDialog(){
		final Dialog karelapan = new Dialog(getActivity());
		karelapan.setContentView(R.layout.karelapan_dialog);
		ImageView pending = (ImageView)karelapan.findViewById(R.id.pending_karelapan);
		pending.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), FilePickerActivity.class);
				intent.putExtra(FilePickerActivity.EXTRA_FILE_PATH, Exchanger.KARELAPAN_PATH);
				getActivity().startActivityForResult(intent, KARELAPAN_PICK_FILE);
				karelapan.dismiss();
			}
		});
		ImageView download = (ImageView)karelapan.findViewById(R.id.new_karelapan);
		download.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),Karelapan.class);
				getActivity().startActivityForResult(intent, KARELAPAN_DOWNLOAD_FILE);
				karelapan.dismiss();
			}
		});
		karelapan.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				karelapan.dismiss();
			}
		});
		karelapan.setTitle("Mundos de Karelapan");
		karelapan.show();
	}
	
	public void loadFromKarelapan(String world_url){
		final String url = world_url;
		Thread trd = new Thread(new Runnable(){
			  @Override
			  public void run(){
				  try{
						URL uri = new URL(url);
						BufferedReader br = new BufferedReader(new InputStreamReader(uri.openStream()));
						StringBuilder sb = new StringBuilder();
						String cad = "";
						while((cad = br.readLine())!= null){
							sb.append(cad);
						}
						JSONObject json = new JSONObject(new String(sb));
						Exchanger.kworld.limpiar();
						Exchanger.kworld.cargaJSON(json);
						startActivity(new Intent(getActivity(),KWorldGraphics.class));
						EXISTING_CODE = true;
					}catch(Exception e){
						e.printStackTrace();
					}
			  }
			});
		trd.start();
		
	}
	
	public void newFile(){
		if(!newCodeOn && !Exchanger.KARELAPAN_MODE_ACTIVATED){
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
		if(!newCodeOn && textEdit.getText().toString().equals("") && !Exchanger.KARELAPAN_MODE_ACTIVATED){
			Intent intent = new Intent(getActivity(), FilePickerActivity.class);
			intent.putExtra(FilePickerActivity.EXTRA_FILE_PATH, Exchanger.CODE_PATH);
			getActivity().startActivityForResult(intent, REQUEST_PICK_FILE);
		}else{
			new AlertDialog.Builder(getActivity()).setTitle("KarelTheRobot").setMessage("¿Deseas guardar el archivo actual?")
		    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
		         public void onClick(DialogInterface dialog, int whichButton) {
		        	saveFile();
		        	Intent intent = new Intent(getActivity(), FilePickerActivity.class);
		 			intent.putExtra(FilePickerActivity.EXTRA_FILE_PATH, Exchanger.CODE_PATH);
		 			getActivity().startActivityForResult(intent, REQUEST_PICK_FILE);
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
		if(Exchanger.KARELAPAN_MODE_ACTIVATED){
			file_name = Exchanger.code.toString();
			perform();
			try {
				poodleDeveloper.karel.data.karelmovil.KWorld w = Exchanger.kworld;
	        	JSONObject cosa = w.exporta_mundo();
	        	String res = cosa.toString();
	        	File f = new File(file_name);
	        	FileWriter fw = new FileWriter(f);
	        	fw.write(res);
	        	fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(!EXISTING_CODE && newCodeOn){
			final EditText input = new EditText(getActivity());
			new AlertDialog.Builder(getActivity()).setTitle("KarelTheRobot").setMessage("Escribe el nombre del archivo").setView(input)
		    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		         public void onClick(DialogInterface dialog, int whichButton) {
		        	 file_name = Exchanger.CODE_PATH+File.separator+input.getText().toString()+".karel";
		        	 perform();
		         }
		    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		         public void onClick(DialogInterface dialog, int whichButton) {}
		    }).show();
		}else{
			if(Exchanger.code.toString()!=null){
				file_name = Exchanger.code.toString();
				perform();
			}
		}
	}
	
	public void perform(){
		try {
			if(Exchanger.code == null)
				Exchanger.code = new File(file_name);
			FileWriter fw = new FileWriter(Exchanger.code);
			PrintWriter pw = new PrintWriter(fw);
			pw.println(textEdit.getText().toString());
			fw.close();
			Toast.makeText(getActivity(), "Se guardo el archivo como: "+file_name, Toast.LENGTH_SHORT).show();
			newCodeOn = false;
			EXISTING_CODE = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
