package skaftanis.smyc.newepisodes;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

public class SplashScreen extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_splashscreeen); //set the XML for this activity
	    
	    File folder = new File(Environment.getExternalStorageDirectory()+File.separator+"NewEpisodes");  
	    if (!folder.exists()) {
		    folder.mkdir();
		}
		
	    Thread thread = new Thread () { //create the splashscreen thread
	    	
	    	@Override
	    	public void run () {
	    		try {
	    			sleep (500);
	    			startActivity(new Intent(getApplicationContext(),MainActivity.class));
	    		} catch (InterruptedException e) {
	    			e.printStackTrace();
	    		}
	    		
	    	}    	
	    	
	    };
	   thread.start();
	    
	    
	}

}
