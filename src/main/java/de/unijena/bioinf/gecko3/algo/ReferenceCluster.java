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

package de.unijena.bioinf.gecko3.algo;

import java.math.BigDecimal;
import java.util.*;

public class ReferenceCluster {
	private int genomeNr;
	private int chrNr;
	private int leftBorder;
	private int rightBorder;
	private final int size;
	private int coveredGenomes;
	private int coveredGenomeGroups;
	private final int maxDistance;
	private final List<List<DeltaLocation>> dLocLists;
	private BigDecimal bestCombined_pValue;
	private BigDecimal bestCombined_pValueCorrected;
	private List<Integer> geneContent;
    private boolean containsSingletonGene;
	private final boolean searchRefInRef;

    public ReferenceCluster(Pattern refPattern, List<ListOfDeltaLocations> dLocLists, boolean searchRefInRef, int nrOfGenomeGroups, Map<Integer, Integer> genomeGroupMapping){
		genomeNr = refPattern.getRefGenomeNr();
		chrNr = refPattern.getRefChromosomeNr();
		leftBorder = refPattern.getLeftBorder();
		rightBorder = refPattern.getRightBorder();
		size = refPattern.getSize();
		this.searchRefInRef = searchRefInRef;
		
		this.dLocLists = new ArrayList<>(dLocLists.size());
		int coverCount = 0;
		int maxD = -1;
		
		boolean[] coveredGenomeGroups = new boolean[nrOfGenomeGroups];
		int[] minGenomeGroupDistance = new int[nrOfGenomeGroups];
		Arrays.fill(minGenomeGroupDistance, Integer.MAX_VALUE);
		
		// We now need a list, that is sorted by the ordering of DeltaLocation
		for (int i=0; i<dLocLists.size(); i++){
			ListOfDeltaLocations dLocList = dLocLists.get(i);
            Set<DeltaLocation> set = new TreeSet<>();
            for (DeltaLocation dLoc : dLocList)
                set.add(new DeltaLocation(dLoc));
            this.dLocLists.add(new ArrayList<>(set));
            
            int genomeMinDistance = Integer.MAX_VALUE;
            for (DeltaLocation dLoc : dLocList)
                if (dLoc.getDistance() < genomeMinDistance)
                    genomeMinDistance = dLoc.getDistance();
            if (genomeMinDistance != Integer.MAX_VALUE && genomeMinDistance > maxD)
            	maxD = genomeMinDistance;
            
            if (dLocList.size() > 0){
                coverCount++;
                if (genomeGroupMapping != null) {
                	coveredGenomeGroups[genomeGroupMapping.get(i)] = true;
                	if (genomeMinDistance < minGenomeGroupDistance[genomeGroupMapping.get(i)])
                		minGenomeGroupDistance[genomeGroupMapping.get(i)] = genomeMinDistance;
                }
            }
        }
		this.coveredGenomes = coverCount;
		
		if (genomeGroupMapping != null){
			coverCount = 0;
			maxD = -1;
			for (int i=0; i<coveredGenomeGroups.length; i++){
				if (coveredGenomeGroups[i]) {
					coverCount++;
					if (minGenomeGroupDistance[i] > maxD)
						maxD = minGenomeGroupDistance[i];
				}
			}
		}
		this.coveredGenomeGroups = coverCount;
		this.maxDistance = maxD;
	}
	
	public int getSize() {
		return size;
	}
	
	public void changeReferenceOccurrence(DeltaLocation newRefOcc)
    {
		this.genomeNr = newRefOcc.getGenomeNr();
        this.chrNr = newRefOcc.getChrNr();
        this.leftBorder = newRefOcc.getL();
        this.rightBorder = newRefOcc.getR();
	}

    public int getMaxDistance() {
        return maxDistance;
    }

    public int getGenomeNr() {
        return genomeNr;
    }

    public int getChrNr() {
        return chrNr;
    }

    public int getLeftBorder() {
        return leftBorder;
    }
    
    public void setLeftBorder(int x){
    	this.leftBorder = x;
    }

    public int getRightBorder() {
        return rightBorder;
    }
    
    public void setRightBorder(int x){
    	this.rightBorder = x;
    }

	public BigDecimal getBestCombined_pValue() {
		return bestCombined_pValue;
	}

	public BigDecimal getBestCombined_pValueCorrected() {
		return bestCombined_pValueCorrected;
	}

	public void setBestCombined_pValue(BigDecimal bestCombined_pValue) {
		this.bestCombined_pValue = bestCombined_pValue;
	}

	public void setBestCombined_pValueCorrected(
			BigDecimal bestCombined_pValueCorrected) {
		this.bestCombined_pValueCorrected = bestCombined_pValueCorrected;
	}

	public int getCoveredGenomes() {
		return coveredGenomes;
	}
	
	public int getCoveredGenomeGroups() {
		return coveredGenomeGroups;
	}
	
	public List<List<DeltaLocation>> getAllDeltaLocations(){
		return dLocLists;
	}

	public List<DeltaLocation> getDeltaLocations(int index){
		return dLocLists.get(index);
	}
	
	public int[] getMinimumDistances(){
		int[] minDistances = new int[dLocLists.size()];
		for (int i=0; i<dLocLists.size(); i++){
			if (searchRefInRef && i==genomeNr)
				minDistances[i] = getMinimumReferenceDistance(dLocLists.get(i));
			else
				minDistances[i] = getMinimumDistance(dLocLists.get(i));
		}
		return minDistances;
	}
	
	private int getMinimumDistance(List<DeltaLocation> dLocs) {
		int minDist = Integer.MAX_VALUE;
		for (DeltaLocation dLoc : dLocs)
			if (dLoc.getDistance() < minDist)
				minDist = dLoc.getDistance();
		return (minDist != Integer.MAX_VALUE) ? minDist : -1;
	}
	
	private int getMinimumReferenceDistance(List<DeltaLocation> dLocs) {
		int minDist = Integer.MAX_VALUE;
		boolean refHitFound = false;
		if (1 == dLocs.size())
			minDist = Math.min(minDist, dLocs.get(0).getDistance());
		else {
			for (DeltaLocation dLoc : dLocs)
				if (!refHitFound && dLoc.getDistance() == 0)
					refHitFound = true;
				else if (dLoc.getDistance() < minDist)
					minDist = dLoc.getDistance();
		}
		return (minDist != Integer.MAX_VALUE) ? minDist : -1;
	}

	public boolean areAll_dLocsNested(ReferenceCluster otherCluster) {
		for (int k=0; k<Math.min(dLocLists.size(), otherCluster.dLocLists.size()); k++)
			if (!areAll_dLocsNested(dLocLists.get(k), otherCluster.dLocLists.get(k)))
				return false;
		
		if (dLocLists.size() > otherCluster.dLocLists.size())
			if (!areAll_dLocsNested(dLocLists.get(dLocLists.size() - 1), otherCluster.dLocLists.get(otherCluster.getGenomeNr())))
				return false;
		
		if (dLocLists.size() < otherCluster.dLocLists.size())
			if (!areAll_dLocsNested(dLocLists.get(genomeNr), otherCluster.dLocLists.get(otherCluster.dLocLists.size() - 1)))
				return false;
		
		return true;
	}
	
	private boolean areAll_dLocsNested(List<DeltaLocation> first, List<DeltaLocation> second){
		for (DeltaLocation dLoc : first){
			boolean nestedFound = false;
			
			for (DeltaLocation other_dLoc : second){
				if (dLoc.getChrNr() == other_dLoc.getChrNr())
					if (dLoc.getL() >= other_dLoc.getL() && dLoc.getR() <= other_dLoc.getR())
						if (dLoc.getDistance() >= other_dLoc.getDistance())
							nestedFound = true;
				if (nestedFound)
					break;
			}
			if (!nestedFound)
				return false;
		}
		return true;
	}

	public void setGeneContent(GenomeList genomes) {
		geneContent = new ArrayList<>(size);
        containsSingletonGene = false;
		for (int i=leftBorder; i<=rightBorder && geneContent.size()<size; i++){
            int gene = genomes.get(genomeNr).get(chrNr).getGene(i);
            if (gene < 0) {
                containsSingletonGene = true;
                for (int g=0; g<-gene; g++)
                    geneContent.add(-1);
            }
			else if (genomes.get(genomeNr).get(chrNr).getPrevOCC(i) < leftBorder){
				geneContent.add(genomes.get(genomeNr).get(chrNr).getGene(i));
			}
		}
	}
	
	public List<Integer> getGeneContent(){
		return geneContent;
	}

    public boolean isOnlyPossibleReferenceOccurrence(DeltaLocation dLoc) {
        return dLoc.getDistance() == 0 && dLoc.getGenomeNr() == this.getGenomeNr() && containsSingletonGene;
    }

	/**
	 * Converts additional reference hits in the second reference genome into hits in the original reference genome. 
	 * @param numberOfGenomes the number of genomes including the reference copy
	 * @return false if only one hit remains
	 */
	public boolean mergeAdditionalReferenceHits(int numberOfGenomes) {
		if (dLocLists.size() != numberOfGenomes)
			return true;
		
		List<DeltaLocation> refInRefList = dLocLists.get(dLocLists.size()-1);
		
		if (!refInRefList.isEmpty()) {
			coveredGenomes--;
			coveredGenomeGroups--;
		}
		
		Iterator<DeltaLocation> dLocIt = refInRefList.iterator();
		while (dLocIt.hasNext()) {
			DeltaLocation dLoc = dLocIt.next();
			if (dLoc.isPartOfOtherDeltaLocation(dLocLists.get(genomeNr)))
				dLocIt.remove();
			else
				dLoc.setGenomeNr(genomeNr);
		}
		
		dLocLists.get(genomeNr).addAll(refInRefList);
		dLocLists.remove(dLocLists.size()-1);
		
		int nrOfLocs = 0;
		for (List<DeltaLocation> dLocList : dLocLists) {
			nrOfLocs += dLocList.size();
			if (nrOfLocs >= 2)
				return true;
		}
		return false;
	}

    @Override
    public String toString() {
        return "ReferenceCluster{" +
                "genomeNr=" + genomeNr +
                ", chrNr=" + chrNr +
                ", leftBorder=" + leftBorder +
                ", rightBorder=" + rightBorder +
                '}';
    }

    public void correctMergedPositions(int[][][] runLengthMergedLookup, int[][][] intArray) {
        if (runLengthMergedLookup != null) {
            int[] correctedPositions = getCorrectedPosition(
                    getLeftBorder(),
                    getRightBorder(),
                    runLengthMergedLookup[getGenomeNr()][getChrNr()],
                    intArray[getGenomeNr()][getChrNr()]);
            setLeftBorder(correctedPositions[0]);
            setRightBorder(correctedPositions[1]);
        }

        for (int i=0; i<getAllDeltaLocations().size(); i++) {
            for (DeltaLocation dLoc : getDeltaLocations(i)) {
                if (runLengthMergedLookup != null) {
                    int[] correctedPositions = getCorrectedPosition(
                            dLoc.getL(),
                            dLoc.getR(),
                            runLengthMergedLookup[dLoc.getGenomeNr()][dLoc.getChrNr()],
                            intArray[dLoc.getGenomeNr()][dLoc.getChrNr()]);
                    dLoc.setL(correctedPositions[0]);
                    dLoc.setR(correctedPositions[1]);
                }
            }
        }
    }


    private int[] getCorrectedPosition(int leftBorder, int rightBorder, int[] runLengthMergedLookup, int[] sequence) {
        int[] correctedPositions = new int[]{leftBorder, rightBorder};
        for (int i=0; i<runLengthMergedLookup.length; i++){
            if (runLengthMergedLookup[i] >= rightBorder)
                break;
            if (runLengthMergedLookup[i] < leftBorder)
                correctedPositions[0] -= sequence[runLengthMergedLookup[i]]+1;
            if (runLengthMergedLookup[i] < rightBorder)
                correctedPositions[1] -= sequence[runLengthMergedLookup[i]]+1;
        }
        return correctedPositions;
    }
}
