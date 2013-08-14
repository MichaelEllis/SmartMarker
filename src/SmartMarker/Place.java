package SmartMarker;

import java.util.ArrayList;


/**
 * @author NickP
 *
 * A place is a mapping between a specific student
 * chromosome (source) and a specific teacher chromosome (target).
 * This mapping is assigned a value based on how much the areas of
 * the two chromosome overlap
 */ 
public class Place
  implements Comparable<Place>
{
	public Place(Place p) {
		source = p.source;
		target = p.target;
		contested = p.contested;
		intersectPercentage = p.intersectPercentage;
	}
	
	public Place() {
		intersectPercentage = 0;
	}

	Chromosome source; //Student
	Chromosome target; //Teacher
	ArrayList<Chromosome> contested;
	double intersectPercentage;

	public boolean isContested()
	{
		return (contested.size() > 1);
	}
	
	@Override
	public int compareTo(Place p)
	{
		int result = source.compareTo(p.source);
		
		if(0 == result)
		{
			result = target.compareTo(p.target);

			if(0 == result)
			{
				double dResult = intersectPercentage - p.intersectPercentage;
				
				if(dResult > 0)
				{
					result = 1;
				}
				else if(dResult < 0)
				{
					result = -1;
				}
			}
		}
		
		return result;
	}
}
