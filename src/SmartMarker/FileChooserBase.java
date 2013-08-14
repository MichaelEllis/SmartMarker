package SmartMarker;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author NickP
 *
 */public class FileChooserBase extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3775291273807728939L;
	JButton go;
	JTextField txtPath;
	Label aLabel;

	JFileChooser chooser;
	String choosertitle;

	public FileChooserBase() {
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0.0;

		setLayout(gridbag);

		aLabel = new Label("Please pick a folder");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = GridBagConstraints.RELATIVE; //most of row
		gridbag.setConstraints(aLabel, c);
	    add(aLabel);

		go = new JButton("...");
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		gridbag.setConstraints(go, c);
		add(go);

		txtPath = new JTextField(40);
        c.anchor = GridBagConstraints.WEST;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		c.fill = GridBagConstraints.HORIZONTAL;
		gridbag.setConstraints(txtPath, c);
		add(txtPath);
	}

	public void setLabel(String lbl) {
		aLabel.setText(lbl);
		aLabel.invalidate(); // make sure the component is marked as non-valid
		this.validate();
	}

	public void setToolTip(String tt) {
		go.setToolTipText(tt);
		go.invalidate(); // make sure the component is marked as non-valid
		this.validate();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(400, 70);
	}
}
