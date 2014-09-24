/**
 * 
 */
package com.drgarbage.controlflowgraph.intf;

/**
 * @author kvbx
 *
 */
public interface IArborescence extends IDirectedGraphExt {
	public INodeExt getRoot();
	public void setRoot(INodeExt r);

}
