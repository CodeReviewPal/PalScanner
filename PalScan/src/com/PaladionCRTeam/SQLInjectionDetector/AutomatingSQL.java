
package com.PaladionCRTeam.SQLInjectionDetector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AutomatingSQL {

	public static String path=null;

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter your Codebase file path: ");
		path = scanner.nextLine();
		scanner.close();
		traversedirectory(new File(path));
		System.out.println("Report Generated Successfully in .CSV file format!!!");
		System.out.println("Done! Thanku for using MyScript. :-)");

	}
	
/*
 * method to traverse all the directory within the code base.
 */
	public static void traversedirectory(File node){

		Path path = FileSystems.getDefault().getPath(node.getAbsolutePath());

		String sourcepaths = path.toString();

		// Filtering useful files according to their extensions.
		fileExtensionFilteration(sourcepaths);

		if(node.isDirectory()){
			String[] subNote = node.list();
			for(String filename : subNote){
				traversedirectory(new File(node, filename));
			}
		}

	}
/*
 * method to filter useful files according to their extensions.
 */
	public static void fileExtensionFilteration(String path){
		String filterRegex = "(^.*\\.(java|jsp|xml|sql|asp|aspx|cs)$)";
		Pattern p = Pattern.compile(filterRegex);
		Matcher matcher = p.matcher(path);
		if(matcher.find()){
			try {
				checkDynamicSQLQuery(path);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

/*
 * method to check for two things
 * (1) DML Queries in the code base irrespective with space or Multiline.
 * (2) Check for dynamic string present in the DML Query.
 */
	public static void checkDynamicSQLQuery(String path) throws FileNotFoundException{

		TreeMap<Integer,String> query = new TreeMap<Integer,String>();
		File file  = new File(path);
		System.out.println();
		System.out.println("===========================================================================");
		System.out.println("FileName: " + file.getName());
		System.out.println();
		System.out.println("FilePath: " + file.getAbsolutePath());
		System.out.println();
		System.out.println();
		List<Output> finaloutput = null;
		String[] REGEX = new String[4];
		
		// REGEX to find SELECT query
		REGEX[0] = "((?i)([\"']([\\s]*?)select\\s|(^[\\w\\.])+\\w from([\\s]*?)).*?[;$])"; 

		// REGEX to find INSERT query
		REGEX[1] = "((?i)(['\"]([\\s]*?)insert([\\s]+)into([\\s]*?)).*?[;$])";

		// REGEX to find UPDATE query
		REGEX[2] = "((?i)(['\"]([\\s]*?)update([\\s\\w,]*)set([\\s]*?)).*?[;$])";

		// REGEX to find DELETE query
		REGEX[3] = "((?i)(['\"]([\\s]*?)delete([\\s]+)from([\\s]*?)).*?[;$])";


		//Iterate the String array and check in the line
		for(String regex : REGEX){

			Pattern p = Pattern.compile(regex,Pattern.MULTILINE|Pattern.DOTALL);
			BufferedReader r = new BufferedReader(new FileReader(path));
			String line;
			int lineno = 0;

			try {
				while((line = r.readLine()) != null){
					lineno++;
					Matcher m = p.matcher(line);

					while(m.find()){
						String dyn_regex = "([\"']([\\s]*?)(''[+@&].*|[^']*').*?[;$])";
						Pattern p1 = Pattern.compile(dyn_regex,Pattern.MULTILINE|Pattern.DOTALL);
						Matcher m1 = p1.matcher(line);
						while(m1.find()){
							query.put(lineno, line);
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		finaloutput = new ArrayList<Output>();
		int index = 1;
		for(Map.Entry m: query.entrySet()){  
			System.out.println("Line " + m.getKey()+" : " + m.getValue());
			Output output = new Output(index,file.getName(),file.getAbsolutePath(),m.getKey(),m.getValue()); 
			finaloutput.add(output);
			index++;
		}
		// For generating final result in CSV Format.
		
		GenerateReport.writeCsvFile(finaloutput);
	}
}

/*
 * Class to Generate final report in CSV Format.
 */
class GenerateReport{

	static GenerateReport report = new GenerateReport();
	static String report_path = AutomatingSQL.path; 
	//Delimiter used in CSV file
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";

	//CSV file header
	private static final String FILE_HEADER = "S.No.,File Name,Path,Line Number,Instance";
	private static String filename = report_path +"/Final_Report.csv";
	static FileWriter filewriter = null;
	public static void writeCsvFile(List<Output> finaloutput) {
		try {

			File finalreport = new File(filename);
			if (!finalreport.exists()) {
				finalreport.createNewFile();
			}

			filewriter = new FileWriter(finalreport,true);

			//Write the CSV file header
			filewriter.append(FILE_HEADER.toString());

			//Add a new line separator after the header
			filewriter.append(NEW_LINE_SEPARATOR);

			for(Output output : finaloutput){

				//Write the data to CSV file
				filewriter.append(String.valueOf(output.getIndex()));
				filewriter.append(COMMA_DELIMITER);
				filewriter.append(output.getFileName());
				filewriter.append(COMMA_DELIMITER);
				filewriter.append(output.getPath());
				filewriter.append(COMMA_DELIMITER);
				filewriter.append(String.valueOf(output.getLineNumber()));
				filewriter.append(COMMA_DELIMITER);
				filewriter.append(output.getInstance());
				filewriter.append(NEW_LINE_SEPARATOR);
			}
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {

			try {
				filewriter.flush();
				filewriter.close();

			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
				e.printStackTrace();

			}	
		}
	}
}

/*
 * POJO class for CSV Format output file.
 */
class Output{

	private int index;
	private String fileName;
	private String path;
	private int lineNumber;
	private String instance;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	public Output(int index, String fileName, String path, Object object, Object object2) {
		super();
		this.index = index;
		this.fileName = fileName;
		this.path = path;
		this.lineNumber = (int) object;
		this.instance = (String) object2;
	}

	public Output() {
		super();
		// TODO Auto-generated constructor stub
	}	
}
