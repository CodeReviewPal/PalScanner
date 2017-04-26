package com.PaladionCRTeam.CommandInjectionDetector;
/*Author:Sumukh R
Date: 25/04/2017*/
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


public class CommandInjectionDetector {

	public static String path=null;

	public static void main(String[] args) 
	{

		Scanner scanner = new Scanner(System.in);
		
		System.out.println("Enter your Codebase file path: ");
		//enter the path here-->
		path = scanner.nextLine();
		
		scanner.close();
		//call the traverseddirectory() method
		GoDir(new File(path));

	}
	
/*
 * method to traverse all the directory within the code base.
 */
	public static void GoDir(File node)
	{

		Path path = FileSystems.getDefault().getPath(node.getAbsolutePath());

		String sourcepaths = path.toString();

		// Filtering useful java and jsp files based on .java and .jsp extensions.
		FileFilter(sourcepaths);

		if(node.isDirectory())
		{
			String[] subNote = node.list();
			for(String filename : subNote)
			{
				GoDir(new File(node, filename));
			}
		}

	}
	
	
/*
 * method to filter useful files according to their extensions(JAVA and JSP).
 */
	public static void FileFilter(String path){
		
		
		
		String filterRegex = "(^.*\\.(java|jsp|aspx)$)";
		Pattern p = Pattern.compile(filterRegex);
		Matcher matcher = p.matcher(path);
		if(matcher.find()){
			
			String content;

			try {
				content = new String(Files.readAllBytes(Paths.get(path)));
				 
			     //regex to find exec()
				BufferedReader br=new BufferedReader(new FileReader(path));
				String line;
                while((line=br.readLine())!=null){
                Pattern p1=Pattern.compile("[.]exec[(]|System.Diagnostics");
                //the above pattern matches *.exec(*
                Matcher m=p1.matcher(line);
               
                while (m.find()) 
                {
                    System.out.println("FILE PATH: "+path);
                    System.out.println("LINE ===>>: "+line);
                }
                }
                
                } catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
                	//don't print stacktrace;)
			}
		}
		
	}
}