/**
 * 
 */
package com.drgarbage.controlflowgraph.intf;

/**
 * @author kvbx
 *
 */
public interface ISpanningTree extends IDirectedGraphExt {
	public INodeExt getRoot();
	public void setRoot(INodeExt r);

}
