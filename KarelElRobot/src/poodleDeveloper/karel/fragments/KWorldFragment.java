package poodleDeveloper.karel.fragments;

import poodleDeveloper.karel.KWorld;
import poodleDeveloper.karel.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;

public class KWorldFragment extends SherlockFragment{

	public KWorldFragment(){
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
		View view = inflater.inflate(R.layout.kworld_fragment, container,false);
		return view;
	}
}
