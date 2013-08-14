/**
 * 
 */
package SmartMarker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author NickP
 * 
 */
public class ListOfPlaces extends LinkedHashMap<Chromosome, Places> {
	private final static String CLASS_NAME = ListOfPlaces.class.getName();
	private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
	/**
	 * 
	 */
	private static final long serialVersionUID = -852607653668206649L;

	/**
	 * make a list of all the places it is worth testing each of the chromosomes on the students
	 * list
	 */
	public void recordContestants(Places places) {
		int placesSize = places.size();
		if (placesSize > 0) {
			// mark each entry's contestants
			ArrayList<Chromosome> contested = new ArrayList<Chromosome>();

			for (Place place : places) {
				contested.add(place.source);
			}

			for (Place place : places) {
				place.contested = contested;
			}

			for (Place place : places) {
				// add the chromosome (if needed) and its place

				if (!containsKey(place.source)) {
					Places newList = new Places();
					put(place.source, newList);
				}

				get(place.source).add(place);
			}
		}
	}

	// Tidy up - throw away places that are worthless
	// if the highest value place on a student chromosome is uncontested
	// there is no point keeping the contested ones
	public void tidyUp() {
		boolean doAgain = false;

		do {
			doAgain = false;

			for (Places places : values()) {
				double highestUncontestedResult = places.getHighestUncontestedIntersectPercentage();

				Places tidy = new Places();
				Places rejectedContests = places
						.getContestedPlacesWithIntersectPercentageBelow(highestUncontestedResult);

				// go through this chromosomes contest list and withdraw
				// from each contest in the rejectedContests list
				for (Place place : rejectedContests) {
					for (Chromosome other : place.contested) {
						if (place.source != other) {
							if (containsKey(other)) {
								Places otherPlaces = get(other);

								for (Place otherPlace : otherPlaces) {
									Integer i = 0;
									boolean stop = false;

									while ((i < otherPlace.contested.size()) && !(stop)) {
										if (otherPlace.contested.get(i) == place.source) {
											otherPlace.contested.remove(i);
											doAgain = true;
											stop = true;
										}

										i++;
									}
								}
							}
						}
					}
				}

				Places contested = places
						.getContestedPlacesWithIntersectPercentageAbove(highestUncontestedResult);
				Places uncontested = places
						.getUncontestedPlacesWithIntersectPercentageOf(highestUncontestedResult);

				tidy.addAll(contested);
				tidy.addAll(uncontested);

				places.clear();
				places.addAll(tidy);
			}
		} while (doAgain);

	}

	// DEBUG
	public void dump() {
		for (Chromosome c : keySet()) {
			LOGGER.info("Chromosome s" + c.getClassName());

			Places places = get(c);
			String logMessage = "";
			for (Place result : places) {
				logMessage = "Place found (s" + result.source.getClassName() + " -> t"
						+ result.target.getClassName() + ") = " + result.intersectPercentage;

				if (result.isContested()) {
					String strWith = new String();
					Boolean first = true;

					for (Chromosome contestant : result.contested) {
						if (contestant != result.source) {
							if (first) {
								strWith = "s" + contestant.getClassName();
								first = false;
							} else {
								strWith += ", s" + contestant.getClassName();
							}
						}
					}

					// verbose debug
					// LOGGER.info("Place found (s" + result.source.getClassName() + " -> t" +
					// result.target.getClassName() + ") = " + result.intersectPercentage +
					// " contested with " + strWith);
					logMessage += " contested with " + strWith;
				} else {
					// verbose debug
					// LOGGER.info("Place found (s" + result.source.getClassName() + " -> t" +
					// result.target.getClassName() + ") = " + result.intersectPercentage);
				}
			}
			LOGGER.info(logMessage);
		}

	}

	private double getPattern(int pattern, Iterator<Chromosome> it, Map<Chromosome, Place> map) {
		double result = 0;
		
		String logMessage = "";

		if (it.hasNext()) {
			Chromosome c = it.next();

			Places places = get(c);
			int solution = pattern % places.size();
			Place place = places.get(solution);
			result = place.intersectPercentage;

			logMessage = " " + place.source.getClassOrdinal() + "-"
					+ place.target.getClassOrdinal();

			if (map.containsKey(place.target)) {
				logMessage += "CLASH";
				map.clear();
				result = 0;
			} else {
				map.put(place.target, place);

				int subPattern = pattern / places.size();
				result += getPattern(subPattern, it, map);

				if (map.isEmpty()) {
					result = 0;
				}
			}
		}
		
		if (!logMessage.isEmpty())
			LOGGER.fine(logMessage);

		return result;
	}

	public double getPattern(int pattern, Map<Chromosome, Place> map) {
		Iterator<Chromosome> it = keySet().iterator();
		double result = getPattern(pattern, it, map);
		// LOGGER.info("");

		if (map.isEmpty()) {
			result = 0;
		}

		return result;
	}

}
