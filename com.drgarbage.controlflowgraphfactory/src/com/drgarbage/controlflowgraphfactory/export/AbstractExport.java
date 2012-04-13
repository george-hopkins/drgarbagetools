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

package com.drgarbage.controlflowgraphfactory.export;

import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.drgarbage.bytecode.ByteCodeConstants;
import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryPlugin;
import com.drgarbage.core.CoreConstants;
import com.drgarbage.visualgraphic.model.CommentElement;
import com.drgarbage.visualgraphic.model.Connection;
import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagram;
import com.drgarbage.visualgraphic.model.VertexBase;


public abstract class AbstractExport extends AbstractExport2 {

	/**
	 * This method will be called for each edge.
	 * @param edge
	 * @return
	 * @throws IOException 
	 */
	protected abstract void appendEdge(Connection con, Appendable out) throws IOException;
	
	
	/**
	 * This method should not be overwritten by clients.
	 * @param diagram
	 * @return string
	 * @throws IOException  
	 */
	protected void appendGraph(ControlFlowGraphDiagram diagram, Appendable buf) throws IOException {
		appendGraphStart(diagram, buf);
		appendNodesAndEdges(diagram.getChildren(), buf);
		appendGraphEnd(diagram, buf);
	}

	/**
	 * This method will be called after the createGraph method. 
	 * The clients can implement hire an opening pattern like <code>}</code>
	 * @param diagram
	 * @return string
	 * @throws IOException 
	 */
	protected abstract void appendGraphEnd(ControlFlowGraphDiagram diagram, Appendable out) throws IOException;

	/**
	 * This method will be called before the createGraph method. 
	 * The clients can implement hire an opening pattern like <code>{</code>
	 * @param diagram
	 * @return string
	 * @throws IOException 
	 */
	protected abstract void appendGraphStart(ControlFlowGraphDiagram diagram, Appendable out) throws IOException;
	
	/**
	 * Creates a header string.
	 * @param diagram
	 * @return string header
	 * @throws IOException 
	 */
	protected abstract void appendHeader(ControlFlowGraphDiagram diagram, Appendable out) throws IOException;
	
	protected void appendHeaderComment(Appendable sb) throws IOException {
		
		ArrayList<String> headerLines = createHeaderCommentLines();
		
		if (headerLines != null && headerLines.size() > 0) {
			int maxLength = 0;
			for (String line : headerLines) {
				if (line.length() > maxLength) {
					maxLength = line.length();
				}
			}
			
			sb.append(getCommentBegin());
			sb.append(' ');
			String comment_filler = getCommentFiller();
			for (int i = 0; i < maxLength; i++) {
				sb.append(comment_filler);
			}
			sb.append(' ');
			sb.append(getCommentEnd());
			sb.append('\n');
			
			for (String line : headerLines) {
				sb.append(getCommentBegin());
				sb.append(' ');
				sb.append(line);
				for (int i = 0; i < maxLength - line.length(); i++) {
					sb.append(' ');
				}
				sb.append(' ');
				sb.append(getCommentEnd());
				sb.append('\n');
			}

			sb.append(getCommentBegin());
			sb.append(' ');
			for (int i = 0; i < maxLength; i++) {
				sb.append(comment_filler);
			}
			sb.append(' ');
			sb.append(getCommentEnd());
			sb.append('\n');

		}
		
		/* free the list */
		headerLines = null;
		
	}
	
	/**
	 * Convert escape sequences ...
	 * @param label
	 * @return converted label
	 * @throws IOException 
	 */
	protected abstract void appendLabel(String label, Appendable out) throws IOException;

	/**
	 * This method should not be overwritten by clients.
	 * @param nodes
	 * @return String
	 * @throws IOException 
	 */
	protected void appendNodesAndEdges(List<VertexBase> nodes, Appendable buf) throws IOException{

		List<Connection> connections = new ArrayList<Connection>();
		Iterator<VertexBase> it = nodes.iterator();
		VertexBase vb = null;
		while(it.hasNext()){
			vb = it.next();
			if(!graphSpecification.isExportComments()){
				if(vb instanceof CommentElement){
					continue;
				}
			}

			/* append vertex info */
			appendVertex(vb, buf);

			/* build connection list */
			List<Connection> targetConnection = vb.getTargetConnections();
			Iterator<Connection> itTargetConnections = targetConnection.iterator();
			while(itTargetConnections.hasNext()){
				connections.add(itTargetConnections.next());
			}
		}

		/*append connection list */
		Iterator<Connection> allConnections = connections.iterator();
		Connection con = null;         
		while(allConnections.hasNext()){
			con = allConnections.next();

			/* append connection info */
			appendEdge(con, buf);
		}

	}
	
	/**
	 * This method will be called for each node.
	 * @param edge
	 * @return
	 * @throws IOException 
	 */
	protected abstract void appendVertex(VertexBase vb, Appendable out) throws IOException;

	protected ArrayList<String> createHeaderCommentLines() {
		ArrayList<String> headerLines = new ArrayList<String>(8);
		
		Bundle bundle = ControlFlowFactoryPlugin.getDefault().getBundle();
		if (bundle == null) {
			/* this should not happen */
			throw new RuntimeException(""+ CoreConstants.CONTROL_FLOW_GRAPH_FACTORY +" not installed.");
		}

		String providerWww = Platform.getResourceString(bundle, "%"+ CoreConstants.providerWww);
		String provider = Platform.getResourceString(bundle, "%"+ CoreConstants.providerNameLabel);
		String pluginName = Platform.getResourceString(bundle, "%"+ CoreConstants.pluginName);
		
		String msg = MessageFormat.format(ByteCodeConstants.Generated_by_x, new Object[] {provider + " "+ pluginName});
		
		headerLines.add(msg);
		headerLines.add(providerWww);
		
		headerLines.add(ByteCodeConstants.Version + ": " + ControlFlowFactoryPlugin.PLUGIN_VERSION);
		
		SimpleDateFormat sdf = new SimpleDateFormat(CoreConstants.ISO_DATE_TIME_FORMAT_FULL);
		headerLines.add(ByteCodeConstants.Retrieved_on + ": " + sdf.format(new Date()));

		return headerLines;
	}
	
	/**
	 * Begin Pattern for DOT format is <code>'/*'</code>
	 * For GraphXML <code>'&lt!--'</code>
	 * @return begin pattern
	 */
	protected abstract String getCommentBegin();


	/**
	 * End Pattern for DOT format is <code>* /</code>
	 * For GraphXML <code>--&gt</code>
	 * @return end pattern
	 */
	protected abstract String getCommentEnd();

	/**
	 * @see COMMENT_FILLER
	 * @return filler pattern for a comment
	 */
	protected abstract String getCommentFiller();

	/**
	 * Creates the content to export. This method should not 
	 * be overwritten by clients.
	 * @param diagram
	 * @return content as a string
	 * @throws IOException 
	 */
	public void write(ControlFlowGraphDiagram diagram, Writer buf) throws ExportException {
		Assert.isNotNull(graphSpecification);
		try {
			appendHeader(diagram, buf);
			appendGraph(diagram, buf);
		} catch (IOException e) {
			String msg = MessageFormat.format(
					ControlFlowFactoryMessages.Export_Could_not_export_0_, 
					new Object[] {e.getLocalizedMessage()}
			);
			throw new ExportException(msg, e);
		}
	}
	
}
