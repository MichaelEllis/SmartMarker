package SmartMarker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;

/**
 * @author NickP
 * 
 */
class Places extends ArrayList<Place> {
	private final static String CLASS_NAME = Places.class.getName();
	private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
	/**
	 * 
	 */
	private static final long serialVersionUID = 5327371302746074380L;

	public double getHighestUncontestedIntersectPercentage() {
		double result = 0;

		for (Place p : this) {
			if (!p.isContested()) {
				if (p.intersectPercentage > result) {
					result = p.intersectPercentage;
				}
			}
		}

		return result;
	}

	public Places getContestedPlacesWithIntersectPercentageAbove(double result) {
		Places places = new Places();

		for (Place p : this) {
			if (p.isContested()) {
				if (p.intersectPercentage > result) {
					places.add(p);
				}
			}
		}

		return places;
	}

	public Places getContestedPlacesWithIntersectPercentageBelow(double result) {
		Places places = new Places();

		for (Place p : this) {
			if (p.isContested()) {
				if (p.intersectPercentage < result) {
					places.add(p);
				}
			}
		}

		return places;
	}

	public Places getUncontestedPlacesWithIntersectPercentageOf(double result) {
		Places places = new Places();

		for (Place p : this) {
			if (!p.isContested()) {
				if (p.intersectPercentage >= result) {
					places.add(p);
				}
			}
		}

		return places;
	}

	public Chromosomes FindMissingTeacherChromosomes(Chromosomes teacher) {
		Chromosomes missing = new Chromosomes();
		HashSet<Chromosome> UniqueStore = new HashSet<Chromosome>();

		// 1. place all my teacher chromosomes into the UniqueStore
		// 2. try to place each supplied teacher chromosome into the UniqueStore
		// 2.1 if it is accepted place it on 'missing' as well

		// 1. place all my teacher chromosomes into the UniqueStore
		for (Place p : this) {
			UniqueStore.add(p.target);
		}

		// 2. try to place each supplied teacher chromosome into the UniqueStore
		for (Chromosome c : teacher) {
			// 2.1 if it is accepted place it on 'missing' as well
			if (UniqueStore.add(c)) {
				missing.add(c);
			}
		}

		return missing;
	}

	// if there is an overlap store this as a possible location
	public boolean appendIntersectionOf(Chromosome source, Chromosome target) {
		boolean appended = false;

		if (source.intersects(target)) {
			double intersectPercentage = source.intersectPercentage(target);

			if (intersectPercentage > 0) {
				// take a copy to prevent lines such as 'c.ordinalStudent++;' from affecting
				// the contents of the places list
				Place result = new Place();

				result.source = source;
				result.target = target;
				result.intersectPercentage = intersectPercentage;

				// debug
				// double i2 = source.intersectPercentage(target);

				// put the copy on the list
				add(result);
				appended = true;
				LOGGER.info("Place found (" + result.source.getClassName() + " -> "
						+ result.target.getClassName() + ") = " + result.intersectPercentage);
			}
		}

		return appended;
	}
}
