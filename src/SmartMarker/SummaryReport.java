/**
 * 
 */
package SmartMarker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author NickP
 *
 */
public class SummaryReport implements IReport
{
	private final static String CLASS_NAME = SummaryReport.class.getName();
	private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);

	protected double Compare(Chromosomes teacher, Chromosomes student, Vector<String> strings)
	{
		return student.compareSummary(teacher, strings);
	}
	
	public String GetDefaultReportDirectoryName()
	{
		return "SmartType reports";
	}

	public void CreateDirIfNeeded(File dirReports)
	{
		// if the reports directory does not exist, create it
		if (!dirReports.exists())
		{
			LOGGER.info("creating directory: " + dirReports.getAbsolutePath());
			boolean result = dirReports.mkdir();  
			if(result)
			{    
				LOGGER.info("DIR created");  
			}
		}
	}
	
	protected double Compare(String fnStudent, Chromosomes teacher, TypeXmlReaderStudent student, File dirReports, Vector<String> strings) throws IOException
	{
		// if the reports directory does not exist, create it
		CreateDirIfNeeded(dirReports);					
		strings.add("<tr><td>" + fnStudent + "</td>");

		double results = Compare(teacher, student.chromosomes, strings);

		strings.add("</tr>\r\n");
		return results;
	}
	
	protected String getFilenameNoExt()
	{
		return "summary";
	}
	
	protected String getFilename()
	{
		return getFilenameNoExt() + ".HTML";
	}
	
	protected void getHeader(TypeXmlReaderTeacher teacher, FileWriter fileWriter) throws IOException
	{
		fileWriter.write("<html>\r\n<title>" + teacher.AbsPath + "</title>\r\n<body>\r\n<table border='1'>\r\n<tr>");
		fileWriter.write("<td>Name</td>");
		fileWriter.write(teacher.chromosomes.summaryTitle());
		fileWriter.write("</tr>\r\n");
	}
	
	protected void getTail(TypeXmlReaderTeacher teacher, FileWriter fileWriter) throws IOException
	{
		fileWriter.write("</table>\r\n</body>\r\n</html>\r\n");
	}
	
	public double Compare(String StudentFn, TypeXmlReaderTeacher teacher, TypeXmlReaderStudent student, File dirReports, Vector<String> strings) throws IOException
	{
		double results = Compare(StudentFn, teacher.chromosomes, student, dirReports, strings);
		return results;
	}
	
	public void Compare(TypeXmlReaderTeacher teacher, File directory, String StudentFn, File dirReports, Vector<String> strings) throws IOException
	{
		TypeXmlReaderStudent student = new TypeXmlReaderStudent();

		// This may be possible when jre7 is more available
		//Path pathStudent = Paths.get(dirStudent, StudentFn);
		File fileStudent = new File(directory, StudentFn);
		String StudentAbsPath = fileStudent.getAbsolutePath();
	
		// This may be possible when jre7 is more available
		//System.out.println("Compare " + pathStudent.toString() + " with " + pathTeacher.toString());
		LOGGER.info("<Compare SourceFile='" + StudentAbsPath + "' TargetFile='" + teacher.AbsPath + "'>");
		
		// This may be possible when jre7 is more available
		//if(pathStudent.equals(pathTeacher))
		if(teacher.AbsPath.equals(StudentAbsPath))
		{
			LOGGER.info("Teachers file ignored.");
		}
		else
		{
			// This may be possible when jre7 is more available
			//student.run(pathStudent);
			student.run(StudentAbsPath);
				
			Compare(StudentFn, teacher, student, dirReports, strings);
		}

		LOGGER.info("</Compare>");

	}
	
	// return true for at least one file written
	public boolean Compare(TypeXmlReaderTeacher teacher, File directory, String[] studentFiles)
	{
		boolean noFiles = false;
	
		if(null == studentFiles)
		{
			noFiles = true;
		}
		else
		{
			if(0 == studentFiles.length)
			{
				noFiles = true;
			}
		}
		
		if(noFiles)
		{
			LOGGER.info("No student files found");
			return false;
		}

		boolean fileWritten = false;

		// fetch all the files from the folder
		FileWriter fileWriter = null;
		File dirReports = new File(directory, GetDefaultReportDirectoryName());
		
		try
		{
			Vector<String> strings = new Vector<String>();

			for (String StudentFn : studentFiles)
			{
				Compare(teacher, directory, StudentFn, dirReports, strings);
			}
			
			//create the report file if needed	
			if(strings.size() > 0)
			{
				File newTextFile = new File(dirReports, getFilename());
				fileWriter = new FileWriter(newTextFile);
				getHeader(teacher, fileWriter);

				for(String line : strings)
				{
					if(null != fileWriter)
					{
						fileWriter.write(line);
					}
				}

				getTail(teacher, fileWriter);
		
				if(null != fileWriter)
				{
					fileWriter.close();
					fileWritten = true;
				}
			}
		}
		catch (FileNotFoundException ex)
		{
			LOGGER.log(Level.WARNING, "In Summary Report", ex);
		}
		catch (IOException ex)
		{
			LOGGER.log(Level.SEVERE, "In Summary Report", ex);
		}
		finally
		{
			try
			{
				if(null != fileWriter)
				{
					fileWriter.close();
				}
			}
			catch (IOException ex)
			{
				Logger.getLogger(SummaryReport.class.getName())
						.log(Level.SEVERE, null, ex);
			}
		}
		
		return fileWritten;
	}
	
	// return true for at least one file written
	@Override
	public boolean Compare(String TeacherAbsPath, String dirStudent)
	{
		boolean fileWritten = false;
		
		// fetch all the files from the folder
		TypeXmlReaderTeacher teacher = new TypeXmlReaderTeacher();
		// This may be possible when jre7 is more available
		//Path pathTeacher = Paths.get(TeacherFn);
		
		// This may be possible when jre7 is more available
		//teacher.run(pathTeacher);
		if(teacher.run(TeacherAbsPath))
		{
			String[] studentFiles;
			FilenameFilter filter = new FilenameFilter() {
				@Override
				public boolean accept(File directory, String fileName) {
					return fileName.endsWith(".kry");
				}
			};
			
			File directory = new File(dirStudent);
			studentFiles = directory.list(filter);
			
			fileWritten = Compare(teacher, directory, studentFiles);
		}
		
		return fileWritten;
	}		
}