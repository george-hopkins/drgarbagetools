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
import java.util.Set;

import junit.framework.TestCase;

import com.drgarbage.controlflowgraph.intf.GraphExtentionFactory;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;

/**
 * Test class for {@link com.drgarbage.algorithms.MaxCardBipartiteMatching}
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class MaxCardBipartiteMatchingTest extends TestCase {

	/**
	 * Matching algorithm to be tested.
	 */
	MaxCardBipartiteMatching m = new MaxCardBipartiteMatching();
	
	/**
	 * Test set consists of a bipartite graph and two partitions.
	 */
	class TestSet{
		 IDirectedGraphExt graph = GraphExtentionFactory.createDirectedGraphExtention();
		 List<INodeExt> partA = new ArrayList<INodeExt>(); 
		 List<INodeExt> partB = new ArrayList<INodeExt>();
	}
	
	/**
	 * Prints the graph.
	 * @param g the graph
	 */
	private static void printGraph(IDirectedGraphExt g) {

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
	 * Prints the matched edges list.
	 * @param edges matching set
	 */
	private static void printMatchedEdges(Set<IEdgeExt> edges){
		System.out.println("Matched edges:");
		for(IEdgeExt e: edges){
			System.out.println("  " 
					+ e.getSource().getData() 
					+ " - "
					+ e.getTarget().getData());
		}
	}
	
	/**
	 * The test set 1. <br>
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
	 * <code>A =(a1, a2, a3)</code> and  <code>B =(b1, b2, b3)</code>
	 * <br>
	 * 
	 * Expected matching <code>M = (a1-b1, a2-b2, a3-b3)</code> with |M|=3:
	 * <pre>
	 *   a1 --- b1
	 *       
	 *      
	 *   a2  -- b2
	 *       
	 *       
	 *   a3 --- b3
	 * </pre>
	 * 
	 * or <code>M = (a1-b1, a2-b3, a3-b2)</code>:
	 * 
	 * <pre>
	 *   a1 --- b1
	 *       
	 *      
	 *   a2     b2
	 *      \ /
	 *      / \
	 *   a3     b3
	 * </pre>
	 * 
	 * @return the test set
	 */
	private TestSet createTestSet1(){
		TestSet t = new TestSet();

        INodeExt a1 = GraphExtentionFactory.createNodeExtention("a1");
        t.graph.getNodeList().add(a1);
        t.partA.add(a1);
        INodeExt a2 = GraphExtentionFactory.createNodeExtention("a2");
        t.graph.getNodeList().add(a2);
        t.partA.add(a2);
        INodeExt a3 = GraphExtentionFactory.createNodeExtention("a3");
        t.graph.getNodeList().add(a3);
        t.partA.add(a3);
        
        INodeExt b1 = GraphExtentionFactory.createNodeExtention("b1");
        t.graph.getNodeList().add(b1);
        t.partB.add(b1);
        INodeExt b2 = GraphExtentionFactory.createNodeExtention("b2");
        t.graph.getNodeList().add(b2);
        t.partB.add(b2);
        INodeExt b3 = GraphExtentionFactory.createNodeExtention("b3");
        t.graph.getNodeList().add(b3);
        t.partB.add(b3);
        
        IEdgeExt edge = GraphExtentionFactory.createEdgeExtention(a1, b1);
        t.graph.getEdgeList().add(edge);
		edge = GraphExtentionFactory.createEdgeExtention(a2, b1);
		t.graph.getEdgeList().add(edge);
		edge = GraphExtentionFactory.createEdgeExtention(a2, b2);
		t.graph.getEdgeList().add(edge);
		edge = GraphExtentionFactory.createEdgeExtention(a2, b3);
		t.graph.getEdgeList().add(edge);
		edge = GraphExtentionFactory.createEdgeExtention(a3, b3);
		t.graph.getEdgeList().add(edge);
	
		return t;
	}
	
	/**
	 * The test set 2. <br>
	 * The same graph like in {@link #createTestSet1()} but the order 
	 * of nodes has been changed.
	 * @return the test set
	 */
	private TestSet createTestSet2(){
		TestSet t = new TestSet();

        INodeExt a2 = GraphExtentionFactory.createNodeExtention("a2");
        t.graph.getNodeList().add(a2);
        t.partA.add(a2);
        INodeExt a3 = GraphExtentionFactory.createNodeExtention("a3");
        t.graph.getNodeList().add(a3);
        t.partA.add(a3);
        INodeExt a1 = GraphExtentionFactory.createNodeExtention("a1");
        t.graph.getNodeList().add(a1);
        t.partA.add(a1);
        
        INodeExt b1 = GraphExtentionFactory.createNodeExtention("b1");
        t.graph.getNodeList().add(b1);
        t.partB.add(b1);
        INodeExt b2 = GraphExtentionFactory.createNodeExtention("b2");
        t.graph.getNodeList().add(b2);
        t.partB.add(b2);
        INodeExt b3 = GraphExtentionFactory.createNodeExtention("b3");
        t.graph.getNodeList().add(b3);
        t.partB.add(b3);
        
        IEdgeExt edge = GraphExtentionFactory.createEdgeExtention(a1, b1);
        t.graph.getEdgeList().add(edge);
		edge = GraphExtentionFactory.createEdgeExtention(a2, b1);
		t.graph.getEdgeList().add(edge);
		edge = GraphExtentionFactory.createEdgeExtention(a2, b2);
		t.graph.getEdgeList().add(edge);
		edge = GraphExtentionFactory.createEdgeExtention(a2, b3);
		t.graph.getEdgeList().add(edge);
		edge = GraphExtentionFactory.createEdgeExtention(a3, b3);
		t.graph.getEdgeList().add(edge);
	
		return t;
	}
	
	/**
	 * The test set 3. <br>
	 * The graph <code>G = (A + B, E)</code>:
	 * <pre>
	 *   a1 --- b1
	 *       
	 *      
	 *   a2  -- b2
	 *      \ /
	 *      / \
	 *   a3 --- b3
	 * </pre>
	 * <code>A =(a1, a2, a3)</code> and  <code>B =(b1, b2, b3)</code>
	 * <br>
	 * 
	 * Expected matching <code>M = (a1-b1, a2-b2, a3-b3)</code> with |M|=3:
	 * <pre>
	 *   a1 --- b1
	 *       
	 *      
	 *   a2  -- b2
	 *       
	 *       
	 *   a3 --- b3
	 * </pre>
	 * 
	 * or <code>M = (a1-b1, a2-b3, a3-b2)</code>:
	 * 
	 * <pre>
	 *   a1 --- b1
	 *       
	 *      
	 *   a2     b2
	 *      \ /
	 *      / \
	 *   a3     b3
	 * </pre>
	 * 
	 * @return the test set
	 */
	private TestSet createTestSet3(){
		TestSet t = new TestSet();
		
        INodeExt a1 = GraphExtentionFactory.createNodeExtention("a1");
        t.graph.getNodeList().add(a1);
        t.partA.add(a1);
        INodeExt a2 = GraphExtentionFactory.createNodeExtention("a2");
        t.graph.getNodeList().add(a2);
        t.partA.add(a2);
        INodeExt a3 = GraphExtentionFactory.createNodeExtention("a3");
        t.graph.getNodeList().add(a3);
        t.partA.add(a3);
        
        INodeExt b1 = GraphExtentionFactory.createNodeExtention("b1");
        t.graph.getNodeList().add(b1);
        t.partB.add(b1);
        INodeExt b2 = GraphExtentionFactory.createNodeExtention("b2");
        t.graph.getNodeList().add(b2);
        t.partB.add(b2);
        INodeExt b3 = GraphExtentionFactory.createNodeExtention("b3");
        t.graph.getNodeList().add(b3);
        t.partB.add(b3);
        
        IEdgeExt edge = GraphExtentionFactory.createEdgeExtention(a1, b1);
        t.graph.getEdgeList().add(edge);
		edge = GraphExtentionFactory.createEdgeExtention(a2, b2);
		t.graph.getEdgeList().add(edge);
		edge = GraphExtentionFactory.createEdgeExtention(a2, b3);
		t.graph.getEdgeList().add(edge);
		edge = GraphExtentionFactory.createEdgeExtention(a3, b3);
		t.graph.getEdgeList().add(edge);
	
		return t;
	}
	
	/**
	 * The test set 4. <br>
	 * The graph <code>G = (A + B, E)</code>:
	 * <pre>
	 *   a1  -- b1
	 *      \ /
	 *      / \
	 *   a2     b2
	 * </pre>
	 * <code>A =(a1, a2)</code> and  <code>B =(b1, b2)</code>
	 * <br>
	 * 
	 * Expected matching <code>M = (a1-b1, a2-b2)</code> with |M|=2:
	 * <pre>
	 *   a1 --- b1
	 *       
	 *      
	 *   a2  -- b2
	 * </pre>
	 * 
	 * or <code>M = (a1-b2, a2-b1)</code>:
	 * 
	 * <pre>
	 *   a1     b1
	 *      \ /
	 *      / \
	 *   a2     b2
	 * </pre>
	 * 
	 * @return the test set
	 */
	private TestSet createTestSet4(){
		TestSet t = new TestSet();
		
        INodeExt a1 = GraphExtentionFactory.createNodeExtention("a1");
        t.graph.getNodeList().add(a1);
        t.partA.add(a1);
        INodeExt a2 = GraphExtentionFactory.createNodeExtention("a2");
        t.graph.getNodeList().add(a2);
        t.partA.add(a2);
        
        INodeExt b1 = GraphExtentionFactory.createNodeExtention("b1");
        t.graph.getNodeList().add(b1);
        t.partB.add(b1);
        INodeExt b2 = GraphExtentionFactory.createNodeExtention("b2");
        t.graph.getNodeList().add(b2);
        t.partB.add(b2);
        
        IEdgeExt edge = GraphExtentionFactory.createEdgeExtention(a1, b1);
        t.graph.getEdgeList().add(edge);
		edge = GraphExtentionFactory.createEdgeExtention(a1, b2);
		t.graph.getEdgeList().add(edge);
		edge = GraphExtentionFactory.createEdgeExtention(a2, b1);
		t.graph.getEdgeList().add(edge);
	
		return t;
	}
	
	/**
	 * The test set 4. <br>
	 * The graph <code>G = (A + B, E)</code>:
	 * <code>A =(a1, a2)</code> and  <code>B =(b1, b2, b3)</code>
	 * <code>E =(a1-b1, a1-b2, a1-b3, a2-b1, a2-b2, a2-b3)</code>
	 * <br>
	 * 
	 * Expected matching <code>M = (a1-b1, a2-b2)</code>,
	 * <code>M = (a1-b1, a2-b3)</code> or 
	 * <code>M = (a1-b2, a2-b1)</code> etc. 
	 * with |M|=2.
	 * 
	 * @return the test set
	 */
	private TestSet createTestSet5(){
		TestSet t = new TestSet();
		
        INodeExt a1 = GraphExtentionFactory.createNodeExtention("a1");
        t.graph.getNodeList().add(a1);
        t.partA.add(a1);
        INodeExt a2 = GraphExtentionFactory.createNodeExtention("a2");
        t.graph.getNodeList().add(a2);
        t.partA.add(a2);
        
        INodeExt b1 = GraphExtentionFactory.createNodeExtention("b1");
        t.graph.getNodeList().add(b1);
        t.partB.add(b1);
        INodeExt b2 = GraphExtentionFactory.createNodeExtention("b2");
        t.graph.getNodeList().add(b2);
        t.partB.add(b2);
        INodeExt b3 = GraphExtentionFactory.createNodeExtention("b3");
        t.graph.getNodeList().add(b3);
        t.partB.add(b3);
        
        IEdgeExt edge = GraphExtentionFactory.createEdgeExtention(a1, b1);
        t.graph.getEdgeList().add(edge);
		edge = GraphExtentionFactory.createEdgeExtention(a1, b2);
		t.graph.getEdgeList().add(edge);
		edge = GraphExtentionFactory.createEdgeExtention(a1, b3);
		t.graph.getEdgeList().add(edge);
		edge = GraphExtentionFactory.createEdgeExtention(a2, b1);
		t.graph.getEdgeList().add(edge);
		edge = GraphExtentionFactory.createEdgeExtention(a2, b2);
		t.graph.getEdgeList().add(edge);
		edge = GraphExtentionFactory.createEdgeExtention(a2, b3);
		t.graph.getEdgeList().add(edge);
	
		return t;
	}
	
	/**
	 * Test method for {@link com.drgarbage.algorithms.MaxCardBipartiteMatching}
	 * @see #createTestSet1()
	 */
	public final void testMaxCardBipartiteMatching1() {	
		TestSet t = createTestSet1();
		printGraph(t.graph);
		m.start(t.graph, t.partA, t.partB);
		printMatchedEdges(m.getMatchedEdges());
		
		assertEquals(3, m.getMatchedEdges().size());
	}
	
	/**
	 * Test method for {@link com.drgarbage.algorithms.MaxCardBipartiteMatching}
	 * @see #createTestSet2()
	 */
	public final void testMaxCardBipartiteMatching2() {		
		TestSet t = createTestSet2();
		printGraph(t.graph);
		m.start(t.graph, t.partA, t.partB);
		printMatchedEdges(m.getMatchedEdges());
		
		assertEquals(3, m.getMatchedEdges().size());
	}

	/**
	 * Test method for {@link com.drgarbage.algorithms.MaxCardBipartiteMatching}
	 * @see #createTestSet3()
	 */
	public final void testMaxCardBipartiteMatching3() {		
		TestSet t = createTestSet3();
		printGraph(t.graph);
		m.start(t.graph, t.partA, t.partB);
		printMatchedEdges(m.getMatchedEdges());
		
		assertEquals(3, m.getMatchedEdges().size());
	}
	
	/**
	 * Test method for {@link com.drgarbage.algorithms.MaxCardBipartiteMatching}
	 * @see #createTestSet4()
	 */
	public final void testMaxCardBipartiteMatching4() {		
		TestSet t = createTestSet4();
		printGraph(t.graph);
		m.start(t.graph, t.partA, t.partB);
		printMatchedEdges(m.getMatchedEdges());
		
		assertEquals(2, m.getMatchedEdges().size());
	}
	
	/**
	 * Test method for {@link com.drgarbage.algorithms.MaxCardBipartiteMatching}
	 * @see #createTestSet5()
	 */
	public final void testMaxCardBipartiteMatching5() {		
		TestSet t = createTestSet5();
		printGraph(t.graph);
		m.start(t.graph, t.partA, t.partB);
		printMatchedEdges(m.getMatchedEdges());
		
		assertEquals(2, m.getMatchedEdges().size());
	}
}
