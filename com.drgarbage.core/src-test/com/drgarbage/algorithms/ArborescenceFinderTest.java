package com.drgarbage.algorithms;

import junit.framework.TestCase;

import com.drgarbage.controlflowgraph.ControlFlowGraphException;
import com.drgarbage.controlflowgraph.intf.GraphExtentionFactory;
import com.drgarbage.controlflowgraph.intf.IArborescence;
import com.drgarbage.controlflowgraph.intf.IDirectedGraphExt;
import com.drgarbage.controlflowgraph.intf.IEdgeListExt;
import com.drgarbage.controlflowgraph.intf.INodeExt;

public class ArborescenceFinderTest extends TestCase {
	/**
	 * Creates a test graph
	 * 
	 * @return
	 */
	private IDirectedGraphExt createTestTree0() {
		IDirectedGraphExt testTree = GraphExtentionFactory
				.createDirectedGraphExtention();
		INodeExt v0 = GraphExtentionFactory.createNodeExtention("v0");
		testTree.getNodeList().add(v0);
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v0, v0));

		return testTree;
	}

	private IDirectedGraphExt createTestTree1() {
		IDirectedGraphExt testTree = GraphExtentionFactory
				.createDirectedGraphExtention();
		INodeExt v0 = GraphExtentionFactory.createNodeExtention("v0");
		testTree.getNodeList().add(v0);
		INodeExt v1 = GraphExtentionFactory.createNodeExtention("v1");
		testTree.getNodeList().add(v1);
		INodeExt v2 = GraphExtentionFactory.createNodeExtention("v2");
		testTree.getNodeList().add(v2);
		INodeExt v3 = GraphExtentionFactory.createNodeExtention("v3");
		testTree.getNodeList().add(v3);
		INodeExt v4 = GraphExtentionFactory.createNodeExtention("v4");
		testTree.getNodeList().add(v4);
		INodeExt v5 = GraphExtentionFactory.createNodeExtention("v5");
		testTree.getNodeList().add(v5);
		INodeExt v6 = GraphExtentionFactory.createNodeExtention("v6");
		testTree.getNodeList().add(v6);
		INodeExt v7 = GraphExtentionFactory.createNodeExtention("v7");
		testTree.getNodeList().add(v7);

		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v0, v1));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v1, v2));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v2, v3));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v3, v4));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v4, v5));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v5, v6));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v6, v7));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v7, v0));

		return testTree;
	}

	private IDirectedGraphExt createTestTree2() {
		IDirectedGraphExt testTree = GraphExtentionFactory
				.createDirectedGraphExtention();
		INodeExt v0 = GraphExtentionFactory.createNodeExtention("v0");
		testTree.getNodeList().add(v0);
		INodeExt v1 = GraphExtentionFactory.createNodeExtention("v1");
		testTree.getNodeList().add(v1);
		INodeExt v2 = GraphExtentionFactory.createNodeExtention("v2");
		testTree.getNodeList().add(v2);
		INodeExt v3 = GraphExtentionFactory.createNodeExtention("v3");
		testTree.getNodeList().add(v3);
		INodeExt v4 = GraphExtentionFactory.createNodeExtention("v4");
		testTree.getNodeList().add(v4);
		INodeExt v5 = GraphExtentionFactory.createNodeExtention("v5");
		testTree.getNodeList().add(v5);
		INodeExt v6 = GraphExtentionFactory.createNodeExtention("v6");
		testTree.getNodeList().add(v6);
		INodeExt v7 = GraphExtentionFactory.createNodeExtention("v7");
		testTree.getNodeList().add(v7);

		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v0, v1));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v1, v2));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v2, v3));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v3, v0));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v0, v4));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v4, v5));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v5, v6));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v6, v0));
		// testTree.getEdgeList().add(GraphExtentionFactory.createEdgeExtention(v0,
		// v7));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v7, v0));

		return testTree;
	}

	private IDirectedGraphExt createTestTree3() {
		IDirectedGraphExt testTree = GraphExtentionFactory
				.createDirectedGraphExtention();
		INodeExt v0 = GraphExtentionFactory.createNodeExtention("v0");
		testTree.getNodeList().add(v0);
		INodeExt v1 = GraphExtentionFactory.createNodeExtention("v1");
		testTree.getNodeList().add(v1);
		INodeExt v2 = GraphExtentionFactory.createNodeExtention("v2");
		testTree.getNodeList().add(v2);
		INodeExt v3 = GraphExtentionFactory.createNodeExtention("v3");
		testTree.getNodeList().add(v3);
		INodeExt v4 = GraphExtentionFactory.createNodeExtention("v4");
		testTree.getNodeList().add(v4);
		INodeExt v5 = GraphExtentionFactory.createNodeExtention("v5");
		testTree.getNodeList().add(v5);
		INodeExt v6 = GraphExtentionFactory.createNodeExtention("v6");
		testTree.getNodeList().add(v6);
		INodeExt v7 = GraphExtentionFactory.createNodeExtention("v7");
		testTree.getNodeList().add(v7);

		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v3, v0));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v3, v1));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v3, v2));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v3, v4));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v3, v5));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v3, v6));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v3, v7));

		return testTree;
	}

	private IDirectedGraphExt createTestTree4() {
		IDirectedGraphExt testTree = GraphExtentionFactory
				.createDirectedGraphExtention();
		INodeExt v0 = GraphExtentionFactory.createNodeExtention("v0");
		testTree.getNodeList().add(v0);
		INodeExt v1 = GraphExtentionFactory.createNodeExtention("v1");
		testTree.getNodeList().add(v1);
		INodeExt v2 = GraphExtentionFactory.createNodeExtention("v2");
		testTree.getNodeList().add(v2);
		INodeExt v3 = GraphExtentionFactory.createNodeExtention("v3");
		testTree.getNodeList().add(v3);
		INodeExt v4 = GraphExtentionFactory.createNodeExtention("v4");
		testTree.getNodeList().add(v4);
		INodeExt v5 = GraphExtentionFactory.createNodeExtention("v5");
		testTree.getNodeList().add(v5);
		INodeExt v6 = GraphExtentionFactory.createNodeExtention("v6");
		testTree.getNodeList().add(v6);
		INodeExt v7 = GraphExtentionFactory.createNodeExtention("v7");
		testTree.getNodeList().add(v7);

		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v0, v1));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v1, v2));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v2, v3));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v3, v4));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v4, v5));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v6, v5));
		testTree.getEdgeList().add(
				GraphExtentionFactory.createEdgeExtention(v7, v0));

		return testTree;
	}

	public final void testFind() {
		IDirectedGraphExt testTree = createTestTree0();
		IArborescence arborescence = null;
		try {
			arborescence = ArborescenceFinder.find(testTree);
		} catch (ControlFlowGraphException e) {
			System.out.println(e);
		}
		assertEquals(1, arborescence.getNodeList().size());
		assertEquals(0, arborescence.getEdgeList().size());
		assertEquals(arborescence.getRoot().getData(), "v0");
		
		testTree = createTestTree1();
		try {
			arborescence = ArborescenceFinder.find(testTree);
		} catch (ControlFlowGraphException e) {
			System.out.println(e);
		}
		assertEquals(8, arborescence.getNodeList().size());
		assertEquals(7, arborescence.getEdgeList().size());
		
		testTree = createTestTree2();
		try {
			arborescence = ArborescenceFinder.find(testTree);
		} catch (ControlFlowGraphException e) {
			System.out.println(e);
		}
		assertEquals(8, arborescence.getNodeList().size());
		assertEquals(7, arborescence.getEdgeList().size());
		assertEquals(arborescence.getRoot().getData(), "v7");
		
		testTree = createTestTree3();
		try {
			arborescence = ArborescenceFinder.find(testTree);
		} catch (ControlFlowGraphException e) {
			System.out.println(e);
		}
		assertEquals(8, arborescence.getNodeList().size());
		assertEquals(7, arborescence.getEdgeList().size());
		assertEquals(arborescence.getRoot().getData(), "v3");
		
		testTree = createTestTree4();
		Exception cfge = null;
		try {
			arborescence = ArborescenceFinder.find(testTree);
		} catch (ControlFlowGraphException e) {
			cfge = e;
		}
		assertTrue(cfge instanceof ControlFlowGraphException);
	}
	
	
	public final void testFindBackEdges() {
		IEdgeListExt backEdges = ArborescenceFinder.findBackEdges(createTestTree0());
		assertEquals(backEdges.size(), 1);
		
		
		backEdges = ArborescenceFinder.findBackEdges(createTestTree1());
		assertEquals(backEdges.size(), 1);
		
		backEdges = ArborescenceFinder.findBackEdges(createTestTree2());
		assertEquals(backEdges.size(), 2);
		
		backEdges = ArborescenceFinder.findBackEdges(createTestTree3());
		assertEquals(backEdges.size(), 0);
		
		backEdges = ArborescenceFinder.findBackEdges(createTestTree4());
		assertEquals(backEdges.size(), 0);
		
	}
}
