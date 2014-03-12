package gecko2.algorithm;

import java.io.Serializable;
import java.util.*;


public class Genome implements Serializable {

	private static final long serialVersionUID = 370380955909547007L;

	private final List<Chromosome> chromosomes;
	private String name;
	
	public Genome() {
		this("");
	}
	
	public Genome(String name) {
		chromosomes = new ArrayList<Chromosome>();
		this.name = name;
	}
	
	public void setName(String name) {	
		this.name = name;
	}
	
	public String getName() {	
		return this.name;
	}
	
	/**
	 * Gets the full combined name of the genome + the chromosome
	 * @param nr the nr of the chromosome
	 * @return the name, either "genomeName + " " + chromName" or "genomeName", if chromName is "".
	 */
	public String getFullChromosomeName(int nr){
		Chromosome chr = chromosomes.get(nr);
		if (chr.getName().equals(""))
			return name;
		else
			return name + " " + chr.getName();
	}
	
	public List<Chromosome> getChromosomes() {
		return Collections.unmodifiableList(chromosomes);
	}
	
	public void addChromosome(Chromosome chr) {
		chromosomes.add(chr);
	}

	public Gene[] getSubsequence(Subsequence s) {
		List<Gene> geneList = new ArrayList<Gene>(s.getStop() - (s.getStart()-1));
		for (int i=s.getStart()-1; i<s.getStop(); i++) 
			geneList.add(chromosomes.get(s.getChromosome()).getGenes().get(i));
		return geneList.toArray(new Gene[geneList.size()]);
	}
	
	public int getTotalGeneNumber() {
		int geneNumber = 0;
		for (Chromosome chr : chromosomes) {
			geneNumber += chr.getGenes().size();
		}
		return geneNumber;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Genome genome = (Genome) o;

        if (!chromosomes.equals(genome.chromosomes)) return false;
        if (!name.equals(genome.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = chromosomes.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    /**
	 * Generates an int array from the genomes
	 * @param genomes the genomes
	 * @return an int array, containing all the genes
	 */
	public static int[][][] toIntArray(Genome[] genomes) {
		return toIntArray(genomes, false);
	}

    /**
     * Generates an int array from the genomes
     * @param genomes the genomes
     * @return an int array, containing all the genes
     */
    public static int[][][] toReducedIntArray(Genome[] genomes) {
        return toIntArray(genomes, true);
    }

    /**
     * Generates an int array from the genomes
     * @param genomes the genomes
     * @param useMemoryReduction if memory reduction should be applied
     * @return an int array, containing all the genes
     */
    private static int[][][] toIntArray(Genome[] genomes, boolean useMemoryReduction) {
        int genomeArray[][][] = new int[genomes.length][][];
        for (int i=0;i<genomes.length;i++) {
            genomeArray[i] = new int[genomes[i].getChromosomes().size()][];
            for (int j=0;j<genomeArray[i].length;j++)
                genomeArray[i][j] = useMemoryReduction?genomes[i].getChromosomes().get(j).toReducedIntArray(true):genomes[i].getChromosomes().get(j).toIntArray(true, true);
        }
        return genomeArray;
    }

    /**
     * Print the int array generated by Genome.toIntArray
     * @param genomes the genomes
     */
    public static void printIntArray(int[][][] genomes) {
        for (int[][] genome : genomes) {
            StringBuilder builder = new StringBuilder();
            for (int[] chromosome : genome)
                builder.append(Arrays.toString(chromosome));
            System.out.println(builder.toString());
        }
    }
	
	public String toString() {
		if (getChromosomes().size()>1)
				return getName() + " [and more...]";
		else
			return getName();
	}

    public static int getMaxNameLength(Genome[] genomes) {
        int maxLength = -1;
        for (Genome g : genomes)
            for (Chromosome chr : g.chromosomes)
                for (Gene gene : chr.getGenes())
                    if (gene.getName().length() > maxLength)
                        maxLength = gene.getName().length();

        return maxLength;
    }

    public static int getMaxLocusTagLength(Genome[] genomes) {
        int maxLength = -1;
        for (Genome g : genomes)
            for (Chromosome chr : g.chromosomes)
                for (Gene gene : chr.getGenes())
                    if (gene.getTag().length() > maxLength)
                        maxLength = gene.getTag().length();

        return maxLength;
    }

    /**
     * Print statistics of gene family sizes for all genomes
     * @param genomes The genomes
     * @param alphabetSize The size of the alphabet
     */
    private static void printGenomeStatistics(Genome[] genomes, int alphabetSize){
        printGenomeStatistics(genomes, alphabetSize, -1, -1);
    }

    /**
     * Print statistics of gene family sizes for all genomes with genomeSize +/- genomeSizeDelta genes.
     * @param genomes The genomes
     * @param alphabetSize The size of the alphabet
     * @param genomeSize  The number of genes that is needed for a genome to be reported. -1 will report statistics for all genomes.
     * @param genomeSizeDelta The maximum deviation form the genomeSize for a genome to be reported
     */
    private static void printGenomeStatistics(Genome[] genomes, int alphabetSize, int genomeSize, int genomeSizeDelta) {
        int[][] alphabetPerGenome = new int[genomes.length][alphabetSize + 1];
        String[][] annotations = new String[genomes.length][alphabetSize + 1];

        SortedMap<Integer,Integer> summedFamilySizes = new TreeMap<Integer, Integer>();
        int nrReportedGenomes = 0;
        List<Integer> genomeSizes = new ArrayList<Integer>();
        // Generate family sizes per genome and print it
        for (int n=0; n<genomes.length; n++){
            Genome g = genomes[n];
            if (genomeSize != -1 && (g.getTotalGeneNumber() < genomeSize-genomeSizeDelta || g.getTotalGeneNumber() > genomeSize + genomeSizeDelta))
                continue;
            else {
                nrReportedGenomes++;
                genomeSizes.add(g.getTotalGeneNumber());
            }
            for (Chromosome chr : g.getChromosomes()) {
                for (Gene gene : chr.getGenes()) {
                    alphabetPerGenome[n][Math.abs(gene.getId())]++;
                    //if (n!=0)
                    annotations[n][Math.abs(gene.getId())] = gene.getAnnotation();
                    //else
                    //annotations[n][Math.abs(gene.getId())] = String.format("%s: %s", chr.getName().substring(24), gene.getAnnotation());
                }
            }
            SortedMap<Integer,Integer> familySizes = new TreeMap<Integer, Integer>();
            for (int i=1; i<alphabetPerGenome[n].length; i++){
                // add family size for this genome
                Integer fS = familySizes.get(alphabetPerGenome[n][i]);
                if (fS != null)
                    familySizes.put(alphabetPerGenome[n][i], ++fS);
                else
                    familySizes.put(alphabetPerGenome[n][i], 1);

                // add summed family sizes
                fS = summedFamilySizes.get(alphabetPerGenome[n][i]);
                if (fS != null)
                    summedFamilySizes.put(alphabetPerGenome[n][i], ++fS);
                else
                    summedFamilySizes.put(alphabetPerGenome[n][i], 1);
            }

            Integer nonOccFamilies = familySizes.get(0);
            System.out.println(String.format("%s: %d genes, %d gene families", g.getName(), g.getTotalGeneNumber(), (nonOccFamilies == null) ? alphabetPerGenome[n].length-1 : alphabetPerGenome[n].length - 1 - nonOccFamilies));
            for (Map.Entry<Integer, Integer> entry : familySizes.entrySet()){
                System.out.println(String.format("%d\t%d", entry.getKey(), entry.getValue()));
            }
            System.out.println();
        }

        for (Integer size : genomeSizes)
            System.out.print(size + ", ");
        System.out.println("");
        System.out.println(String.format("Sum of %d genomes:", nrReportedGenomes));
        for (Map.Entry<Integer, Integer> entry : summedFamilySizes.entrySet()){
            if (entry.getKey() != 0)
                System.out.println(String.format("%d\t%d", entry.getKey(), entry.getValue()));
        }
        System.out.println();

        // generate complete family sizes and print it
        int[] alphabet = new int[alphabetSize + 1];
        for (Genome g : genomes){
            if (genomeSize != -1 && (g.getTotalGeneNumber() < genomeSize-genomeSizeDelta || g.getTotalGeneNumber() > genomeSize + genomeSizeDelta))
                continue;
            for (Chromosome chr : g.getChromosomes())
                for (Gene gene : chr.getGenes())
                    alphabet[Math.abs(gene.getId())]++;
        }
        SortedMap<Integer,Integer> familySizes = new TreeMap<Integer, Integer>();
        for (int i=1; i<alphabet.length; i++){
            Integer fS = familySizes.get(alphabet[i]);
            if (fS != null)
                familySizes.put(alphabet[i], ++fS);
            else
                familySizes.put(alphabet[i], 1);
        }

        System.out.println("Complete:");
        for (Map.Entry<Integer, Integer> entry : familySizes.entrySet()){
            System.out.println(String.format("%d\t%d", entry.getKey(), entry.getValue()));
        }

			/*
			for(int j=0; j<alphabetPerGenome.length; j++) {
				System.out.print(String.format("%s\t", genomes[j].getName()));
			}
			System.out.println("");
			for (int i=0; i<alphabet.length; i++) {
				for(int j=0; j<alphabetPerGenome.length; j++) {
					System.out.print(String.format("%d\t", alphabetPerGenome[j][i]));
				}
				System.out.println("");
			}
			for(int j=0; j<alphabetPerGenome.length; j++) {
				System.out.print(String.format("%s\t", genomes[j].getName()));
			}
			System.out.println("");
			*/
			/*
			for (int i=0; i<alphabet.length; i++) {
				if (alphabetPerGenome[0][i] == 1) {
					boolean print = false;
					StringBuilder builder = new StringBuilder();
					builder.append("\"").append(annotations[0][i]).append("\"");
					for(int j=1; j<alphabetPerGenome.length; j++) {
						if (alphabetPerGenome[j][i] == 1) {
							builder.append("\t").append("\"").append(annotations[j][i]).append("\"");
							print = true;
						} else
							builder.append("\t").append("-");
					}
					if (print)
						System.out.println(builder.toString());
				}
			}*/
    }
}
