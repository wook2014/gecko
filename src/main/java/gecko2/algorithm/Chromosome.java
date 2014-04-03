package gecko2.algorithm;

import gecko2.GeckoInstance;
import sun.java2d.xr.MutableInteger;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Chromosome implements Serializable {
	
	private static final long serialVersionUID = -1724388125243376854L;
	private final MouseListener chromosomeMouseListener;
	
	private final Genome parent;
	private String name;
	private List<Gene> genes;
	
	public Chromosome(String name, Genome parent) {
		this.name = name;
		this.genes = null;
		this.parent = parent;
		this.chromosomeMouseListener = new ChromosomeMouseListener();
	}
	
	public Chromosome(String name, List<Gene> genes, Genome parent) {
		this.name = name;
		this.genes = genes;
		this.parent = parent;
		this.chromosomeMouseListener = new ChromosomeMouseListener();
	}
	
	public MouseListener getChromosomeMouseListener() {
		return chromosomeMouseListener;
	}

	public void setGenes(List<Gene> genes) {
		this.genes = genes;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public List<Gene> getGenes() {
		return genes;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chromosome that = (Chromosome) o;

        if (!genes.equals(that.genes)) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + genes.hashCode();
        return result;
    }

    /**
     * Returns the int array from the list of genes.
     * @param genes the list of Gene
     * @param unHomologueGeneFamilyId the first id that is used for un-homologue genes
     * @param addZeros if the array should begin and end with 0
     * @param abs use absolute gene ids, incompatible with useReduction
     * @return the array of gene ids
     */
    private static int[] toIntArray(List<Gene> genes, MutableInteger unHomologueGeneFamilyId, boolean addZeros, boolean abs) {
        int array[] = addZeros?new int[genes.size()+2]:new int[genes.size()];
        final int offset = addZeros?1:0;

        if (addZeros) {
            array[0] = 0;
            array[array.length-1] = 0;
        }

        for (int i=0;i<genes.size();i++) {
            int family;
            if (genes.get(i).isUnknown()) {
                family = unHomologueGeneFamilyId.getValue();
                unHomologueGeneFamilyId.setValue(family + 1);
            } else {
                family = genes.get(i).getAlgorithmId();
            }
            array[i+offset] = abs ? family : family * genes.get(i).getOrientation().getSign();
        }

        return array;
    }

    /**
     * Returns the int array for this chromosome. All un-homologue genes will have id -1
     * @param addZeros if the array should begin and end with 0
     * @return the array of gene ids
     */
    public int[] toReducedIntArray(boolean addZeros) {
        int array[] = addZeros?new int[genes.size()+2]:new int[genes.size()];
        final int offset = addZeros?1:0;

        if (addZeros) {
            array[0] = 0;
            array[array.length-1] = 0;
        }

        for (int i=0;i<genes.size();i++) {
            array[i+offset] = genes.get(i).isUnknown() ? -1 : genes.get(i).getAlgorithmId();
        }
        return array;
    }

    public int[] toIntArray(MutableInteger unHomologueGeneFamilyId, boolean addZeros, boolean abs) {
        return toIntArray(genes, unHomologueGeneFamilyId, addZeros, abs);
	}

    public int[] toRandomIntArray(MutableInteger unHomologueGeneFamilyId, boolean addZeros, boolean abs) {
        List<Gene> tmp = new ArrayList<>(genes);
        Collections.shuffle(tmp);
        return toIntArray(tmp, unHomologueGeneFamilyId, addZeros, abs);
    }
	
	private Genome getParent() {
		return parent;
	}
	
	public String getFullName() {
		if (getParent().getName() == null)
			return getName();
		else if (getName() == null)
			return getParent().getName();
		else if (getParent().getName().equals(getName()))
			return getName();
		else 
			return getParent().getName() + " " + getName();
	}
	
	private class ChromosomeMouseListener extends MouseAdapter implements Serializable {
		
		private static final long serialVersionUID = -1016912396598206487L;

		@Override
		public void mouseEntered(MouseEvent e) {
			
			String infotext = "";
					
			if (Chromosome.this.getChromosomeMouseListener().equals(Chromosome.this.chromosomeMouseListener)) {
					
				infotext = getFullName();
			}
			
			GeckoInstance.getInstance().getGui().setInfobarText(infotext);
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			super.mouseExited(e);
			GeckoInstance.getInstance().getGui().setInfobarText("");
		}
	}
}
