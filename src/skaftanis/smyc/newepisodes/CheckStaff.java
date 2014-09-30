package skaftanis.smyc.newepisodes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;


public class CheckStaff {
	
	private static String serieLink;
	private static String serieEpisode;
	private static boolean thereIs; //true if there is a new episode
	
	//Read and Return the names of Files Stored in /NewEpisodes Folder
	private static  List<String> readFileNames () {
		
		List<String> paths = new ArrayList<String>();
		File sdCard = Environment.getExternalStorageDirectory();
    	File directory = new File(sdCard.getAbsolutePath()+File.separator+"NewEpisodes");
    	File[] files = directory.listFiles();
    	for (int i = 0; i < files.length; ++i) {
    	    paths.add(files[i].getAbsolutePath());
    	}
	  
		return paths;
	}
	
	//Read the Link (First row in the file)
	private static  String getLink(String name) throws IOException {
		File sdCard = Environment.getExternalStorageDirectory();
		String Link = null;
		name=sdCard.getAbsolutePath()+"/"+"NewEpisodes"+"/"+name;
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(name));
			String line;
			while ((line = br.readLine()) != null) {
			   Link=line;
			   break;
			}
			br.close();
				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return Link;
}

	//Read Episode (Second row in the file)
	private static String getEpisode(String name) throws IOException {
		String Episode = null;
		int counter=0;
		File sdCard = Environment.getExternalStorageDirectory();
		name=sdCard.getAbsolutePath()+"/"+"NewEpisodes"+"/"+name;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(name));
			String line;
			while ((line = br.readLine()) != null) {
			   Episode=line;
			   if (counter == 1) 
				   break;
			   counter++;
			}
			br.close();
				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return Episode;
	}
	
	
	
	//Guess the next episode. If current is S02E12 next might be S02E13
	private static String nextEpisodeGuess (String current) {
		
		String newEpisodeNumber;
    	String season;
    	
    	String episodeNumber=current.substring(4); //take the number of episode with substring method
		int episodeNumberInt=Integer.parseInt(episodeNumber);
		episodeNumberInt++;
		if (episodeNumberInt < 10 )
			 newEpisodeNumber = "0"+String.valueOf(episodeNumberInt);
		else
			 newEpisodeNumber = String.valueOf(episodeNumberInt);
		 season = current.substring(1,3);
		 if ( Integer.parseInt(season) == episodeNumberInt-1 ) { //eg S02E02 
			
			 StringBuffer sb = new StringBuffer();
			 Pattern p = Pattern.compile(episodeNumber);
			 Matcher m = p.matcher(current);
			 int count = 0;
			 while(m.find()) {
			     if(count++ % 2 != 0) {
			 	m.appendReplacement(sb, newEpisodeNumber);
			     }
			 }
			 m.appendTail(sb);
			 current=sb.toString();

		 }
		 else
			current=current.replace(episodeNumber, newEpisodeNumber);
		 
		return current;
		
	}
	
	//Basic Method. Check for the newEpisode. Here is the good staff ;)
	public static String[][] Check() {
		List<String> paths = new ArrayList<String>();
		paths=readFileNames();
		String serieName=null;
		
		String [][] returnArray = new String[paths.size()][4];
		for (int i=0; i<paths.size(); i++)
			for (int j=0; j<4;j++)
			returnArray[i][j]="0";
		
		/*The return array has 4 collums, one for the name, one boolean (true if there is new episode, false otherwise). one for the current
		episode and one for the next */
		for (int i=0; i<paths.size(); i++) {
		
			serieName=paths.get(i);
			File sdCard = Environment.getExternalStorageDirectory();
			serieName=serieName.replace(sdCard.getAbsolutePath()+"/"+"NewEpisodes"+"/", "");
			
			returnArray[i][0]=serieName;
			
			try {
				serieLink=getLink(serieName);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				serieEpisode=getEpisode(serieName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

         /*   try {
                    URL url = new URL(serieLink);
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
                    if ( result.contains(serieEpisode) ) thereIs=true;
                    else
                    	thereIs=false;
                
            } catch (MalformedURLException e) {
                    e.printStackTrace();
            } catch (IOException e) {
                    e.printStackTrace();
            } */
			
			String output = null;
	     	try {
				output=new CheckWebsite().execute(serieLink).get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	     	
	     	serieName=serieName.replace(".txt", "");
	     	
	     	//create NewSeaechString which contains <b> and </b> characters for better search in html (bug fix)
	    	String [] splited = (serieName+" "+serieEpisode).split("\\s+");
	    	
	    	//convert first word letter to upper case
	    	for (int j=0;j<splited.length;j++)
				splited[j]=splited[j].substring(0, 1).toUpperCase() + splited[j].substring(1);
	    	
	    	
			String NewSearchString = "";
			for (int j=0; j<splited.length;j++){
				NewSearchString+="<b>"+splited[j]+"</b>"+" ";
			}
	     	
			int lastIndex = 0;
			int count =0;

			while(lastIndex != -1){

			       lastIndex = output.indexOf(NewSearchString,lastIndex);

			       if( lastIndex != -1){
			             count ++;
			             lastIndex+=NewSearchString.length();
			      }
			}
			
	     	
	     	if (output.contains(serieEpisode) && count>0)  thereIs=true; 
	     	else thereIs=false;
			
            if (thereIs) returnArray[i][1]="1";
            else returnArray[i][1]="0";
            
           returnArray[i][2]=serieEpisode;
           returnArray[i][3]=nextEpisodeGuess(serieEpisode);
           //returnArray[i][3]="xxxx";
          
            
		} //for end
		//String cut = paths.get(2).replace("/mnt/sdcard/NewEpisodes/", "");
		//return returnArray[0][1];
		//return serieLink;
		return returnArray;
	}
	

}
