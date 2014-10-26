/**
 * Copyright (c) 2008-2014, Dr. Garbage Community
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.drgarbage.algorithms;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.drgarbage.controlflowgraph.intf.GraphExtentionFactory;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;

/**
 * Test class for {@link com.drgarbage.algorithms.MaxWeightedBipartiteMatching}
 * 
 * @author Artem Garishin, Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class MaxWeightedBipartiteMatchingTest extends TestCase{

	protected boolean DEBUG = false;
	
	/**
	 * Test set consists of a bipartite graph and two partitions.
	 */
	class TestSet{
		 IDirectedGraphExt graph = GraphExtentionFactory.createDirectedGraphExtention();
		 List<INodeExt> partA = new ArrayList<INodeExt>(); 
		 List<INodeExt> partB = new ArrayList<INodeExt>();
	}
	
	/**
	 * The test set <br>
	 *  
	 * @param weights the weights to be assigned to the edges
	 * @return the test set
	 */
	private TestSet createTestSet(int [][] weights){

		TestSet t = new TestSet();
		
			for(int i = 0; i < weights.length; i++){
				INodeExt a1 = GraphExtentionFactory.createNodeExtention("a" + i);
				t.graph.getNodeList().add(a1);
		        t.partA.add(a1);
		        
	
		        INodeExt b1 = GraphExtentionFactory.createNodeExtention("b" + i);
		        t.graph.getNodeList().add(b1);
		        t.partB.add(b1);
			}
			
			for(int i = 0; i < weights.length; i++){
				for(int j = 0; j < weights.length; j++){
					/* ignore zero values */
					if(weights[i][j] != 0){
						INodeExt a = t.partA.get(i);
						INodeExt b = t.partB.get(j);
						IEdgeExt edge = GraphExtentionFactory.createEdgeExtention(a, b);
						edge.setCounter(weights[i][j]);
						t.graph.getEdgeList().add(edge);
					}
				}
			}
			
		return t;
	}
	/**
	 * Checks whether the input matrix squared
	 * @param input matrix weights
	 * @return boolean 
	 */
	private boolean isSquared(int [][] weights){
		boolean squared = true;
			for(int i = 0; i < weights.length; i++){
				if(weights.length != weights[i].length){
					squared = false;
				}
			}
		if(!squared && DEBUG){
			System.out.println("matrix is not squared");
		}	
			
		return squared;
	}

	/**
	 * The test 1. <br>
	 * The graph <code>G = (A + B, E)</code>:
	 * <pre>
	 *   a1 --- b1
	 *       /
	 *      /
	 *   a2  -- b2
	 *      \ /
	 *      / \
	 *   a3 --- b3
	 * </pre>
	 * <code>A =(a1, a2, a3)</code>, <code>B =(b1, b2, b3)</code>
	 * and <code>E =((a1 - b1), (a2 - b1), (a2 - b2), (a2 - b3), (a3 - b2), (a3 - b3))</code>
	 * <br>
	 * 
	 * The following weights are assigned to the edges:
	 * <pre>
	 * (a1 - b1)  1
	 * (a2 - b1)  5
	 * (a2 - b2)  1
	 * (a2 - b3)  1
	 * (a3 - b2)  6
	 * (a3 - b3)  1
	 * </pre>
	 * 
	 * Expected matching <code>M = (a2-b1, a3-b2)</code> 
	 * with |M|=2 and W = 5 + 6 = 11:
	 * <pre>
	 *   a1     b1
	 *       /
	 *      /
	 *   a2     b2
	 *       /
	 *      / 
	 *   a3     b3
	 * </pre>
	 * 
	 * ====== <br>
	 * 
	 * The steps of algorithm:
	 * <ol>
	 * 	<li> <b>Create a matrix</b><br> 
	 * 	Ensure that the matrix is square by the addition of dummy rows/columns if necessary.
	 * 	Assign zero value to the missing edges.
	 * 	<pre>
	 *  	b1  b2  b3
	 *  a1	1   0   0
	 *  a2	5   1   1
	 *  a3	0   6   1
	 *  </pre>
	 * 	</li>
	 * 
	 * <li> <b>Convert the matrix from min to max</b><br>
	 *   Multiply each value in the matrix by -1 and add the max value + 1 to each element
	 *   except the zero values.
	 * 	<pre>
	 *  	b1  b2  b3
	 *  a1	6   0   0
	 *  a2	2   6   6
	 *  a3	0   1   6
	 *  </pre>
	 * </li>
	 * 
	 * <li> <b>Reduce the matrix values</b><br>
	 * Reduce the rows by subtracting the minimum value of each row from that row.
	 *	<pre>
	 *  	b1  b2  b3
	 *  a1	6   0   0
	 *  a2	0   4   4
	 *  a3	0   1   6
	 *  </pre> 
	 *  Reduce the columns by subtracting the minimum value of each column from that column.
	 *	<pre>
	 *  	b1  b2  b3
	 *  a1	6   0   0
	 *  a2	0   4   4
	 *  a3	0   1   6
	 *  </pre> 
	 *  No changes in this example.  
	 * </li>
	 * 
	 * <li> <b>Start loop</b><br>
	 * Cover the zero elements with the minimum number of lines it is possible to cover them with.
	 * If the number of lines is equal to the number of rows then leave the loop and
	 * go to step 7
	 *	<pre>
	 *  	  b1  b2  b3
	 *  	  |
	 *  a1	--6---0---0--
	 *  	  |
	 *  a2	  0   4   4
	 *  	  |
	 *  a3	  0   1   6
	 *  	  |
	 *  </pre> 
	 * </li>
	 * 
	 * <li> 
	 *  Add the minimum uncovered element to every covered element. 
	 *  If an element is covered twice, add the minimum element to it twice.
	 *  <pre>
	 *  	b1  b2  b3
	 *  a1	8   1   1
	 *  a2	1   4   4
	 *  a3	1   1   6
	 *  </pre> 
	 * </li>
	 * <li> Subtract the minimum element from every element in the matrix.
	 * 	<pre>
	 *  	b1  b2  b3
	 *  a1	7   0   0
	 *  a2	0   3   3
	 *  a3	0   0   5
	 *  </pre> 
	 *  And now go to the beginning of the loop (step 4).
	 *  <br>
	 *  This example had to be reduced once more. 
	 *  The second iteration of the steps 4, 5 and 6 are described here:
	 *	<pre>
	 *  	  b1  b2  b3
	 *  	  |   |
	 *  a1	--7---0---0--
	 *  	  |   |
	 *  a2	  0   3   3
	 *  	  |   |
	 *  a3	  0   0   5
	 *  	  |   |
	 *  </pre>
	 * 	<pre>
	 *  	b1  b2  b3
	 *  a1	13  3   3
	 *  a2	3   6   3
	 *  a3	3   3   5
	 *  </pre>   
	 * 	<pre>
	 *  	b1  b2  b3
	 *  a1	10  0   0
	 *  a2	0   3   0
	 *  a3	0   0   2
	 *  </pre> 
	 *	<pre>
	 *  	b1  b2  b3
	 *  	|   |   |
	 *  a1	10  0   0
	 *  	|   |   |
	 *  a2	0   3   0
	 *  	|   |   |
	 *  a3	0   0   2
	 *  	|   |   |
	 *  </pre>
	 * </li>
	 * <li> <b>Select a matching</b><br>
	 * Select a matching by choosing a set of zeros so that each row or column has only one selected.
	 * 	<pre>
	 *  	b1  b2  b3
	 *  a1	10   0  (0)
	 *  a2	(0)  3   0
	 *  a3	 0  (0)  2
	 *  </pre> 
	 *  Apply the matching to the original matrix, disregarding dummy rows. 
	 * 	<pre>
	 *  	b1   b2  b3
	 *  a1	 1    0  (0)
	 *  a2	(5)   1   1
	 *  a3	 0   (6)  1
	 *  </pre>
	 *  Matching found.
	 * </li>
	 * </ol>
	 * 
	 */
	public void testMax1() {
		System.out.println("------------");
		
		int [][] weights = {
				{ 1, 0, 0 },
				{ 5, 1, 1 },
				{ 0, 6, 1 }
		};
		
		System.out.println("Input:");
		printMatrix(weights);
		
		TestSet t = createTestSet(weights);
		if(DEBUG)printGraph(t.graph);
		
		List<IEdgeExt> edges  = new MaxWeightedBipartiteMatching(DEBUG).execute(t.graph, t.partA, t.partB);
		assertEquals(2, edges.size());
		
		System.out.println("Output:");
		int weight = 0;
    	for(IEdgeExt e : edges){
    		weight += e.getCounter();
    		System.out.println(e.getSource().getData() + "-" + e.getTarget().getData() + " " + e.getCounter());
    	}
		
		assertEquals(11, weight);
		
		System.out.println("\nOK, sum = " + weight);
		System.out.println("------------");
	}
	
	/**
	 * Test
	 */
	public void testMax2() {
		System.out.println("------------");
		
		int [][] weights = {
				{ 10, 1, 3  },
				{  5, 6, 2  },
				{  1, 4, 14 }
		};
		
		System.out.println("Input:");
		printMatrix(weights);
		
		TestSet t = createTestSet(weights);
		if(DEBUG)printGraph(t.graph);
		
		List<IEdgeExt> edges  = new MaxWeightedBipartiteMatching(DEBUG).execute(t.graph, t.partA, t.partB);
		assertEquals(3, edges.size());
		
		System.out.println("Output:");
		int weight = 0;
    	for(IEdgeExt e : edges){
    		weight += e.getCounter();
    		System.out.println(e.getSource().getData() + "-" + e.getTarget().getData() + " " + e.getCounter());
    	}
		
		assertEquals(30, weight);
		
		System.out.println("\nOK, sum = " + weight);
		System.out.println("------------");
	}
	
	/**
	 * Test with high dimension
	 */
	public void testMax3() {
		System.out.println("------------");
		
		int [][] weights = {
				{ 10, 1,  3, 20 },
				{  5, 6,  2,  3 },
				{  1, 4,  0,  0 },
				{  1, 4, 14, 33 },
		};
		
		if(isSquared(weights)){
			return;
		}
		System.out.println("Input:");
		printMatrix(weights);
		
		TestSet t = createTestSet(weights);
		if(DEBUG)printGraph(t.graph);
		
		List<IEdgeExt> edges  = new MaxWeightedBipartiteMatching(DEBUG).execute(t.graph, t.partA, t.partB);
		assertEquals(3, edges.size());
		
		System.out.println("Output:");
		int weight = 0;
    	for(IEdgeExt e : edges){
    		weight += e.getCounter();
    		System.out.println(e.getSource().getData() + "-" + e.getTarget().getData() + " " + e.getCounter());
    	}
		
		assertEquals(49, weight);
		
		System.out.println("\nOK, sum = " + weight);
		System.out.println("------------");
	}
	
	/**
	 * Test if the matrix is not squared
	 */
	public void testMax4() {
		System.out.println("------------");
		
		int [][] weights = {
				{ 1, 0, 0 },
				{ 5, 1    },
				{ 0, 6, 1 }
		};
		
		/*check if matrix squared*/		
		if(isSquared(weights))
		{
			System.out.println("Input:");
			printMatrix(weights);

			TestSet t = createTestSet(weights);
			if(DEBUG)printGraph(t.graph);
				
			List<IEdgeExt> edges  = new MaxWeightedBipartiteMatching(DEBUG).execute(t.graph, t.partA, t.partB);
			assertEquals(2, edges.size());
			
			System.out.println("Output:");
			int weight = 0;
	    	for(IEdgeExt e : edges){
	    		weight += e.getCounter();
	    		System.out.println(e.getSource().getData() + "-" + e.getTarget().getData() + " " + e.getCounter());
	    	}
			
			assertEquals(11, weight);
			
			System.out.println("\nOK, sum = " + weight);
			System.out.println("------------");
		}
	}
	
	
	/**
	 *  testMax5
	 *  
	 * <pre>
	 *   a1 --- b1
	 *       /
	 *      /
	 *   a2     b2
	 *      \ /
	 *      / \
	 *   a3 --- b3
	 * </pre>
	 * 
	 * <code>A =(a1, a2, a3)</code>, <code>B =(b1, b2, b3)</code>
	 * and <code>E =((a1 - b1), (a2 - b1), (a2 - b3), (a3 - b2), (a3 - b3))</code>
	 * <br>
	 *  The following weights are assigned to the edges:
	 * <pre>
	 * (a1 - b1)  1
	 * (a2 - b1)  5
	 * (a2 - b3)  1
	 * (a3 - b2)  6
	 * (a3 - b3)  1
	 * </pre>
	 * 
	 * Expected matching <code>M = (a2-b1, a3-b2)</code> 
	 * with |M|=2 and W = 5 + 6 = 11:
	 * <pre>
	 *   a1     b1
	 *       /
	 *      /
	 *   a2     b2
	 *       /
	 *      / 
	 *   a3     b3
	 * </pre>
	 * 
	 * ====== <br>
	 * 
	 * The steps of algorithm:
	 *  <ol>
	 * 	<li> <b>Create a matrix</b><br> 
	 * 	Ensure that the matrix is square by the addition of dummy rows/columns if necessary.
	 * 	Assign zero value to the missing edges.
	 * 	<pre>
	 *  	b1  b2  b3
	 *  a1	1   0   0
	 *  a2	5   0   1
	 *  a3	0   6   1
	 *  </pre>
	 *  
	 * 	</li>
	 *<li> <b>Convert the matrix from min to max</b><br>
	 *   Multiply each value in the matrix by -1 and add the max value + 1 to each element
	 *   except the zero values.
	 * 	<pre>
	 *  	b1  b2  b3
	 *  a1	6   0   0
	 *  a2	2   0   6
	 *  a3	0   1   6
	 *  </pre>
	 * </li>
	 *  
	 * <li> <b>Reduce the matrix values</b><br>
	 * Reduce the rows by subtracting the minimum value of each row from that row.
	 *	<pre>
	   	b1  b2  b3
	 *  a1	6   0   0
	 *  a2	2   0   6
	 *  a3	0   1   6
	 *  
	 *  No changes in this example.
	 *  </pre> 
	 *  Reduce the columns by subtracting the minimum value of each column from that column.
	 *	<pre>
	  	b1  b2  b3
	 *  a1	6   0   0
	 *  a2	2   0   6
	 *  a3	0   1   6
	 *  </pre> 
	 *  No changes in this example.  
	 * </li>
	 * 
	 * <li> <b>Start loop</b><br>
	 * Cover the zero elements with the minimum number of lines it is possible to cover them with.
	 * If the number of lines is equal to the number of rows then leave the loop and
	 * go to step 7
	 *	<pre>
	 *  	  b1  b2  b3
	 *  	  |   |
	 *  a1	--6---0---0--
	 *  	  |   |
	 *  a2	  2   0   6
	 *  	  |   |
	 *  a3	  0   1   6
	 *  	  |   |
	 *  </pre> 
	 * </li>
	 *  
	 *	<pre>
	  	b1  b2  b3
	 *  a1	6   0   0
	 *  a2	2   0   6
	 *  a3	0   1   6
	 *  
	 *  </pre> 
	 *  *  Matching:
	 * <pre>
	 *  	b1  b2  b3
	 *  a1	 1   0  (0)
	 *  a2	(5)  1   1
	 *  a3	 0  (6)   1
	 *  </pre>
	 *  
	 */
	public void testMax5() {
		System.out.println("------------");
		
		int [][] weights = {
				{ 1, 0, 0 },
				{ 5, 0, 1 },
				{ 0, 6, 1 }
		};
		
		/*check if matrix squared*/		
		if(isSquared(weights))
		{
			System.out.println("Input:");
			printMatrix(weights);

			TestSet t = createTestSet(weights);
			if(DEBUG)printGraph(t.graph);
			
			List<IEdgeExt> edges  = new MaxWeightedBipartiteMatching(DEBUG).execute(t.graph, t.partA, t.partB);
			assertEquals(2, edges.size());
			
			System.out.println("Output:");
			int weight = 0;
	    	for(IEdgeExt e : edges){
	    		weight += e.getCounter();
	    		System.out.println(e.getSource().getData() + "-" + e.getTarget().getData() + " " + e.getCounter());
	    	}
			
			assertEquals(11, weight);
			
			System.out.println("\nOK, sum = " + weight);
			System.out.println("------------");
		}
	}
	/**
	 * 
	 */
	public void testValiente1() {
		System.out.println("------------");
		
		int [][] weights = {
				{ 3, 5, 3 },
				{ 4, 5, 4 },
				{ 0, 0, 0 }
		};
		
		/*check if matrix squared*/		
		if(isSquared(weights))
		{
			System.out.println("Input:");
			printMatrix(weights);

			TestSet t = createTestSet(weights);
			if(DEBUG)printGraph(t.graph);
			
			List<IEdgeExt> edges  = new MaxWeightedBipartiteMatching(DEBUG).execute(t.graph, t.partA, t.partB);
			assertEquals(2, edges.size());
			
			System.out.println("Output:");
			int weight = 0;
	    	for(IEdgeExt e : edges){
	    		weight += e.getCounter();
	    		System.out.println(e.getSource().getData() + "-" + e.getTarget().getData() + " " + e.getCounter());
	    	}
			
			assertEquals(9, weight);
			
			System.out.println("\nOK, sum = " + weight);
			System.out.println("------------");
		}
	}
	
	public void testValiente2() {
		System.out.println("------------");
		
		int [][] weights = {
				{ 1, 2},
				{ 0, 0}
		};
		
		/*check if matrix squared*/		
		if(isSquared(weights))
		{
			System.out.println("Input:");
			printMatrix(weights);

			TestSet t = createTestSet(weights);
			if(DEBUG)printGraph(t.graph);
			
			List<IEdgeExt> edges  = new MaxWeightedBipartiteMatching(DEBUG).execute(t.graph, t.partA, t.partB);
			assertEquals(1, edges.size());
			
			System.out.println("Output:");
			int weight = 0;
	    	for(IEdgeExt e : edges){
	    		weight += e.getCounter();
	    		System.out.println(e.getSource().getData() + "-" + e.getTarget().getData() + " " + e.getCounter());
	    	}
			
			assertEquals(2, weight);
			
			System.out.println("\nOK, sum = " + weight);
			System.out.println("------------");
		}
	}
	
	public void testValiente3() {
		System.out.println("------------");
		
		int [][] weights = {
				{ 1, 1},
				{ 2, 1}
		};
		
		/*check if matrix squared*/		
		if(isSquared(weights))
		{
			System.out.println("Input:");
			printMatrix(weights);

			TestSet t = createTestSet(weights);
			if(DEBUG)printGraph(t.graph);
			
			List<IEdgeExt> edges  = new MaxWeightedBipartiteMatching(DEBUG).execute(t.graph, t.partA, t.partB);
			assertEquals(2, edges.size());
			
			System.out.println("Output:");
			int weight = 0;
	    	for(IEdgeExt e : edges){
	    		weight += e.getCounter();
	    		System.out.println(e.getSource().getData() + "-" + e.getTarget().getData() + " " + e.getCounter());
	    	}
			
			assertEquals(3, weight);
			
			System.out.println("\nOK, sum = " + weight);
			System.out.println("------------");
		}
	}
	
	public void testValiente4() {
		System.out.println("------------");
		
		int [][] weights = {
				{ 1, 1, 2},
				{ 0, 0, 0},
				{ 0, 0, 0}
		};
		
		/*check if matrix squared*/		
		if(isSquared(weights))
		{
			System.out.println("Input:");
			printMatrix(weights);

			TestSet t = createTestSet(weights);
			if(DEBUG)printGraph(t.graph);
			
			List<IEdgeExt> edges  = new MaxWeightedBipartiteMatching(DEBUG).execute(t.graph, t.partA, t.partB);
			assertEquals(1, edges.size());
			
			System.out.println("Output:");
			int weight = 0;
	    	for(IEdgeExt e : edges){
	    		weight += e.getCounter();
	    		System.out.println(e.getSource().getData() + "-" + e.getTarget().getData() + " " + e.getCounter());
	    	}
			
			assertEquals(2, weight);
			
			System.out.println("\nOK, sum = " + weight);
			System.out.println("------------");
		}
	}
	
	public void testValiente5() {
		System.out.println("------------");
		
		int [][] weights = {
				{ 1, 1, 2 },
				{ 1, 1, 1 },
				{ 0, 0, 0 }
		};
		
		/*check if matrix squared*/		
		if(isSquared(weights))
		{
			System.out.println("Input:");
			printMatrix(weights);

			TestSet t = createTestSet(weights);
			if(DEBUG)printGraph(t.graph);
			
			List<IEdgeExt> edges  = new MaxWeightedBipartiteMatching(DEBUG).execute(t.graph, t.partA, t.partB);
			assertEquals(2, edges.size());
			
			System.out.println("Output:");
			int weight = 0;
	    	for(IEdgeExt e : edges){
	    		weight += e.getCounter();
	    		System.out.println(e.getSource().getData() + "-" + e.getTarget().getData() + " " + e.getCounter());
	    	}
			
			assertEquals(3, weight);
			
			System.out.println("\nOK, sum = " + weight);
			System.out.println("------------");
		}
	}
	
	public void testFromUser() {
		System.out.println("------------");
		
		int [][] weights = new int[][]{
	            {6,14,4,1,1,16,4,5,1,1,3,1,7,6,4,1,1},
	            {6,12,4,1,1,15,4,5,1,1,3,1,7,6,4,1,1},
	            {6,17,4,1,1,7,4,5,1,1,3,1,7,8,4,1,1},
	            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},       
	            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	            {0,11,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
		};
		
		/*check if matrix squared*/		
		if(isSquared(weights))
		{
			System.out.println("Input:");
			//printMatrix(weights);

			TestSet t = createTestSet(weights);
			if(DEBUG)printGraph(t.graph);
			
			List<IEdgeExt> edges  = new MaxWeightedBipartiteMatching(DEBUG).execute(t.graph, t.partA, t.partB);
			//assertEquals(16, edges.size());
			
			System.out.println("Output:");
			int weight = 0;
	    	for(IEdgeExt e : edges){
	    		weight += e.getCounter();
	    		System.out.println(e.getSource().getData() + "-" + e.getTarget().getData() + ": " + e.getCounter());
	    	}
			
	    	//
			//assertEquals(29, weight);
			
			System.out.println("\nOK, sum = " + weight);
			System.out.println("------------");
		}
		
		
	}
	
	
	public void testFromUser2() {
		int [][] weights2 = {
				{ 1, 1, 2, 0 },
				{ 1, 1, 1, 1 },
				{ 0, 0, 0, 3 }
		};
		TestSet t = createTestSet(weights2);
		MaxWeightedBipartiteMatching m = new MaxWeightedBipartiteMatching();
		List<IEdgeExt> edges  = new MaxWeightedBipartiteMatching(DEBUG).execute(t.graph, t.partA, t.partB);
		
		List<INodeExt> partAnew = new ArrayList<INodeExt>();
		List<INodeExt> partBnew = new ArrayList<INodeExt>();
		//m.createSymetricalCompleteBipartiteGraph(t1.partA, partAnew, t1.partB, partBnew);
		
	}
	
	/**
	 * For debugging purposes only.
	 * @param matrix the matrix
	 */
	private void printMatrix(int[][] matrix){
		StringBuffer buf = new StringBuffer();
		
		buf.append("   ");
		for(int i = 0; i < matrix.length; i++){
			buf.append('a');
			buf.append(i);
			buf.append('\t');
		}
		buf.append('\n');
		
		for(int i = 0; i < matrix.length; i++){
			buf.append('b');
			buf.append(i);
			buf.append(' ');
			for(int j = 0; j < matrix.length; j++){
				buf.append(matrix[i][j]);
				buf.append('\t');
			}
			buf.append('\n');
		}
		
		System.out.println(buf.toString());
	}

	/**
	 * Prints the graph.
	 * @param g the graph
	 */
	private void printGraph(IDirectedGraphExt g) {
		if(!DEBUG){
			return;
		}
		
		System.out.println("Print Graph:");

		System.out.println("Nodes:");
		for (int i = 0; i < g.getNodeList().size(); i++) {
			System.out.println("  " + g.getNodeList().getNodeExt(i).getData());
		}

		System.out.println("Edges:");
		for (int i = 0; i < g.getEdgeList().size(); i++) {
			IEdgeExt e = g.getEdgeList().getEdgeExt(i);
			System.out.println("  " 
					+ e.getSource().getData() 
					+ " - "
					+ e.getTarget().getData());
		}
	}
	


	
}
