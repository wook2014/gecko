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
import de.unijena.bioinf.gecko3.testUtils.*;
import de.unijena.bioinf.gecko3.algo.GeneClusterTestUtils.PValueComparison;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.*;
import java.util.zip.DataFormatException;

import static de.unijena.bioinf.gecko3.algo.GeneClusterTestUtils.automaticGeneClusterTestFromFile;
import static de.unijena.bioinf.gecko3.algo.GeneClusterTestUtils.compareReferenceClusters;

/**
 * The class tests the computeClusters algorithm from the Gecko3 program
 * 
 * @author Hans-Martin Haase
 * @version 0.54
 *
 */
public class ReferenceClusterTest 
{
	@Test
    public void testMemoryReductionWithMergedGenes()
    {
        // def array for computation
        int genomes[][][] = {{{0, 1, 2, -4, 3, 4, 0}}, {{0, 3, 2, -1, 1, 4, 0}}};

        Parameter p = new Parameter(1, 3, 1, Parameter.OperationMode.reference, Parameter.ReferenceType.allAgainstAll);

        // def result (using p values from calculated result)
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 1, 2, 1);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 2, 4, 0);
        List<Integer> genes1 = Arrays.asList(-1, 1, 2);
        int[] minimumDistances = new int[]{1, 0};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues = {{dLoc1_1},{dLoc1_2}};

        ExpectedReferenceClusterValues[] referenceClusterValues = {
                new ExpectedReferenceClusterValues(
                        genes1,
                        minimumDistances,
                        1,
                        0,
                        2,
                        expectedDeltaLocationValues
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
    }

    @Test
    public void testMemoryReductionClusterWithMergedGenes()
    {
        // def array for computation
        int genomes[][][] = {{{0, 1, 2, 0}}, {{0, 2, -2, 1, 0}}};

        Parameter p = new Parameter(2, 3, 1, Parameter.OperationMode.reference, Parameter.ReferenceType.allAgainstAll);

        // def result (using p values from calculated result)
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 1, 2, 2);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 1, 3, 0);
        List<Integer> genes1 = Arrays.asList(-1, -1, 1, 2);
        int[] minimumDistances = new int[]{2, 0};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues = {{dLoc1_1},{dLoc1_2}};

        ExpectedReferenceClusterValues[] referenceClusterValues = {
                new ExpectedReferenceClusterValues(
                        genes1,
                        minimumDistances,
                        1,
                        0,
                        2,
                        expectedDeltaLocationValues
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
    }
	
    @Test
    public void testMemoryReduction()
    {
        // def array for computation
        int genomes[][][] = {{{0, 1, 2, -1, -1, -1, -1, 3, 4, 0}}, {{0, 3, 2, -1, 1, 4, 0}}};

        Parameter p = new Parameter(1, 3, 2, Parameter.OperationMode.reference, Parameter.ReferenceType.allAgainstAll);

        // def result (using p values from calculated result)
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 1, 2, 1);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 2, 4, 0);
        List<Integer> genes1 = Arrays.asList(-1, 1, 2);
        int[] minimumDistances = new int[]{1, 0};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues = {{dLoc1_1},{dLoc1_2}};

        ExpectedReferenceClusterValues[] referenceClusterValues = {
                new ExpectedReferenceClusterValues(
                        genes1,
                        minimumDistances,
                        1,
                        0,
                        2,
                        expectedDeltaLocationValues
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
    }

	@Test
	public void testComputeClusters1() 
	{
		// def array for computation
		int genomes[][][] = {{{0, 1, 2, 5, 3, 0}}, {{0, 1, 2, 5, 4, 0}}};
			
		// def parameters
			
		Parameter p = new Parameter(0, 3, 2, Parameter.OperationMode.reference, Parameter.ReferenceType.allAgainstAll);

        // def result (using p values from calculated result)
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 1, 3, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 1, 3, 0);
        List<Integer> genes1 = Arrays.asList(1, 2, 5);
        int[] minimumDistances = new int[]{0, 0};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues = {{dLoc1_1},{dLoc1_2}};

        ExpectedReferenceClusterValues[] referenceClusterValues = {
                new ExpectedReferenceClusterValues(
                        genes1,
                        minimumDistances,
                        0,
                        0,
                        2,
                        expectedDeltaLocationValues
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
	}

    @Test
    public void testComputeClusters1_memoryReduction()
    {
        // def array for computation
        int genomes[][][] = {{{0, 1, 2, 3, -1, 0}}, {{0, 1, 2, 3, -1, 0}}};

        // def parameters
        Parameter p = new Parameter(0, 3, 2, Parameter.OperationMode.reference, Parameter.ReferenceType.allAgainstAll);

        // def result (using p values from calculated result)
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 1, 3, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 1, 3, 0);
        List<Integer> genes1 = Arrays.asList(1, 2, 3);
        int[] minimumDistances = new int[]{0, 0};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues = {{dLoc1_1},{dLoc1_2}};

        ExpectedReferenceClusterValues[] referenceClusterValues = {
                new ExpectedReferenceClusterValues(
                        genes1,
                        minimumDistances,
                        0,
                        0,
                        2,
                        expectedDeltaLocationValues
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
    }

	@Test
	public void testComputeClusters2()
	{
		// def array for computation
		int genomes[][][] = {{{0, 1, 2, 5, 3, 0}}, {{0, 1, 2, 6, 5, 4, 0}}};

		Parameter p = new Parameter(1, 3, 2, Parameter.OperationMode.reference, Parameter.ReferenceType.allAgainstAll);

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
	public void testComputeClusters2_memoryReduction()
	{
		// def array for computation
		int genomes[][][] = {{{0, 1, 2, 3, -1, 0}}, {{0, 1, 2, -1, 3, -1, 0}}};

        Parameter p = new Parameter(1, 3, 2, Parameter.OperationMode.reference, Parameter.ReferenceType.allAgainstAll);

        // def result 1
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 1, 3, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 1, 4, 1);
        List<Integer> genes1 = Arrays.asList(1, 2, 3);
        int[] minimumDistances1 = new int[]{0, 1};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues1 = {{dLoc1_1},{dLoc1_2}};

        // def result 2
        ExpectedDeltaLocationValues dLoc2_1 = new ExpectedDeltaLocationValues(0, 1, 3, 1);
        ExpectedDeltaLocationValues dLoc2_2 = new ExpectedDeltaLocationValues(0, 1, 4, 0);
        List<Integer> genes2 = Arrays.asList(-1, 1, 2, 3);
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
		int genomes[][][] = {{{0, 1, 2, 5, 3, 0}, {0, 3, 3, 1, 2, 5, 6, 0}}, {{0, 1, 2, 5, 4, 0}}};

		Parameter p = new Parameter(0, 3, 2, Parameter.OperationMode.reference, Parameter.ReferenceType.allAgainstAll);

        // def result 1
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 1, 3, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 1, 3, 0);
        List<Integer> genes1 = Arrays.asList(1, 2, 5);
        int[] minimumDistances1 = new int[]{0, 0};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues1 = {{dLoc1_1},{dLoc1_2}};

        // def result 2
        ExpectedDeltaLocationValues dLoc2_1 = new ExpectedDeltaLocationValues(1, 3, 5, 0);
        ExpectedDeltaLocationValues dLoc2_2 = new ExpectedDeltaLocationValues(0, 1, 3, 0);
        List<Integer> genes2 = Arrays.asList(1, 2, 5);
        int[] minimumDistances2 = new int[]{0, 0};

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
                        1,
                        2,
                        expectedDeltaLocationValues2
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
	}

	@Test
	public void testComputeClusters3InvertedGenomes()
	{
		// def array for computation
		int genomes[][][] = {{{0, 1, 2, 5, 4, 0}}, {{0, 1, 2, 5, 3, 0}, {0, 3, 3, 1, 2, 5, 6, 0}}};

		Parameter p = new Parameter(0, 3, 2, Parameter.OperationMode.reference, Parameter.ReferenceType.allAgainstAll);

        // def result (using p values from calculated result)
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 1, 3, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 1, 3, 0);
        ExpectedDeltaLocationValues dLoc1_3 = new ExpectedDeltaLocationValues(1, 3, 5, 0);
        List<Integer> genes1 = Arrays.asList(1, 2, 5);
        int[] minimumDistances = new int[]{0, 0};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues = {{dLoc1_1},{dLoc1_2, dLoc1_3}};

        ExpectedReferenceClusterValues[] referenceClusterValues = {
                new ExpectedReferenceClusterValues(
                        genes1,
                        minimumDistances,
                        1,
                        0,
                        2,
                        expectedDeltaLocationValues
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
	}

	@Test
	public void testComputeClusters3WithInvertedGenomesSingleRef()
	{
		// def array for computation
		int genomes[][][] = {{{0, 1, 2, 5, 4, 0}}, {{0, 1, 2, 5, 3, 0}, {0, 3, 3, 1, 2, 5, 6, 0}}};

		Parameter p = new Parameter(0, 3, 2, Parameter.OperationMode.reference, Parameter.ReferenceType.genome);

        // def result (using p values from calculated result)
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 1, 3, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 1, 3, 0);
        ExpectedDeltaLocationValues dLoc1_3 = new ExpectedDeltaLocationValues(1, 3, 5, 0);
        List<Integer> genes1 = Arrays.asList(1, 2, 5);
        int[] minimumDistances = new int[]{0, 0};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues = {{dLoc1_1},{dLoc1_2, dLoc1_3}};

        ExpectedReferenceClusterValues[] referenceClusterValues = {
                new ExpectedReferenceClusterValues(
                        genes1,
                        minimumDistances,
                        0,
                        0,
                        2,
                        expectedDeltaLocationValues
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
	}

	@Test
	public void testComputeClusters4()
	{
		// def array for computation
		int genomes[][][] = {{{0, 1, 2, 5, 3, 0}, {0, 3, 8, 1, 2, 5, 6, 0}}, {{0, 9, 1, 2, 7, 5, 4, 0}}};

		Parameter p = new Parameter(1, 3, 2, Parameter.OperationMode.reference, Parameter.ReferenceType.allAgainstAll);

        // def result 1
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 1, 3, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 2, 5, 1);
        List<Integer> genes1 = Arrays.asList(1, 2, 5);
        int[] minimumDistances1 = new int[]{0, 1};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues1 = {{dLoc1_1},{dLoc1_2}};

        // def result 2
        ExpectedDeltaLocationValues dLoc2_1 = new ExpectedDeltaLocationValues(1, 3, 5, 0);
        ExpectedDeltaLocationValues dLoc2_2 = new ExpectedDeltaLocationValues(0, 2, 5, 1);
        List<Integer> genes2 = Arrays.asList(1, 2, 5);
        int[] minimumDistances2 = new int[]{0, 1};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues2 = {{dLoc2_1},{dLoc2_2}};

        // def result 3
        ExpectedDeltaLocationValues dLoc3_1 = new ExpectedDeltaLocationValues(0, 1, 3, 1);
        ExpectedDeltaLocationValues dLoc3_2 = new ExpectedDeltaLocationValues(1, 3, 5, 1);
        ExpectedDeltaLocationValues dLoc3_3 = new ExpectedDeltaLocationValues(0, 2, 5, 0);
        List<Integer> genes3 = Arrays.asList(1, 2, 5, 7);
        int[] minimumDistances3 = new int[]{1, 0};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues3 = {{dLoc3_1, dLoc3_2},{dLoc3_3}};

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
                        1,
                        2,
                        expectedDeltaLocationValues2),
                new ExpectedReferenceClusterValues(
                        genes3,
                        minimumDistances3,
                        1,
                        0,
                        2,
                        expectedDeltaLocationValues3
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
	}

    /**
     * Test the update of L_prime in case c_old is a new gap in the interval
     */
    @Test
    public void testComputeClustersL_PrimeUpdate()
    {
        // def array for computation
        int genomes[][][] = {{{0, 1, 2, 5, 3, 0}}, {{0, 4, 6, 7, 2, 1, 1, 5, 0}}};

        Parameter p = new Parameter(1, 3, 2, Parameter.OperationMode.reference, Parameter.ReferenceType.genome);

        // def result 1
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 1, 3, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 4, 7, 0);
        List<Integer> genes1 = Arrays.asList(1, 2, 5);
        int[] minimumDistances1 = new int[]{0, 0};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues1 = {{dLoc1_1},{dLoc1_2}};

        ExpectedReferenceClusterValues[] referenceClusterValues = {
                new ExpectedReferenceClusterValues(
                        genes1,
                        minimumDistances1,
                        0,
                        0,
                        2,
                        expectedDeltaLocationValues1)
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
    }

	@Test
	public void testComputeClusters5()
	{
		// def array for computation
		int genomes[][][] = {{{0, 1, 2, 5, 3, 0}, {0, 3, 8, 1, 2, 5, 6, 0}}, {{0, 9, 1, 2, 5, 4, 0}, {0,11, 10, 7, 2, 1, 5, 0}}};

		Parameter p = new Parameter(0, 3, 2, Parameter.OperationMode.reference, Parameter.ReferenceType.allAgainstAll);

        // def result 1
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 1, 3, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 2, 4, 0);
        ExpectedDeltaLocationValues dLoc1_3 = new ExpectedDeltaLocationValues(1, 4, 6, 0);
        List<Integer> genes1 = Arrays.asList(1, 2, 5);
        int[] minimumDistances1 = new int[]{0, 0};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues1 = {{dLoc1_1},{dLoc1_2, dLoc1_3}};

        // def result 2
        ExpectedDeltaLocationValues dLoc2_1 = new ExpectedDeltaLocationValues(1, 3, 5, 0);
        ExpectedDeltaLocationValues dLoc2_2 = new ExpectedDeltaLocationValues(0, 2, 4, 0);
        ExpectedDeltaLocationValues dLoc2_3 = new ExpectedDeltaLocationValues(1, 4, 6, 0);
        List<Integer> genes2 = Arrays.asList(1, 2, 5);
        int[] minimumDistances2 = new int[]{0, 0};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues2 = {{dLoc2_1},{dLoc2_2, dLoc2_3}};

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
                        1,
                        2,
                        expectedDeltaLocationValues2
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
	}

	@Test
	public void testComputeClusters6()
	{
		// def array for computationsub2, sub4
		int genomes[][][] = {{{0, 1, 2, 5, 3, 0}, {0, 3, 10, 1, 2, 5, 6, 0}}, {{0, 9, 1, 2, 8, 5, 4, 0}, {0, 7, 11, 11, 2, 1, 12, 5, 0}}};

		Parameter p = new Parameter(1, 3, 2, Parameter.OperationMode.reference, Parameter.ReferenceType.allAgainstAll);

        // def result 1
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 1, 3, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 2, 5, 1);
        ExpectedDeltaLocationValues dLoc1_3 = new ExpectedDeltaLocationValues(1, 4, 7, 1);
        List<Integer> genes1 = Arrays.asList(1, 2, 5);
        int[] minimumDistances1 = new int[]{0, 1};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues1 = {{dLoc1_1},{dLoc1_2, dLoc1_3}};

        // def result 2
        ExpectedDeltaLocationValues dLoc2_1 = new ExpectedDeltaLocationValues(1, 3, 5, 0);
        ExpectedDeltaLocationValues dLoc2_2 = new ExpectedDeltaLocationValues(0, 2, 5, 1);
        ExpectedDeltaLocationValues dLoc2_3 = new ExpectedDeltaLocationValues(1, 4, 7, 1);
        List<Integer> genes2 = Arrays.asList(1, 2, 5);
        int[] minimumDistances2 = new int[]{0, 1};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues2 = {{dLoc2_1},{dLoc2_2, dLoc2_3}};

        // def result 3
        ExpectedDeltaLocationValues dLoc3_1 = new ExpectedDeltaLocationValues(0, 1, 3, 1);
        ExpectedDeltaLocationValues dLoc3_2 = new ExpectedDeltaLocationValues(1, 3, 5, 1);
        ExpectedDeltaLocationValues dLoc3_3 = new ExpectedDeltaLocationValues(0, 2, 5, 0);
        List<Integer> genes3 = Arrays.asList(1, 2, 5, 8);
        int[] minimumDistances3 = new int[]{1, 0};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues3 = {{dLoc3_1, dLoc3_2},{dLoc3_3}};

        // def result 3
        ExpectedDeltaLocationValues dLoc4_1 = new ExpectedDeltaLocationValues(0, 1, 3, 1);
        ExpectedDeltaLocationValues dLoc4_2 = new ExpectedDeltaLocationValues(1, 3, 5, 1);
        ExpectedDeltaLocationValues dLoc4_3 = new ExpectedDeltaLocationValues(1, 4, 7, 0);
        List<Integer> genes4 = Arrays.asList(1, 2, 5, 12);
        int[] minimumDistances4 = new int[]{1, 0};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues4 = {{dLoc4_1, dLoc4_2},{dLoc4_3}};

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
                        1,
                        2,
                        expectedDeltaLocationValues2),
                new ExpectedReferenceClusterValues(
                        genes3,
                        minimumDistances3,
                        1,
                        0,
                        2,
                        expectedDeltaLocationValues3),
                new ExpectedReferenceClusterValues(
                        genes4,
                        minimumDistances4,
                        1,
                        1,
                        2,
                        expectedDeltaLocationValues4
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
	}

	@Test
	public void testComputeClusters7()
	{
		// def array for computationsub2, sub4
		int genomes[][][] = {{{0, 1, 2, 5, 3, 0}}, {{0, 9, 1, 2, 5, 4, 0}}, {{0, 8, 10, 1, 2, 5, 11, 6, 7, 0}}};

		Parameter p = new Parameter(0, 3, 3, Parameter.OperationMode.reference, Parameter.ReferenceType.allAgainstAll);

        // def result (using p values from calculated result)
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 1, 3, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 2, 4, 0);
        ExpectedDeltaLocationValues dLoc1_3 = new ExpectedDeltaLocationValues(0, 3, 5, 0);
        List<Integer> genes1 = Arrays.asList(1, 2, 5);
        int[] minimumDistances = new int[]{0, 0, 0};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues = {{dLoc1_1},{dLoc1_2}, {dLoc1_3}};

        ExpectedReferenceClusterValues[] referenceClusterValues = {
                new ExpectedReferenceClusterValues(
                        genes1,
                        minimumDistances,
                        0,
                        0,
                        3,
                        expectedDeltaLocationValues
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
	}

	@Test
	public void testComputeClusters8()
	{
		// def array for computation
		int genomes[][][] = {{{0,13, 4, 12, 1, 2, 5, 3, 0}}, {{0, 9, 1, 2, 5, 6, 4, 0}}, {{0, 8, 10, 1, 2, 7, 5, 11,0}}};

		Parameter p = new Parameter(1, 3, 3, Parameter.OperationMode.reference, Parameter.ReferenceType.allAgainstAll);

        // def result 1
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 4, 6, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 2, 4, 0);
        ExpectedDeltaLocationValues dLoc1_3 = new ExpectedDeltaLocationValues(0, 3, 6, 1);
        List<Integer> genes1 = Arrays.asList(1, 2, 5);
        int[] minimumDistances1 = new int[]{0, 0, 1};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues1 = {{dLoc1_1},{dLoc1_2}, {dLoc1_3}};

        // def result 2
        ExpectedDeltaLocationValues dLoc2_1 = new ExpectedDeltaLocationValues(0, 4, 6, 1);
        ExpectedDeltaLocationValues dLoc2_2 = new ExpectedDeltaLocationValues(0, 2, 4, 1);
        ExpectedDeltaLocationValues dLoc2_3 = new ExpectedDeltaLocationValues(0, 3, 6, 0);
        List<Integer> genes2 = Arrays.asList(1, 2, 5, 7);
        int[] minimumDistances2 = new int[]{1, 1, 0};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues2 = {{dLoc2_1},{dLoc2_2}, {dLoc2_3}};

        ExpectedReferenceClusterValues[] referenceClusterValues = {
                new ExpectedReferenceClusterValues(
                        genes1,
                        minimumDistances1,
                        1,
                        0,
                        3,
                        expectedDeltaLocationValues1),
                new ExpectedReferenceClusterValues(
                        genes2,
                        minimumDistances2,
                        2,
                        0,
                        3,
                        expectedDeltaLocationValues2
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
	}

	@Test
	public void testComputeClusters9()
	{
		// def array for computation
		int genomes[][][] = {{{0,13, 4, 12, 1, 2, 5, 3, 0}}, {{0, 9, 1, 2, 5, 6, 4, 0}}, {{0, 8, 10, 1, 2, 7, 5, 11,0}}};

		Parameter p = new Parameter(1, 3, 2, Parameter.OperationMode.reference, Parameter.ReferenceType.allAgainstAll);

        // def result 1
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 4, 6, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 2, 4, 0);
        ExpectedDeltaLocationValues dLoc1_3 = new ExpectedDeltaLocationValues(0, 3, 6, 1);
        List<Integer> genes1 = Arrays.asList(1, 2, 5);
        int[] minimumDistances1 = new int[]{0, 0, 1};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues1 = {{dLoc1_1},{dLoc1_2}, {dLoc1_3}};

        // def result 2
        ExpectedDeltaLocationValues dLoc2_1 = new ExpectedDeltaLocationValues(0, 4, 6, 1);
        ExpectedDeltaLocationValues dLoc2_2 = new ExpectedDeltaLocationValues(0, 2, 4, 1);
        ExpectedDeltaLocationValues dLoc2_3 = new ExpectedDeltaLocationValues(0, 3, 6, 0);
        List<Integer> genes2 = Arrays.asList(1, 2, 5, 7);
        int[] minimumDistances2 = new int[]{1, 1, 0};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues2 = {{dLoc2_1},{dLoc2_2}, {dLoc2_3}};

        ExpectedReferenceClusterValues[] referenceClusterValues = {
                new ExpectedReferenceClusterValues(
                        genes1,
                        minimumDistances1,
                        1,
                        0,
                        3,
                        expectedDeltaLocationValues1),
                new ExpectedReferenceClusterValues(
                        genes2,
                        minimumDistances2,
                        2,
                        0,
                        3,
                        expectedDeltaLocationValues2
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
	}

	@Test
	public void testComputeClusters10()
	{
		// def array for computation
		int genomes[][][] = {{{0,13, 4, 12, 1, 2, 5, 3, 0}}, {{0, 9, 1, 2, 5, 6, 4, 0}}, {{0, 8, 10, 1, 2, 7, 5, 11,0}}};

		Parameter p = new Parameter(0, 3, 2, Parameter.OperationMode.reference, Parameter.ReferenceType.allAgainstAll);

        // def result (using p values from calculated result)
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 4, 6, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 2, 4, 0);
        List<Integer> genes1 = Arrays.asList(1, 2, 5);
        int[] minimumDistances = new int[]{0, 0, -1};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues = {{dLoc1_1},{dLoc1_2}, {}};

        ExpectedReferenceClusterValues[] referenceClusterValues = {
                new ExpectedReferenceClusterValues(
                        genes1,
                        minimumDistances,
                        1,
                        0,
                        2,
                        expectedDeltaLocationValues
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
	}

	@Test
	public void testComputeClusters11()
	{
		// def array for computation
		int genomes[][][] = {{{0,13, 4, 12, 1, 2, 7, 5, 3, 0}}, {{0, 9, 1, 2, 5, 6, 4, 0}}, {{0, 8, 10, 1, 2, 5, 11,0}}};

		Parameter p = new Parameter(0, 3, 2, Parameter.OperationMode.reference, Parameter.ReferenceType.allAgainstAll);

        // def result (using p values from calculated result)
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 2, 4, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 3, 5, 0);
        List<Integer> genes1 = Arrays.asList(1, 2, 5);
        int[] minimumDistances = new int[]{-1, 0, 0};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues = {{},{dLoc1_1}, {dLoc1_2}};

        ExpectedReferenceClusterValues[] referenceClusterValues = {
                new ExpectedReferenceClusterValues(
                        genes1,
                        minimumDistances,
                        1,
                        0,
                        2,
                        expectedDeltaLocationValues
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
	}

	@Test
	public void testComputeClusters12()
	{
		// def array for computation
		int genomes[][][] = {{{0,13, 4, 12, 1, 2, 7, 5, 3, 0}}, {{0, 9, 1, 2, 5, 6, 4, 0}}, {{0, 8, 10, 1, 2, 5, 11,0}}};

		Parameter p = new Parameter(1, 3, 2, Parameter.OperationMode.reference, Parameter.ReferenceType.allAgainstAll);

        // def result 1
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 4, 7, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 2, 4, 1);
        ExpectedDeltaLocationValues dLoc1_3 = new ExpectedDeltaLocationValues(0, 3, 5, 1);
        List<Integer> genes1 = Arrays.asList(1, 2, 5, 7);
        int[] minimumDistances1 = new int[]{0, 1, 1};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues1 = {{dLoc1_1},{dLoc1_2}, {dLoc1_3}};

        // def result 2
        ExpectedDeltaLocationValues dLoc2_1 = new ExpectedDeltaLocationValues(0, 4, 7, 1);
        ExpectedDeltaLocationValues dLoc2_2 = new ExpectedDeltaLocationValues(0, 2, 4, 0);
        ExpectedDeltaLocationValues dLoc2_3 = new ExpectedDeltaLocationValues(0, 3, 5, 0);
        List<Integer> genes2 = Arrays.asList(1, 2, 5);
        int[] minimumDistances2 = new int[]{1, 0, 0};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues2 = {{dLoc2_1},{dLoc2_2}, {dLoc2_3}};

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
                        1,
                        0,
                        3,
                        expectedDeltaLocationValues2
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
	}

	@Test
	public void testComputeClusters13()
	{
		// def array for computation
		int genomes[][][] = {{{0, 1, 2, 0}}, {{0, 1, 2, 0}}};

		// def parameters
		Parameter p = new Parameter(1, 2, 2, Parameter.OperationMode.reference, Parameter.ReferenceType.allAgainstAll);

        // def result (using p values from calculated result)
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 1, 2, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 1, 2, 0);
        List<Integer> genes1 = Arrays.asList(1, 2);
        int[] minimumDistances = new int[]{0, 0};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues = {{dLoc1_1}, {dLoc1_2}};

        ExpectedReferenceClusterValues[] referenceClusterValues = {
                new ExpectedReferenceClusterValues(
                        genes1,
                        minimumDistances,
                        0,
                        0,
                        2,
                        expectedDeltaLocationValues
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
	}

	@Test
	public void testComputeClusters14()
	{
		// def array for computation
		int genomes[][][] = {{{0, 1, 2, 3, 0}}, {{0, 1, 2, 0}}, {{0, 2, 3, 0}}};

		// def parameters
		Parameter p = new Parameter(1, 3, 3, Parameter.OperationMode.reference, Parameter.ReferenceType.allAgainstAll);

        // def result 1
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 1, 3, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 1, 2, 1);
        ExpectedDeltaLocationValues dLoc1_3 = new ExpectedDeltaLocationValues(0, 1, 2, 1);
        List<Integer> genes1 = Arrays.asList(1, 2, 3);
        int[] minimumDistances1 = new int[]{0, 1, 1};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues1 = {{dLoc1_1},{dLoc1_2},{dLoc1_3}};

        ExpectedReferenceClusterValues[] referenceClusterValues = {
                new ExpectedReferenceClusterValues(
                        genes1,
                        minimumDistances1,
                        0,
                        0,
                        3,
                        expectedDeltaLocationValues1)};

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
	}

	/**
	 *
	 * Parameter set:
	 * 		genomes: 2 (one chromosome)
	 * 		cluster size: 3
	 * 		delta: 0
	 * 		operation mode: r
	 * 		refType: d
	 * 		qtype: QUORUM_NO_COST
	 * 		q (number of genomes where cluster appears): 2
	 * 		contigSpanning: false
	 *
	 */
	@Test
	public void testComputeClustersRefInRef()
	{
		// def array for computation
		int genomes[][][] = {{{0, 1, 2, 5, 1, 2, 0}}, {{0, 3, 4, 5, 4, 0}}};

		// def parameters
		Parameter p = new Parameter(0, 2, 1, Parameter.OperationMode.reference, Parameter.ReferenceType.allAgainstAll, true, false);

        // def result (using p values from calculated result)
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 1, 2, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 4, 5, 0);
        List<Integer> genes1 = Arrays.asList(1, 2);
        int[] minimumDistances = new int[]{0, -1};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues = {{dLoc1_1, dLoc1_2}, {}};

        ExpectedReferenceClusterValues[] referenceClusterValues = {
                new ExpectedReferenceClusterValues(
                        genes1,
                        minimumDistances,
                        0,
                        0,
                        1,
                        expectedDeltaLocationValues
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
	}

	@Test
	public void testComputeClustersRefInRefWithErrors()
	{
		// def array for computation
		int genomes[][][] = {{{0, 1, 3, 2, 5, 1, 2, 0}}, {{0, 3, 4, 5, 4, 0}}};

		// def parameters
		Parameter p = new Parameter(1, 3, 1, Parameter.OperationMode.reference, Parameter.ReferenceType.allAgainstAll, true, false);

        // def result (using p values from calculated result)
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 1, 3, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 5, 6, 1);
        List<Integer> genes1 = Arrays.asList(1, 2, 3);
        int[] minimumDistances = new int[]{1, -1};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues = {{dLoc1_1, dLoc1_2}, {}};

        ExpectedReferenceClusterValues[] referenceClusterValues = {
                new ExpectedReferenceClusterValues(
                        genes1,
                        minimumDistances,
                        0,
                        0,
                        1,
                        expectedDeltaLocationValues
                )
        };

        GeneClusterTestUtils.performTest(p, genomes, referenceClusterValues);
	}

    @Test
    public void memoryReductionFromFileTest() throws URISyntaxException, IOException, DataFormatException, ParseException {
        ReferenceClusterTestSettings settings = ReferenceClusterTestSettings.memoryReductionDataD2S4Q2();

        automaticGeneClusterTestFromFile(settings);
    }

    @Test
    public void memoryReductionBugFromFileTest() throws URISyntaxException, IOException, DataFormatException, ParseException {
        ReferenceClusterTestSettings settings = ReferenceClusterTestSettings.memoryReductionBugD2S5Q2();

        automaticGeneClusterTestFromFile(settings);
    }

    @Test
    public void memoryReductionWithSuboptimalOccurrenceD3S5() throws URISyntaxException, IOException, DataFormatException, ParseException {
        ReferenceClusterTestSettings settings = ReferenceClusterTestSettings.memoryReductionWithSuboptimalOccurrenceD3S5();

        automaticGeneClusterTestFromFile(settings);
    }

	@Test
	public void fiveProteobacterReferenceClusterTest() throws URISyntaxException, IOException, DataFormatException, ParseException {
        ReferenceClusterTestSettings settings = ReferenceClusterTestSettings.fiveProteobacterD3S6Q4();

		automaticGeneClusterTestFromFile(settings);
	}

	@Test
	public void fiveProteobacterReferenceClusterTestWithGrouping() throws URISyntaxException, IOException, DataFormatException, ParseException {
        ReferenceClusterTestSettings settings = ReferenceClusterTestSettings.fiveProteobacterD3S6Q2Grouping();

		automaticGeneClusterTestFromFile(settings);
	}

	@Test
	public void testQuorumParameterJava()
	{
		// def array for computationsub2, sub4
		int genomes[][][] = {{{0, 1, 2, 3, 0}}, {{0, 1, 2, 3, 0}}, {{0, 1, 3, 0}}};

		// def parameters
		Parameter maxQuorumParameters = new Parameter(0, 3, 3, Parameter.OperationMode.reference, Parameter.ReferenceType.allAgainstAll);

		// result of computation
		List<ReferenceCluster> maxQuorumResult = ReferenceClusterAlgorithm.computeReferenceClusters(genomes, maxQuorumParameters);

		Parameter noQuorumParameters = new Parameter(0, 3, 0, Parameter.OperationMode.reference, Parameter.ReferenceType.allAgainstAll);

		// result of computation
		List<ReferenceCluster> noQuorumResult = ReferenceClusterAlgorithm.computeReferenceClusters(genomes, noQuorumParameters);

		compareReferenceClusters(maxQuorumResult, noQuorumResult, PValueComparison.COMPARE_ALL);
	}

	@Test
	public void testComputeClustersWithGroupedGenomes()
	{
		// def array for computationsub2, sub4
		int genomes[][][] = {{{0, 1, 2, 3, 8 ,9, 0}}, {{0, 1, 2, 4, 6, 7, 8, 9, 0}}, {{0, 1, 2, 5, 6, 7, 0}}};

		// def genome groups, grouping genomes 2 and 3
		List<Set<Integer>> genomeGroups = new ArrayList<>(2);
		Set<Integer> set1 = new HashSet<>();
		set1.add(0);
		genomeGroups.add(set1);
		Set<Integer> set2 = new HashSet<>();
		set2.add(1);
		set2.add(2);
		genomeGroups.add(set2);

		// def parameters
		Parameter p = new Parameter(0, 2, 2, Parameter.OperationMode.reference, Parameter.ReferenceType.allAgainstAll);

        // def result 1
        ExpectedDeltaLocationValues dLoc1_1 = new ExpectedDeltaLocationValues(0, 1, 2, 0);
        ExpectedDeltaLocationValues dLoc1_2 = new ExpectedDeltaLocationValues(0, 1, 2, 0);
        ExpectedDeltaLocationValues dLoc1_3 = new ExpectedDeltaLocationValues(0, 1, 2, 0);
        List<Integer> genes1 = Arrays.asList(1, 2);
        int[] minimumDistances1 = new int[]{0, 0, 0};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues1 = {{dLoc1_1},{dLoc1_2},{dLoc1_3}};

        // def result 2
        ExpectedDeltaLocationValues dLoc2_1 = new ExpectedDeltaLocationValues(0, 4, 5, 0);
        ExpectedDeltaLocationValues dLoc2_2 = new ExpectedDeltaLocationValues(0, 6, 7, 0);
        List<Integer> genes2 = Arrays.asList(8, 9);
        int[] minimumDistances2 = new int[]{0, 0, -1};

        ExpectedDeltaLocationValues[][] expectedDeltaLocationValues2 = {{dLoc2_1},{dLoc2_2}, {}};

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

        GeneClusterTestUtils.performTest(p, genomes, genomeGroups, referenceClusterValues);
	}
}

