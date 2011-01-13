package gecko2.algorithm;


/**
 * This class models an occurrence of a {@link GeneCluster} (which is uniquely
 * identified by the set of genes it contains).
 * 
 * @author Leon Kuchenbecker <lkuchenb@inf.fu-berlin.de>
 *
 */
public class GeneClusterOccurrence {
	
	private int id;									// A unique ID for every occurrence (unique within every cluster)
	private Subsequence[][] subsequences;			// The list of subsequences
	private double pValue;							// The pValue of this occurrence
	private int dist;								// The distance of this occurrence
	private int support;							// The number of genomes that support this occurrence
	
	public static final int GENOME_NOT_INCLUDED = -1;
	
	/**
	 * Create a new {@link GeneClusterOccurrence} object.
	 * @param id The ID of this {@link GeneClusterOccurrence} (unique within each {@link GeneCluster})
	 * @param subsequences The subsequences that describe this occurrence, where subsequence[i]
	 * contains the subsequences on genome i
	 * @param pValue The pValue of this occurrence
	 * @param totalDist The distance of this occurrence
	 */
	public GeneClusterOccurrence(int id, Subsequence[][] subsequences, double pValue, int totalDist, int support) {
		super();
		this.id = id;
		this.subsequences = subsequences;
		this.pValue = pValue;
		this.dist = totalDist;
		this.support = support;
	}
	
	/**
	 * Returns the number of genomes that support this occurrence
	 * @return the number of genomes that support this occurrence
	 */
	public int getSupport() {
		return support;
	}
	
	/**
	 * Returns the ID of this {@link GeneClusterOccurrence}.
	 * @return The ID of this {@link GeneClusterOccurrence}
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Returns the array of subsequences that describe this {@link GeneClusterOccurrence}.
	 * @return The array of subsequences that describe this {@link GeneClusterOccurrence}.
	 */
	public Subsequence[][] getSubsequences() {
		return subsequences;
	}
	
	/**
	 * Returns the pValue
	 * @return The pValue
	 */
	public double getpValue() {
		return pValue;
	}
	
	/**
	 * Returns the distance
	 * @return The distance
	 */
	public int getTotalDist() {
		return dist;
	}
		

}
