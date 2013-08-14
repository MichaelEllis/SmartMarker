package SmartMarker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import SmartMarker.Chromosomes.RotationTolerance;
import SmartMarker.Chromosomes.VerticalTolerance;

/**
 * @author NickP
 * 
 */
public class ReportHTML extends Report implements IReport {
	private final static String CLASS_NAME = ReportHTML.class.getName();
	private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);

	@Override
	protected double Compare(Chromosomes teacher, Chromosomes student, Vector<String> strings) {
		return student.compareHTML(teacher, strings, verbosity);
	}

	@Override
	protected double Compare(String fnStudent, Chromosomes teacher, TypeXmlReaderStudent student,
			File dirReports, Vector<String> strings) throws IOException {
		// if the reports directory does not exist, create it
		CreateDirIfNeeded(dirReports);
		strings.add("<div>" + fnStudent + "\r\n");

		if (includeImages) {
			strings.add("<table><tr><td>\r\n");
		}

		double results = Compare(teacher, student.chromosomes, strings);

		if (includeImages) {
			String strImg = TypeXmlReaderStudent.encodeJpegToString(student.imgKaryotype);
			strings.add("</td><td width='50%'><figure>\r\n");
			strings.add("<figcaption style='text-align:center'>Student Karyotype</figcaption>");
			strings.add("<img src='data:image/jpeg;base64,");
			strings.add(strImg);
			strings.add("' width='95%'/>\r\n");
			strings.add("</figure></td></td></table>\r\n");
		}

		strings.add("</div>\r\n<hr>\r\n");

		return results;
	}

	protected void ImportBranding(FileWriter fileWriter) throws IOException {
		// import any branding
		try {
			FileReader brandingTextFile = new FileReader("Branding.txt");
			BufferedReader br = new BufferedReader(brandingTextFile);

			String line;

			while ((line = br.readLine()) != null) {
				fileWriter.write(line);
			}

			brandingTextFile.close();
		} catch (FileNotFoundException ex) {
			String logMessage = "FileNotFoundException in Report ImportBranding user.dir="
					+ System.getProperty("user.dir");
			LOGGER.log(Level.WARNING, logMessage, ex);
		} catch (IOException ex) {
			String logMessage = "IOException in Report ImportBranding user.dir="
					+ System.getProperty("user.dir");
			LOGGER.log(Level.WARNING, logMessage, ex);
		} finally {
		}
	}

	protected void getHeader(String fileName, FileWriter fileWriter) throws IOException {
		fileWriter.write("<html>\r\n<title>" + fileName + "</title>\r\n<body>\r\n");

		// import any branding
		ImportBranding(fileWriter);

		fileWriter.write("<h2> Date: " + getDate() + "</h2>");
	}

	@Override
	protected void getHeader(TypeXmlReaderTeacher Teacher, FileWriter fileWriter)
			throws IOException {
		getHeader(Teacher.AbsPath, fileWriter);
	}

	@Override
	protected void getTail(TypeXmlReaderTeacher teacher, FileWriter fileWriter) throws IOException {
		fileWriter.write("<h3>Key and Explanation</h3>\r\n<ol>\r\n<li>\r\n");
		fileWriter
				.write("Chromosomes are identifed by class and rank e.g. 2.1 is class 2 and rank 1. A Chromosome class of U means the object is unclassifed (i.e. not a chromosome).");
		fileWriter.write("</li>\r\n<li>\r\n");
		fileWriter.write("Outline % refers to the accuracy of the chromosome boundary outline.");
		fileWriter.write("</li>\r\n<li>\r\n");
		fileWriter
				.write("Rotation \u00B0 refers to the rotation angle deviation from the ideal angle. ");
		fileWriter.write("+/-" + new DecimalFormat("#.##").format(RotationTolerance.GREEN.value));
		fileWriter.write(" green, Upside down (but within green tolerance) or ");
		fileWriter.write("+/-" + new DecimalFormat("#.##").format(RotationTolerance.AMBER.value));
		fileWriter.write(" amber, anything more red.");
		fileWriter.write("</li>\r\n<li>\r\n");
		fileWriter
				.write("Shift refers to the accuracy in placing the chromosome relative to it's centromere (in pixels) ");
		fileWriter.write("+/-" + VerticalTolerance.GREEN.value);
		fileWriter.write(" green, +/-" + VerticalTolerance.AMBER.value);
		fileWriter.write(" amber, anything more red.");
		fileWriter.write("</li>\r\n</ol>\r\n");

		if (includeImages) {
			fileWriter.write("<table><tr><td width='50%'><figure>\r\n");
			fileWriter
					.write("<figcaption style='text-align:center'>Reference Metaphase</figcaption>");
			String strImg = TypeXmlReaderStudent.encodeJpegToString(teacher.imgMetaphase);
			fileWriter.write("<img src='data:image/jpeg;base64,");
			fileWriter.write(strImg);
			fileWriter.write("' width='95%'/>\r\n");
			fileWriter.write("</figure></td><td width='50%'><figure>\r\n");
			fileWriter
					.write("<figcaption style='text-align:center'>Reference Karyotype</figcaption>");
			strImg = TypeXmlReaderStudent.encodeJpegToString(teacher.imgKaryotype);
			fileWriter.write("<img src='data:image/jpeg;base64,");
			fileWriter.write(strImg);
			fileWriter.write("' width='95%'/>\r\n");
			fileWriter.write("</figure></td></td></table>\r\n");
		}

		fileWriter.write("</body>\r\n</html>\r\n");
	}
}
