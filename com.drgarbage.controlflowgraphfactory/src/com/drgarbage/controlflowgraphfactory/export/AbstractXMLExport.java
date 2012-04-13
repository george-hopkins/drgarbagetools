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

import java.io.Writer;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.Assert;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

import com.drgarbage.controlflowgraphfactory.ControlFlowFactoryMessages;
import com.drgarbage.visualgraphic.model.ControlFlowGraphDiagram;

public abstract class AbstractXMLExport extends AbstractExport2 {
	protected class GraphFilter extends XMLFilterImpl {
		protected ControlFlowGraphDiagram diagram;
		public GraphFilter(ControlFlowGraphDiagram diagram, XMLReader parent) {
			super(parent);
			this.diagram = diagram;
		}
		
		/**
		 * Returns qualified item name, i.e. prepends the default prefix. 
		 * This default implementation returns unchanged <code>item</code>.
		 * 
		 * @param item
		 * @return
		 */
		protected String q(String item) {
			return item;
		}
		
		
		protected Map<String, Integer> idMap = null;
		
		protected String nextId(String prefix) {
			if (idMap == null) {
				idMap = new HashMap<String, Integer>();
			}
			
			Integer val = idMap.get(prefix);
			
			if (val == null) {
				idMap.put(prefix, Integer.valueOf(0));
				return prefix + "0";
			}
			else {
				val = Integer.valueOf(val.intValue() + 1);
				idMap.put(prefix, val);
				return prefix + val.intValue();
			}
			
		}

	}

	protected abstract XMLFilter createXmlFilter(ControlFlowGraphDiagram diagram, XMLReader xmlReader);	
	
	protected Transformer createTransformer() throws TransformerConfigurationException, TransformerFactoryConfigurationError {
		
		Transformer t = TransformerFactory.newInstance().newTransformer();
		t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
 		t.setOutputProperty(OutputKeys.INDENT, "yes");
 		t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		
 		return t;
	}
	
	protected abstract InputSource createExportStub();
//		return new InputSource(corpusReader);
//	}
	
	@Override
	public void write(ControlFlowGraphDiagram diagram, Writer out) throws ExportException {

		Assert.isNotNull(graphSpecification);
		
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			XMLReader xmlReader = parser.getXMLReader();
			
			XMLFilter filter = createXmlFilter(diagram, xmlReader);
			InputSource in = createExportStub();
			SAXSource src = new SAXSource(filter, in);
			StreamResult rslt = new StreamResult(out);
			
			Transformer t = createTransformer();
			t.transform(src, rslt);

			
		} catch (ParserConfigurationException e) {
			String msg = MessageFormat.format(
					ControlFlowFactoryMessages.Export_Could_not_export_0_, 
					new Object[] {e.getLocalizedMessage()}
			);
			throw new ExportException(msg, e);
			
		} catch (SAXException e) {
			String msg = MessageFormat.format(
					ControlFlowFactoryMessages.Export_Could_not_export_0_, 
					new Object[] {e.getLocalizedMessage()}
			);
			throw new ExportException(msg, e);
		} catch (TransformerConfigurationException e) {
			String msg = MessageFormat.format(
					ControlFlowFactoryMessages.Export_Could_not_export_0_, 
					new Object[] {e.getLocalizedMessage()}
			);
			throw new ExportException(msg, e);
		} catch (TransformerFactoryConfigurationError e) {
			String msg = MessageFormat.format(
					ControlFlowFactoryMessages.Export_Could_not_export_0_, 
					new Object[] {e.getLocalizedMessage()}
			);
			throw new ExportException(msg, e);
		} catch (TransformerException e) {
			String msg = MessageFormat.format(
					ControlFlowFactoryMessages.Export_Could_not_export_0_, 
					new Object[] {e.getLocalizedMessage()}
			);
			throw new ExportException(msg, e);
		}
	}

}
