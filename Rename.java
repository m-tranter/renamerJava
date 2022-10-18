/* 
"Rename.java" by Mark Tranter (tranter.m@sky.com).
Renaming program for the Congleton Chronicle.
Last revision 07/10/2015.

Files (.pdf) come in to "D:\FTP\C To Send" (at time of writing).
Call program with the path to FTP folder as command arg.
Eg. "java Rename D:\FTP\".

First four letters of the name is the edition: "Alsa", "Bidd", "Cong" or "Sand".
Next four characters represent the day of the month ("01" - "31") and then the page number ("01" - "99"). 
Eg. "Cong2730.pdf".

New file name format is:
"Con" + edition number + month (as three letter abbreviation) + date + "P0" + page number + ".pdf"
Eg. "Con1Oct27P030.pdf" (Cong. is edition "1", Bidd. is "2", Sand. is "3", Alsa. is "4").

We generate the month code from today's date. 
If the paper comes out on the 1st of the month, we must choose the code for the NEXT month.
During execution, original files will be copied to "D:\FTP\C Sent".
*/

import java.util.*;
import java.util.regex.*;
import java.io.*;

public class Rename {
	public static void main(String[] args) {


                // Set up timer.
                final long startTime = System.currentTimeMillis();
		// Declare a help string.
		String msg = "Give the path to the FTP folder as an argument.\nEg.: \"D:\\FTP\\\".";
		
		// If no path given, print the help message & quit.
		if (args.length == 0 || args[0].equals("--help")) {
			System.out.println(msg);
			return;
		} else {
			System.out.println("\n*** Rename ***\n");
		}
		
		// More declarations.
		String myPath = args[0], month = "", date = "", f, newF;
		String [] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
				    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		String [] eds = {"Cong", "Bidd", "Sand", "Alsa"};
		File dir;
		int m;
		
		// Set up regular expression for valid filenames.
		Pattern p = Pattern.compile("^(Cong|Bidd|Sand|Alsa)(\\d{2})(\\d{2})(.pdf$|.PDF$)");
		
		// Get list of files.
		dir = new File(myPath + "C To Send");
		
		// Exit if the path is not valid.
		if (! dir.isDirectory()){
			System.out.println("Invalid directory.\n" + msg);
			return;
		}
		
		// Loop over all files in the directory.
		for (File myFile: dir.listFiles()){
			// Get the name.
			f = myFile.getName();
			// Reject files that don't match the pattern.
			Matcher mat = p.matcher(f);
			if (! mat.find()){
				System.out.println("Skipping unrecognised file: " + f);
				continue;
				}
			// Get month code, if not already set.
			if (month == ""){
				// Get publication date from the file name.
				date = f.substring(4,6);
				// Find month today.
				Calendar now = Calendar.getInstance();
				m = now.get(Calendar.MONTH);
				// If publication date is in next month, use next month's code.
				if (Integer.parseInt(date) < now.get(Calendar.DAY_OF_MONTH)){
					m = (m + 1) % 11;
					}
				// Get the month string.
				month = months[m];
				System.out.println("Date of publication is: " + date + " " + month + ".\n");
				}
				
			// Get the edition code number and generate the new name (using regular expression).
			newF = "Con" + Integer.toString(Arrays.asList(eds).indexOf(f.substring(0,4)) + 1) + month + mat.replaceFirst("$2P0$3.pdf");
			
			// Copy to "C Sent".
			try{
				copyFile(myFile, new File(myPath + "C Sent/" + f));
			}catch(IOException e){
				e.printStackTrace();
			}
			// Rename file.
			renameFile(myFile, new File(myPath + "C To Send\\" + newF));
                }
                        // Calculate and display time of execution.
                        final long endTime = System.currentTimeMillis();
                        System.out.println("Execution time: " + ((double)endTime - startTime) / 1000);
		
		}		
	// Methods for copy and rename.
	private static void copyFile(File source, File dest)
     throws IOException {

    InputStream input = null;
    OutputStream output = null;
    try {
        input = new FileInputStream(source);
        output = new FileOutputStream(dest);
        byte[] buf = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buf)) > 0) {
            output.write(buf, 0, bytesRead);
        }
    } finally {
        input.close();
        output.close();
    }
}
   private static void renameFile(File oldName,File newName) {
      if(oldName.renameTo(newName)) {
         System.out.println(newName.getName() + " created.");
      } else {
         System.out.println("Error");
      }
   }
}
