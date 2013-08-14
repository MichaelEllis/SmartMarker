/**
 * 
 */
package SmartMarker;

import imagescope.graphics.PolygonChain;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.util.List;

import org.jdom.DataConversionException;
import org.jdom.Element;

/**
 * @author NickP
 * 
 */
public class DsukArea extends Area {

	/**
	 * 
	 */
	public DsukArea() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public DsukArea(Shape arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param bound
	 * @throws DataConversionException
	 */
	public DsukArea(Element bound) throws DataConversionException {
		List children = bound.getChildren("poly");
		List<Element> objs = children;
		for (Element poly : objs) {
			int x = (int) poly.getAttribute("x").getFloatValue();
			int y = (int) poly.getAttribute("y").getFloatValue();

			String chain = poly.getValue();
			Polygon p = PolygonChain.getPolygon(x, y, chain);
			Area pArea = new Area(p);
			add(pArea);
		}
	}

	/**
	 * return the number of pixels contained in this area
	 */
	public static int getPixelCount(Area a) {
		Rectangle r = a.getBounds();

		int sum = 0;
		int minX = r.x;
		int maxX = minX + r.width;
		int minY = r.y;
		int maxY = minY + r.height;

		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {
				if (a.contains(x, y)) {
					sum++;
				}
			}
		}

		return sum;
	}

	/**
	 * return the number of pixels contained in this area
	 */
	public int getPixelCount() {
		return getPixelCount(this);
	}

	/**
	 * return the percentage of pixels in this intersect compared to the initial count.
	 */
	public double intersectPercentage(DsukArea target) {
		double percentage = 0;

		// the intersect instruction will change the area
		// so make a copy to work with first
		Area source = (Area) this.clone();

		// find the intersecting pixels
		source.intersect(target);

		// is there any intersect?
		if (!source.isEmpty()) {
			int targetCount = getPixelCount(target);

			if (targetCount > 0) {
				int intersectCount = getPixelCount(source);
				double dinominator = 0;
				double numorator = 0;

				// two solutions:
				// 1) this may cover all of target and more
				// 2) target may be bigger then this

				if (intersectCount < targetCount) {
					// target is bigger
					numorator = intersectCount;
					dinominator = targetCount;
				} else {
					// this covers all of target and more
					numorator = targetCount;
					dinominator = getPixelCount(this);
				}

				if (dinominator > 0) {
					percentage = numorator / dinominator;
				}
			}
		}

		return percentage;
	}

}
