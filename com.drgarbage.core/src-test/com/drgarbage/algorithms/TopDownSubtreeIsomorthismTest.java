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

import com.drgarbage.algorithms.BottomUpSubtreeIsomorphismTest.TestSet;
import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraph.intf.GraphExtentionFactory;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;

/**
 * Test class for {@link com.drgarbage.algorithms.TopDownSubtreeIsomorphism}
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class TopDownSubtreeIsomorthismTest extends TestCase {

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
	void printMap(Map<INodeExt, INodeExt> map){
		for(Entry<INodeExt, INodeExt> entry : map.entrySet()){
			System.out.println(entry.getKey().getData()
					+ " = "
					+ entry.getValue().getData());
		}
	}
	
	/**
	 * The test set 1. <br>
	 * The trees to compare <code>T1</code> and <code>T2</code>
	 *  are defined as described in 
	 * {@link com.drgarbage.algorithms.TopDownSubtreeIsomorphism TopDownSubtreeIsomorthism}.
	 * 
	 * Expected map is 
	 * <pre>
	 *  v1 = w12
	 *  v7 = w18
	 *  v2 = w2
	 *  v5 = w4
	 *  v6 = w12
	 *  v4 = w1
	 *  v3 = w3
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

		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v3, v2));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v5, v3));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v5, v4));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v7, v1));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v7, v5));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v7, v6));

		/* create right tree */
		INodeExt w18 = GraphExtentionFactory.createNodeExtention("w18");
		t.treeRight.getNodeList().add(w18);
		
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
	 * The test set 3. <br>
	 * Two simple trees.
	 * 
	 * Expected map is 
	 * <pre>
	 *  v1 = w1
	 *  v2 = w2
	 *  v3 = w3
	 * </pre>
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

		return t;
	}

	/**
	 * The test set 4. <br>
	 * The right graph is not e tree.
	 * Expected is a ControlFlowGraphException.
	 * 
	 * @return the test set
	 */
	private TestSet createTestSet4(){
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
	 * The test set 5. <br>
	 * Two equivalent trees.
	 * 
	 * @return the test set
	 */
	private TestSet createTestSet5(){
		TestSet t = new TestSet();

		/* create left tree */
		INodeExt v1 = GraphExtentionFactory.createNodeExtention("w1");
		t.treeLeft.getNodeList().add(v1);
		INodeExt v2 = GraphExtentionFactory.createNodeExtention("w2");
		t.treeLeft.getNodeList().add(v2);
		INodeExt v3 = GraphExtentionFactory.createNodeExtention("w3");
		t.treeLeft.getNodeList().add(v3);
		INodeExt v4 = GraphExtentionFactory.createNodeExtention("w4");
		t.treeLeft.getNodeList().add(v4);
		INodeExt v5 = GraphExtentionFactory.createNodeExtention("w5");
		t.treeLeft.getNodeList().add(v5);
		INodeExt v6 = GraphExtentionFactory.createNodeExtention("w6");
		t.treeLeft.getNodeList().add(v6);
		INodeExt v7 = GraphExtentionFactory.createNodeExtention("w7");
		t.treeLeft.getNodeList().add(v7);
		INodeExt v8 = GraphExtentionFactory.createNodeExtention("w8");
		t.treeLeft.getNodeList().add(v8);
		INodeExt v9 = GraphExtentionFactory.createNodeExtention("w9");
		t.treeLeft.getNodeList().add(v9);
		INodeExt v10 = GraphExtentionFactory.createNodeExtention("w10");
		t.treeLeft.getNodeList().add(v10);
		INodeExt v11 = GraphExtentionFactory.createNodeExtention("w11");
		t.treeLeft.getNodeList().add(v11);
		INodeExt v12 = GraphExtentionFactory.createNodeExtention("w12");
		t.treeLeft.getNodeList().add(v12);
		INodeExt v13 = GraphExtentionFactory.createNodeExtention("w13");
		t.treeLeft.getNodeList().add(v13);
		INodeExt v14 = GraphExtentionFactory.createNodeExtention("w14");
		t.treeLeft.getNodeList().add(v14);
		INodeExt v15 = GraphExtentionFactory.createNodeExtention("w15");
		t.treeLeft.getNodeList().add(v15);
		INodeExt v16 = GraphExtentionFactory.createNodeExtention("w16");
		t.treeLeft.getNodeList().add(v16);
		INodeExt v17 = GraphExtentionFactory.createNodeExtention("w17");
		t.treeLeft.getNodeList().add(v17);
		INodeExt v18 = GraphExtentionFactory.createNodeExtention("w18");
		t.treeLeft.getNodeList().add(v18);
		
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v18, v4));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v18, v12));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v18, v17));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v4, v1));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v4, v3));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v12, v5));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v12, v11));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v17, v13));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v17, v14));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v17, v16));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v3, v2));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v11, v9));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v11, v10));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v16, v15));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v9, v8));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v8, v6));
		t.treeLeft.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v8, v7));

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
		
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w18, w4));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w18, w12));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w18, w17));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w4, w1));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w4, w3));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w12, w5));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w12, w11));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w17, w13));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w17, w14));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w17, w16));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w3, w2));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w11, w9));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w11, w10));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w16, w15));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w9, w8));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w8, w6));
		t.treeRight.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(w8, w7));
		
		return t;
	}
	
	/**
	 * Test method for {@link com.drgarbage.algorithms.TopDownSubtreeIsomorphism TopDownSubtreeIsomorthism}
	 * @throws ControlFlowGraphException 
	 * @see #createTestSet1()
	 */
	public final void testTopDownSubtreeIsomorthism1() throws ControlFlowGraphException {	
		TopDownSubtreeIsomorphism tsi = new TopDownSubtreeIsomorphism();
		TestSet t = createTestSet1();
		printGraph(t.treeLeft);
		printGraph(t.treeRight);
		Map<INodeExt, INodeExt> map = tsi.execute(t.treeLeft, t.treeRight);
		printMap(map);

		assertEquals(7, map.size());
	}

	/**
	 * Test method for {@link com.drgarbage.algorithms.TopDownSubtreeIsomorphism TopDownSubtreeIsomorthism}
	 * The same test set as for {@link #testTopDownSubtreeIsomorthism1()} 
	 * but the input trees <code>T1</code> and <code>T2</code> are swapped.
	 * @throws ControlFlowGraphException 
	 * @see #createTestSet1()
	 */
	public final void testTopDownSubtreeIsomorthism2() throws ControlFlowGraphException {		
		TopDownSubtreeIsomorphism tsi = new TopDownSubtreeIsomorphism();
		TestSet t = createTestSet1();
		printGraph(t.treeLeft);
		printGraph(t.treeRight);
		Map<INodeExt, INodeExt> map = tsi.execute(t.treeRight, t.treeLeft);
		assertNull(map);
	}

	/**
	 * Test method for {@link com.drgarbage.algorithms.TopDownSubtreeIsomorphism TopDownSubtreeIsomorthism}
	 * @throws ControlFlowGraphException 
	 * @see #createTestSet3()
	 */
	public final void testTopDownSubtreeIsomorthism3() throws ControlFlowGraphException {		
		TopDownSubtreeIsomorphism tsi = new TopDownSubtreeIsomorphism();
		TestSet t = createTestSet3();
		printGraph(t.treeLeft);
		printGraph(t.treeRight);
		Map<INodeExt, INodeExt> map = tsi.execute(t.treeLeft, t.treeRight);
		printMap(map);

		assertEquals(3, map.size());
	}
	
	/**
	 * Test method for {@link com.drgarbage.algorithms.TopDownSubtreeIsomorphism TopDownSubtreeIsomorthism}
	 * The input is not a tree.
	 * @see #createTestSet3()
	 */
	public final void testTopDownSubtreeIsomorthism4() {		
		TopDownSubtreeIsomorphism tsi = new TopDownSubtreeIsomorphism();
		TestSet t = createTestSet4();
		printGraph(t.treeLeft);
		printGraph(t.treeRight);

		try {
			tsi.execute(t.treeLeft, t.treeRight);
		} catch (ControlFlowGraphException e) {
			assertNotNull(e);
			return;
		}

		fail("ControlFlowGraphException has not been throwen.");
	}
	
	/**
	 * Test method for {@link com.drgarbage.algorithms.TopDownSubtreeIsomorphism TopDownSubtreeIsomorthism}
	 * Two equivalent trees.
	 * @throws ControlFlowGraphException 
	 * @see #createTestSet1()
	 */
	public final void testTopDownSubtreeIsomorthism5() throws ControlFlowGraphException {		
		TopDownSubtreeIsomorphism tsi = new TopDownSubtreeIsomorphism();
		TestSet t = createTestSet5();
		
		printGraph(t.treeLeft);
		printGraph(t.treeRight);
		
		Map<INodeExt, INodeExt> map = tsi.execute(t.treeLeft, t.treeRight);
		
		assertEquals(t.treeLeft.getNodeList().size(), map.size());
	}
}
