/*
 * Copyright 2014 Sascha Winter, Tobias Mann, Hans-Martin Haase, Leon Kuchenbecker and Katharina Jahn
 *
 * This file is part of Gecko3.
 *
 * Gecko3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Gecko3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Gecko3.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.unijena.bioinf.gecko3.gui;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.unijena.bioinf.gecko3.GeckoInstance;
import de.unijena.bioinf.gecko3.datastructures.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.prefs.Preferences;


public class StartComputationDialog extends JDialog {
    private static final Logger logger = LoggerFactory.getLogger(StartComputationDialog.class);
	private static final long serialVersionUID = -5635614016950101153L;

    private int quorum;
    private final JComboBox<String> qCombo;
	private Parameter.OperationMode opMode;
    private Parameter.ReferenceType refType;
	private final JComboBox<Parameter.ReferenceType> refCombo;
    private final JPanel additionalRefClusterSettings;
    final JTabbedPane tabbedDistancePane;

	private final JCheckBox mergeResults;
    private final JCheckBox refInRef;

    private final DeltaTable deltaTable;
    private final JSpinner distanceSpinner;
    private final JSpinner sizeSpinner;

	public StartComputationDialog(JFrame parent) {
        final GeckoInstance gecko = GeckoInstance.getInstance();
		this.setModalityType(DEFAULT_MODALITY_TYPE);
		this.setResizable(false);
        setIconImages(parent.getIconImages());
		this.setTitle("Configure computation");

        this.opMode = Parameter.OperationMode.reference;
        this.refType = Parameter.ReferenceType.allAgainstAll;

        this.sizeSpinner = new JSpinner(new SpinnerNumberModel(7, 0, Integer.MAX_VALUE, 1));
        this.distanceSpinner = new JSpinner(new SpinnerNumberModel(3, 0, Integer.MAX_VALUE, 1));
        this.deltaTable = new DeltaTable();

        /*
         * Tabbed distance and size pane
         */
        tabbedDistancePane = new JTabbedPane();
        tabbedDistancePane.addTab("Single Distance", getDistancePanel());
        tabbedDistancePane.add("Distance Table", deltaTable);

        /*
         * All other options
         */
		final String[] qValues = new String[Math.max(gecko.getGenomes().length, 1)];
		qValues[0] = "all";
		for (int i=1;i<qValues.length;i++)
			qValues[i] = Integer.toString(i+1);
		qCombo = new JComboBox<>(qValues);
		qCombo.setSelectedIndex(0);

        refCombo = new JComboBox<>(Parameter.ReferenceType.getSupported());

        mergeResults = new JCheckBox("Merge Results");
        mergeResults.setSelected(false);

        refInRef = new JCheckBox("Search Ref. in Ref.");
        refInRef.setSelected(false);

        /*
         * Ref cluster options
         */
        final JTextField refClusterField = new JTextField() {
            private static final long serialVersionUID = 8768959243069148651L;

            @Override
            public Point getToolTipLocation(MouseEvent event) {
                Point p = this.getLocation();
                p.y = p.y + getHeight();
                return p;
            }
        };

        EventList<Genome> genomeEventList = new BasicEventList<>();
        genomeEventList.addAll(Arrays.asList(GeckoInstance.getInstance().getGenomes()));
        final JComboBox refGenomeCombo = new JComboBox();
        AutoCompleteSupport support = AutoCompleteSupport.install(refGenomeCombo, genomeEventList);
        support.setStrict(true);
        refGenomeCombo.setSelectedIndex(0);

        additionalRefClusterSettings = new JPanel(new CardLayout());
        additionalRefClusterSettings.add(new JPanel(), Parameter.ReferenceType.allAgainstAll.toString());
        additionalRefClusterSettings.add(refGenomeCombo, Parameter.ReferenceType.genome.toString());
        additionalRefClusterSettings.add(refClusterField, Parameter.ReferenceType.cluster.toString());

        /*
         * layout of other options
         */

        // Actions
        qCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (qCombo.getSelectedIndex()==0)
                    quorum = 0;
                else
                    quorum = qCombo.getSelectedIndex()+1;
            }
        });

		refCombo.setEnabled(true);
		mergeResults.setEnabled(true);

		refClusterField.setToolTipText("<html>Enter a sequence of gene IDs here, separated by spaces.<br>" +
				"<B>Hint</B>: Instead of entering a sequence of genes by hand you can also select a gene cluster from the result list and copy it!</html>");
		
		Document refClusterFieldDocument = new PlainDocument(){
			
			/**
			 * Random generated serialization UID
			 */
			private static final long serialVersionUID = -1398119324023579537L;

			@Override
			public void insertString(int offs, String str, AttributeSet a)
					throws BadLocationException {;
				super.insertString(offs, str, a);
			}
			
		};
		
		refClusterField.setDocument(refClusterFieldDocument);

		refCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refType = (Parameter.ReferenceType) refCombo.getSelectedItem();
                CardLayout layout = (CardLayout)(additionalRefClusterSettings.getLayout());
                layout.show(additionalRefClusterSettings, refType.toString());
                if (refType.equals(Parameter.ReferenceType.cluster)){
                    ToolTipManager.sharedInstance().mouseMoved(
                            new MouseEvent(refClusterField, 0, 0, 0,
                                    (int) refClusterField.getLocation().getX(), // X-Y of the mouse for the tool tip
                                    (int) refClusterField.getLocation().getY(),
                                    0, false));
                }
            }
        });

		Action okAction = new AbstractAction("OK") {
			private static final long serialVersionUID = 6197096728152587585L;
			public void actionPerformed(ActionEvent e) {
                Parameter parameter;
                if (tabbedDistancePane.getSelectedIndex() == 0) {
                    parameter = new Parameter(
                            (Integer) distanceSpinner.getValue(),
                            (Integer) sizeSpinner.getValue(),
                            quorum,
                            opMode,
                            refType,
                            refInRef.isSelected(),
                            false);
                } else {
                    if (!deltaTable.isValidDeltaTable()) {
                        JOptionPane.showMessageDialog(StartComputationDialog.this, "Invalid Distance Table!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    } else {
                        parameter = new Parameter(
                                deltaTable.getDeltaTable(),
                                deltaTable.getClusterSize(),
                                quorum,
                                opMode,
                                refType,
                                refInRef.isSelected(),
                                false);
                    }
                }

				// Reorder the genomes if necessary
				if (opMode==Parameter.OperationMode.reference && refType==Parameter.ReferenceType.genome && refGenomeCombo.getSelectedIndex()!=0) {
                    gecko.reorderGenomes(refGenomeCombo.getSelectedIndex());
				} else if (opMode==Parameter.OperationMode.reference && refType==Parameter.ReferenceType.cluster) {
					Genome cluster = new Genome("Reference cluster");
					ArrayList<Gene> genes = new ArrayList<>();
					for (String id : refClusterField.getText().split(" "))
						if (id!=null && (!(id.equals("")))) {
							GeneFamily geneFamily = gecko.getGeneFamily(id);
							if (geneFamily!=null)
								genes.add(new Gene(geneFamily));
                            else {
                                JOptionPane.showMessageDialog(StartComputationDialog.this, "Invalid Gene Id: " + id, "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
						}
					cluster.addChromosome(new Chromosome("", genes, cluster));
					gecko.addReferenceGenome(cluster);
				}
                saveToPreferences(gecko.getPreferences());
                StartComputationDialog.this.setVisible(false);
				boolean mergeResultsEnabled = false;
				if (opMode==Parameter.OperationMode.reference && mergeResults.isSelected())
					mergeResultsEnabled = true;

				gecko.performClusterDetection(
                        parameter,
                        mergeResultsEnabled,
                        1.1);
			}
			
		};
		JButton okButton = new JButton(okAction);

		Action cancelAction = new AbstractAction() {
			private static final long serialVersionUID = 2057638030083370800L;
			public void actionPerformed(ActionEvent e) {
				StartComputationDialog.this.setVisible(false);
			}
			
		};
		cancelAction.putValue(Action.NAME, "Cancel");
		JButton cancelButton = new JButton(cancelAction);
        JPanel buttonPanel = new ButtonBarBuilder().addButton(okButton, cancelButton).build();

        FormLayout layout = new FormLayout("default", "default:grow, default, default");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.append(tabbedDistancePane);
        builder.nextLine();
        builder.append(getBody());
        builder.nextLine();
        builder.append(buttonPanel);

        JPanel panel = builder.build();
        panel.getActionMap().put("okAction", okAction);
        panel.getActionMap().put("cancelAction", cancelAction);
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "okAction");
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancelAction");

        this.setContentPane(panel);
		this.pack();
        this.loadFromPreferences(gecko.getPreferences());
	}

    private void setParameters(Parameter parameters){
        // Distance and size
        if (parameters.useDeltaTable()){
            tabbedDistancePane.setSelectedIndex(1);
            deltaTable.setDeltaValues(parameters.getDeltaTable(), parameters.getMinClusterSize());
        } else {
            tabbedDistancePane.setSelectedIndex(0);
            distanceSpinner.setValue(parameters.getDelta());

            sizeSpinner.setValue(parameters.getMinClusterSize());
        }

        // Mode, all-against-all, genome or cluster
        if (parameters.getRefType().equals(Parameter.ReferenceType.cluster) || parameters.getRefType().equals(Parameter.ReferenceType.genome))
            refCombo.setSelectedItem(Parameter.ReferenceType.genome);
        else
            refCombo.setSelectedItem(parameters.getRefType());

        // Quorum
        if (parameters.getQ() == 0)
            qCombo.setSelectedIndex(0);
        else
            qCombo.setSelectedIndex(parameters.getQ()-1);

        // Ref in Ref
        refInRef.setSelected(parameters.searchRefInRef());

    }

    private JComponent getBody(){
        FormLayout layout = new FormLayout(
                "pref, 4dlu, pref",
                "p, 2dlu, p, 2dlu, p, 2dlu, p"
        );
        PanelBuilder builder  = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        builder.addLabel("Minimum # of genomes:", cc.xy(1, 1));
        builder.add(qCombo,                         cc.xy(3, 1));

        builder.addLabel("Reference type:",         cc.xy(1, 3));
        builder.add(refCombo,                       cc.xy(3, 3));

        builder.add(additionalRefClusterSettings,   cc.xyw(1, 5, 3));

        //builder.add(mergeResults, cc.xy(1, 7));
        builder.add(refInRef,                       cc.xy(1, 7));

        return builder.getPanel();
    }

    private JPanel getDistancePanel() {
        FormLayout layout = new FormLayout(
                "pref, 4dlu, pref",
                ""
        );
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);

        builder.append("Maximum distance:", distanceSpinner);
        builder.append("Minimum cluster size:", sizeSpinner);

        return builder.getPanel();
    }


    private void saveToPreferences(Preferences preferences) {
        if (preferences != null) {
            preferences.putInt("delta", (int)distanceSpinner.getValue());
            preferences.putInt("deltaSize", (int)sizeSpinner.getValue());
            preferences.put("deltaTable", Arrays.deepToString(deltaTable.getDeltaTable()));
            preferences.putInt("deltaTableSize", deltaTable.getClusterSize());
            preferences.putInt("distancePaneSelected", tabbedDistancePane.getSelectedIndex());
            preferences.putInt("quorum", quorum);
            preferences.put("refType", Character.toString(refType.getCharMode()));
            preferences.put("operationMode", Character.toString(opMode.getCharMode()));
            preferences.putBoolean("refInRef", refInRef.isSelected());
        }
    }

    private void loadFromPreferences(Preferences preferences) {
        if (preferences != null) {
            // Load from preferences
            int delta = preferences.getInt("delta", 3);
            int deltaSize = preferences.getInt("deltaSize", 7);

            String deltaTableString = preferences.get("deltaTable", "null");
            int[][] deltaTable = null;
            if (!deltaTableString.equals("null")){
                try {
                    String[] split = deltaTableString.replaceFirst("^\\[*", "").replaceAll("\\]*$", "").split("\\],\\s*\\[");
                    deltaTable = new int[split.length][];
                    for (int i = 0; i < split.length; i++) {
                        String[] values = split[i].split(",");
                        if (values.length != Parameter.DELTA_TABLE_SIZE) {
                            deltaTable = null;
                            logger.warn("Invalid delta table size at {}", split[i]);
                            break;
                        }
                        deltaTable[i] = new int[values.length];
                        for (int j = 0; j < values.length; j++)
                            deltaTable[i][j] = Integer.parseInt(values[j].trim());
                    }
                }catch (NumberFormatException e){
                    deltaTable = null;
                    logger.warn("Invalid deltaTable format, could not read the data", e);
                }
            }
            int deltaTableSize = preferences.getInt("deltaTableSize", Parameter.DeltaTable.getDefault().getMinimumSize());
            if (deltaTable == null) {
                deltaTable = Parameter.DeltaTable.getDefault().getDeltaTable();
                deltaTableSize = Parameter.DeltaTable.getDefault().getMinimumSize();
            }
            int distancePaneSelected = preferences.getInt("distancePaneSelected", 0);
            int q = preferences.getInt("quorum", 0);
            Parameter.ReferenceType referenceType = Parameter.ReferenceType.getReferenceTypeFromChar(preferences.get("refType", "a").charAt(0));
            Parameter.OperationMode operationMode = Parameter.OperationMode.getOperationModeFromChar(preferences.get("operationMode", "r").charAt(0));
            boolean searchRefInRef = preferences.getBoolean("refInRef", false);

            // Set values

            // Distance and size
            tabbedDistancePane.setSelectedIndex(distancePaneSelected);
            this.deltaTable.setDeltaValues(deltaTable, deltaTableSize);
            distanceSpinner.setValue(delta);
            sizeSpinner.setValue(deltaSize);

            this.opMode = operationMode;
            // Mode, all-against-all, genome or cluster
            if (referenceType.equals(Parameter.ReferenceType.cluster) || referenceType.equals(Parameter.ReferenceType.genome))
                refCombo.setSelectedItem(Parameter.ReferenceType.genome);
            else
                refCombo.setSelectedItem(referenceType);

            // Quorum
            if (q == 0 || q >= qCombo.getItemCount())
                qCombo.setSelectedIndex(0);
            else
                qCombo.setSelectedIndex(q-1);

            // Ref in Ref
            refInRef.setSelected(searchRefInRef);
        }
    }

}
