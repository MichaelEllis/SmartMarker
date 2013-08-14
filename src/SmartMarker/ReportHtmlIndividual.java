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
public class ReportHtmlIndividual extends ReportHTML implements IReport {

	/**
	 * 
	 */
	public ReportHtmlIndividual() {
	}

	@Override
	protected void getHeader(String fileName, FileWriter fileWriter) throws IOException {
		fileWriter.write("<html>\r\n<body>\r\n");

		// import any branding
		ImportBranding(fileWriter);

		fileWriter.write("<h2> File: " + fileName + "</h2>");
		fileWriter.write("<h2> Date: " + getDate() + "</h2>");
	}

	@Override
	protected double Compare(Chromosomes teacher, Chromosomes student, Vector<String> strings) {
		return student.compareHTML(teacher, strings, verbosity);
	}

	@Override
	public double Compare(String fnStudent, TypeXmlReaderTeacher teacher,
			TypeXmlReaderStudent student, File dirReports, Vector<String> strings)
			throws IOException {
		// if the reports directory does not exist, create it
		CreateDirIfNeeded(dirReports);
		Vector<String> localStrings = new Vector<String>();

		double results = Compare(teacher.chromosomes, student.chromosomes, localStrings);

		// create the report file if needed
		if (localStrings.size() > 0) {
			File newTextFile = new File(dirReports, fnStudent + ".html");
			FileWriter fileWriter = new FileWriter(newTextFile);
			getHeader(fnStudent, fileWriter);

			switch (verbosity) {
			case 0:
				fileWriter.write(" Errors only.");
				break;

			case 1:
				fileWriter.write(" Warnings and errors.");
				break;

			default:
				; // do nothing
			}

			fileWriter.write("\r\n");

			if (includeImages) {
				fileWriter.write("<table><tr><td>\r\n");
			}

			if (includeImages) {
				String strImg = TypeXmlReaderStudent.encodeJpegToString(student.imgKaryotype);
				localStrings.add("</td><td width='50%'><figure>\r\n");
				localStrings
						.add("<figcaption style='text-align:center'>Student Karyotype</figcaption>");
				localStrings.add("<img src='data:image/jpeg;base64,");
				localStrings.add(strImg);
				localStrings.add("' width='95%'/>\r\n");
				localStrings.add("</figure></td></td></table>\r\n");
			}

			for (String line : localStrings) {
				if (null != fileWriter) {
					fileWriter.write(line);
				}
			}

			getTail(teacher, fileWriter);

			if (null != fileWriter) {
				fileWriter.close();
			}
		}

		return results;
	}
}
