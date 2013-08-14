/**
 * 
 */
package SmartMarker;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author NickP
 *
 */
public class Report extends SummaryReport implements IReport {

	public Integer verbosity;
	public boolean includeImages;

	/**
	 * 
	 */
	public Report() {
		verbosity = 2; // show everything
		includeImages = false;
	}
	
	public String getDate()
	{
		Date dNow = new Date( );
	    SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd h:mm:ss");

		return ft.format(dNow);
	}
	
	@Override protected String getFilenameNoExt() {
		return "report";
	}
}
