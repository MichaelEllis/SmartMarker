/**
 * 
 */
package SmartMarker;

/**
 * @author NickP
 *
 */
public interface IReport {
	// return true for at least one file written
	public boolean Compare(String TeacherAbsPath, String dirStudent);
}
