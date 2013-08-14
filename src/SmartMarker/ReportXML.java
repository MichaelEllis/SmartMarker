/**
 * 
 */
package SmartMarker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;


/**
 * @author NickP
 *
 */
public class ReportXML extends Report implements IReport {

	/**
	 * 
	 */
	public ReportXML() {
		// TODO Auto-generated constructor stub
	}
	
	@Override protected double Compare(Chromosomes teacher, Chromosomes student, Vector<String> strings) {
		return student.compareXML(teacher, strings);
	}

	@Override protected double Compare(String fnStudent, Chromosomes teacher, TypeXmlReaderStudent student, File dirReports, Vector<String> strings) throws IOException {
		// if the reports directory does not exist, create it
		CreateDirIfNeeded(dirReports);					
		strings.add("<student path='" + fnStudent + "'>\r\n");

		if(includeImages)
		{
			String strImg = TypeXmlReaderStudent.encodeJpegToString(student.imgKaryotype);
			strings.add("<Karyotype src='data:image/jpeg;base64,");
			strings.add(strImg);
			strings.add("' />\r\n");
		}
		
		double results = Compare(teacher, student.chromosomes, strings);
		strings.add("</student>\r\n");

		return results;
	}
	
	@Override protected String getFilename()
	{
		return getFilenameNoExt() + ".XML";
	}
	
	protected Vector<String> getHeader(TypeXmlReaderTeacher teacher)
	{		
		Vector<String> strings = new Vector<String>();

		strings.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");	      
		strings.add("<?xml-stylesheet type='text/xsl' href='report.xsl'?>\r\n");
		strings.add("<teacher path='" + teacher.AbsPath + "' date='" + getDate() + "'>\r\n" );

		strings.add("<verbosity value='" + verbosity + "' description='" );

		switch(verbosity)
		{
		case 0:
			strings.add("errors only");
			break;
			
		case 1:
			strings.add("warnings and errors");
			break;
			
		default:
			strings.add("everything");
		}
		
		strings.add("'/>\r\n");
		
		if(includeImages)
		{
			String strImg = TypeXmlReaderStudent.encodeJpegToString(teacher.imgMetaphase);
			strings.add("<Metaphase src='data:image/jpeg;base64,");
			strings.add(strImg);
			strings.add("' />\r\n");
			strImg = TypeXmlReaderStudent.encodeJpegToString(teacher.imgKaryotype);
			strings.add("<Karyotype src='data:image/jpeg;base64,");
			strings.add(strImg);
			strings.add("' />\r\n");
		}
		
		return strings;
	}

	@Override protected void getHeader(TypeXmlReaderTeacher teacher, FileWriter fileWriter) throws IOException
	{		
		Vector<String> strings = getHeader(teacher);
		
		for(String line : strings)
		{
			if(null != fileWriter)
			{
				fileWriter.write(line);
			}
		}
	}
	
	protected Vector<String> getTail()
	{
		Vector<String> strings = Chromosomes.footerXML();
		
		if(!strings.isEmpty())
		{
			strings.add("</teacher>\r\n");
		}
		
		return strings;
	}
	
	@Override protected void getTail(TypeXmlReaderTeacher teacher, FileWriter fileWriter) throws IOException
	{
		Vector<String> strings = getTail();

		for(String line : strings)
		{
			if(null != fileWriter)
			{
				fileWriter.write(line);
			}
		}
	}

	// return true for at least one file written
	@Override
	public boolean Compare(String TeacherAbsPath, String dirStudent)
	{
		boolean fileWritten = super.Compare(TeacherAbsPath, dirStudent);
		
		return fileWritten;
	}
}
