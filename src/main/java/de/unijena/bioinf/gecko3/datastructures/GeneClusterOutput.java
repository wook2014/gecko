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

package de.unijena.bioinf.gecko3.datastructures;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Contains all the information about a gene cluster that is need for printing
 * @author swinter
 */
public class GeneClusterOutput {
    private final int id;
	private final BigDecimal pValue;
	private final int refSeq;
	private final int[] distances; 
	private final Map<GeneFamily, Gene[][]> geneAnnotations;
	/**
	 * The gene ids for all the intervals. The first list contains one entry per genome. 
	 * The second contains one entry per occurrence on the genome. The third contains
	 * the gene ids for the occurrence.
	 */
	private final List<List<List<Gene>>> intervals;
	/**
	 * The borders for all the intervals. The first list contains one entry per genome. 
	 * The second contains one entry per occurrence on the genome. The array is always
	 * of size 2 and contains the left and right borders of the interval.
	 */
	private final List<List<int[]>> intervalBorders;

	private final List<List<String>> chromosomes;
	private final int[] nrOfOccurrences;

    public int getId() {
        return id;
    }

    public static class Builder {
        private final int id;
		private final int nrOfSequences;
		
		private BigDecimal pValue;
		private int refSeq;
		private final int[] distances;
		private Map<GeneFamily, Gene[][]> geneAnnotations;
		private List<List<List<Gene>>> intervals;
		private List<List<int[]>> intervalBorders;
		private List<List<String>> chromosomes;

		private int[] nrOfOccurrences;
		
		public Builder (int nrOfSequences, int id) {
			this.nrOfSequences = nrOfSequences;
            this.id = id;
			
			distances = new int[this.nrOfSequences];
			Arrays.fill(distances, 0);
		}
		
		public Builder pValue(BigDecimal pV) {
			pValue = pV;
			return this;
		}
		
		public Builder refSeq(int rS) {
			refSeq = rS;
			return this;
		}
		
		public Builder setDistance(int d, int i) {
			distances[i] = d;
			return this;
		}
		
		public Builder annotations(Map<GeneFamily, Gene[][]> geneAnnotations) {
			this.geneAnnotations = geneAnnotations;
			return this;
		}
		
		public void intervals(List<List<List<Gene>>> intervals) {
			this.intervals = intervals;
		}
		
		public void intervalBorders(List<List<int[]>> intervalBorders){
			this.intervalBorders = intervalBorders;
		}

		public Builder chromosomes(List<List<String>> chrom) {
			this.chromosomes = chrom;
			return this;
		}

		public Builder nrOfOccurrences(int[] nrOfOccurrences) {
			this.nrOfOccurrences = nrOfOccurrences;
			return this;
		}
		
		public GeneClusterOutput build() {
			return new GeneClusterOutput(this);
		}
	}
	
	private GeneClusterOutput(Builder builder) {
        id = builder.id;
		pValue = builder.pValue;
		refSeq = builder.refSeq;
		distances = Arrays.copyOf(builder.distances, builder.distances.length);
		geneAnnotations = builder.geneAnnotations;
		intervals = builder.intervals;
		intervalBorders = builder.intervalBorders;
		chromosomes = builder.chromosomes;
		nrOfOccurrences = Arrays.copyOf(builder.nrOfOccurrences, builder.nrOfOccurrences.length);
	}

	public List<List<String>> getChromosomes() {
		return chromosomes;
	}

	public BigDecimal getPValue() {
		return pValue;
	}

	public int getRefSeq() {
		return refSeq;
	}

	public int[] getDistances() {
		return distances;
	}

	public Map<GeneFamily, Gene[][]> getGeneAnnotations() {
		return geneAnnotations;
	}
	
	public List<List<List<Gene>>> getIntervals() {
		return intervals;
	}
	
	public List<List<int[]>> getIntervalBorders() {
		return intervalBorders;
	}
	
	public int[] getNrOfOccurrences() {
		return nrOfOccurrences;
	}
}
