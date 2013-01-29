package poodleDeveloper.karel.fragments;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import poodleDeveloper.tools.Exchanger;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

public class KarelapanPendingFragment extends SherlockListFragment{
	
	private ArrayList<File> mFiles;
	protected String[] acceptedFileExtensions;
	private Context context;
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);	
	}
	
	public KarelapanPendingFragment(){
		File directory = new File(Exchanger.KARELAPAN_PATH);
		acceptedFileExtensions = new String[] {".karel",".mdo"};
		ExtensionFilenameFilter filter = new ExtensionFilenameFilter(acceptedFileExtensions);
		File[] problems = directory.listFiles(filter);
		mFiles = new ArrayList<File>();
		if(problems != null && problems.length > 0){
			for(File f : problems)
				mFiles.add(f);
			Collections.sort(mFiles, new FileComparator());
		}
		
		context = getSherlockActivity();
		if(context == null)
			System.out.println("Contexto nulo");
	}	
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
	}

	
	public class FileComparator implements Comparator<File> {
	    @Override
	    public int compare(File f1, File f2) {
	    	if(f1 == f2) {
	    		return 0;
	    	}
	    	if(f1.isDirectory() && f2.isFile()) {
	        	// Show directories above files
	        	return -1;
	        }
	    	if(f1.isFile() && f2.isDirectory()) {
	        	// Show files below directories
	        	return 1;
	        }
	    	// Sort the directories alphabetically
	        return f1.getName().compareToIgnoreCase(f2.getName());
	    }
	}
	
	public class ExtensionFilenameFilter implements FilenameFilter {
		private String[] mExtensions;
		
		public ExtensionFilenameFilter(String[] extensions) {
			super();
			mExtensions = extensions;
		}
		
		@Override
		public boolean accept(File dir, String filename) {
			if(new File(dir, filename).isDirectory()) {
				// Accept all directory names
				return true;
			}
			if(mExtensions != null && mExtensions.length > 0) {
				for(int i = 0; i < mExtensions.length; i++) {
					if(filename.endsWith(mExtensions[i])) {
						// The filename ends with the extension
						return true;
					}
				}
				// The filename did not match any of the extensions
				return false;
			}
			// No extensions has been set. Accept all file extensions.
			return true;
		}
	}
	
}
