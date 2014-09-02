package skaftanis.smyc.newepisodes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

	//static variables for adding 
	public static String helpString;
	public static String EndLink;
	public static String SerieName;
	public static String SerieCode;
	
	//static variables for checking
	public static int noOfEpisodesStatic = CheckStaff.Check().length; //clever ;)
	
	public static String Predictions[] = new String [20];
	public static String Old[] = new String [20];
	public static String Names[] = new String [20];
	public static boolean Clicked[] = new boolean [20]; //true when button in pos i clicked
	
	public static boolean sure;
	
// ----------------------------------------------onCreate---------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		
		if (isFirstLaunch()) {
			AddSerrie();
		} 
		
		if (isOnline())
				Refresh();
		else {
			setContentView(R.layout.no_internet);
			Button oneMoreTry = (Button)findViewById(R.id.button1);
			oneMoreTry.setOnClickListener(tryAgainClicked);
		}
	
			
		//TextView textViewToChange = (TextView) findViewById(R.id.text);
		//textViewToChange.setText(Integer.toString(temp));
	
		
	}
	
	
		
// ----------------------------------------------Refresh---------------------------------------------------

	//The Basic method. Reads the array with all the info from CheckStaff class and updates the universe
	private void Refresh () { 
		setContentView(R.layout.activity_main);
		LinearLayout l1=(LinearLayout)findViewById(R.id.linearLayout1);

		String[][] InfoTable = CheckStaff.Check(); //Read the table with all info for CheckStaff magic class
		int noOfEpisodes = InfoTable.length;

		noOfEpisodesStatic=noOfEpisodes; //we need to use prediction in other method,so we use the static trick
		for (int i=0; i<noOfEpisodesStatic; i++) {
			Predictions[i]=InfoTable[i][3];
			Clicked[i]=false;
			Names[i]=InfoTable[i][0];
			Old[i]=InfoTable[i][2];
		}


		String textViewName;
		//Object [] allTexts = new Object [noOfEpisodes]; //array to store TextViews

		for (int i=0; i<noOfEpisodes; i++){
			String printedText;
			textViewName=InfoTable[i][0].replace(".txt", "");
			TextView t1 = new TextView(this);

			Button sawIt = new Button (this);
			//Button change = new Button (this);
			Button delete = new Button (this);
			Button edit = new Button (this);
			sawIt.setId(i);
			edit.setId(i);
			//change.setId(i);
			delete.setId(i);
			

			//setup the listeners. we don't use xml for gui (because we want it dynamic) so we use listeners with pure java
			sawIt.setOnClickListener(sawItClicked);
			//change.setOnClickListener(changeClicked);
			delete.setOnClickListener(deleteClicked);
			edit.setOnClickListener(editClicked);

			sawIt.setText("I Saw It");
			//sawIt.setText(Integer.toString(sawIt.getId()));
			//change.setText("Change Season");
			edit.setText("Edit");
			delete.setText("Delete");
			//allTexts[i]=t1;
			if ( InfoTable[i][1] == "1") { //there is a new episode situation
				printedText=textViewName+"\n"+"There is a new Episode! Episode: "+InfoTable[i][2];
				t1.setBackgroundResource(R.drawable.frame);
			}
			else {
				printedText=textViewName+"\n"+"There is not a new Episode";
				t1.setBackgroundResource(R.drawable.frame2);
			}
			t1.setText(printedText);
			t1.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
			l1.addView(t1);


			LinearLayout l2 = new LinearLayout(this);

			l2.setOrientation(LinearLayout.HORIZONTAL);
			l2.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);


			l1.addView(l2);

			l2.addView(sawIt);
			//l2.addView(change);
			l2.addView(edit);
			l2.addView(delete);

			if (InfoTable[i][1] == "0")
				sawIt.setVisibility(View.GONE);

		}


	}

	// ----------------------------------------------Listeners---------------------------------------------------

	
	
	private OnClickListener changeClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Clicked[v.getId()]=true;
			EditBox4SeasonChange();
		}
		
	};
	
	private OnClickListener deleteClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			//yesNoBox();
		//	if (sure) {
			File sdCard = Environment.getExternalStorageDirectory();
			deleteFile(sdCard.getAbsolutePath()+"/"+"NewEpisodes"+"/"+Names[v.getId()]);
			//if ( ) {
				Toast.makeText(getApplicationContext(), "Deleted",	Toast.LENGTH_LONG).show();
				Refresh();
			//}
		//}
	}
		
	};
	
	
	private OnClickListener editClicked = new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			Clicked[arg0.getId()]=true;
			EditBox("Set manually the next episode follwing the right format");
			
		}
		
	};
	
	private OnClickListener sawItClicked=new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			Clicked[arg0.getId()]=true;
			ConfirmBox();
		}
		
	};
	
	private OnClickListener tryAgainClicked=new OnClickListener() {

		@Override
		public void onClick(View arg0) {
				if (isNetworkAvailable())
					Refresh();
			
		}
		
	};
	

	private void ConfirmBox () {
		Builder builder = new AlertDialog.Builder(this);
		for (int i=0; i< noOfEpisodesStatic; i++) {
			Button temp = (Button)findViewById(i);
			if ( Clicked[temp.getId()] == true ) {
				builder.setMessage("Nice! Next Episode is "+Predictions[temp.getId()]);
				break;
			}
			
		}
		
		builder.setCancelable(true);
		builder.setPositiveButton("Yes", new OkOnClickListener());
		builder.setNegativeButton("No, no", new CancelOnClickListener());
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	private final class CancelOnClickListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			//Toast.makeText(getApplicationContext(), "Cancle selected",	Toast.LENGTH_LONG).show();
			
			//give the possibility to select your own episode
			EditBox("Don't worry! You can set your own next episode from here");
		}
	}

	private final class OkOnClickListener implements DialogInterface.OnClickListener { 	//change episode in file and refresh
		public void onClick(DialogInterface dialog, int which) {
			File sdCard = Environment.getExternalStorageDirectory();
			for (int i=0; i< noOfEpisodesStatic; i++) {
				Button temp = (Button)findViewById(i);
				if ( Clicked[temp.getId()] == true ) {
					Clicked[temp.getId()]=false;
					replaceInFile(sdCard.getAbsolutePath()+"/"+"NewEpisodes"+"/"+Names[temp.getId()], Old[temp.getId()], Predictions[temp.getId()]);
					Refresh();
					break;
				}
				
			}
		}
	} 
	
	
	private void yesNoBox () {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure?").setPositiveButton("Yes", new DialogInterface.OnClickListener(){
			
			 public void onClick(DialogInterface dialog, int which) {
				 sure=true;
			 }
		})
		    .setNegativeButton("No", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					sure=false;
					dialog.dismiss();					
				}
			}).show();
		
	}
	
	private void EditBox (String message) { //EditBox implements their listeners. ConfirmBox not. It's another to do the same thing. Just for demostration!  
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(message);

		// Set up the input
		final EditText input = new EditText(this);
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.setHint("eg. S03E12");
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        String m_Text = input.getText().toString();
		        File sdCard = Environment.getExternalStorageDirectory();
				for (int i=0; i< noOfEpisodesStatic; i++) {
					Button temp = (Button)findViewById(i);
					if ( Clicked[temp.getId()] == true ) {
						replaceInFile(sdCard.getAbsolutePath()+"/"+"NewEpisodes"+"/"+Names[temp.getId()], Old[temp.getId()], m_Text); //changeEpisode
						replaceInFile(sdCard.getAbsolutePath()+"/"+"NewEpisodes"+"/"+Names[temp.getId()], Old[temp.getId()].substring(0,3) ,m_Text.substring(0,3) );
						Refresh();
						Clicked[temp.getId()]=false;
						break;
					}	
				}
		    }
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});

		builder.show();
	}
	
	private void EditBox4SeasonChange () {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Type a new season");

		// Set up the input
		final EditText input = new EditText(this);
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.setHint("eg. 02");
		builder.setView(input);
		input.setInputType(InputType.TYPE_CLASS_PHONE);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        String m_Text = input.getText().toString();
		        File sdCard = Environment.getExternalStorageDirectory();
				for (int i=0; i< noOfEpisodesStatic; i++) {
					Button temp = (Button)findViewById(i);
					if ( Clicked[temp.getId()] == true ) {
						replaceInFile(sdCard.getAbsolutePath()+"/"+"NewEpisodes"+"/"+Names[temp.getId()], Old[temp.getId()], "S"+m_Text+Old[temp.getId()].substring(3,6)); //change only the Season
						replaceInFile(sdCard.getAbsolutePath()+"/"+"NewEpisodes"+"/"+Names[temp.getId()], Old[temp.getId()].substring(0,3) ,"S"+m_Text ); //changing the value in the link
						Refresh();
						Clicked[temp.getId()]=false;
						break;
					}	
				}
		    }
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});

		builder.show();
	}

	
// ----------------------------------------------Technical---------------------------------------------------

	private boolean isFirstLaunch() {
		File directory = new File("mnt/sdcard/NewEpisodes");
		File[] files = directory.listFiles();
		if (files.length == 0)
			return true;
		else
			return false;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.Add) { //adding a new serie staff
 			AddSerrie();
			return true;
		}
		if (id == R.id.Refresh) { //refresh the series
			Refresh();
		}
		if (id == R.id.About) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("About New Episodes");
			builder.setMessage("New Episodes is an app that you help to watch your favoutite tv series. It informs"
					+ "you when a new episode is available in torrents, so you can watch it. Created by Spiros Kaftanis");
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();

			// Must call show() prior to fetching text view
			TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
			messageView.setGravity(Gravity.CENTER);
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	 
	private boolean isNetworkAvailable() {  //NOT USED ANYMORE. Replaced by isOnline() 
	    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	public boolean isOnline() {
	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}

	
// ----------------------------------------------For Files (and adding)------------------------------------------------

	public boolean deleteFile (String fileName) {
		
		File file = new File(fileName);
		boolean deleted = file.delete();
		return deleted;
		
	}
	
	
	
	public void CreateFolder () {
		File folder = new File("mnt/sdcard/NewEpisodes");
		boolean success = true;
		if (!folder.exists()) {
		    success = folder.mkdir();
		}
		if (success) { //if the file created, so we are in the first adding
			
		}
	}
	
	public void WriteToFile(String sFileName, String first, String second, String third){
	    try
	    {
	        File root = new File(Environment.getExternalStorageDirectory(), "NewEpisodes");
	        if (!root.exists()) {
	            root.mkdirs();
	        }
	        File gpxfile = new File(root, sFileName);
	        FileWriter writer = new FileWriter(gpxfile);
	        first+="\r\n";
	        writer.append(first);
	        second+="\r\n";
	        writer.append(second);
	        third+="\r\n";
	        writer.append(third);
	        writer.flush();
	        writer.close();
	        
	        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
	    }
	    catch(IOException e)
	    {
	         e.printStackTrace();
	         //importError = e.getMessage();
	        // iError();
	    }
	   }  
	
	public void AddSerrie () {
		// Toast.makeText(this, "Clicked on Button", Toast.LENGTH_LONG).show();
		final EditText txtUrl = new EditText(this);
		
		//Input Dialog 
		txtUrl.setHint("eg. How I met your mother");

		new AlertDialog.Builder(this)
		.setTitle("Add Serrie")
		.setMessage("Type the name of the new serrie you want to watch")
		.setView(txtUrl)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String input = txtUrl.getText().toString();
				SerieName=input+".txt";
				//create the url string for search in torrentz
				String url="https://torrentz.eu/search?f=";
				url=url+input;
				url=url.replaceAll("\\s","+");  //creates the torrentz url of the serrie
				//helpString=url;
				AddSeason(url); //add Season to the end of the serie's link
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		})
		.show(); 
		
	}
	
	public void AddSeason (String start) {
		// Toast.makeText(this, "Clicked on Button", Toast.LENGTH_LONG).show();
		final EditText txtUrl = new EditText(this);
		//txtUrl.setText("0",TextView.BufferType.EDITABLE);
		helpString=start;
		//Input Dialog 
		txtUrl.setHint("eg. 02");
		txtUrl.setInputType(InputType.TYPE_CLASS_PHONE);
		new AlertDialog.Builder(this)
		.setTitle("Add Season")
		.setMessage("Type the number of the season you want to start")
		
		
		.setView(txtUrl)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//create the final String and save it to static variable EndLink 
				String input = txtUrl.getText().toString();
				if (Integer.parseInt(input)/10 < 1)
					input="0"+input;
				SerieCode="S"+input; //add serie's season number in this static string
				input=helpString+"+"+"S"+input;
				//helpString=input;
				AddEpisode(input);
				EndLink=input;
				//Button p1_button = (Button)findViewById(R.id.button1);
				//p1_button.setText(EndLink);
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		})
		.show(); 
	}
	
	public void AddEpisode(String start) {
		// Toast.makeText(this, "Clicked on Button", Toast.LENGTH_LONG).show();
				final EditText txtUrl = new EditText(this);
			//	txtUrl.setText("0",TextView.BufferType.EDITABLE);
				helpString=start;
				//Input Dialog 
				txtUrl.setHint("eg. 09");
				txtUrl.setInputType(InputType.TYPE_CLASS_NUMBER);
				new AlertDialog.Builder(this)
				.setTitle("Add Episode")
				.setMessage("Type the number of the episode you want to start")
				.setView(txtUrl)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//create the final String and save it to static variable EndLink 
						String input = txtUrl.getText().toString();
						if (Integer.parseInt(input)/10 < 1)
							input="0"+input;
						SerieCode+="E"+input; //add the episode in SerieCode. SerieCode Format = SxxExx						
						//WriteToFile("names.txt", SerieName, "", ""); //writes serie name to names.txt
						WriteToFile(SerieName, EndLink, SerieCode,SerieName );
						Refresh();
						//WriteToFile(SerieName, EndLink); //create a file with Serie name with all the information inside it
						//WriteToFile(SerieName, SerieCode); //2nd line is the Code
						//WriteToFile(SerieName, SerieName);
						
						//EndLink=input;
						//Button p1_button = (Button)findViewById(R.id.button1);
						//p1_button.setText(EndLink);
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				})
				.show(); 
		
	}
	
	

	private void replaceInFile ( String filename,  String replace, String with) {
		  try
          {
          File file = new File(filename);
          BufferedReader reader = new BufferedReader(new FileReader(file));
          String line = "", oldtext = "";
          while((line = reader.readLine()) != null)
              {
              oldtext += line + "\r\n";
          }
          reader.close();
          
          // replace a word in a file
          String newtext = oldtext.replaceAll(replace, with);
         
          FileWriter writer = new FileWriter(filename);
          writer.write(newtext);writer.close();
      }
      catch (IOException ioe)
          {
          ioe.printStackTrace();
          } 
      }



}