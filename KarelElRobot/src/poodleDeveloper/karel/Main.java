package poodleDeveloper.karel;

import java.util.concurrent.ExecutionException;

import poodleDeveloper.karel.data.JSONParser;
import com.actionbarsherlock.app.SherlockActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Main extends SherlockActivity {

	private final static String url = "http://127.0.0.1/mundo.json";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        JSONParser j = new JSONParser();
		try {
			j.init(url,this);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
    }
    
}
