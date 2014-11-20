package gecko2.gui;

import gecko2.datastructures.GeneCluster;
import gecko2.datastructures.GeneClusterOccurrence;
import gecko2.datastructures.GeneFamily;
import gecko2.datastructures.Subsequence;

/**
 * Created by swinter on 18.11.2014.
 */
public class GeneClusterLocationSelection {
    private final GeneCluster cluster;
    private final int[] subselection;
    private final boolean includeSubOptimalOccurrences;
    private final boolean[] flipped;
    private final int[] alignmentGeneCluster;
    private final int[] alignmentGeneChromosome;

    private int refPaintGenome;
    private int paintWidth;
    private int leftPaintWidth;
    private int[] paintOffset;

    public GeneClusterLocationSelection(GeneCluster cluster, int[] subselection, boolean includeSubOptimalOccurrences){
        this(cluster, subselection, includeSubOptimalOccurrences, null, null, null);
    }

    public GeneClusterLocationSelection(GeneCluster cluster, int[] subselection, boolean includeSubOptimalOccurrences, boolean[] flipped, int[] alignmentGeneCluster, int[] alignmentGeneChromosome) {
        this.cluster = cluster;
        this.subselection = subselection;
        this.includeSubOptimalOccurrences = includeSubOptimalOccurrences;
        this.flipped = flipped;
        this.alignmentGeneCluster = alignmentGeneCluster;
        this.alignmentGeneChromosome = alignmentGeneChromosome;
        this.refPaintGenome = -1;
        this.paintWidth = -1;
    }


    /**
     * A new cluster location selection, based on the previous selections, but centered on the given GeneFamily
     * @param geneFamily
     * @return
     */
    public GeneClusterLocationSelection getGeneClusterLocationSelection(GeneFamily geneFamily) {
        return cluster.getGeneClusterLocationSelection(geneFamily, subselection, includeSubOptimalOccurrences);
    }

    public GeneCluster getCluster() {
        return cluster;
    }

    public int[] getSubselection() {
        return subselection;
    }

    public boolean includeSubOptimalOccurrences() {
        return includeSubOptimalOccurrences;
    }

    public boolean isFlipped(int genomeIndex) {
        return flipped[genomeIndex];
    }

    public Subsequence getSubsequence(int i){
        if (subselection[i] == GeneClusterOccurrence.GENOME_NOT_INCLUDED)
            return null;
        return cluster.getOccurrences(includeSubOptimalOccurrences).getSubsequences()[i][subselection[i]];
    }

    /**
     * Returns the chromosome position of the gene used for alignment, or -1 if not aligned
     * @param genomeIndex
     * @return
     */
    public int getAlignmentGenePosition(int genomeIndex){
        if (subselection[genomeIndex] == GeneClusterOccurrence.GENOME_NOT_INCLUDED)
            return -1;
        return alignmentGeneChromosome[genomeIndex];
    }

    /**
     * The total number of genomes in the dataset, including genomes not in the cluster
     * @return
     */
    public int getTotalGenomeNumber() {
        return cluster.getOccurrences(includeSubOptimalOccurrences).getSubsequences().length;
    }

    private void computePaintWidths(){
        if (alignmentGeneCluster == null) {
            paintWidth = 0;
            for (int i = 0; i < getTotalGenomeNumber(); i++) {
                if (subselection[i] == GeneClusterOccurrence.GENOME_NOT_INCLUDED)
                    continue;
                Subsequence subSequence = getSubsequence(i);
                int newWidth = subSequence.getStop() - subSequence.getStart() + 1;
                if (newWidth > paintWidth) {
                    paintWidth = newWidth;
                    refPaintGenome = i;
                }
            }
        } else {
            leftPaintWidth = 0;
            int rigthWidth = 0;
            for (int i=0; i < getTotalGenomeNumber(); i++) {
                if (subselection[i] == GeneClusterOccurrence.GENOME_NOT_INCLUDED)
                    continue;
                Subsequence subSequence = getSubsequence(i);
                int width = subSequence.getStop() - subSequence.getStart() + 1;
                if (alignmentGeneCluster[i] != -1) {
                    if (isFlipped(i)) {
                        leftPaintWidth = Math.max(width - alignmentGeneCluster[i], leftPaintWidth);
                        rigthWidth = Math.max(alignmentGeneCluster[i], rigthWidth);
                    } else {
                        leftPaintWidth = Math.max(alignmentGeneCluster[i], leftPaintWidth);
                        rigthWidth = Math.max(width - alignmentGeneCluster[i], rigthWidth);
                    }
                } else {
                    paintWidth = Math.max(width, paintWidth);
                }
            }
            leftPaintWidth = Math.max(leftPaintWidth, paintWidth/2);
            paintWidth = Math.max(leftPaintWidth + rigthWidth, paintWidth);
        }
    }

    private void computePaintOffsets() {
        if (paintWidth < 0)
            computePaintWidths();
        paintOffset = new int[getTotalGenomeNumber()];
        for (int i = 0; i < getTotalGenomeNumber(); i++) {
            if (subselection[i] == GeneClusterOccurrence.GENOME_NOT_INCLUDED)
                continue;
            Subsequence subSequence = getSubsequence(i);
            int seqLength = subSequence.getStop() - subSequence.getStart() + 1;
            if (alignmentGeneCluster == null || alignmentGeneCluster[i] == -1) {
                // half cluster sequence + 1 if number of genes in the sequence is no straight
                if (paintWidth % 2 == 0)
                    paintOffset[i] = paintWidth/2 - (seqLength/2 + seqLength%2);
                else
                    paintOffset[i] = paintWidth/2 - seqLength/2;
            } else {
                if (isFlipped(i))
                    paintOffset[i] = leftPaintWidth-(seqLength- alignmentGeneCluster[i]);
                else
                    paintOffset[i] = leftPaintWidth- alignmentGeneCluster[i];
            }
        }
    }

    /**
     * Gives the width (number of genes) of the cluster location, taking into account the gene alignment
     * @return
     */
    public int getMaxClusterLocationWidth(){
        if (paintWidth < 0)
            computePaintWidths();
        return paintWidth;
    }

    public int getGeneOffset(int genomeIndex) {
        if (paintOffset == null)
            computePaintOffsets();
        return paintOffset[genomeIndex];
    }
}
