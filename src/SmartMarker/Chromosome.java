/**
 * 
 */
package SmartMarker;

import java.awt.Rectangle;

import org.jdom.DataConversionException;
import org.jdom.Element;

/**
 * @author NickP
 * 
 */
public class Chromosome implements Comparable<Chromosome> {
	public boolean flip;
	public double rota;
	public int classNum;
	public int rank;
	public int microX;
	public int microY;
	public DsukArea area;

	// each chromosome is marked out of 25
	public static Integer MAX_MARK = 25;

	public Chromosome() {

	}

	public Chromosome(Chromosome c) {
		classNum = c.classNum;
		rank = c.rank;
		rota = c.rota;
		microX = c.microX;
		microY = c.microY;
		flip = c.flip;
		area = new DsukArea(c.area);
	}

	public Chromosome(Element o) throws DataConversionException {
		classNum = Integer.parseInt(o.getAttributeValue("cls", "0"));
		rank = Integer.parseInt(o.getAttributeValue("rank", "0"));
		rota = o.getAttribute("rota").getFloatValue();
		microX = (int) o.getAttribute("microX").getFloatValue();
		microY = (int) o.getAttribute("microY").getFloatValue();
		flip = (o.getAttribute("flip").getFloatValue() > 0);
		area = new DsukArea(o.getChild("bound"));
	}

	public String getClassName() {
		String className = new String();
		className = classNum + "." + rank;

		return className;
	}

	public String getClassOrdinal() {
		Integer classOrdinal = ((2 * classNum) + rank);
		return classOrdinal.toString();
	}

	/*
	 * Convert rota from radians to degrees and output at 2dp
	 */
	public double getDegRota() {
		double deg = Math.toDegrees(rota);
		double deg100 = Math.round(deg * 100.0);
		return (deg100 / 100);
	}

	@Override
	public String toString() {
		String detail = "cls=" + classNum + " rank=" + rank + " microX=" + microX + " microY="
				+ microY + "flip=" + flip + " rota=" + rota + " area=" + area;

		return detail;
	}

	public boolean intersects(Chromosome target) {
		Rectangle sr = area.getBounds();
		Rectangle tr = target.area.getBounds();

		return sr.intersects(tr);
	}

	public double intersectPercentage(DsukArea target) {
		return area.intersectPercentage(target);
	}

	public double intersectPercentage(Chromosome target) {
		return area.intersectPercentage(target.area);
	}

	@Override
	public int compareTo(Chromosome o) {
		int result = classNum - o.classNum;

		if (0 == result) {
			result = rank - o.rank;
		}

		return result;
	}
}
