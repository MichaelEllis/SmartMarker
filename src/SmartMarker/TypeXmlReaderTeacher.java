package SmartMarker;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * 
 */

/**
 * @author Nick Penny
 *
 */
public class TypeXmlReaderTeacher extends TypeXmlReaderStudent {
	private final static String CLASS_NAME = TypeXmlReaderTeacher.class.getName();
	private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
	BufferedImage imgMetaphase;  // may be null
	
    /**
     * Populate the attributes from a filename
     * @param fileName The absolute path to the kry file
     * @return boolean true  if all ok
     */
	@Override
	public boolean run(String fileName)
	{
		LOGGER.entering(CLASS_NAME, "run", String.format("fileName=\"%s\"", fileName));

		try {

			Vector<Color> tints = new Vector<Color>();
			tints.add(new Color(255, 0, 0, 80));
			tints.add(new Color(0, 255, 0, 80));
			tints.add(new Color(0, 0, 255, 80));
			tints.add(new Color(255, 255, 0, 80));
			tints.add(new Color(0, 255, 255, 80));
			tints.add(new Color(255, 0, 255, 80));

			chromosomes = new Chromosomes();

			if (fileName == null)
				return false;

			ZipFile zf;

			zf = new ZipFile(fileName.toString());

			Enumeration<? extends ZipEntry> entries = zf.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();

				LOGGER.info(entry.getName() + " " + (entry.isDirectory() ? "directory" : "file"));
			}
			
			/* 
			 * Fetch the Karyotype image if possible 
			 */
			try
			{
				ZipEntry ze = zf.getEntry("Karyotype.jpg");
				
				// if not found, look for older format entry
				if(null == ze)
				{
					ze = zf.getEntry("KVThumb.jpg");
				}
				
				if(null != ze)
				{
					InputStream is = zf.getInputStream(ze);
					imgKaryotype = ImageIO.read(is);
				}
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "", e);
			}
			
			/* 
			 * Fetch the Metaphase image if possible 
			 */
			try
			{
				ZipEntry ze = zf.getEntry("Metaphase.jpg");
				
				// if not found, look for older format entry
				if(null == ze)
				{
					ze = zf.getEntry("MVThumb.jpg");
				}
				
				if(null != ze)
				{
					InputStream is = zf.getInputStream(ze);
					imgMetaphase = ImageIO.read(is);
				}
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "", e);
			}
			
			SAXBuilder builder = new SAXBuilder();
			Document doc = null;

			InputStream xmlDocStream = zf.getInputStream(zf.getEntry("document.xml"));
			if (xmlDocStream != null)
				doc = builder.build(xmlDocStream);

			Element root = null;
			if (doc != null)
				root = doc.getRootElement();

			List<Element> objs = root.getChild("metaFrame").getChildren("framesource");
			class FrameSource {
				int stackheight = 0, roix = 0, roiy = 0, roiwidth = 0, roiheight = 0, posx = 0,
						posy = 0;

				@Override
				public String toString() {
					return "FrameSource[stackheight=" + stackheight + " roix=" + roix + " roiy=" + roiy
							+ " roiwidth=" + roiwidth + " roiheight=" + roiheight + " posx=" + posx
							+ " posy=" + posy + "]";
				}
			}
			ArrayList<FrameSource> frames = new ArrayList<FrameSource>();
			Rectangle union = new Rectangle(0, 0, 0, 0); // Union of all frame
			// bounding
			// rectangles
			for (Element o : objs) {
				FrameSource fs = new FrameSource();
				fs.roix = o.getAttribute("roix") != null ? o.getAttribute("roix").getIntValue() : 0;
				fs.roiy = o.getAttribute("roiy") != null ? o.getAttribute("roiy").getIntValue() : 0;
				fs.roiwidth = o.getAttribute("roiwidth") != null ? o.getAttribute("roiwidth")
						.getIntValue() : 0;
				fs.roiheight = o.getAttribute("roiheight") != null ? o.getAttribute("roiheight")
						.getIntValue() : 0;
				fs.posx = o.getAttribute("posx") != null ? o.getAttribute("posx").getIntValue() : 0;
				fs.posy = o.getAttribute("posy") != null ? o.getAttribute("posy").getIntValue() : 0;

				final int stackheight = o.getAttribute("stackheight").getIntValue();
				frames.ensureCapacity(stackheight + 1);
				frames.add(stackheight, fs);
				LOGGER.info(fs.toString());
			}

			LOGGER.info(union.toString());

			objs = root.getChild("metaFrame").getChildren("object");
			for (Element o : objs) {
				if (o.getAttribute("type").getIntValue() == 0) {
					// type == 0 => boundary is chromosome included in karyotype
					// however a chromosome of class 0 is unclasified
					// and so we should only extract chromosomes with class > 0 
					if (o.getAttribute("cls").getIntValue() > 0) {
						Chromosome chromosome = new Chromosome(o);
						chromosomes.add(chromosome);
					}
				}
			}
			AbsPath = zf.getName();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "", e);
			return false;
		}

		return true;
	}

}

