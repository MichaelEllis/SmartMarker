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
public class ReportXmlIndividual extends ReportXML implements IReport {

	/**
	 * 
	 */
	public ReportXmlIndividual() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public double Compare(String fnStudentNoExt, TypeXmlReaderTeacher teacher,
			TypeXmlReaderStudent student, File dirReports, Vector<String> strings)
			throws IOException {
		// if the reports directory does not exist, create it
		CreateDirIfNeeded(dirReports);
		Vector<String> localStrings = new Vector<String>();

		localStrings.addAll(getHeader(teacher));

		localStrings.add("<student path='" + fnStudentNoExt + "' date='" + getDate() + "'>\r\n");

		if (includeImages) {
			String strImg = TypeXmlReaderStudent.encodeJpegToString(student.imgKaryotype);
			localStrings.add("<Karyotype src='data:image/jpeg;base64,");
			localStrings.add(strImg);
			localStrings.add("' />\r\n");
		}

		double results = Compare(teacher.chromosomes, student.chromosomes, localStrings);

		localStrings.add("</student>\r\n");

		localStrings.addAll(getTail());

		// create the report file if needed
		if (localStrings.size() > 0) {
			boolean fileWritten = false;
			File newTextFile = new File(dirReports, fnStudentNoExt + ".xml");
			FileWriter fileWriter = new FileWriter(newTextFile);

			for (String line : localStrings) {
				if (null != fileWriter) {
					fileWriter.write(line);
				}
			}

			if (null != fileWriter) {
				fileWriter.close();
				fileWritten = true;
			}
		}

		return results;
	}
}
