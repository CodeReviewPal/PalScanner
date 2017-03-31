
package com.PaladionCRTeam.OrphanCodes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Orphan {

	public static String path=null;

	public static void main(String[] args) 
	{

		Scanner scanner = new Scanner(System.in);
		
		System.out.println("Enter your Codebase file path: ");
		//enter the path here-->
		path = scanner.nextLine();
		
		scanner.close();
		//call the traverseddirectory() method
		traversedirectory(new File(path));
		
		System.out.println("done!!!");	

	}
	
/*
 * method to traverse all the directory within the code base.
 */
	public static void traversedirectory(File node)
	{

		Path path = FileSystems.getDefault().getPath(node.getAbsolutePath());

		String sourcepaths = path.toString();

		// Filtering useful java and jsp files based on .java and .jsp extensions.
		fileExtensionFilteration(sourcepaths);

		if(node.isDirectory())
		{
			String[] subNote = node.list();
			for(String filename : subNote)
			{
				traversedirectory(new File(node, filename));
			}
		}

	}
	
	
/*
 * method to filter useful files according to their extensions.
 */
	public static void fileExtensionFilteration(String path){
		
		
		
		String filterRegex = "(^.*\\.(java|jsp|)$)";
		Pattern p = Pattern.compile(filterRegex);
		Matcher matcher = p.matcher(path);
		if(matcher.find()){
			//System.out.println(path);
			String content;

			try {
				//content = new String(Files.readAllBytes(Paths.get(path)));
				//System.out.println(content);
				int tokencount;
				   FileReader fr=new FileReader(path);
				   BufferedReader br=new BufferedReader(fr);
				   String s;
				   int linecount=0;
				    
				   String keyword="AdminApproval";
				   String line;
				    
				   while ((s=br.readLine())!=null){
				      if(s.contains(keyword))
				     System.out.println(s);
				   }
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
					


	



	
