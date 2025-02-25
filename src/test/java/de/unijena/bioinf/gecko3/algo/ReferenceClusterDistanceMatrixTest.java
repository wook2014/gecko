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

import de.unijena.bioinf.gecko3.datastructures.Parameter;
import de.unijena.bioinf.gecko3.testUtils.ExpectedDeltaLocationValues;
import de.unijena.bioinf.gecko3.testUtils.ExpectedReferenceClusterValues;
import de.unijena.bioinf.gecko3.algo.GeneClusterTestUtils.PValueComparison;
import de.unijena.bioinf.gecko3.testUtils.ReferenceClusterTestSettings;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;

import static de.unijena.bioinf.gecko3.algo.GeneClusterTestUtils.automaticGeneClusterTestFromFile;

public class ReferenceClusterDistanceMatrixTest {
	
	@Test
	public void testComputeClustersConstMatrix() 
	{
		// def array for computation
		int genomes[][][] = {{{0, 1, 2, 5, 3, 0}}, {{0, 1, 2, 6, 5, 4, 0}}};
			
		// def parameters
		int[][] distanceMatrix = {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}};
		Parameter p = new Parameter(distanceMatrix, 3, 2, Parameter.OperationMode.reference, Parameter.ReferenceType.allAgainstAll);

        // def result 1
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 1, 3, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 1, 4, 1);
        List<Integer> genes1 = Arrays.asList(1, 2, 5);
        int[] minimumDistances1 = new int[]{0, 1};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues1 = {{dLoc1_1},{dLoc1_2}};

        // def result 2
        ExpectedDeltaLocationValues dLoc2_1 = new ExpectedDeltaLocationValues(0, 1, 3, 1);
        ExpectedDeltaLocationValues dLoc2_2 = new ExpectedDeltaLocationValues(0, 1, 4, 0);
        List<Integer> genes2 = Arrays.asList(1, 2, 5, 6);
        int[] minimumDistances2 = new int[]{1, 0};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues2 = {{dLoc2_1},{dLoc2_2}};

        ExpectedReferenceClusterValues[] referenceClusterValues = {
                new ExpectedReferenceClusterValues(
                        genes1,
                        minimumDistances1,
                        0,
                        0,
                        2,
                        expectedDeltaLocationValues1),
                new ExpectedReferenceClusterValues(
                        genes2,
                        minimumDistances2,
                        1,
                        0,
                        2,
                        expectedDeltaLocationValues2
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
	}
	
	@Test
	public void testComputeClusters3() 
	{
		// def array for computation
		int genomes[][][] = {{{0, 1, 2, 5, 4 ,3 , 0}}, {{0, 1, 2, 6, 5, 4, 0}}, {{0, 1, 2, 7, 5, 8, 3, 4, 0}}};
		
		int[][] distanceMatrix = {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {2, 2, 2}};
		Parameter p = new Parameter(distanceMatrix, 4, 2, Parameter.OperationMode.reference, Parameter.ReferenceType.genome);

        // def result 1
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 1, 4, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 1, 5, 1);
        List<Integer> genes1 = Arrays.asList(1, 2, 4, 5);
        int[] minimumDistances1 = new int[]{0, 1, -1};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues1 = {{dLoc1_1},{dLoc1_2},{}};

        // def result 2
        ExpectedDeltaLocationValues dLoc2_1 = new ExpectedDeltaLocationValues(0, 1, 5, 0);
        ExpectedDeltaLocationValues dLoc2_2 = new ExpectedDeltaLocationValues(0, 1, 5, 2);
        ExpectedDeltaLocationValues dLoc2_3 = new ExpectedDeltaLocationValues(0, 1, 7, 2);
        List<Integer> genes2 = Arrays.asList(1, 2, 3, 4, 5);
        int[] minimumDistances2 = new int[]{0, 2, 2};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues2 = {{dLoc2_1},{dLoc2_2},{dLoc2_3}};

        ExpectedReferenceClusterValues[] referenceClusterValues = {
                new ExpectedReferenceClusterValues(
                        genes1,
                        minimumDistances1,
                        0,
                        0,
                        2,
                        expectedDeltaLocationValues1),
                new ExpectedReferenceClusterValues(
                        genes2,
                        minimumDistances2,
                        0,
                        0,
                        3,
                        expectedDeltaLocationValues2
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
	}

    @Test
    public void testComputeClustersBugInR_prime()
    {
        // def array for computation
        int genomes[][][] = {{{0, 1, 2, 5 , 4, 6, 3 , 8, 0}}, {{0, 3, 2, 1, 9, 4, 7, 5, 8, 0}}};

        // def parameters
        int[][] distanceMatrix = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {3, 3, 3}};
        Parameter p = new Parameter(distanceMatrix, 2, 2, Parameter.OperationMode.reference, Parameter.ReferenceType.genome);

        // def result 1
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 1, 2, 0);
        ExpectedDeltaLocationValues dLoc1_3 = new ExpectedDeltaLocationValues(0, 2, 3, 0);
        List<Integer> genes1 = Arrays.asList(1, 2);
        int[] minimumDistances1 = new int[]{0, 0};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues1 = {{dLoc1_1},{dLoc1_3}};

        // def result 2
        ExpectedDeltaLocationValues dLoc2_1 = new ExpectedDeltaLocationValues(0, 1, 7, 0);
        ExpectedDeltaLocationValues dLoc2_2 = new ExpectedDeltaLocationValues(0, 1, 8, 3);
        List<Integer> genes2 = Arrays.asList(1, 2, 3, 4, 5, 6, 8);
        int[] minimumDistances2 = new int[]{0, 3};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues2 = {{dLoc2_1},{dLoc2_2}};

        ExpectedReferenceClusterValues[] referenceClusterValues = {
                new ExpectedReferenceClusterValues(
                        genes1,
                        minimumDistances1,
                        0,
                        0,
                        2,
                        expectedDeltaLocationValues1),
                new ExpectedReferenceClusterValues(
                        genes2,
                        minimumDistances2,
                        0,
                        0,
                        2,
                        expectedDeltaLocationValues2
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
    }

	@Test
	public void testComputeClusters4() 
	{
		// def array for computation
		int genomes[][][] = {{{0, 1, 2, 5 , 4, 11, 3 , 10, 0}}, {{0, 1, 2, 6, 7, 8, 0}}, {{0, 3, 2, 1, 9, 4, 7, 5, 10, 0}}};
			
		// def parameters
		int[][] distanceMatrix = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {3, 3, 3}};
		Parameter p = new Parameter(distanceMatrix, 2, 2, Parameter.OperationMode.reference, Parameter.ReferenceType.genome);

        // def result 1
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 1, 2, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 1, 2, 0);
        ExpectedDeltaLocationValues dLoc1_3 = new ExpectedDeltaLocationValues(0, 2, 3, 0);
        List<Integer> genes1 = Arrays.asList(1, 2);
        int[] minimumDistances1 = new int[]{0, 0, 0};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues1 = {{dLoc1_1},{dLoc1_2},{dLoc1_3}};

        // def result 2
        ExpectedDeltaLocationValues dLoc2_1 = new ExpectedDeltaLocationValues(0, 1, 7, 0);
        ExpectedDeltaLocationValues dLoc2_2 = new ExpectedDeltaLocationValues(0, 1, 8, 3);
        List<Integer> genes2 = Arrays.asList(1, 2, 3, 4, 5, 10, 11);
        int[] minimumDistances2 = new int[]{0, -1, 3};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues2 = {{dLoc2_1},{},{dLoc2_2}};

        ExpectedReferenceClusterValues[] referenceClusterValues = {
                new ExpectedReferenceClusterValues(
                        genes1,
                        minimumDistances1,
                        0,
                        0,
                        3,
                        expectedDeltaLocationValues1),
                new ExpectedReferenceClusterValues(
                        genes2,
                        minimumDistances2,
                        0,
                        0,
                        2,
                        expectedDeltaLocationValues2
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
	}
	
	@Test
	public void testComputeClustersNoDeletions() 
	{
		// def array for computation
		int genomes[][][] = {{{0, 1, 2, 3, 0}}, {{0, 1, 2, 4, 3, 0}}, {{0, 1, 3, 0}}};
			
		// def parameters
		int[][] distanceMatrix = {{1, 0, 1}, {1, 0, 1}, {1, 0, 1}, {1, 0, 1}};
		Parameter p = new Parameter(distanceMatrix, 3, 2, Parameter.OperationMode.reference, Parameter.ReferenceType.genome);

        // def result 1
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 1, 3, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 1, 4, 1);
        List<Integer> genes1 = Arrays.asList(1, 2, 3);
        int[] minimumDistances1 = new int[]{0, 1, -1};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues1 = {{dLoc1_1},{dLoc1_2},{}};

        ExpectedReferenceClusterValues[] referenceClusterValues = {
                new ExpectedReferenceClusterValues(
                        genes1,
                        minimumDistances1,
                        0,
                        0,
                        2,
                        expectedDeltaLocationValues1
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
	}
	
	@Test
	public void testComputeClustersNoInsertions() 
	{
		// def array for computation
		int genomes[][][] = {{{0, 1, 2, 3, 4, 0}}, {{0, 1, 2, 5, 3, 0}}, {{0, 1, 3, 4, 0}}};
			
		// def parameters
		int[][] distanceMatrix = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 1, 1}};
		Parameter p = new Parameter(distanceMatrix, 3, 2, Parameter.OperationMode.reference, Parameter.ReferenceType.genome);

        // def result 1
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 1, 4, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 1, 3, 1);
        List<Integer> genes1 = Arrays.asList(1, 2, 3, 4);
        int[] minimumDistances1 = new int[]{0, -1, 1};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues1 = {{dLoc1_1},{},{dLoc1_2}};

        ExpectedReferenceClusterValues[] referenceClusterValues = {
                new ExpectedReferenceClusterValues(
                        genes1,
                        minimumDistances1,
                        0,
                        0,
                        2,
                        expectedDeltaLocationValues1
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
	}
	
	@Test
	public void testComputeClustersHigherTotalDist() 
	{
		// def array for computation
		int genomes[][][] = {{{0, 1, 2, 3, 0}}, {{0, 1, 4, 3, 0}}, {{0, 1, 4, 5, 3, 0}}};
			
		// def parameters
		int[][] distanceMatrix = {{1, 1, 2}, {1, 1, 2}, {1, 1, 2}, {1, 1, 2}};
		Parameter p = new Parameter(distanceMatrix, 3, 2, Parameter.OperationMode.reference, Parameter.ReferenceType.genome);

        // def result 1
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 1, 3, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 1, 3, 2);
        List<Integer> genes1 = Arrays.asList(1, 2, 3);
        int[] minimumDistances1 = new int[]{0, 2, -1};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues1 = {{dLoc1_1},{dLoc1_2},{}};

        ExpectedReferenceClusterValues[] referenceClusterValues = {
                new ExpectedReferenceClusterValues(
                        genes1,
                        minimumDistances1,
                        0,
                        0,
                        2,
                        expectedDeltaLocationValues1
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
	}
	
	/**
	 * For the given genomes and parameters, delta table and d=5, s=8 should give equal results
	 */
	@Test
	public void testComputeClustersD5S8_DeltaTable() 
	{
		// def array for computation
		int genomes[][][] = {{{0, 1, 2, 3, 4, 5, 6, 7 , 8, 0}},
				{{0, 4, 5, 6, 7, 8, 0}},
				{{0, 7, 6, 5, 4, 0}},
				{{0, 7, 6, 5, 4, 9, 1, 0}},
				{{0, 4, 5, 8, 0}},
				{{0, 4, 5, 1, 0}},
				{{0, 7, 6, 5, 4, 10, 1, 0}}};
		
		// def parameters
		Parameter p = new Parameter(5, 8, genomes.length-1, Parameter.OperationMode.reference, Parameter.ReferenceType.genome);
		
		int[][] deltaTable = new int[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {1, 1, 1}, {2, 2, 2}, {3, 3, 3}, {5, 5, 5}};
		Parameter p_deltaTable = new Parameter(deltaTable, 4, genomes.length-1, Parameter.OperationMode.reference, Parameter.ReferenceType.genome);
			
		// Test the java implementation
		List<ReferenceCluster> deltaTableRes = ReferenceClusterAlgorithm.computeReferenceClusters(genomes, p_deltaTable);
        List<ReferenceCluster> res = ReferenceClusterAlgorithm.computeReferenceClusters(genomes, p);
		
		// def result (using p values from calculated result)
		
		GeneClusterTestUtils.compareReferenceClusters(deltaTableRes, res, PValueComparison.COMPARE_NONE);
	}
	
	
	
	@Test
	public void fiveProteobacterReferenceClusterWithDistanceMatrixTest() throws URISyntaxException, IOException, DataFormatException, ParseException {
        ReferenceClusterTestSettings settings = ReferenceClusterTestSettings.fiveProteobacterDeltaTable();

        automaticGeneClusterTestFromFile(settings);
	}
}
