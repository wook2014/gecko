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

package de.unijena.bioinf.gecko3;

import de.unijena.bioinf.gecko3.algo.ReferenceClusterAlgorithm;
import de.unijena.bioinf.gecko3.algo.status.AlgorithmProgressListener;
import de.unijena.bioinf.gecko3.algo.status.AlgorithmStatusEvent;
import de.unijena.bioinf.gecko3.datastructures.*;
import de.unijena.bioinf.gecko3.event.DataEvent;
import de.unijena.bioinf.gecko3.event.DataListener;
import de.unijena.bioinf.gecko3.gui.GenomePainting;
import de.unijena.bioinf.gecko3.gui.Gui;
import de.unijena.bioinf.gecko3.gui.StartComputationDialog;
import de.unijena.bioinf.gecko3.io.ResultWriter;
import de.unijena.bioinf.gecko3.io.ExportType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.prefs.Preferences;

public class GeckoInstance {
    private static final Logger logger = LoggerFactory.getLogger(GeckoInstance.class);
    private Preferences prefs;

    private static GeckoInstance instance;

    private DataSet data;

    public enum ResultFilter {
        showFiltered,
        showAll,
        showSelected;

        public static final String types = "showFiltered, showAll";
    }

    private SwingWorker<List<GeneCluster>, Void> geneClusterSwingWorker = null;
	
	private File currentWorkingDirectoryOrFile;

    private List<GeneCluster> clusterSelection;
	private List<GeneCluster> reducedList;

    private Gui gui;

    private StartComputationDialog scd = null;

    public static final int DEFAULT_MAX_GENE_NAME_LENGTH = 6;

    private int maxGeneNameLength = DEFAULT_MAX_GENE_NAME_LENGTH;

    private final EventListenerList eventListener = new EventListenerList();
	
    /**
     * Setter for the upper bound of the maximum gene name length.
     * @param maxGeneNameLength the maximum gene name length.
     */
    public void setMaxGeneNameLength(int maxGeneNameLength) {
        this.maxGeneNameLength = maxGeneNameLength;
    }
	
	/**
	 * Getter for the variable maxIdLength
	 * 
	 * @return the length of the longest id in the read data
	 */
	public int getMaxLength(GenomePainting.NameType nameType)
	{
        int maxLength;
        switch (nameType) {
            case ID: maxLength = data.getMaxIdLength(); break;
            case NAME: maxLength =  data.getMaxNameLength(); break;
            case LOCUS_TAG:maxLength =  data.getMaxLocusTagLength(); break;
            default:
                return -1;
        }
        if (maxGeneNameLength == Integer.MAX_VALUE)
            return maxLength;
        else
            return maxGeneNameLength;
	}
	
	/*
	 * DataEvents
	 */
	
	public void addDataListener(DataListener l) {
		eventListener.add(DataListener.class, l);
	}
	
	public void removeDataListener(DataListener l) {
		eventListener.remove(DataListener.class, l);
	}
	
	public synchronized void fireDataChanged() {
		for (DataListener d : eventListener.getListeners(DataListener.class) ) {
			d.dataChanged(new DataEvent(this));
		}
	}
	/*
	 * END DataEvents
	 */

	public synchronized StartComputationDialog getStartComputationDialog() {
		if (scd==null) {
            scd = new StartComputationDialog(gui.getMainframe());
        }
		return scd;
	}
	
	/**
	 * Adds the cluster with the given index to the cluster selection
	 * 
	 * @param cluster the cluster
	 */
	public void addToClusterSelection(GeneCluster cluster) {
		if (clusterSelection == null)
			clusterSelection = new ArrayList<>();
        if (!clusterSelection.contains(cluster))  //TODO LinkedHashSet for performance?
		    clusterSelection.add(cluster);
	}
	
	/**
	 * Clears all selected clusters
	 */
	public void clearClusterSelection() {
		clusterSelection.clear();
	}

    public Parameter getParameters() {
        return data.getParameters();
    }
	
	/**
	 * Returns the list of gene clusters under the given filter condition. 
	 * @param filter the filter condition
	 * @return the list of gene clusters
	 */
	public List<GeneCluster> getClusterList(ResultFilter filter) {
        switch (filter) {
            case showAll:
                return data.getClusters();
            case showFiltered:
                return reducedList;
            case showSelected:
                return clusterSelection;
        }
        throw new RuntimeException("Invalid filter tpye: " + filter);
	}

    public Color getGeneColor(GeneFamily geneFamily) {
        return data.getGeneColor(geneFamily);
    }

    public GeneFamily getGeneFamily(String externalId){
        return data.getGeneFamily(externalId);
    }
	
	public void setGui(Gui gui) {
		this.gui = gui;
	}
	
	public Gui getGui() {
		return gui;
	}

    /**
     * Swap the genome at position index with the first genome
     * @param index the position of the new first genome
     */
    public void reorderGenomes(int index) {
        this.data.reorderGenomes(index);
        dataUpdated();
    }
	
	public void addReferenceGenome(Genome referenceGenome) {
        this.data.addReferenceGenome(referenceGenome);
        dataUpdated();
	}
	
	public void setClusters(List<GeneCluster> clusters, Parameter parameter) {
		this.data.setClusters(clusters, parameter);
        handleUpdatedClusterResults();
	}

    private void updateReducedDataAndSelection() {
        GeckoInstance.this.reducedList = GeneCluster.generateReducedClusterList(GeckoInstance.this.getClusters());
        GeckoInstance.this.clusterSelection = null;
    }

    private void handleUpdatedClusterResults() {
        updateReducedDataAndSelection();
        GeckoInstance.this.fireDataChanged();
        if (GeckoInstance.this.gui != null) {
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    GeckoInstance.this.gui.changeMode(Gui.Mode.SESSION_IDLE);
                }
            });
        }
    }

    private void dataUpdated() {
        if (this.gui != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    gui.updateViewscreen();
                    if (data.getGenomes() != null) {
                        scd = new StartComputationDialog(gui.getMainframe());
                    }
                    else
                        scd = null;
                    fireDataChanged();
                }
            });
        } else
            scd = null;
    }

    public DataSet getData() {
        return data;
    }

    public void setGeckoInstanceData(){
        setGeckoInstanceData(this.data);
    }

    public void setGeckoInstanceData(final DataSet data) {
        if (gui != null)
            gui.clearSelection();
        this.data = data;
        this.updateReducedDataAndSelection();
        if (gui != null) {
            Runnable updateGui = new Runnable() {
                @Override
                public void run() {
                    gui.updateViewscreen();
                    if (data.equals(DataSet.getEmptyDataSet()))
                        gui.changeMode(Gui.Mode.NO_SESSION);
                    else
                        gui.changeMode(Gui.Mode.SESSION_IDLE);
                }
            };

            if (SwingUtilities.isEventDispatchThread())
                updateGui.run();
            else
                SwingUtilities.invokeLater(updateGui);

            dataUpdated();
        }
    }
	
	public List<GeneCluster> getClusters() {
		return data.getClusters();
	}
	
	public File getCurrentWorkingDirectoryOrFile() {
		return currentWorkingDirectoryOrFile;
	}

	public void setCurrentWorkingDirectoryOrFile(File currentWorkingDirectoryOrFile) {
		this.currentWorkingDirectoryOrFile = currentWorkingDirectoryOrFile;
        if (prefs != null)
            prefs.put("workingDirectory", currentWorkingDirectoryOrFile.getAbsolutePath());
	}

	private GeckoInstance() {
        try {
            prefs = Preferences.userRoot().node("Gecko3");
        } catch (SecurityException e) {
            logger.warn("No permission to access Preferences", e);
            prefs = null;
        }
        if (prefs != null) {
            String workingDir = prefs.get("workingDirectory", System.getProperty("user.home"));
            if (workingDir != null)
                setCurrentWorkingDirectoryOrFile(new File(workingDir));
        }
	}

    public Preferences getPreferences() {
        return prefs;
    }
	
	public static synchronized GeckoInstance getInstance() {
		if (instance==null) {
			instance = new GeckoInstance();
		}
		return instance;
	}

    private static final boolean USE_MEMORY_REDUCTION_DEFAULT = true;

	/**
	 * Computes the gene clusters for the given genomes with the given parameters
	 * @param data the data
	 * @param params the parameters
	 * @return the gene clusters
	 */
	public static List<GeneCluster> computeClustersJava(DataSet data, Parameter params, AlgorithmProgressListener listener) {
		return computeClustersJava(data, params, null, USE_MEMORY_REDUCTION_DEFAULT, listener);
	}

    /**
     * Computes the gene clusters for the given genomes with the given parameters
     * @param data the data
     * @param params the parameters
     * @param useMemoryReduction
     * @return the gene clusters
     */
    public static List<GeneCluster> computeClustersJava(DataSet data, Parameter params, boolean useMemoryReduction, AlgorithmProgressListener listener) {
        return computeClustersJava(data, params, null, useMemoryReduction, listener);
    }

    /**
     * Computes the gene clusters for the given genomes with the given parameters
     * @param data the data
     * @param params the parameters
     * @param genomeGrouping
     * @return the gene clusters
     */
    public static List<GeneCluster> computeClustersJava(DataSet data, Parameter params, List<Set<Integer>> genomeGrouping, AlgorithmProgressListener listener) {
        return computeClustersJava(data, params, genomeGrouping, USE_MEMORY_REDUCTION_DEFAULT, listener);
    }

    /**
	 * Computes the gene clusters for the given genomes with the given parameters
	 * @param data the data
	 * @param params the parameters
	 * @param genomeGrouping the grouping of the genomes, only one genome per group is used for quorum and p-value
     * @param useMemoryReduction
     * @param listener
	 * @return the gene clusters
	 */
	public static List<GeneCluster> computeClustersJava(DataSet data, Parameter params, List<Set<Integer>> genomeGrouping, boolean useMemoryReduction, AlgorithmProgressListener listener) {
		return ReferenceClusterAlgorithm.computeReferenceClusters(data, params, useMemoryReduction, genomeGrouping, listener);
	}

    public void stopComputation() {
        geneClusterSwingWorker.cancel(true);
    }

    class GeneClusterDetectionTask extends SwingWorker<List<GeneCluster>, Void> implements AlgorithmProgressListener{
        private final Parameter p;
        private final boolean mergeResults;
        private final double groupingFactor;
        private final DataSet data;

        public GeneClusterDetectionTask(Parameter p, boolean mergeResults, double groupingFactor, DataSet data) {
            this.p = p;
            this.mergeResults = mergeResults;
            this.groupingFactor = groupingFactor;
            this.data = data;
        }

        /**
         * Computes a result, or throws an exception if unable to do so.
         * <p/>
         * <p/>
         * Note that this method is executed only once.
         * <p/>
         * <p/>
         * Note: this method is executed in a background thread.
         *
         * @return the computed result
         * @throws Exception if unable to compute a result
         */
        @Override
        protected List<GeneCluster> doInBackground() throws Exception {
            //data.printGenomeStatistics();
            //BreakPointDistance.breakPointDistance(GeckoInstance.this.genomes, false);
            //BreakPointDistance.groupGenomes(genomes, 0.1, 0.95, 0.1, false);
            //System.out.println("\n");
            //BreakPointDistance.breakPointDistance(GeckoInstance.this.genomes, true);

            List<Set<Integer>> genomeGroups = null;
            if (groupingFactor <= 1.0)
                genomeGroups = BreakPointDistance.groupGenomes(data, groupingFactor, false);

            Date before = new Date();
            if (!mergeResults){
                data.clearClusters();
            }
            List<GeneCluster> res = computeClustersJava(data, p, genomeGroups, this);
            Date after = new Date();
            setProgressStatus(100, AlgorithmStatusEvent.Task.Done);
            logger.info("Time required for computation: {}s", (after.getTime() - before.getTime()) / 1000F);
            return res;
        }

        @Override
        public void done() {
            try {
                List<GeneCluster> results = get();
                if (mergeResults)
                    GeckoInstance.this.mergeClusters(results, p);
                else
                    GeckoInstance.this.setClusters(results, p);
                if (gui != null)
                    JOptionPane.showMessageDialog(gui.getMainframe(),
                            String.format("Computation done. Detected a total of %d gene clusters!", results.size()));
            } catch (CancellationException e){
                if (gui != null) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            gui.disableProgressBar();
                            GeckoInstance.this.gui.changeMode(Gui.Mode.SESSION_IDLE);
                        }
                    });
                }
            } catch (InterruptedException | ExecutionException e) {
                if (gui != null) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            gui.disableProgressBar();
                            GeckoInstance.this.gui.changeMode(Gui.Mode.SESSION_IDLE);
                        }
                    });
                }
                JOptionPane.showMessageDialog(gui.getMainframe(), e.getMessage(), "Error computing gene clusters", JOptionPane.ERROR_MESSAGE);
                logger.error("Error in cluster computation", e);
            }
        }

        @Override
        public void algorithmProgressUpdate(AlgorithmStatusEvent statusEvent) {
            setProgressStatus(statusEvent.getProgress(), statusEvent.getTask());
        }
    }

    public void mergeClusters(List<GeneCluster> results, Parameter p) {
        data.mergeClusters(results, p);
        handleUpdatedClusterResults();
    }

    /**
     * Creates and executes the swing worker that performs the cluster detection.
     * Returns the SwingWorker, so the calling method can wait (by calling get() ) until it is done.
     * @param p
     * @param mergeResults
     * @param genomeGroupingFactor
     * @return
     */
	public SwingWorker<List<GeneCluster>, Void> performClusterDetection(Parameter p, boolean mergeResults, double genomeGroupingFactor) {
        if (gui != null)
		    gui.changeMode(Gui.Mode.PREPARING_COMPUTATION);

        geneClusterSwingWorker = new GeneClusterDetectionTask(p, mergeResults, genomeGroupingFactor, GeckoInstance.this.getData());
        geneClusterSwingWorker.execute();
        return geneClusterSwingWorker;
	}
	
	public void displayMessage(String message) {
		final String m = message;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog(gui.getMainframe(), m);
			}
		});
	}
	
	public void setProgressStatus(int value, final AlgorithmStatusEvent.Task task) {
		if (gui != null)
            gui.setProgressStatus(value, task);
	}
	
	public Genome[] getGenomes() {
		return data.getGenomes();
	}
	
	public boolean exportResultsToFile(File f, ResultFilter filter, ExportType type) {
        setCurrentWorkingDirectoryOrFile(f);
        return ResultWriter.exportResultsToFile(f, type, filter);
	}
}
