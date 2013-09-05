package gecko2.gui;

import gecko2.io.GeneClusterToPDFWriter;
import gecko2.io.ImageWriter;
import gecko2.gui.GeneClusterPicture.NameType;
import gecko2.util.FileUtils;
import gecko2.algorithm.GeneCluster;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;


/**
 * This class implements an dialog for setting option to export a GeneCluster
 * (picture in MultipleGenomesBrowser) into a pdf file and several image formats.
 * 
 * @author Hans-Martin Haase <hans-martin.haase at uni-jena dot de>
 * @version 0.15
 */
public class GeneClusterExportDialog extends JDialog {

	/**
	 * Random serial version UID
	 */
	private static final long serialVersionUID = 1468873732499397039L;
	
	/**
	 * Text filed which contains the path to the pdf file we want to store.
	 */
	private final JTextField storingLocation = new JTextField();
	
	/**
	 * Text field which contains the users name.
	 */
	private final JTextField authorName = new JTextField(System.getProperty("user.name"));
	
	/**
	 * Predefined file name for the pdf file.
	 */
	private final String FILENAME = "ClusterExport";

	/**
	 * Here we store whether extra data from the GeneClusterDisplay shall be
	 * added to the PDF.
	 */
	private boolean eData = false;
	
	/**
	 * True if we have to use the optional output format png.
	 */
	private boolean png = false;
	
	/**
	 * True if we have to use the optional output format jpg.
	 */
	private boolean jpg = false;
	
	/**
	 * Default output format
	 */
	private boolean pdf = true;
	
	/**
	 * The object which contains/creates the image content
	 */
	private GeneClusterPicture clusterPics;
	
	/**
	 * The preview area on the gui
	 */
	private final Preview prev;
	
	/**
	 * Constructor sets the elements of the dialog.
	 * 
	 * @param parent the parent frame
	 */
	public GeneClusterExportDialog (final Frame parent, GeneCluster cluster, int[] subselection) {
		
		// Setup the dialog window
		super(parent,"Export gene cluster");
		super.setModal(true);
		this.rootPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(1010, 700));
		this.setIconImage(Gui.createImageIcon("images/gecko2_a_small.png").getImage());

        JLabel ovlapStatus1 = new JLabel();
		ovlapStatus1.setForeground(Color.RED);
        JLabel ovlapStatus2 = new JLabel();
		ovlapStatus2.setForeground(Color.RED);
	
		// two main panels
		JPanel mainPanel1 = new JPanel();
		mainPanel1.setLayout(new GridLayout(23, 1, 5, 5));
		JPanel mainPanel2 = new JPanel();
		mainPanel2.setLayout(new BorderLayout());
		
		// description label for the text field
		JLabel storLocLabel = new JLabel("File name:  ");
		this.storingLocation.setPreferredSize(new Dimension(280, storingLocation.getHeight()));
		this.storingLocation.setText(System.getProperty("user.dir") + File.separatorChar + FILENAME + ".pdf");
		
		
		// file chooser button including action if pressed
		JButton browseButton = new JButton("Browse...");
		browseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				// set up file chooser
				JFileChooser fileLocation = new JFileChooser();
				fileLocation.setName(FILENAME);
				fileLocation.addChoosableFileFilter(new FileUtils.GenericFilter("pdf;png;jpg"));
				fileLocation.setDialogTitle("Select the location to save the file...");
					
				do {
					
					fileLocation.showDialog(parent, "Ok");
					fileLocation.setSelectedFile(new File(checkAndFixFileExtension(fileLocation.getSelectedFile().getAbsolutePath())));
				}
				while (GeneClusterExportDialog.this.checkFileExistence(fileLocation.getSelectedFile()));
				
				// set the path into the text field
				GeneClusterExportDialog.this.storingLocation.setText(fileLocation.getSelectedFile().getAbsolutePath());	
			}
		});
		
		// check box for using genome names instead of the internal mapped number
		JCheckBox gName = new JCheckBox();
		gName.setText("Use genome names instead of numbers.");
		
		gName.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				
				int status = e.getStateChange();
				
				// update preview on select/deselect
				if (status == ItemEvent.DESELECTED)	
            		clusterPics.setGnames(false);
            	else 
            		clusterPics.setGnames(true);
				updateImage();
			}
		});
		
		
		final JComboBox geneNamingComboBox = new JComboBox(NameType.values());
		geneNamingComboBox.setSelectedIndex(0);
		geneNamingComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				clusterPics.setNameType((NameType)geneNamingComboBox.getSelectedItem());
				updateImage();
			}
		});
		
		// check box for the extra data from the gene cluster display
		JCheckBox extraData = new JCheckBox();
		extraData.setText("Add extra information about the cluster. (Just for .pdf export)");
		
		extraData.addItemListener(new ItemListener() {

			// just save the status we use this for the pdf only
			@Override
			public void itemStateChanged(ItemEvent arg0) {				
				int status = arg0.getStateChange();

                GeneClusterExportDialog.this.eData = status != ItemEvent.DESELECTED;
			}
			
		});		
		
		// disable because it is not implemented
		//extraData.setEnabled(false);
		
		// color chooser for preventing random colors in the non clustered parts
		// of the genome
		// TODO implement actionListener
		/*
		 * commented because currently not implemented
		 */
		//JButton colorChooser = new JButton("Custom cluster color...");
		//colorChooser.setEnabled(false);
		
		// Button to abort the export including the actionListener
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				GeneClusterExportDialog.this.setVisible(false);
			}
			
		});
		
		// Button for launching the export
		JButton exportButton = new JButton("Export");
		exportButton.addActionListener(new AbstractAction() {
            /**
             * Action which is launched by pressing the export button.
             * Causes the export of the cluster to a pdf file.
             */
            @Override
            public void actionPerformed(ActionEvent arg0) {
                // if the user just used the text field for editing the file name/path
                // we have to check the file existence independent from the browse button
                // behavior
                String filename = checkAndFixFileExtension(GeneClusterExportDialog.this.storingLocation.getText());
                GeneClusterExportDialog.this.storingLocation.setText(filename);

                if (!checkFileExistence(new File(filename))) {
                    // png export
                    if (GeneClusterExportDialog.this.png) {
                        ImageWriter.createPNGPic(GeneClusterExportDialog.this.clusterPics.createImage(), GeneClusterExportDialog.this.storingLocation.getText());
                        GeneClusterExportDialog.this.setVisible(false);
                    }

                    // jpg export
                    if (GeneClusterExportDialog.this.jpg) {
                        ImageWriter.createJPGPic(GeneClusterExportDialog.this.clusterPics.createImage(), GeneClusterExportDialog.this.storingLocation.getText());
                        GeneClusterExportDialog.this.setVisible(false);
                    }

                    // pdf export
                    if (GeneClusterExportDialog.this.pdf) {
                        GeneClusterToPDFWriter gcw = new GeneClusterToPDFWriter(new File(GeneClusterExportDialog.this.storingLocation.getText()),
                                GeneClusterExportDialog.this.authorName.getText(),
                                GeneClusterExportDialog.this.clusterPics);
                        gcw.setOutputFile(GeneClusterExportDialog.this.storingLocation.getText());
                        gcw.createPDF();
                        GeneClusterExportDialog.this.setVisible(false);
                    }
                }
            }
        });
		
		JLabel author = new JLabel("Author:  ");
		
		// add single components to the two main panels
		mainPanel1.add(storLocLabel);
		JPanel storPanel = new JPanel();
		storPanel.setLayout(new BorderLayout());
		storPanel.add(storingLocation, BorderLayout.WEST);
		storPanel.add(browseButton, BorderLayout.CENTER);
		mainPanel1.add(storPanel);
		//mainPanel1.add(storingLocation);
		mainPanel1.add(gName);
		mainPanel1.add(geneNamingComboBox);
		//mainPanel1.add(extraData);
		mainPanel1.add(ovlapStatus1);
		mainPanel1.add(ovlapStatus2);
		JPanel authorPane = new JPanel();
		authorPane.setLayout(new BorderLayout());
		authorPane.add(author, BorderLayout.WEST);
		authorPane.add(this.authorName, BorderLayout.CENTER);
		mainPanel1.add(authorPane);
		
		
		// output format chooser
		JLabel exportFormats = new JLabel("Export formats: ");
		JRadioButton pngExport = new JRadioButton();
		pngExport.setText(".png");
		
		pngExport.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				
				int status = e.getStateChange();
				
				if (status == ItemEvent.SELECTED) {
					
					pdf = false;
					png = true;
					jpg = false;

                    matchFileEndingToFiletype();
				}
			}
		});
		
		JRadioButton jpgExport = new JRadioButton();
		jpgExport.setText(".jpg");
		
		jpgExport.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				
				int status = e.getStateChange();
				
				if (status == ItemEvent.SELECTED) {
					
					pdf = false;
					png = false;
					jpg = true;

                    matchFileEndingToFiletype();
				}
			}
		});

		JRadioButton pdfExport = new JRadioButton();
		pdfExport.setText(".pdf");
		pdfExport.setSelected(true);
		
		pdfExport.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				
				int status = e.getStateChange();
				
				if (status == ItemEvent.SELECTED) {
					
					pdf = true;
					png = false;
					jpg = false;

                    matchFileEndingToFiletype();
				}
			}
		});
		
		ButtonGroup formatOptions = new ButtonGroup();
		formatOptions.add(pdfExport);
		formatOptions.add(jpgExport);
		formatOptions.add(pngExport);
		
		mainPanel1.add(exportFormats);
		mainPanel1.add(pngExport);
		mainPanel1.add(jpgExport);
		mainPanel1.add(pdfExport);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.add(cancelButton, BorderLayout.WEST);
		buttonPanel.add(exportButton, BorderLayout.EAST);
		mainPanel1.add(buttonPanel);
		
		// add main panels to the window
		this.add(mainPanel1, BorderLayout.WEST); 
		
		// create a scrollPanel with the cluster image
		clusterPics = new GeneClusterPicture(cluster, subselection);
		prev = new Preview(clusterPics.createImage());
		JScrollPane previewScroll = new JScrollPane(prev);
		previewScroll.setEnabled(true);
		previewScroll.setBackground(Color.white);
		previewScroll.setDoubleBuffered(true);
		previewScroll.setBorder(BorderFactory.createEmptyBorder());
		
		//previewScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		mainPanel2.add(previewScroll);
		this.add(mainPanel2, BorderLayout.CENTER);
		this.pack();
		
	}

    private void matchFileEndingToFiletype() {
        int dot = GeneClusterExportDialog.this.storingLocation.getText().lastIndexOf(".");
        String newFilename = GeneClusterExportDialog.this.storingLocation.getText().substring(0, dot);
        if (pdf)
            storingLocation.setText(newFilename + ".pdf");
        if (png)
            storingLocation.setText(newFilename + ".png");
        if (jpg)
            storingLocation.setText(newFilename + ".jpg");
    }
	
	private void updateImage() {
		GeneClusterExportDialog.this.prev.updatePreview(GeneClusterExportDialog.this.clusterPics.createImage());
		
		GeneClusterExportDialog.this.validate();
		GeneClusterExportDialog.this.repaint();
	}
	
	/**
	 * The method test whether a given file object points to a existing file.
	 * 
	 * @param fileToTest file object to test
	 * @return true if the file object points to a existing file otherwise false
	 */
	private boolean checkFileExistence(File fileToTest) {
		
		if (fileToTest == null) {
			
			return false;
		}
		
		if (fileToTest.isFile()) {
			
			JOptionPane.showMessageDialog(null, "The file" + fileToTest.getAbsoluteFile() + " already exists.", "Error", JOptionPane.ERROR_MESSAGE);
			return true;
		}
		
		return false;
	}
	
	/**
	 * The method checks whether the given file (given as string) has the correct file extension.
	 * 
	 * @param absoluteFile String with the absolute file path
	 * @return the fixed file path as String
	 */
	private String checkAndFixFileExtension(String absoluteFile) {
		
		// get the correct file extension
		String suffix = ".pdf";

		if (jpg) {
			
			suffix = ".jpg";
		}
		
		if (png) {
			
			suffix = ".png";
		}
		
		String absoluteFileFixed = absoluteFile;
		
		// fix file extension if the user entered a other extension than the selected one
		if ((absoluteFile.contains(".png") || absoluteFile.contains(".jpg")) && this.pdf) {
			
			absoluteFileFixed = absoluteFile.replaceAll("\\.png|\\.jpg", ".pdf");
		}
		
		if ((absoluteFile.contains(".jpg") || absoluteFile.contains(".pdf")) && this.png) {
			
			absoluteFileFixed = absoluteFile.replaceAll("\\.pdf|\\.jpg", ".png");
		}
		
		if ((absoluteFile.contains(".png") || absoluteFile.contains(".pdf")) && this.jpg) {
			
			absoluteFileFixed = absoluteFile.replaceAll("\\.pdf|\\.png", ".jpg");
		}
		
		if (!absoluteFile.contains(".jpg") && !absoluteFile.contains(".png") && !absoluteFile.contains(".pdf")) {
			
			absoluteFileFixed = absoluteFile + suffix;
		}
		
		return absoluteFileFixed;
	}
}
