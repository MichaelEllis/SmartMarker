package SmartMarker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JFileChooser;

/**
 * @author NickP
 * 
 */
public class DsukFolderChooser extends FileChooserBase implements ActionListener {
	private final static String CLASS_NAME = DsukFolderChooser.class.getName();
	private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);

	/**
	 * 
	 */
	private static final long serialVersionUID = 4249471669771784364L;

	public DsukFolderChooser() {
		go.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle(choosertitle);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		//
		// disable the "All files" option.
		//
		chooser.setAcceptAllFileFilterUsed(false);
		//
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			String folder = chooser.getSelectedFile().getAbsolutePath();
			LOGGER.info("getSelectedFile(): " + folder);
			txtPath.setText(folder);
		} else {
			LOGGER.info("No Selection ");
		}
	}

}
