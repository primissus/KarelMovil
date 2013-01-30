package poodleDeveloper.tools;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import poodleDeveloper.karel.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Karelapan extends ListActivity{
	
	private ArrayList<Problema> problemas;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.karelapan_activity);
		setTitle("Karelapan.com");
		LoadProblemsTask task = new LoadProblemsTask();
		task.execute();
	}
	
	
	
	public void showProblems(ArrayList<Problema> problemas){
		setListAdapter(new ProblemsAdapter(this, R.layout.problem_item, problemas));
		((ProblemsAdapter)getListAdapter()).setNotifyOnChange(true);
		this.problemas = problemas;
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		final int x = position;
		new AlertDialog.Builder(this).setTitle(problemas.get(position).title).setMessage(problemas.get(position).description)
		.setPositiveButton("Descargar", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent data = new Intent();
				data.putExtra("WORLD", problemas.get(x).url_world);
				setResult(RESULT_OK,data);
				finish();
			}
		}).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		}).show();
	}
	
	class LoadProblemsTask extends AsyncTask<Void, Void, Bundle>{

		@Override
		protected Bundle doInBackground(Void... params) {
			try{
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document dom = db.parse(new URL("http://karelweb.ingeniacrea.com/problemas/feed").openStream());
				
				ArrayList<Problema> problemas = new ArrayList<Problema>();
				NodeList channels = dom.getDocumentElement().getChildNodes();
				
				Node channel = channels.item(0);
				for(int x=0; x<channels.getLength(); x++){
					Node node = channels.item(x);
					if(node.getNodeName().equalsIgnoreCase("channel")){
						channel = node;
						break;
					}
				}
				
				NodeList nodes = channel.getChildNodes();
				for(int x=0; x<nodes.getLength(); x++){
					Node node = nodes.item(x);
					if(node.getNodeName().equalsIgnoreCase("item")){
						System.out.println(node.getNodeName());
						Problema problema = parseProblem(node);
						problemas.add(problema);
					}
				}
				
				Bundle bundle = new Bundle();
				bundle.putSerializable("PROBLEMAS", problemas);
				return bundle;
			}catch(Exception e){
				
			}
			return null;
		}
		
		public Problema parseProblem(Node node){
			
			Problema problem = new Problema();
			NodeList nodes = node.getChildNodes();
			for(int i = 0; i < nodes.getLength(); i++){
				Node attr = nodes.item(i);
				if(attr.getNodeName().equalsIgnoreCase("title"))
					problem.title = attr.getFirstChild().getNodeValue();
				else if(attr.getNodeName().equalsIgnoreCase("description"))
					problem.description = attr.getFirstChild().getNodeValue();
				else if(attr.getNodeName().equalsIgnoreCase("bestscore"))
					problem.best_score = Integer.valueOf(attr.getFirstChild().getNodeValue());
				else if(attr.getNodeName().equalsIgnoreCase("besttime"))
					problem.best_time = Integer.valueOf(attr.getFirstChild().getNodeValue());
				else if(attr.getNodeName().equalsIgnoreCase("timessolved"))
					problem.time_solved = Integer.valueOf(attr.getFirstChild().getNodeValue());
				else if(attr.getNodeName().equalsIgnoreCase("world"))
					problem.url_world = attr.getFirstChild().getNodeValue();
			}
			return problem;
		}
	
		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(Bundle result) {
			try{
				ArrayList<Problema> problemas = (ArrayList<Problema>)result.getSerializable("PROBLEMAS");
				showProblems(problemas);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	class ProblemsAdapter extends ArrayAdapter<Problema>{

		public ProblemsAdapter(Context context, int textViewResourceId, List<Problema> objects) {
			super(context, textViewResourceId,objects);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.problem_item, null);
			}
			
			Problema p = getItem(position);
			TextView title = (TextView)convertView.findViewById(R.id.title_problem);
			title.setText(p.title);
			title.setTypeface(Typeface.MONOSPACE);
			TextView bestscore = (TextView)convertView.findViewById(R.id.best_score);
			bestscore.setText(String.valueOf(p.best_score)); 
			TextView solved = (TextView)convertView.findViewById(R.id.problem_solved);
			solved.setText(String.valueOf(p.time_solved));
			TextView tried = (TextView)convertView.findViewById(R.id.problem_tried);
			tried.setText(String.valueOf(p.best_time));
			
			return convertView;
		}
		
	}
}
