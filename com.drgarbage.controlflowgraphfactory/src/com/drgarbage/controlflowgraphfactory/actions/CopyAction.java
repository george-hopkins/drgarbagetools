/**
 * Copyright (c) 2008-2012, Dr. Garbage Community
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

package com.drgarbage.controlflowgraphfactory.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.internal.GEFMessages;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteTemplateEntry;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.WorkbenchMessages;

import com.drgarbage.controlflowgraphfactory.editors.ControlFlowGraphEditor;
import com.drgarbage.visualgraphic.model.Connection;
import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagram;
import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagramFactory;
import com.drgarbage.visualgraphic.model.ModelElement;
import com.drgarbage.visualgraphic.model.VertexBase;

public class CopyAction extends WorkbenchPartAction
implements ISelectionChangedListener
{

	private List<EditPart> template;

	/**
	 * Constructs a new CopyTemplateAction.  You must manually add this action to the palette
	 * viewer's list of selection listeners.  Otherwise, this action's enabled state won't be
	 * updated properly.
	 * 
	 * @param editor the workbench part
	 * @see org.eclipse.gef.ui.actions.EditorPartAction#EditorPartAction(IEditorPart)
	 */
	public CopyAction(IEditorPart editor) {
		super(editor);
		setId(ActionFactory.COPY.getId());
		setText(WorkbenchMessages.Workbench_copy);

		ISharedImages sharedImages = editor.getSite().getWorkbenchWindow().getWorkbench().getSharedImages();

		setToolTipText(WorkbenchMessages.Workbench_copyToolTip);
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));

	}

	/**
	 * Returns whether the selected EditPart is a TemplateEditPart.
	 * @return whether the selected EditPart is a TemplateEditPart
	 */
	protected boolean calculateEnabled() {
		return template != null;
	}

	/**
	 * @see org.eclipse.gef.ui.actions.EditorPartAction#dispose()
	 */
	public void dispose() {
		template = null;
	}

	/**
	 * Sets the default {@link Clipboard Clipboard's} contents to be the currently selected
	 * template.
	 */
	public void run() {
		/* copy models to clip board */	
		List<VertexBase> newElements = new ArrayList<VertexBase>();
		Map<VertexBase, VertexBase> vertices = new HashMap<VertexBase, VertexBase>();
		for(EditPart e: template){
				VertexBase vb = (VertexBase)(e.getModel());
				
				VertexBase b = ControlFlowGraphDiagramFactory.clone(vb);
				newElements.add(b);
				
				vertices.put(vb, b);
		}
		
		List<Connection> connectionList = new ArrayList<Connection>();
		for(EditPart e: template){
				VertexBase vb = (VertexBase)(e.getModel());
				
				List<Connection> list = vb.getSourceConnections();
				for(Connection c: list){
					if(vertices.containsKey(c.getSource()) && vertices.containsKey(c.getTarget())){
						connectionList.add(c);
					}
				}
				vb.getTargetConnections();
			}
		
		for(Connection con: connectionList){
				VertexBase source = vertices.get(con.getSource());
				VertexBase target = vertices.get(con.getTarget());
				
				Connection newCon = new Connection(source, target);
				newCon.setLabel(con.getLabel());
				newCon.setLineStyle(con.getLineStyle());
		}
		
		Clipboard.getDefault().setContents(newElements);

		/* deselect all */
		GraphicalViewer viewer = (GraphicalViewer)getWorkbenchPart().getAdapter(GraphicalViewer.class);
		if (viewer != null){
			viewer.setSelection(new StructuredSelection());
		}

	}

	/**
	 * Sets the selected EditPart and refreshes the enabled state of this action.
	 * 
	 * @see ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		ISelection s = event.getSelection();
		if (!(s instanceof IStructuredSelection))
			return;
		IStructuredSelection selection = (IStructuredSelection)s;
		template = null;
		if (selection != null) {

			Object objs[] = selection.toArray();
			List<EditPart> elements = new ArrayList<EditPart>();
			for(Object o : objs){
				if (o instanceof EditPart) {
					Object model = ((EditPart)o).getModel();
					if(model != null && !(model instanceof ControlFlowGraphDiagram)){
						elements.add((EditPart)o);
					}
				}	
			}
			if(elements.size() != 0){
				template = elements;
			}

		}
		refresh();
	}

}
