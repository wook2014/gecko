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

import de.unijena.bioinf.gecko3.GeckoInstance;

import java.awt.*;
import java.io.Serializable;

public class Gene implements Serializable {
	private static final long serialVersionUID = 7903694077854093398L;

    public enum GeneOrientation {
        POSITIVE(1, "+"), NEGATIVE(-1, "-"), UNSIGNED(1, "");
        private final int sign;
        private final String encoding;
        GeneOrientation(int sign, String encoding){
            this.sign = sign;
            this.encoding = encoding;
        }
        public int getSign(){
            return sign;
        }
        public String getEncoding() {
            return encoding;
        }
    }

	private final String name;
	private final String tag;
	private final String annotation;
    private final GeneFamily geneFamily;
    private final GeneOrientation orientation;

    public Gene(GeneFamily geneFamily){
        this("", geneFamily, GeneOrientation.POSITIVE);
    }

	public Gene(String name, GeneFamily geneFamily, GeneOrientation orientation) {
		this(name, geneFamily, orientation, null);
	}
	
	public Gene(String name, GeneFamily geneFamily, GeneOrientation orientation, String annotation) {
        this(name, "", geneFamily, orientation, annotation);
	}
	
	public Gene(String name, String tag, GeneFamily geneFamily, GeneOrientation orientation, String annotation) {
		this.name = name;
		this.tag = tag;
		this.geneFamily = geneFamily;
		this.annotation = annotation;
        this.orientation = orientation;
	}
	
	public Gene(Gene other) {
		this.name = other.name;
		this.tag = other.tag;
		this.geneFamily = other.geneFamily;
		this.annotation = other.annotation;
        this.orientation = other.orientation;
	}

    public boolean isUnknown() {
        return geneFamily.isSingleGeneFamily();
    }

    public int getFamilySize() {
        return geneFamily.getFamilySize();
    }

    public GeneFamily getGeneFamily() {
        return geneFamily;
    }

    /**
     * Returns the external id from the input file
     * @return
     */
    public String getExternalId(){
        return geneFamily.getExternalId();
    }

    public int getAlgorithmId() {
        return getGeneFamily().getAlgorithmId();
    }

    public Color getGeneColor() {
        return GeckoInstance.getInstance().getGeneColor(geneFamily);
    }
	
	public String getName() {
		return name;
	}
	
	public String getTag() {
		return tag;
	}

	public String getAnnotation() {
		return annotation;
	}

    /**
     * Returns a String of all search terms of the gene, blank separated
     * @return
     */
    public String getFilterString() {
        StringBuilder builder = new StringBuilder();
        if (annotation != null && annotation != ""){
            builder.append(annotation);
            builder.append(" ");
        }
        if (name != null && name != ""){
            builder.append(name);
            builder.append(" ");
        }
        if (tag != null && tag != ""){
            builder.append(tag);
            builder.append(" ");
        }
        if (!geneFamily.getExternalId().equals(GeneFamily.UNKNOWN_GENE_ID))
            builder.append(geneFamily.getExternalId());
        return builder.toString();
    }


	public String getSummary() {
		if (annotation==null)
			if (name!=null && !name.equals("----")) {
				return name;
			} else
				return "[no annotation available]";
		if (name==null || name.equals(""))
			if (tag == null || tag.equals(""))
				return "---- - "+ annotation;
			else
				return tag+" - "+annotation;

		return name+" - "+annotation;
	}
	
	@Override
	public String toString() {
		return "["+geneFamily+","+name+","+annotation+"]";
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Gene gene = (Gene) o;

        if (!annotation.equals(gene.annotation)) return false;
        if (!geneFamily.equals(gene.geneFamily)) return false;
        if (!name.equals(gene.name)) return false;
        if (orientation != gene.orientation) return false;
        if (!tag.equals(gene.tag)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + tag.hashCode();
        result = 31 * result + annotation.hashCode();
        result = 31 * result + geneFamily.hashCode();
        result = 31 * result + orientation.hashCode();
        return result;
    }

    public GeneOrientation getOrientation() {
        return orientation;
    }
}
