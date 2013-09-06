package gecko2.gui;

import gecko2.algorithm.GeneCluster;
import gecko2.algorithm.Genome;
import gecko2.event.BrowserContentListener;
import gecko2.event.ClusterSelectionListener;
import gecko2.gui.MultipleGenomesBrowser.ScrollListener;

import javax.swing.*;

public abstract class AbstractMultipleGenomeBrowser extends JPanel implements ClusterSelectionListener{
	public abstract void addBrowserContentListener(BrowserContentListener l);
	public abstract void addSelectionListener(ClusterSelectionListener s);
	protected abstract void fireBrowserContentChanged(short type);
	public abstract ScrollListener getWheelListener();
	
	public abstract void clear();
	public abstract void clearSelection();
	public abstract void addGenomes(Genome[] g);
	
	public abstract int getNrGenomes();
	public abstract int getGeneWidth();
	public abstract int[] getGeneNumbers(int genomeIndex);
	public abstract boolean isFlipped(int genomeIndex);
	public abstract void centerCurrentClusterAt(int geneID);
	public abstract int getScrollMaximum();
	public abstract int getScrollWidth();
	public abstract int getScrollValue(int genomeIndex);
	public abstract int[] getSubselection();
	public abstract GeneCluster getSelectedCluster();
	public abstract void changeGeneElementHight(int adjustment);
	public abstract void hideNonClusteredGenomes(boolean hide);
}
