package SmartMarker;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * @author NickP
 *
 */
public class SmartMarkerDlg extends JDialog implements ActionListener {

	private final static String CLASS_NAME = SmartMarkerDlg.class.getName();
	private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);
	/**
	 * 
	 */
	private static final long serialVersionUID = -5389965765901631255L;
	private DsukFileChooser panelFile;
	private DsukFolderChooser panelFolder;
	private JComboBox cmbFullIndHtml;
	JRadioButton rdbtnIndividual;
	JRadioButton rdbtnAggregate;
	JRadioButton rdbtnHtml;
	JRadioButton rdbtnXml;
	JCheckBox ckboxImages;

	JButton go;
	String teacherAbsPath;
	String dirStudent;
	
	public enum DlgResult
	{
	    ABORT, 
	    PRODUCE_REPORT, 
	    CANCLE 
	}
	
	public enum ReportType
	{
	    SUMMARY(1),
	    FULL_HTML_INDIVIDUAL(2),
	    FULL_HTML_CLASS(4),
	    FULL_XML_INDIVIDUAL(8),
	    FULL_XML_CLASS(16);
 
        public final Integer value;
 
        private ReportType(int value)
        {
           this.value = value;
        }
	}
	
	Integer reportType;
	Integer verbosity;
	boolean includeImages;

	DlgResult iModalResult;

	private void construct() {
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());
		LOGGER.info(northPanel.getLayout().getClass().toString());
		panelFile = new DsukFileChooser();
		panelFile.setLabel("Reference file:");
		panelFile.choosertitle = "Reference file (.kry)";
		panelFile.setToolTip("Select reference karyotype file");
		northPanel.add(panelFile, "North");

		panelFolder = new DsukFolderChooser();
		panelFolder.setLabel("Answers directory:");
		panelFolder.choosertitle = "Answers folder (.kry)";
		panelFolder.setToolTip("Select directory containing karyotype answers files");
		northPanel.add(panelFolder, "South");
		getContentPane().add(northPanel, "North");
	
		// center pannel asks which reports are needed
		JPanel centrePanel = new JPanel();
		
		JLabel V1Label;
		V1Label = new JLabel("Report Style:");
		rdbtnIndividual = new JRadioButton("Individual");
		rdbtnIndividual.setSelected(true);
		rdbtnIndividual.setToolTipText("Create a separate report for each student karyotype");
		
		rdbtnAggregate = new JRadioButton("Aggregate");
		rdbtnAggregate.setToolTipText("Aggregate all reports for each student karyotype into a single file");
		
		//Group the radio buttons.
	    ButtonGroup groupStyle = new ButtonGroup();
	    groupStyle.add(rdbtnIndividual);
	    groupStyle.add(rdbtnAggregate);
		
		JLabel V2Label;
		V2Label = new JLabel("Report Format:");
		rdbtnHtml = new JRadioButton("HTML");
		rdbtnHtml.setSelected(true);
		rdbtnHtml.setToolTipText("Format report as HTML");
		
		rdbtnXml = new JRadioButton("XML");
		rdbtnXml.setToolTipText("Format report as XML");

		//Group the radio buttons.
	    ButtonGroup groupFormat = new ButtonGroup();
	    groupFormat.add(rdbtnHtml);
	    groupFormat.add(rdbtnXml);
	    
	    JLabel V3Label;
	    V3Label = new JLabel("Include Images:");
	    ckboxImages = new JCheckBox();
		ckboxImages.setSelected(true);
		ckboxImages.setToolTipText("Include images in report");
	    
		JLabel V4Label;
		V4Label = new JLabel("Report Contents:");
		String[] reportStrings = { "Errors only", "Warnings and errors", "All" };
		cmbFullIndHtml = new JComboBox(reportStrings);
		cmbFullIndHtml.setSelectedIndex(1);
		
		getContentPane().add(centrePanel, "Center");
		
		go = new JButton("Report");
		go.setToolTipText("Generate reports");
		
		GroupLayout layout = new GroupLayout(centrePanel);
		centrePanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(V1Label)
								.addComponent(V2Label)
								.addComponent(V3Label)
								.addComponent(V4Label))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addComponent(rdbtnIndividual)
												.addComponent(rdbtnHtml)
												.addComponent(ckboxImages)
												.addComponent(go))
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addComponent(rdbtnAggregate)
												.addComponent(rdbtnXml)))
						.addComponent(cmbFullIndHtml)));

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(V1Label)
								.addComponent(rdbtnIndividual)
								.addComponent(rdbtnAggregate))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(V2Label)
								.addComponent(rdbtnHtml)
								.addComponent(rdbtnXml))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(V3Label)
								.addComponent(ckboxImages))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(V4Label)
								.addComponent(cmbFullIndHtml))
				.addComponent(go));

		go.addActionListener(this);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		teacherAbsPath ="";
		dirStudent = "";
		iModalResult = DlgResult.ABORT;
		verbosity = 2; // show everything
		reportType = 0; // produce no reports

		pack();                                    // Size window for components
	}

	public SmartMarkerDlg() {
		construct();
	}

	public SmartMarkerDlg(JFrame parent, String strTitle) {
		super(parent, strTitle, true);
		construct();
	}

	public DlgResult showModal() {
		iModalResult = DlgResult.ABORT;
		setVisible(true);
		return iModalResult;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		LOGGER.info("ProduceReport()");
		iModalResult = DlgResult.ABORT;
		
		// find the abs path to the teacher file
		String TeacherFn = panelFile.txtPath.getText();
		File fileTeacher = new File(TeacherFn);
		teacherAbsPath = fileTeacher.getAbsolutePath();

		// find the abs path to the student dir
		String strFolder = panelFolder.txtPath.getText();
		File directory;
		
		// if strFolder is empty use the folder containing the TeacherFn
		if(strFolder.isEmpty())
		{
			// This may be possible when jre7 is more available
			//strFolder = pathTeacher.getParent().toString();
			directory = fileTeacher.getParentFile(); // to get the parent dir 
		}
		else
		{
			directory = new File(strFolder);
		}

		if(null == directory)
		{
			LOGGER.severe("No student folder suggested.");
		}
		else
		{
			dirStudent = directory.getAbsolutePath();
			
			reportType = ReportType.SUMMARY.value;
			verbosity = cmbFullIndHtml.getSelectedIndex();
			includeImages = ckboxImages.isSelected();
			
			if(rdbtnIndividual.isSelected())
			{
				if(rdbtnHtml.isSelected())
				{
					reportType += ReportType.FULL_HTML_INDIVIDUAL.value;
				}
				else
				{
					reportType += ReportType.FULL_XML_INDIVIDUAL.value;
				}
			}
			else
			{
				if(rdbtnHtml.isSelected())
				{
					reportType += ReportType.FULL_HTML_CLASS.value;
				}
				else
				{
					reportType += ReportType.FULL_XML_CLASS.value;
				}
			}
			
			iModalResult = DlgResult.PRODUCE_REPORT;
		}
		
		setVisible(false);
	}
}
