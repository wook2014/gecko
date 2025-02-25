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

import java.util.*;

/**
 *
 * @author Sascha Winter (sascha.winter@uni-jena.de)
 */
class GenomeList {

    private final List<Genome> genomes;
    private Rank rank;
    private final int alphabetSize;
    
    private boolean containsReferenceCopy;
    /**
     * Constructor for generating a SetOfSequences from an array of integer with given alphabet size.
     * @param genomes the lists of Integers.
     * @param alphabetSize the alphabet size
     */
    GenomeList(int[][][] genomes, int alphabetSize) {
        int i = 0;
        this.genomes = new ArrayList<>(genomes.length);
        for (int[][] genome : genomes) {
            List<Chromosome> chromosomes = new ArrayList<>(genome.length);
            int j=0;
            for (int[] chromosome : genome) {
                chromosomes.add(new Chromosome(chromosome, j, false));
                j++;
            }
            Genome g = new Genome(i, chromosomes);
            this.genomes.add(g);
            i++;
        }
        this.alphabetSize = alphabetSize;
        this.containsReferenceCopy = false;
    }
    
    /**
     * Constructor for generating a SetOfSequences from an array of integer.
     * @param genomes the lists of Integers.
     */
    GenomeList(int[][][] genomes) {
        Set<Integer> genes = new HashSet<>();
        genes.add(0);
        int alphSize = 0;
        int i = 0;
        this.genomes = new ArrayList<>(genomes.length);
        for (int[][] genome : genomes) {
            List<Chromosome> chromosomes = new ArrayList<>(genome.length);
            int j=0;
            for (int[] chromosome : genome) {
                for (Integer gene : chromosome) {
                	if (gene >= 0 && genes.add(gene))
                		alphSize++;
                }
                chromosomes.add(new Chromosome(chromosome, j, false));
                j++;
            }
            Genome g = new Genome(i, chromosomes);
            this.genomes.add(g);
            i++;
        }
        this.alphabetSize = alphSize;      
        this.containsReferenceCopy = false;
    }
    
    /**
     * Appends a copy of the reference genome to the genome list. 
     * Used for searching reference occurrences in the reference genome.
     * @param referenceGenomeNr the number of the reference genome.
     */
    void appendCopyOfReferenceGenome(int referenceGenomeNr, AlgorithmParameters param) {
    	if (containsReferenceCopy)
    		throw new RuntimeException("Trying to append additional reference copy!");
    	Genome newGenome = new Genome(genomes.get(referenceGenomeNr));
    	for (Chromosome chr : newGenome) {
            chr.initializeForCalculation(alphabetSize, param.getMaximumDelta());
        }
    	genomes.add(newGenome);
    	param.increaseNrOfGenomes();
    	this.containsReferenceCopy = true;
    }
    
    void removeCopyOfReferenceGenome(AlgorithmParameters param) {
    	if (!containsReferenceCopy)
    		throw new RuntimeException("Trying to remove non existing reference copy!");
    	
    	genomes.remove(genomes.size() - 1);
    	param.decreaseNrOfGenomes();
    	this.containsReferenceCopy = false;
    }
    
    /**
     * Initializes the SetOfSequences for a calculation of gene clusters.
     * @param maxDelta the maximum distance
     */
    void initializeForCalculation(int maxDelta) {
        for (Genome g : genomes) {
            for (Chromosome chr : g) {
                chr.initializeForCalculation(alphabetSize, maxDelta);
            }
        }
        rank = new Rank(alphabetSize);
    }

    /**
     * Returns the genome with the number n.
     * @param n the number of the genome.
     * @return the genome with the number n.
     */
    public Genome get(int n) {
        return genomes.get(n);
    }

    /**
     * Returns the number of genomes in the SetOfSequences.
     * @return the number of genomes in the SetOfSequences.
     */
    public int size() {
        return genomes.size();
    }

    /**
     * Returns the size of the alphabet of all sequences.
     * @return the size of the alphabet of all sequences.
     */
    public int getAlphabetSize() {
        return alphabetSize;
    }

    /**
     * Updates the left border of the pattern to the position leftBorder in the reference chromosome refChr from the genome refGenomeNr.
     * @param leftBorder the new left border of the pattern.
     * @param refChr the reference chromosome.
     * @param refGenomeNr the number of the genome the reference chromosome is located on.
     */
    public void updateLeftBorder(int leftBorder, Chromosome refChr, int refGenomeNr) {
        rank.updateRank(refChr, leftBorder);  //TODO rank really in seqSet? Alternative Rank in Pattern

        this.updateL(refGenomeNr, leftBorder, refChr.getGene(leftBorder - 1));
        this.updateR(refGenomeNr, leftBorder, refChr.getGene(leftBorder - 1));
        this.updateL_R_prime(refGenomeNr, leftBorder, refChr.getGene(leftBorder - 1));
    }
    
    public boolean zeroOccs(int refGenomeNr, int refChrNr, int position, boolean searchRefInRef){
    	int c = genomes.get(refGenomeNr).get(refChrNr).getGene(position);
    	
    	for (int l=0; l<genomes.size(); l++){
    		if (l==refGenomeNr)
    			continue;
    			
    		if (searchRefInRef && l == genomes.size()-1){
    			//TODO refInRef
    		} else {
    			if (genomes.get(l).noOcc(c) == 0)
    				return false;
    		}
    	}
    	return true;
    }

    /**
     * Updates or computes the matrix L for each genome in the SetOfGenomes.
     *
     * L holds the max. maxDist positions of the next unmarked characters left of each position in the chromosome.
     * The array rank is used to determine unmarked characters.
     * @param refGenomeNr the number of the current reference genome.
     * @param i the start position of the current reference interval on the reference chromosome.
     * @param c_old the character that was last added to the reference interval.
     */
    private void updateL(int refGenomeNr, int i, int c_old) {  //TODO parallel?
        for (int k=0; k<this.size(); k++) {
            if (k==refGenomeNr) {
                    continue;
            }

            for (Chromosome chr: genomes.get(k)) {
                if (i==1)
                    chr.computeL(rank);
                else if (c_old >= 0)
                    chr.updateL(rank, c_old);
            }
        }
    }

    /**
     * Updates or computes the matrix R for each genome in the SetOfGenomes.
     *
     * R holds the max. maxDist positions of the next unmarked characters right of each position in the chromosome.
     * The array rank is used to determine unmarked characters.
     * @param refGenomeNr the number of the current reference genome.
     * @param i the start position of the current reference interval on the reference chromosome.
     * @param c_old the character that was last added to the reference interval.
     */
    private void updateR(int refGenomeNr, int i, int c_old){
        for (int k=0; k<this.size(); k++) {
            if (k==refGenomeNr)
                continue;

            for (Chromosome chr: genomes.get(k))
                if (i==1)
                    chr.computeR(rank);
                else if (c_old >= 0)
                    chr.updateR(rank, c_old);
        }
    }
    
    private void updateL_R_prime(int refGenomeNr, int leftBorder, int c_old) {
		for (int k=0; k<this.size(); k++) {
			if (k==refGenomeNr)
                continue;

            for (Chromosome c: genomes.get(k)) {
                if (leftBorder==1) {
                    c.computeL_prime(rank);
                    c.computeR_prime(rank);
                } else if (c_old >= 0) {
                    c.updateL_prime(rank, c_old);
                    c.updateR_prime(rank, c_old);
                }
			}
		}
		
	}

    @Override public String toString() {
        StringBuilder b = new StringBuilder(String.format("Alphabet size: %1$d%n", alphabetSize));
        for (Genome genome : genomes) {
            b.append(genome);
        }
        return b.toString();
    }

    public void removeCalculationFields() {
        for (Genome g : genomes) {
            for (Chromosome chr : g) {
                chr.removeCalculationFields();
            }
        }
        rank = null;
    }
}