package com.drgarbage.controlflowgraph;

import com.drgarbage.controlflowgraph.intf.ISpanningTree;
import com.drgarbage.controlflowgraph.intf.INodeExt;

public class SpanningTree extends DirectedGraphExt implements ISpanningTree {

	private INodeExt root;
	
	public void setRoot(INodeExt r) {
		root = r;
	}
	public INodeExt getRoot() {
		return root;
	}

}
