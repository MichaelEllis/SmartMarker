package SmartMarker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

/**
 * @author NickP
 *
 */
public class DsukFileChooser extends FileChooserBase
implements ActionListener {
	
	private final static String CLASS_NAME = DsukFileChooser.class.getName();
	private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
	/**
	 * 
	 */
	private static final long serialVersionUID = 5097237073608163047L;

	public class KryFilter extends FileFilter
	{
	    //Accept all directories and all kry files.
	    @Override
		public boolean accept(File f)
	    {
	    	boolean result = false;
	    	
	        if (f.isDirectory())
	        {
	        	result = true;
	        }
	        else
	        {
	        	String s = f.getName().toLowerCase();
	        	result = (s.endsWith(".kry"));
	        }
	        
	        return result;
	    }

	    //The description of this filter
	    @Override
		public String getDescription() {
	        return "*.kry";
	    }
	}
	
	public DsukFileChooser() {
	 go.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		 chooser = new JFileChooser(); 
		 chooser.setCurrentDirectory(new java.io.File("."));
		 chooser.setDialogTitle(choosertitle);
		 
		 // allow the "All files" option.
		 chooser.setAcceptAllFileFilterUsed(true);
		 // select the "*.kry" option.
		 chooser.setFileFilter(new KryFilter());

		 //    
		 if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
			   String file = chooser.getSelectedFile().getAbsolutePath();
			   LOGGER.info("getSelectedFile(): " 
			      +  file);
			   txtPath.setText(file);
		   }
		 else {
		   LOGGER.info("No Selection ");
		 }
	}
}
