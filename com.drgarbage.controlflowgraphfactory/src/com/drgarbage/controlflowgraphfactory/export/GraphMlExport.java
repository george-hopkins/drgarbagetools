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

import java.io.StringReader;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

import com.drgarbage.graphml.GraphMlAttributeValues;
import com.drgarbage.graphml.GraphMlAttributes;
import com.drgarbage.graphml.GraphMlConstants;
import com.drgarbage.graphml.GraphMlCustomAttributes;
import com.drgarbage.graphml.GraphMlElements;
import com.drgarbage.visualgraphic.model.CommentElement;
import com.drgarbage.visualgraphic.model.Connection;
import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagram;
import com.drgarbage.visualgraphic.model.DecisionVertex;
import com.drgarbage.visualgraphic.model.ExitVertex;
import com.drgarbage.visualgraphic.model.GetVertex;
import com.drgarbage.visualgraphic.model.GotoJumpVertex;
import com.drgarbage.visualgraphic.model.InvokeVertex;
import com.drgarbage.visualgraphic.model.RectangularVertex;
import com.drgarbage.visualgraphic.model.ReturnVertex;
import com.drgarbage.visualgraphic.model.StartVertex;
import com.drgarbage.visualgraphic.model.SwitchVertex;
import com.drgarbage.visualgraphic.model.VertexBase;
import com.drgarbage.xml.XmlConstants;

public class GraphMlExport extends AbstractXMLExport {
	
	protected class GraphMlFilter extends GraphFilter {


		public GraphMlFilter(ControlFlowGraphDiagram diagram, XMLReader parent) {
			super(diagram, parent);
		}

		@Override
		public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {
			
			if (GraphMlElements.graphml.equals(name)) {
				/* the only element expected in the stub */
				
				AttributesImpl newAttributes = new AttributesImpl();
				
				newAttributes.setAttributes(atts); /* set attributes, like xmlns, xmlns:xsi or xsi:schemaLocation */
				newAttributes.addAttribute("", "", GraphMlAttributes.id, XmlConstants.CDATA, GraphMlAttributeValues.DEFAULT_GRAPH_ID);
				newAttributes.addAttribute("", "", GraphMlAttributes.edgedefault, XmlConstants.CDATA, GraphMlAttributeValues.directed);

				super.startElement(uri, localName, name, newAttributes);
			}
			else {
				return;
			}
			
			if (graphSpecification.isExportGeometry()) {
				declareGeometry(uri);
			}
			if (graphSpecification.isExportDecorations()) {
				declareDecorations(uri);
			}
			
			super.startElement(uri, "", q(GraphMlElements.graph), null);
			
			/* export the rest */
			
			/* nodes */
			List<VertexBase> vertices = diagram.getChildren();
			if (vertices != null && vertices.size() > 0) {
				
				for (VertexBase v : vertices) {
					
					if(!graphSpecification.isExportComments() && v instanceof CommentElement){
							continue;
					}

					appendVertex(v, uri);

					/* In GraphML there is no order defined for the appearance of node and edge elements 
					 * see http://graphml.graphdrawing.org/primer/graphml-primer.html#Graph 
					 * */
					List<Connection> ingoingEdges = v.getTargetConnections();
					if (ingoingEdges != null && ingoingEdges.size() > 0) {
						for (Connection edge : ingoingEdges) {
							appendEdge(edge, uri);
						}
					}
					
				}
				
			}
			
			// super.endElement(uri, "", q(GraphMlElements.graph));

		}
		
		protected void appendEdge(Connection edge, String uri) throws SAXException {
			AttributesImpl atts = new AttributesImpl();
			
			atts.addAttribute(uri, "", GraphMlAttributes.source, XmlConstants.CDATA, GraphMlAttributeValues.nodeIdPrefix + edge.getSource().getId());
			atts.addAttribute(uri, "", GraphMlAttributes.target, XmlConstants.CDATA, GraphMlAttributeValues.nodeIdPrefix + edge.getTarget().getId());
			
			super.startElement(uri, "", q(GraphMlElements.edge), atts);
			super.endElement(uri, "", q(GraphMlElements.edge));
		}

		protected void appendVertex(VertexBase v, String uri) throws SAXException {
			
			AttributesImpl atts = new AttributesImpl();
			
			atts.addAttribute(uri, "", GraphMlAttributes.id, XmlConstants.CDATA, GraphMlAttributeValues.nodeIdPrefix + v.getId());
			
			super.startElement(uri, "", q(GraphMlElements.node), atts);
			
			if (graphSpecification.isExportGeometry()) {
				Point p = v.getLocation();
				appendAttribute(uri, GraphMlCustomAttributes.x, String.valueOf(p.x));
				appendAttribute(uri, GraphMlCustomAttributes.y, String.valueOf(p.y));
				Dimension d = v.getSize();
				appendAttribute(uri, GraphMlCustomAttributes.width, String.valueOf(d.width));
				appendAttribute(uri, GraphMlCustomAttributes.height, String.valueOf(d.height));
			}
			if (graphSpecification.isExportDecorations()) {
				//FIXME: where do I get the color from
//				Color rgb = ?;
//				appendAttribute(uri, GraphMlCustomAttributes.color, DotUtils.toHexColor(rgb.r, rgb.g, rgb.b));
				
				appendAttribute(uri, GraphMlCustomAttributes.type, toNodeType(v));
				appendAttribute(uri, GraphMlCustomAttributes.label, v.getLabel().trim());
				
			}

			
			super.endElement(uri, "", q(GraphMlElements.node));
			
		}
		
		protected String toNodeType(VertexBase vb) {
			if(vb instanceof DecisionVertex){
				return GraphMlCustomAttributes.type_decision;
			}
			else if(vb instanceof GetVertex){
				return GraphMlCustomAttributes.type_get;
			}
			else if(vb instanceof GotoJumpVertex){
				return GraphMlCustomAttributes.type_jump;
			}
			else if(vb instanceof InvokeVertex){
				return GraphMlCustomAttributes.type_invoke;
			}
			else if(vb instanceof RectangularVertex){
				return GraphMlCustomAttributes.type_instruction;
			}
			else if(vb instanceof ReturnVertex){
				return GraphMlCustomAttributes.type_return;
			}
			else if(vb instanceof StartVertex){
				return GraphMlCustomAttributes.type_start;
			}
			else if(vb instanceof ExitVertex){
				return GraphMlCustomAttributes.type_exit;
			}
			else if(vb instanceof SwitchVertex){
				return GraphMlCustomAttributes.type_switch;
			}
			else if(vb instanceof CommentElement){
				return GraphMlCustomAttributes.type_comment;
			}
			else {
				throw new IllegalStateException("Unexpected vertex type '"+ vb.getClass().getName() +"'");
			}		
		}
		
		protected void appendAttribute(String uri, String key, String value) throws SAXException {
			AttributesImpl atts = new AttributesImpl();
			atts.addAttribute(uri, "", GraphMlAttributes.key, XmlConstants.CDATA, toAttributeId(key));
			super.startElement(uri, "", q(GraphMlElements.data), atts);
			super.characters(value.toCharArray(), 0, value.length());
			super.endElement(uri, "", q(GraphMlElements.data));
		}
		protected void declareAttribute(String uri, String key, String type) throws SAXException {
			AttributesImpl atts = new AttributesImpl();
			
			atts.addAttribute(uri, "", GraphMlAttributes.id, XmlConstants.CDATA, toAttributeId(key));
			atts.addAttribute(uri, "", GraphMlAttributes.for_, XmlConstants.CDATA, GraphMlElements.node);
			atts.addAttribute(uri, "", GraphMlAttributes.attr_name, XmlConstants.CDATA, key);
			atts.addAttribute(uri, "", GraphMlAttributes.attr_type, XmlConstants.CDATA, type);
			
			super.startElement(uri, "", q(GraphMlElements.key), atts);
			super.endElement(uri, "", q(GraphMlElements.key));
		}
		
		protected void declareGeometry(String uri) throws SAXException {
			declareAttribute(uri, GraphMlCustomAttributes.x, GraphMlAttributeValues.int_);
			declareAttribute(uri, GraphMlCustomAttributes.y, GraphMlAttributeValues.int_);
			declareAttribute(uri, GraphMlCustomAttributes.width, GraphMlAttributeValues.int_);
			declareAttribute(uri, GraphMlCustomAttributes.height, GraphMlAttributeValues.int_);
		}
		
		protected void declareDecorations(String uri) throws SAXException {
			declareAttribute(uri, GraphMlCustomAttributes.color, GraphMlAttributeValues.string);
			declareAttribute(uri, GraphMlCustomAttributes.type, GraphMlAttributeValues.string);
			declareAttribute(uri, GraphMlCustomAttributes.label, GraphMlAttributeValues.string);
		}
		
		private String toAttributeId(String key) {
			return GraphMlAttributeValues.attributeIdPrefix + key;
		}
	}

	@Override
	protected InputSource createExportStub() {
		StringReader r = new StringReader(GraphMlConstants.GRAPH_ML_EXPORT_STUB);
		InputSource result = new InputSource(r);
		return result;
	}

	@Override
	protected XMLFilter createXmlFilter(ControlFlowGraphDiagram diagram, XMLReader xmlReader) {
		return new GraphMlFilter(diagram, xmlReader);
	}

}
