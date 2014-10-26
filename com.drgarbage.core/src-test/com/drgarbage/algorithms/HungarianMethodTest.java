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
 * Test class for {@link com.drgarbage.algorithms.HungarianMethod}
 * 
 * @author Sergej Alekseev, Artem Garishin
 * @version $Revision$
 * $Id$
 */
public class HungarianMethodTest extends TestCase {
	
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
	 * The test set 1. <br>
	 * The graph <code>G = (A + B, E)</code>:
	 * <pre>
	 *   a1 --- b1
	 *      \ /
	 *      / \
	 *   a2 --- b2
	 *      \ /
	 *      / \
	 *   a3 --- b3
	 * </pre>
	 * <code>A =(a1, a2, a3)</code>, <code>B =(b1, b2, b3)</code>
	 * and
	 * <code>E = A X B</code>
	 * <br>
	 * 
	 * The weights, assigned to the edges are represented as a matrix:
	 * 	<pre>
	 *  	b1  b2  b3
	 *  a1	10  20  20
	 *  a2	 5  10  10
	 *  a3	20   6  10
	 *  </pre>
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
					INodeExt a = t.partA.get(i);
					INodeExt b = t.partB.get(j);
					IEdgeExt edge = GraphExtentionFactory.createEdgeExtention(a, b);
					edge.setCounter(weights[i][j]);
					t.graph.getEdgeList().add(edge);
				}			
		}

		return t;
	}
	
	/**
	 * For debugging purposes only.
	 * @param matrix the matrix
	 */
	private void printMatrix(int[][] matrix){
		for(int i = 0; i < matrix.length; i++){
			for(int j = 0; j < matrix[i].length; j++){
				System.out.print(matrix[i][j]);
				System.out.print(' ');
			}
			System.out.println();
		}
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
	/**
	 * Input:
	 *  <pre>
	 *  	10 9 3
	 *  	 5 6 7
	 *  	 1 4 8
	 *  </pre>
	 * 
	 * Result:
	 *  <pre>
	 *  	- - 3
	 *  	- 6 -
	 *  	1 - -
	 *  </pre>
	 * 
	 * Sum 3 + 6 + 1 = 10
	 * 
	 */
	public void testExecuteHungarianMethod1() {
		System.out.println("-------------");
		int [][] weights = {
				{10, 9, 3},
				{ 5, 6, 7},
				{ 1, 4, 8}
		};
		System.out.println("Input:");
		printMatrix(weights);
		
		TestSet t = createTestSet(weights);
		
		List<IEdgeExt> edges = new HungarianMethod(DEBUG).execute(t.graph, t.partA, t.partB);
		assertEquals(3, edges.size());
		
		System.out.println("Output");
		int weight = 0;
    	for(IEdgeExt e : edges){
    		weight += e.getCounter();
    		System.out.println(e.getSource().getData() + "-" + e.getTarget().getData() + " " + e.getCounter());
    	}
		
		assertEquals(10, weight);
		
		System.out.println("OK: sum = " + weight);
		System.out.println("-------------");
	}
	
	/**
	 * Input:
	 *  <pre>
	 *  	 1 9 3
	 *  	 5 1 7
	 *  	10 4 1
	 *  </pre>
	 * 
	 * Result:
	 *  <pre>
	 *  	1 - -
	 *  	- 1 -
	 *  	- - 1
	 *  </pre>
	 * 
	 * Sum 1 + 1 + 1 = 3
	 * 
	 */
	public void testExecuteHungarianMethod2() {
		System.out.println("-------------");
		int [][] weights = {
				{ 1, 9, 3},
				{ 5, 1, 7},
				{ 10, 4, 1}
		};
		System.out.println("Input:");
		printMatrix( weights);
		
		TestSet t = createTestSet(weights);
		
		List<IEdgeExt> edges = new HungarianMethod(DEBUG).execute(t.graph, t.partA, t.partB);
		assertEquals(3, edges.size());
		
		int weight = 0;
    	for(IEdgeExt e : edges){
    		weight += e.getCounter();
    		System.out.println(e.getSource().getData() + "-" + e.getTarget().getData() + " " + e.getCounter());
    	}
		
		assertEquals(3, weight);
		
		System.out.println("OK: sum = " + weight);
		System.out.println("-------------");
	}
	
	
	/**
	 * Input:
	 *  <pre>
	 *  	10 1 3
	 *  	 5 6 2
	 *  	 1 4 8
	 *  </pre>
	 * 
	 * Result:
	 *  <pre>
	 *  	- 1 -
	 *  	- - 2
	 *  	1 - -
	 *  </pre>
	 * 
	 * Sum 1 + 2 + 1 = 4
	 * 
	 */
	public void testExecuteHungarianMethod3() {
		System.out.println("-------------");
		int [][] weights = {
				{10, 1, 3},
				{ 5, 6, 2},
				{ 1, 4, 8}
		};
		System.out.println("Input:");
		printMatrix( weights);
		
		TestSet t = createTestSet(weights);
		
		List<IEdgeExt> edges = new HungarianMethod(DEBUG).execute(t.graph, t.partA, t.partB);
		assertEquals(3, edges.size());
		
		int weight = 0;
    	for(IEdgeExt e : edges){
    		weight += e.getCounter();
    		System.out.println(e.getSource().getData() + "-" + e.getTarget().getData() + " " + e.getCounter());
    	}
		
		assertEquals(4, weight);
		
		System.out.println("OK: sum = " + weight);
		System.out.println("-------------");
	}	
	
	/**
	 * Input:
	 *  <pre>
	 *  	10 1 3
	 *  	 5 6 2
	 *  	 1 4 0
	 *  </pre>
	 * 
	 * Result:
	 *  <pre>
	 *  	- 1 -
	 *  	- - 2
	 *  	1 - -
	 *  </pre>
	 * 
	 * Sum 1 + 2 + 1 = 4
	 * 
	 */
	public void testExecuteHungarianMethod4() {
		System.out.println("-------------");
		int [][] weights = {
				{10, 1, 3},
				{ 5, 6, 2},
				{ 1, 4, 0}
		};
		System.out.println("Input:");
		printMatrix( weights);
		
		TestSet t = createTestSet(weights);
		
		List<IEdgeExt> edges = new HungarianMethod(DEBUG).execute(t.graph, t.partA, t.partB);
		assertEquals(3, edges.size());
		
		int weight = 0;
    	for(IEdgeExt e : edges){
    		weight += e.getCounter();
    		System.out.println(e.getSource().getData() + "-" + e.getTarget().getData() + " " + e.getCounter());
    	}
		
		assertEquals(4, weight);
		
		System.out.println("OK: sum = " + weight);
		System.out.println("-------------");
	}	
	
	/**
	 * Input:
	 *  <pre>
	 *  	10 19 8  15 1
	 *  	10 18 7  17 1
			13 16 9  14 1
			12 19 8  18 1
			14 17 10 19 1
	 *  </pre>
	 * 
	 * Result:
	 *  <pre>
	 *     (10)  19   8   15   1
	 *  	10   18  (7)  17   1
			13   16   9  (14)  1
			12   19   8   18  (1)
			14  (17)  10  19   1
	 *  </pre>
	 * 
	 * Sum 10 + 7 + 14 + 1 + 17 = 49
	 * 
	 */
	public void testExecuteHungarianMethod5() {
		System.out.println("-------------");
		int [][] weights = {
				{ 10, 19, 8, 15, 1},
				{ 10, 18, 7, 17, 1},
				{ 13, 16, 9, 14, 1},
				{ 12, 19, 8, 18, 1},
				{ 14, 17, 10, 19, 1}
		};
		System.out.println("Input:");
		printMatrix(weights);
		
		TestSet t = createTestSet(weights);
		printGraph(t.graph);
		
		List<IEdgeExt> edges = new HungarianMethod(DEBUG).execute(t.graph, t.partA, t.partB);
		assertEquals(5, edges.size());
		
		int weight = 0;
    	for(IEdgeExt e : edges){
    		weight += e.getCounter();
    		System.out.println(e.getSource().getData() + "-" + e.getTarget().getData() + " " + e.getCounter());
    	}
		
		assertEquals(49, weight);
		
		System.out.println("OK: sum = " + weight);
		System.out.println("-------------");
	}	
	
	/**
	 * Input:
	 *  <pre>
	 *  	10 1 3
	 *  	 5 6 0
	 *  	 1 4 0
	 *  </pre>
	 * 
	 * Result:
	 *  <pre>
	 *  	- 1 -
	 *  	- - 2
	 *  	1 - -
	 *  </pre>
	 * 
	 * Sum 1 + 2 + 1 = 4
	 * 
	 */
	public void testExecuteHungarianMethod6() {
		System.out.println("-------------");
		int [][] weights = {
				{10, 1, 3},
				{ 5, 6, 0},
				{ 1, 4, 0}
		};
		System.out.println("Input:");
		printMatrix( weights);
		
		TestSet t = createTestSet(weights);
		
		List<IEdgeExt> edges = new HungarianMethod(DEBUG).execute(t.graph, t.partA, t.partB);
		assertEquals(3, edges.size());
		
		int weight = 0;
    	for(IEdgeExt e : edges){
    		weight += e.getCounter();
    		System.out.println(e.getSource().getData() + "-" + e.getTarget().getData() + " " + e.getCounter());
    	}
		
		assertEquals(2, weight);
		
		System.out.println("OK: sum = " + weight);
		System.out.println("-------------");
	}
	
	public void testExecuteHungarianMethod7() {
		System.out.println("-------------");
		int [][] weights = new int[][]{
	            {6,14,4,1,1,16,4,5,1,1,3,1,7,6,4,1,1},
	            {6,12,4,1,1,15,4,5,1,1,3,1,7,6,4,1,1},
	            {6,13,4,1,1,7,4,5,1,1,3,1,7,8,4,1,1},
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
	            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
		};
		
		System.out.println("Input:");
		//printMatrix( weights);
		
		TestSet t = createTestSet(weights);
		List<IEdgeExt> edges = new HungarianMethod(DEBUG).execute(t.graph, t.partA, t.partB);
		
		int weight = 0;
    	for(IEdgeExt e : edges){
    		weight += e.getCounter();
    		System.out.println(e.getSource().getData() + "-" + e.getTarget().getData() + " " + e.getCounter());
    	}
    	System.out.println("OK: sum = " + weight);
	}
	
	
	
}
