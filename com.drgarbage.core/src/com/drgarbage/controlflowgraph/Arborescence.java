package com.drgarbage.controlflowgraph;

import com.drgarbage.controlflowgraph.intf.IArborescence;
import com.drgarbage.controlflowgraph.intf.INodeExt;

public class Arborescence extends DirectedGraphExt implements IArborescence {

	private INodeExt root;
	
	public void setRoot(INodeExt r) {
		root = r;
	}
	public INodeExt getRoot() {
		return root;
	}

}
