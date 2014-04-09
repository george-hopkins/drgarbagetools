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
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.TestCase;

import com.drgarbage.algorithms.TopDownSubtreeIsomorthismTest.TestSet;
import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraph.intf.GraphExtentionFactory;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;

/**
 * Test class for {@link com.drgarbage.algorithms.TopDownMaxCommonSubTreeIsomorphism}
 * 
 * @author Artem Garishin
 * @version $Revision$
 * $Id$
 */

public class TopDownMaxCommonSubtreeIsomorphismTest extends TestCase {
	/**
	 * Test set consists of two trees.
	 */
	class TestSet{
		IDirectedGraphExt treeLeft = GraphExtentionFactory.createDirectedGraphExtention();
		IDirectedGraphExt treeRight = GraphExtentionFactory.createDirectedGraphExtention();
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
	 * Prints the map of matched tree nodes.
	 * @param map the map
	 */
	void printMap(Map<INodeExt, INodeExt> map, String test){
		System.out.println("\n"+test);
		for(Entry<INodeExt, INodeExt> entry : map.entrySet()){
			System.out.println(entry.getKey().getData()
					+ " => "
					+ entry.getValue().getData());
		}
	}
	
	/**
	 * The test set 1. <br>
	 * The trees to compare <code>T1</code> and <code>T2</code>
	 *  are defined as described in 
	 * {@link com.drgarbage.algorithms.TopDownMaxCommonSubTreeIsomorphism}.
	 * 
	 * Expected map is:
	 * <pre>
	 * v12 => w18
     * v9 => w16
	 * v10 => w14
	 * v6 => w12
	 * v5 => w11
	 * v2 => w8
	 * v7 => w15
	 * v4 => w9
	 * v11 => w17
	 * v1 => w10
	 * </pre>
	 * 
	 * @return the test set
	 */
	private TestSet createTestSet1(){
		TestSet t = new TestSet();

		/* create left tree */		
		INodeExt v1 = GraphExtentionFactory.createNodeExtention("v1");
		t.treeLeft.getNodeList().add(v1);
		INodeExt v2 = GraphExtentionFactory.createNodeExtention("v2");
		t.treeLeft.getNodeList().add(v2);
		INodeExt v3 = GraphExtentionFactory.createNodeExtention("v3");
		t.treeLeft.getNodeList().add(v3);
		INodeExt v4 = GraphExtentionFactory.createNodeExtention("v4");
		t.treeLeft.getNodeList().add(v4);
		INodeExt v5 = GraphExtentionFactory.createNodeExtention("v5");
		t.treeLeft.getNodeList().add(v5);
		INodeExt v6 = GraphExtentionFactory.createNodeExtention("v6");
		t.treeLeft.getNodeList().add(v6);
		INodeExt v7 = GraphExtentionFactory.createNodeExtention("v7");
		t.treeLeft.getNodeList().add(v7);
		INodeExt v8 = GraphExtentionFactory.createNodeExtention("v8");
		t.treeLeft.getNodeList().add(v8);
		INodeExt v9 = GraphExtentionFactory.createNodeExtention("v9");
		t.treeLeft.getNodeList().add(v9);
		INodeExt v10 = GraphExtentionFactory.createNodeExtention("v10");
		t.treeLeft.getNodeList().add(v10);
		INodeExt v11 = GraphExtentionFactory.createNodeExtention("v11");
		t.treeLeft.getNodeList().add(v11);
		INodeExt v12 = GraphExtentionFactory.createNodeExtention("v12");
		t.treeLeft.getNodeList().add(v12);

		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v4, v3));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v4, v2));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v5, v1));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v5, v4));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v9, v7));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v9, v8));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v6, v5));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v11, v9));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v11, v10));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v12, v6));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v12, v11));

		/* create right tree */
		
		INodeExt w1 = GraphExtentionFactory.createNodeExtention("w1");
		t.treeRight.getNodeList().add(w1);
		INodeExt w2 = GraphExtentionFactory.createNodeExtention("w2");
		t.treeRight.getNodeList().add(w2);
		INodeExt w3 = GraphExtentionFactory.createNodeExtention("w3");
		t.treeRight.getNodeList().add(w3);
		INodeExt w4 = GraphExtentionFactory.createNodeExtention("w4");
		t.treeRight.getNodeList().add(w4);
		INodeExt w5 = GraphExtentionFactory.createNodeExtention("w5");
		t.treeRight.getNodeList().add(w5);
		INodeExt w6 = GraphExtentionFactory.createNodeExtention("w6");
		t.treeRight.getNodeList().add(w6);
		INodeExt w7 = GraphExtentionFactory.createNodeExtention("w7");
		t.treeRight.getNodeList().add(w7);
		INodeExt w8 = GraphExtentionFactory.createNodeExtention("w8");
		t.treeRight.getNodeList().add(w8);
		INodeExt w9 = GraphExtentionFactory.createNodeExtention("w9");
		t.treeRight.getNodeList().add(w9);
		INodeExt w10 = GraphExtentionFactory.createNodeExtention("w10");
		t.treeRight.getNodeList().add(w10);
		INodeExt w11 = GraphExtentionFactory.createNodeExtention("w11");
		t.treeRight.getNodeList().add(w11);
		INodeExt w12 = GraphExtentionFactory.createNodeExtention("w12");
		t.treeRight.getNodeList().add(w12);
		INodeExt w13 = GraphExtentionFactory.createNodeExtention("w13");
		t.treeRight.getNodeList().add(w13);
		INodeExt w14 = GraphExtentionFactory.createNodeExtention("w14");
		t.treeRight.getNodeList().add(w14);
		INodeExt w15 = GraphExtentionFactory.createNodeExtention("w15");
		t.treeRight.getNodeList().add(w15);
		INodeExt w16 = GraphExtentionFactory.createNodeExtention("w16");
		t.treeRight.getNodeList().add(w16);
		INodeExt w17 = GraphExtentionFactory.createNodeExtention("w17");
		t.treeRight.getNodeList().add(w17);
		INodeExt w18 = GraphExtentionFactory.createNodeExtention("w18");
		t.treeRight.getNodeList().add(w18);
		
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w8, w6));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w8, w7));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w9, w8));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w11, w9));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w11, w10));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w12, w5));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w12, w11));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w18, w12));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w3, w2));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w4, w1));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w4, w3));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w18, w4));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w16, w15));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w17, w13));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w17, w14));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w17, w16));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w18, w17));
		
		return t;
	}
	
	/**
	 * The test set 2. <br>
	 * The trees to compare <code>T1</code> and <code>T2</code>
	 * 
	 * Expected map is: 
	 * <pre>
	 * v4 => w2
	 * v5 => w3
	 * v1 => w4
	 * v6 => w5
	 * </pre>
	 * 
	 * @return the test set
	 */
	private TestSet createTestSet2(){
		TestSet t = new TestSet();
		
		/* create left tree */		
		INodeExt v1 = GraphExtentionFactory.createNodeExtention("v1");
		t.treeLeft.getNodeList().add(v1);
		INodeExt v2 = GraphExtentionFactory.createNodeExtention("v2");
		t.treeLeft.getNodeList().add(v2);
		INodeExt v3 = GraphExtentionFactory.createNodeExtention("v3");
		t.treeLeft.getNodeList().add(v3);
		INodeExt v4 = GraphExtentionFactory.createNodeExtention("v4");
		t.treeLeft.getNodeList().add(v4);
		INodeExt v5 = GraphExtentionFactory.createNodeExtention("v5");
		t.treeLeft.getNodeList().add(v5);
		INodeExt v6 = GraphExtentionFactory.createNodeExtention("v6");
		t.treeLeft.getNodeList().add(v6);
		
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v6, v1));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v6, v5));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v5, v2));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v5, v4));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v4, v3));
		
		/* create right tree */
		
		INodeExt w1 = GraphExtentionFactory.createNodeExtention("w1");
		t.treeRight.getNodeList().add(w1);
		INodeExt w2 = GraphExtentionFactory.createNodeExtention("w2");
		t.treeRight.getNodeList().add(w2);
		INodeExt w3 = GraphExtentionFactory.createNodeExtention("w3");
		t.treeRight.getNodeList().add(w3);
		INodeExt w4 = GraphExtentionFactory.createNodeExtention("w4");
		t.treeRight.getNodeList().add(w4);
		INodeExt w5 = GraphExtentionFactory.createNodeExtention("w5");
		t.treeRight.getNodeList().add(w5);
		
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w5, w1));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w5, w3));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w5, w4));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w3, w2));
		
		return t;
	}
	
	
	/**
	 * The test set 3. <br>
	 * The right graph is not e tree.
	 * Expected is a ControlFlowGraphException.
	 * 
	 * @return the test set
	 */
	private TestSet createTestSet3(){
		TestSet t = new TestSet();

		/* create left tree */		
		INodeExt v1 = GraphExtentionFactory.createNodeExtention("v1");
		t.treeLeft.getNodeList().add(v1);
		INodeExt v2 = GraphExtentionFactory.createNodeExtention("v2");
		t.treeLeft.getNodeList().add(v2);
		INodeExt v3 = GraphExtentionFactory.createNodeExtention("v3");
		t.treeLeft.getNodeList().add(v3);

		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v1, v2));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v1, v3));

		/* create right tree */
		INodeExt w1 = GraphExtentionFactory.createNodeExtention("w1");
		t.treeRight.getNodeList().add(w1);
		INodeExt w2 = GraphExtentionFactory.createNodeExtention("w2");
		t.treeRight.getNodeList().add(w2);
		INodeExt w3 = GraphExtentionFactory.createNodeExtention("w3");
		t.treeRight.getNodeList().add(w3);
		INodeExt w4 = GraphExtentionFactory.createNodeExtention("w4");
		t.treeRight.getNodeList().add(w4);

		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w1, w2));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w1, w3));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w3, w4));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w4, w1));

		return t;
	}
	
	/**
	 * Test method for {@link com.drgarbage.algorithms.TopDownMaxCommonSubTreeIsomorphism}
	 * @throws ControlFlowGraphException 
	 * @see #createTestSet1()
	 */
	public final void testTopDownMaxCommonSubtreeIsomorthism1() throws ControlFlowGraphException {	
		
		TopDownMaxCommonSubTreeIsomorphism tdmcsi = new TopDownMaxCommonSubTreeIsomorphism();
		TestSet t = createTestSet1();
		Map<INodeExt, INodeExt> map = tdmcsi.topDownMaxCommonUnorderedSubtreeIsomorphism(t.treeLeft, t.treeRight);
		assertEquals(10, map.size());
		printMap(map, "test1");
	}
	
	/**
	 * testTopDownMaxCommonSubtreeIsomorthism2
	 * @throws ControlFlowGraphException 
	 * @see #createTestSet1()
	 */
	public final void testTopDownMaxCommonSubtreeIsomorthism2() throws ControlFlowGraphException {	
		
		TopDownMaxCommonSubTreeIsomorphism tdmcsi = new TopDownMaxCommonSubTreeIsomorphism();
		TestSet t = createTestSet2();
		Map<INodeExt, INodeExt> map = tdmcsi.topDownMaxCommonUnorderedSubtreeIsomorphism(t.treeLeft, t.treeRight);
		assertEquals(4, map.size());
		printMap(map, "test2");
	}
	
	/**
	 * testTopDownSubtreeIsomorthism3
	 * The input is not a tree.
	 * @see #createTestSet3()
	 */
	public final void testTopDownSubtreeIsomorthism3() {		
		TopDownMaxCommonSubTreeIsomorphism tdmcsi = new TopDownMaxCommonSubTreeIsomorphism();
		TestSet t = createTestSet3();

		try {
			tdmcsi.topDownMaxCommonUnorderedSubtreeIsomorphism(t.treeLeft, t.treeRight);
		} catch (ControlFlowGraphException e) {
			assertNotNull(e);
			return;
		}

		fail("ControlFlowGraphException has not been throwen.");
	}

}
