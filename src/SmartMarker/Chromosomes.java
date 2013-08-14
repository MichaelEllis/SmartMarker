/**
 * 
 */
package SmartMarker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * @author NickP
 *
 */
public class Chromosomes extends ArrayList<Chromosome>
{
	private final static String CLASS_NAME = Chromosomes.class.getName();
	private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);

	public enum AreaTolerance
	{
	    AMBER(0.3),
	    GREEN(0.1);
 
        public final double value;
 
        private AreaTolerance(double value)
        {
           this.value = value;
        }
	}
	
	public enum RotationTolerance
	{
	    AMBER(15),
	    GREEN(5);
 
        public final double value;
 
        private RotationTolerance(double value)
        {
           this.value = value;
        }
	}
	
	public enum VerticalTolerance
	{
	    AMBER(5),
	    GREEN(2);
 
        public final Integer value;
 
        private VerticalTolerance(int value)
        {
           this.value = value;
        }
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6752255109208032104L;
	
	/**
	 * make a list of all the places it is worth
	 * testing each of the chromosomes on the students list
	 * against this teachers chromosome
	 */
	public Places findPossiblePlaces(Chromosome target)
	{
		Places places = new Places();

		// check each chromosome on the student list
		for(Chromosome source : this)
		{
			// if there is an overlap store this as a possible location
			places.appendIntersectionOf(source, target);
		}
		
		return places;
	}
	
	/**
	 * make a list of all the places it is worth
	 * testing each of the chromosomes on the students list
	 * N.B. A place is a mapping between a specific student
	 * chromosome (source) and a specific teacher chromosome (target).
	 * This mapping is assigned a value based on how much the areas of
	 * the two chromosome overlap 
	 */
	public ListOfPlaces findListOfPlaces(Chromosomes teacher)
	{
		ListOfPlaces listOfPlaces = new ListOfPlaces();
		
		// Place p = new Place();
		
		// for each chromosome on the teacher list
		for(Chromosome target : teacher)
		{
			// find student chromosomes that may match it
			Places places = findPossiblePlaces(target);
			
			// there may be more than one possible student chromosome
			// so record the contestants
			listOfPlaces.recordContestants(places);
		}

		// Tydy up - throw away places that are worthless
		// if the highest value place on a student chromosome is uncontested
		// there is no point keeping the contested ones
		listOfPlaces.tidyUp();


		// DEBUG
		//listOfPlaces.dump();
		
		return listOfPlaces;
	}

	/**
	 */
	private boolean isAreaWithinTolerance(double intersectpercentage, double tolerance)
	{
		boolean result = false;
		
		// here 1.0 === 100%
		if((intersectpercentage + tolerance) > 1.0)
		{
			if((intersectpercentage - tolerance) < 1.0)
			{
				result = true;
			}
		}
			
		return result;
	}

	/**
	 */
	public boolean isAreaWithinGreenTolerance(double intersectpercentage)
	{
		return isAreaWithinTolerance(intersectpercentage, AreaTolerance.GREEN.value);
	}
	
	/**
	 */
	public boolean isAreaWithinAmberTolerance(double intersectpercentage)
	{
		return isAreaWithinTolerance(intersectpercentage, AreaTolerance.AMBER.value);
	}

	/**
	 */
	public boolean isRotationWithinInvertedTolerance(double rotaStudent, double rotaTeacher, double tolerance)
	{
		boolean result = false;
		
		if(rotaStudent > rotaTeacher)
		{
			result = isRotationWithinTolerance((rotaStudent - 180), rotaTeacher, tolerance);
		}
		else
		{
			result = isRotationWithinTolerance((rotaStudent + 180), rotaTeacher, tolerance);
		}
			
		return result;
	}

	/**
	 */
	public boolean isRotationWithinGreenInvertedTolerance(double rotaStudent, double rotaTeacher)
	{		
		return isRotationWithinInvertedTolerance(rotaStudent, rotaTeacher, RotationTolerance.GREEN.value);
	}

	/**
	 *
	 */
	private boolean isRotationWithinTolerance(double rotaStudent, double rotaTeacher, double tolerance)
	{
		boolean result = false;
		
		if((rotaStudent + tolerance) > rotaTeacher)
		{
			if((rotaStudent - tolerance) < rotaTeacher)
			{
				result = true;
			}
		}
			
		return result;
	}

	/**
	 */
	public boolean isRotationWithinGreenTolerance(double rotaStudent, double rotaTeacher)
	{		
		return isRotationWithinTolerance(rotaStudent, rotaTeacher, RotationTolerance.GREEN.value);
	}

	/**
	 */
	public boolean isRotationWithinAmberTolerance(double rotaStudent, double rotaTeacher)
	{		
		return isRotationWithinTolerance(rotaStudent, rotaTeacher, RotationTolerance.AMBER.value);
	}

	/**
	 * 
	 *
	 */
	public boolean isVerticalCentreWithinTolerance(int microYStudent, int microYTeacher, int tolerance)
	{
		boolean result = false;
		
		if((microYStudent + tolerance) > microYTeacher)
		{
			if((microYStudent - tolerance) < microYTeacher)
			{
				result = true;
			}
		}
			
		return result;
	}

	/**
	 */
	public boolean isVerticalCentreWithinGreenTolerance(int microYStudent, int microYTeacher)
	{		
		return isVerticalCentreWithinTolerance(microYStudent, microYTeacher, VerticalTolerance.GREEN.value);
	}

	/**
	 */
	public boolean isVerticalCentreWithinAmberTolerance(int microYStudent, int microYTeacher)
	{		
		return isVerticalCentreWithinTolerance(microYStudent, microYTeacher, VerticalTolerance.AMBER.value);
	}
	
	/**
	 * title line for the summary report
	 *
	 */
	public String summaryTitle()
	{
		String line = "<td>Missing</td><td>Additional</td><td>Correctly Classified</td><td>Wrongly Classified</td>";
		line += "<td>Outline Accuracy</td><td>Rotation upside-down/inaccurate</td><td>Shift</td><td>Overall Mark/100</td>";
		return line;
	}
	
	/**
	 * compare this list with another and produce a score as to how well they match
	 *
	 */
	public double compareSummary(Chromosomes teacher, Vector<String> strings)
	{
		double score = 0;
		String line = new String();
		
		Map<Chromosome, Place> resultMap = map(teacher);
		
		int ssize = size();
		int tsize = teacher.size();
		
		if(ssize < tsize)
		{
			line = "<td>" + (tsize - ssize) + "</td>";			
		}
		else
		{
			line = "<td>0</td>";			
		}
		
		if(ssize > tsize)
		{
			line += "<td>" + (ssize - tsize) + "</td>";			
		}
		else
		{
			line += "<td>0</td>";			
		}
				
		double totalContribution = 0;
		// walk the map reporting area intersection errors
		// walk the map reporting classification errors
		// walk the map reporting rotation errors
		Integer errShift = 0;
		Integer errClass = 0;
		Integer errRota = 0;
		Integer errUpsideDown = 0;
		Integer outlineAccuracy = 0;
		double intersectPercentage = 0;
		
		for(Place place : resultMap.values())
		{
			// Vector<String> strChromosome = new Vector<String>();
			double contribution = 0;
			
			intersectPercentage += place.intersectPercentage;
			
			if(isAreaWithinGreenTolerance(place.intersectPercentage))
			{
				contribution += 10; 
			}
			else if(isAreaWithinAmberTolerance(place.intersectPercentage))
			{
				contribution += 9; 
			}
			else
			{
				contribution += 5; 
			}
		
			if(place.source.classNum == place.target.classNum)
			{
				contribution += 5; 
			}
			else
			{
				contribution += 0;
				errClass++;
			}
			
			if(isVerticalCentreWithinGreenTolerance(place.source.microY, place.target.microY))
			{
				contribution += 5; 
			}
			else if(isVerticalCentreWithinAmberTolerance(place.source.microY, place.target.microY))
			{
				contribution += 4; 
			}
			else
			{
				contribution += 0; 
				errShift++;
			}
			
			double studentRota = place.source.getDegRota();
			double teacherRota = place.target.getDegRota();
		
			if(isRotationWithinGreenTolerance(studentRota, teacherRota))
			{
				contribution += 5; 
			}
			else if(isRotationWithinAmberTolerance(studentRota, teacherRota))
			{
				contribution += 4; 
			}
			else if(isRotationWithinGreenInvertedTolerance(studentRota, teacherRota))
			{	
				contribution += 4;
				errUpsideDown++;
			}
			else
			{
				contribution += 0;
				errRota++;
			}
		
			totalContribution += contribution;

		}

		double max = 0;
		
		if(tsize > 0)
		{
			// each chromosome is marked out of 25 (MAX_MARK)
			max = tsize * Chromosome.MAX_MARK;
			score = totalContribution / max;
			outlineAccuracy = (int)(100.0 * (intersectPercentage / tsize));
		}
		
		Integer userScore = (int)(score * 100);

		line +=  "<td>" + (tsize - errClass) + "</td><td>" + errClass + "</td><td>" + outlineAccuracy;
		line +=  "%</td><td>" + errUpsideDown + " / " + errRota + "</td><td>" + errShift + "</td><td>" + userScore + "</td>";
		strings.add(line);
		
		/* DEBUG
		for(String string : strings)
		{
			LOGGER.info(string);
		} */
				
		return score;
	}
	
	
	/**
	 * compare this list with another and produce a score as to how well they match
	 * verbosity - 0 = only errors, 1 = Warnings and errors, 2 = everything
	 *
	 */
	public double compareHTML(Chromosomes teacher, Vector<String> strings, Integer verbosity)
	{
		double score = 0;
		Vector<String> localStrings = new Vector<String>();
		
		Map<Chromosome, Place> resultMap = map(teacher);
				
		// sort into student class.rank order
		Places placeList = new Places();
		placeList.addAll(resultMap.values());

		Collections.sort(placeList);

		int ssize = placeList.size();
		int tsize = teacher.size();
		
		if(ssize > tsize)
		{
			localStrings.add(" <div style='padding-bottom: 0px;padding-top: 0px;padding-left: 20px;'>Student clasified " + (ssize - tsize) + " additional chromosomes.</div>\r\n");			
		}
		
		// insert the table and title
		localStrings.add(" <table frame='border' rules='all' cellpadding='5' cellspacing='3'>\r\n");
		localStrings.add(" <tr>\r\n<td align='center' colspan='6'>\r\nKaryotype Report\r\n");
		localStrings.add(" </td>\r\n</tr>\r\n");

		// insert the table headings
		localStrings.add(" <tr>\r\n<td>Chromo</td>\r\n<td>Class</td>\r\n");
		localStrings.add(" <td>Outline %</td>\r\n<td>Rotation &#176;</td>\r\n");
		localStrings.add(" <td>Shift</td>\r\n<td>Marks</td>\r\n</tr>\r\n");
		
		if(ssize < tsize)
		{
			// list missing chromosomes
			Chromosomes missing = placeList.FindMissingTeacherChromosomes(teacher);
			
			for(Chromosome c : missing)
			{
				localStrings.add(" <tr>\r\n<td>");
				
				String s = c.getClassName();
				localStrings.add(s);
				localStrings.add(" </td>\r\n<td colspan='4' bgcolor='#ffaaaa'>" +
					"Unclassified</td>\r\n<td bgcolor='#ffaaaa'>0" +
					"</td>\r\n</tr>\r\n");
			}
		}
						
		double totalContribution = 0;
		// walk the map reporting area intersection errors
		// walk the map reporting classification errors
		// walk the map reporting rotation errors
		for(Place place : placeList)
		{
			//String lineTitle = new String();
			String line = new String();
			// Vector<String> strChromosome = new Vector<String>();
			double contribution = 0;
			boolean bLineNeeded = false;
			int intersectPercentage = (int)((100*place.intersectPercentage) + 0.5);
			int countAmber = 0;
			boolean bRed = false;
			
			line = "<td ";			
			if(place.source.classNum == place.target.classNum)
			{
				if(verbosity > 1)
				{
					bLineNeeded = true;
				}

				line += "bgcolor='#ffffff'>" + place.target.classNum;
				contribution += 5; 
			}
			else
			{
				bLineNeeded = true;
				
				line += "bgcolor='#ffaaaa'>" + place.target.classNum;

				contribution += 0; 
				bRed = true;
			}
			line += "</td>\r\n"; 

			line += "<td ";			
			if(isAreaWithinGreenTolerance(place.intersectPercentage))
			{
				if(verbosity > 1)
				{
					bLineNeeded = true;
				}

				line += "bgcolor='#ffffff'";
				contribution += 10;
			}
			else if(isAreaWithinAmberTolerance(place.intersectPercentage))
			{
				if(verbosity > 0)
				{
					bLineNeeded = true;			
				}

				line += "bgcolor='#ffffaa'";
				contribution += 9; 
				countAmber++;
			}
			else
			{
				bLineNeeded = true;
				line += "bgcolor='#ffaaaa'";			
				contribution += 5; 
				bRed = true;

			}
			line += ">" + new DecimalFormat("#.##").format(intersectPercentage) + "</td>\r\n"; 

			line += "<td ";			
			double studentRota = place.source.getDegRota();
			double teacherRota = place.target.getDegRota();
			
			if(isRotationWithinGreenTolerance(studentRota, teacherRota))
			{
				if(verbosity > 1)
				{
					bLineNeeded = true;
				}

				line += "bgcolor='#ffffff'>";
				contribution += 5; 
			}
			else if(isRotationWithinAmberTolerance(studentRota, teacherRota))
			{
				if(verbosity > 0)
				{
					bLineNeeded = true;
				}

				line += "bgcolor='#ffffaa'>";
				contribution += 4; 
				countAmber++;
			}
			else if(isRotationWithinGreenInvertedTolerance(studentRota, teacherRota))
			{	
				if(verbosity > 0)
				{
					bLineNeeded = true;
				}

				line += "bgcolor='#ffffaa'> &#8659; ";
				countAmber++;
				contribution += 4;
			}
			else
			{
				bLineNeeded = true;
				line += "bgcolor='#ffaaaa'>";			
				contribution += 0; 
				bRed = true;
			}
			line += " " + new DecimalFormat("#.##").format(studentRota - teacherRota) + "</td>\r\n";

			line += "<td ";			
			if(isVerticalCentreWithinGreenTolerance(place.source.microY, place.target.microY))
			{
				if(verbosity > 1)
				{
					bLineNeeded = true;
				}

				line += "bgcolor='#ffffff'";
				contribution += 5; 
			}
			else if(isVerticalCentreWithinAmberTolerance(place.source.microY, place.target.microY))
			{			
				if(verbosity > 0)
				{
					bLineNeeded = true;
				}

				line += "bgcolor='#ffffaa'";
				contribution += 4; 
				countAmber++;
			}
			else
			{
				bLineNeeded = true;
				line += "bgcolor='#ffaaaa'";			
				contribution += 0;
				bRed = true;
			}
			line += ">" + new DecimalFormat("#.##").format(place.source.microY - place.target.microY) + "</td>\r\n"; 
			
			// if more than one amber result in this line
			if(countAmber > 1)
			{
				// treat this line as red
				bLineNeeded = true;
				bRed = true;
			}
			
			if(bLineNeeded)
			{
				localStrings.add(" <tr>\r\n<td>" + place.source.getClassName() + "</td>\r\n");
				line += "<td ";			
				if(bRed)
				{
					line += "bgcolor='#ffaaaa'";			
				}
				else if(countAmber > 0)
				{			
					line += "bgcolor='#ffffaa'";
				}
				else
				{
					line += "bgcolor='#ffffff'";
				}
				line += ">" + contribution + "</td>\r\n"; 
				localStrings.add(line);
				localStrings.add(" </tr>\r\n");
			}
			
			totalContribution += contribution;
		}

		// close the table
		localStrings.add("</table>\r\n");
		
		double max = 0;
		
		if(tsize > 0)
		{
			// each chromosome is marked out of 25 (MAX_MARK)
			max = tsize * Chromosome.MAX_MARK;;
			score = totalContribution / max;
		}
		
		double userScore = score * 100;
			
		strings.add(" <div style='padding-bottom: 5px;padding-top: 0px;padding-left: 0px;'>"
				+ "Marks total=" + new DecimalFormat("#.##").format(totalContribution)
				+ " maximum=" + new DecimalFormat("#.##").format(max)
				+ " percentage=" + new DecimalFormat("#.##").format(userScore)
				+ "</div>\r\n");
		strings.addAll(localStrings);

		/* DEBUG
		for(String string : strings)
		{
			LOGGER.info(string);
		} */
				
		return score;
	}
	
	/**
	 * compare this list with another and produce a score as to how well they match
	 *
	 */
	public double compareHTML(Chromosomes teacher, Vector<String> strings)
	{
		Integer verbosity = 2; // show everything
		return compareHTML(teacher, strings, verbosity);
	}
	
	/**
	 * compare this list with another and produce a score as to how well they match
	 *
	 */
	public double compareXML(Chromosomes teacher, Vector<String> strings)
	{
		double score = 0;
		Vector<String> localStrings = new Vector<String>();
		
		Map<Chromosome, Place> resultMap = map(teacher);
		
		// sort into student class.rank order
		Places placeList = new Places();
		placeList.addAll(resultMap.values());

		Collections.sort(placeList);
		
		int ssize = size();
		int tsize = teacher.size();
		
		if(ssize < tsize)
		{
			localStrings.add("<Missing count='" + (tsize - ssize) + "'>");			
			// list missing chromosomes
			Chromosomes missing = placeList.FindMissingTeacherChromosomes(teacher);
			
			for(Chromosome c : missing)
			{
				localStrings.add(" <Unclassified ClassName='");
				
				String s = c.getClassName();
				localStrings.add(s);
				localStrings.add("' Mark='Red' Contribution='0' />");
			}
			localStrings.add("</Missing>\r\n");			
		}
		
		if(ssize > tsize)
		{
			localStrings.add("<Additional count='" + (ssize - tsize) + "'>");			
			localStrings.add("Student clasified " + (ssize - tsize) + " additional chromosomes.");			
			localStrings.add("</Additional>\r\n");			
		}
				
		double totalContribution = 0;
		// walk the map reporting area intersection errors
		// walk the map reporting classification errors
		// walk the map reporting rotation errors
		for(Place place : placeList)
		{
			String line = new String();
			Vector<String> strChromosome = new Vector<String>();
			double contribution = 0;
			
			line = "<Area intersectPercentage='" + place.intersectPercentage + "' ";			
			if(isAreaWithinGreenTolerance(place.intersectPercentage))
			{
				line += "Mark='Green' Contribution='10'";
				contribution += 10; 
			}
			else if(isAreaWithinAmberTolerance(place.intersectPercentage))
			{
				line += "Mark='Amber' Contribution='9'";			
				contribution += 9; 
			}
			else
			{
				line += "Mark='Red' Contribution='5'";			
				contribution += 5; 
			}
			strChromosome.add(line + "/>\r\n");			
	
			line = "<Classification source='" + place.source.classNum + "' target='" + place.target.classNum + "' ";			
			if(place.source.classNum == place.target.classNum)
			{
				line += "Mark='Green' Contribution='5'";
				contribution += 5; 
			}
			else
			{
				line += "Mark='Red' Contribution='0'";			
				contribution += 0; 
			}
			strChromosome.add(line + "/>\r\n");			

			line = "<VerticalCentre source='" + place.source.microY + "' target='" + place.target.microY + "' ";			
			if(isVerticalCentreWithinGreenTolerance(place.source.microY, place.target.microY))
			{
				line += "Mark='Green' Contribution='5'";
				contribution += 5; 
			}
			else if(isVerticalCentreWithinAmberTolerance(place.source.microY, place.target.microY))
			{
				line += "Mark='Amber' Contribution='4'";
				contribution += 4; 
			}
			else
			{
				line += "Mark='Red' Contribution='0'";
				contribution += 0; 
			}
			strChromosome.add(line + "/>\r\n");
			double studentRota = place.source.getDegRota();
			double teacherRota = place.target.getDegRota();

			line = "<Rotation source='" + studentRota + "' target='" + teacherRota + "' ";			
			if(isRotationWithinGreenTolerance(studentRota, teacherRota))
			{
				strChromosome.add(line + "Mark='Green' Contribution='5' inverted='false'");
				contribution += 5; 
			}
			else if(isRotationWithinAmberTolerance(studentRota, teacherRota))
			{
				strChromosome.add(line + "Mark='Amber' Contribution='4' inverted='false'");
				contribution += 4; 
			}
			else if(isRotationWithinGreenInvertedTolerance(studentRota, teacherRota))
			{	
				strChromosome.add(line + "Mark='Amber' Contribution='4' inverted='true'");
				contribution += 4;
			}
			else
			{
				strChromosome.add(line + "Mark='Red' Contribution='0' inverted='false'");
				contribution += 0; 
			}
			strChromosome.add("/>\r\n");
			
			String strMark = new String("Amber");
			
			if(contribution == Chromosome.MAX_MARK)
			{
				strMark = "Green";
			}
			else if(contribution <= (Chromosome.MAX_MARK - 5))			// 5 is red penalty
			{
				strMark = "Red";
			}
			
			localStrings.add("<Chromosome source='" + place.source.getClassName() + "' target='" + place.target.getClassName() + "' Mark='" + strMark + "' Contribution='" + contribution + "'>\r\n");
			localStrings.addAll(strChromosome);
			localStrings.add("</Chromosome>\r\n");

			totalContribution += contribution;
		}

		double max = 0;
		
		if(tsize > 0)
		{
			// each chromosome is marked out of 25 (MAX_MARK)
			max = tsize * Chromosome.MAX_MARK;;
			score = totalContribution / max;
		}
		
		double userScore = score * 100;
			
		strings.add("<Marks total='" + totalContribution + "' maximum='" + max + "' percentage='" + userScore + "'/>\r\n");
		strings.addAll(localStrings);
		
		/* DEBUG
		for(String string : strings)
		{
			LOGGER.info(string);
		} */
				
		return score;
	}
	
	/**
	 *
	 */
	static Vector<String> footerXML()
	{
		Vector<String> strings = new Vector<String>();

		strings.add("<Tolerances>\r\n");
		strings.add("<Area Green='" + AreaTolerance.GREEN.value + "' Amber='" + AreaTolerance.AMBER.value + "' />\r\n");
		strings.add("<Rotation Green='" + RotationTolerance.GREEN.value + "' Amber='" + RotationTolerance.AMBER.value + "' />\r\n");
		strings.add("<Vertical Green='" + VerticalTolerance.GREEN.value + "' Amber='" + VerticalTolerance.AMBER.value + "' />\r\n");
		strings.add("</Tolerances>\r\n");
		
		return strings;
	}
	
	/**
	* compare the teachers list with a students list and 
	* produce a map that relates each chromosome in the
	* students list to one on the teachers list
	* such that there is maximum correlation
	*/
	static Map<Chromosome, Place> map(Chromosomes teacher, Chromosomes student)
	{
		Map<Chromosome, Place> bestMap = new LinkedHashMap<Chromosome, Place>();
		
		// Trying to work out how to relate a chromosome on one
		// list with a chromosome on the other can generate a factorial
		// sized search space.
		// However it is unlikely that a specific chromosome will overlap with
		// very many on the other list so many combinations can be eliminated
		// up front.
		
		// 1) make a list of all the places it is worth
		// testing each of the chromosomes on the student list
		// 2) test every combination remembering the best one
		// 3) return our best mapping

		// 1) make a list of all the places it is worth
		// testing each of the chromosomes on the student list
		ListOfPlaces listOfPlaces = student.findListOfPlaces(teacher);
		
		// 2) test every combination remembering the best one
		
		// 2.1) find the search space size
		int searchSpace = 1;
		for(Places places : listOfPlaces.values())
		{
			searchSpace = searchSpace * places.size();
		}
		
		LOGGER.info("Search space = " + searchSpace);
		
		int bestPattern = 0;
		double bestResult = 0;
		
		String logMessage = "";
		for(int pattern = 0; pattern < searchSpace; pattern++)
		{
			LinkedHashMap<Chromosome, Place> map = new LinkedHashMap<Chromosome, Place>();

			logMessage = "Pattern " + pattern + " =";
			// 2.2) check each chromosome on the student list
			// against the teacher list in all its places
			double result = listOfPlaces.getPattern(pattern, map);
			
			if(result > bestResult)
			{
				bestResult = result;
				bestPattern = pattern;
			}		
		}

		logMessage += "Best Pattern " + bestPattern + " = " + bestResult + " ->";
		LOGGER.info(logMessage);

		// 3) return our best mapping
		// 3.1) check each chromosome on the best student list
		// against the teacher list in all its places
		// this produces a bestMap
		listOfPlaces.getPattern(bestPattern, bestMap);
		
		return bestMap;
	}

	/**
	 * compare this list with another and produce a 
	 * map that relates each chromosome in the teacher(this)
	 * list to one on the student list
	 * such that there is maximum correlation
	 */
	public Map<Chromosome, Place> map(Chromosomes teacher)
	{
		Map<Chromosome, Place> resultMap = new LinkedHashMap<Chromosome, Place>();
		
		resultMap = map(teacher, this);
		
		return resultMap;
	}


}
