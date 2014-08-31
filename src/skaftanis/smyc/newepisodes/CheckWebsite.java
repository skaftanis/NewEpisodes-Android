package skaftanis.smyc.newepisodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

public class CheckWebsite  extends AsyncTask<String,Integer, String> {	  

	
	
	@Override
	protected String doInBackground(String... params) {
		String returnString = null;
		 try {
             URL url = new URL(params[0]);
             URLConnection urlConnection = url.openConnection();
             InputStream is = urlConnection.getInputStream();
             InputStreamReader isr = new InputStreamReader(is);

             int numCharsRead;
             char[] charArray = new char[1024];
             StringBuffer sb = new StringBuffer();
             while ((numCharsRead = isr.read(charArray)) > 0) {
                     sb.append(charArray, 0, numCharsRead);
             }
             String result = sb.toString();
             returnString=result;
		 } catch (MalformedURLException e) {
             e.printStackTrace();
		 } catch (IOException e) {
             e.printStackTrace();
		 }
		 return returnString;
	}
	
	 @Override
	  protected  void onPostExecute(String string) {
		  super.onPostExecute(string);
	    }
	
	

	
};