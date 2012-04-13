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

package com.drgarbage.ant;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DrGarbageUpdateVersions extends Task {
	
	private class FeatureXmlModifier {
		
		private Document doc;
		private File featureXmlFile;
		private Node requiresNode;
		private Node rootNode;
		private Transformer t;
		private XPath xPath;

		public FeatureXmlModifier(File featureXmlFile, String bundleId, String newVersion) throws TransformerFactoryConfigurationError, TransformerException, IOException, XPathExpressionException {
			super();
			this.featureXmlFile = featureXmlFile;
			
			if (featureXmlFile.exists()) {
				xPath = javax.xml.xpath.XPathFactory.newInstance().newXPath();

				read();
				clean();
				
				setVersion(bundleId, newVersion);
			}
			
		}
		
		
		private void addImport(String element, String bundleId, String match, String version) {
			
			if (doc != null) {
				Node import_ = appendElement(requiresNode, element);
				setAttribute(import_, FeatureXmlConstants.ATTRIBUTE_MATCH, match);
				setAttribute(import_, FeatureXmlConstants.ATTRIBUTE_PLUGIN, bundleId);
				setAttribute(import_, FeatureXmlConstants.ATTRIBUTE_VERSION, version);
				appendText(requiresNode, "\n");
			}
			
		}
		
		public void addImportFeature(String bundleId, String match, String version) {
			addImport(FeatureXmlConstants.ELEMENT_FEATURE, bundleId, match, version);
		}
		
		public void addImportPlugin(String bundleId, String match, String version) {
			addImport(FeatureXmlConstants.ELEMENT_IMPORT, bundleId, match, version);
		}
		
		public void addPlugin(String bundleId, String version) {
			
			if (doc != null) {
				Node plugin = appendElement(rootNode, FeatureXmlConstants.ELEMENT_PLUGIN);
				
				setAttribute(plugin, FeatureXmlConstants.ATTRIBUTE_ID, bundleId);
				setAttribute(plugin, FeatureXmlConstants.ATTRIBUTE_VERSION, version);
				setAttribute(plugin, FeatureXmlConstants.ATTRIBUTE_DOWNLOAD_SIZE, "200");
				setAttribute(plugin, FeatureXmlConstants.ATTRIBUTE_INSTALL_SIZE, "200");
				setAttribute(plugin, FeatureXmlConstants.ATTRIBUTE_UNPACK, Boolean.FALSE.toString());
				appendText(rootNode, "\n");
			}
			
		}
		
		private void clean() throws XPathExpressionException {
			removeNodes(doc, "/feature/plugin|feature/text()", xPath);
			removeNodes(doc, "/feature/requires/import|/feature/requires/text()", xPath);
		}
		
		private void read() throws TransformerFactoryConfigurationError, TransformerException, IOException, XPathExpressionException {
			
			t = javax.xml.transform.TransformerFactory.newInstance().newTransformer();
			InputStream in = null;
			DOMResult rslt = null;
			try {
				in = new BufferedInputStream(new FileInputStream(featureXmlFile));
				Source src = new StreamSource(in);
				rslt = new DOMResult();
				t.transform(src, rslt);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (Exception ignore) {
					}
				}
			}
			
			doc = (Document)rslt.getNode();
			rootNode = doc.getFirstChild();
			requiresNode = (Node) xPath.evaluate(FeatureXmlConstants.ELEMENT_REQUIRES, rootNode, XPathConstants.NODE);
			
		}
		private void setVersion(String bundleId, String newVersion) throws XPathExpressionException {
			setAttribute(rootNode, FeatureXmlConstants.ATTRIBUTE_VERSION, newVersion);
			addPlugin(bundleId, newVersion);
		}
		
		public void write() throws TransformerFactoryConfigurationError, TransformerException, IOException {
			
			if (doc != null) {
				
				OutputStream out = null;
				try {
					Source domSrc = new javax.xml.transform.dom.DOMSource(doc);
					out = new BufferedOutputStream(new FileOutputStream(featureXmlFile));
					Result sreamRslt = new javax.xml.transform.stream.StreamResult(out);
					t.transform(domSrc, sreamRslt);
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (Exception ignored) {
						}
					}
				}
			}
			
		}
		
	}
	
	private static Node appendElement(Node parent, String tagName) {
		Node result = parent.getOwnerDocument().createElement(tagName);
		parent.appendChild(result);
		return result;
	}
	private static Node appendText(Node parent, String value) {
		Node result = parent.getOwnerDocument().createTextNode(value);
		parent.appendChild(result);
		return result;
	}
	private static String createMfInterval(String version, String match) {
		
		//self.log("newversion="+ newversion +" match="+match);
		
		List<String> versionFields = split(version, AntConstants.VERSION_SEPARATOR);
		int major = Integer.valueOf(versionFields.get(0));
		int minor = Integer.valueOf(versionFields.get(1));
		int service = Integer.valueOf(versionFields.get(2));
		String qualifier = null;
		if (versionFields.size() > 3) {
			qualifier = versionFields.get(3);
		}
		StringBuilder sb = new StringBuilder()
			.append(AntConstants.INTERVAL_QUOTE);
		if (AntConstants.MATCH_PERFECT.equals(match)) {
			sb.append(AntConstants.INTERVAL_LEFT_SQUARE_BRACKET)
				.append(major)
				.append(AntConstants.VERSION_SEPARATOR)
				.append(minor)
				.append(AntConstants.VERSION_SEPARATOR)
				.append(service);
			if (qualifier != null) {
				sb.append(AntConstants.VERSION_SEPARATOR);
				sb.append(qualifier);
			}
			sb.append(AntConstants.INTERVAL_COMMA)
				.append(major)
				.append(AntConstants.VERSION_SEPARATOR)
				.append(minor)
				.append(AntConstants.VERSION_SEPARATOR)
				.append(service+1);
			if (qualifier != null) {
				sb.append(AntConstants.VERSION_SEPARATOR)
					.append(qualifier);
			}
			sb.append(AntConstants.INTERVAL_RIGHT_PARENTHESIS);
		}
		else if (AntConstants.MATCH_EQUIVALENT.equals(match)) {
			sb.append(AntConstants.INTERVAL_LEFT_SQUARE_BRACKET)
				.append(major)
				.append(AntConstants.VERSION_SEPARATOR)
				.append(minor)
				.append(AntConstants.VERSION_SEPARATOR)
				.append(service);
			if (qualifier != null) {
				sb.append(AntConstants.VERSION_SEPARATOR)
					.append(qualifier);
			}
			sb.append(AntConstants.INTERVAL_COMMA)
				.append(major)
				.append(AntConstants.VERSION_SEPARATOR)
				.append(minor+1)
				.append(AntConstants.VERSION_SEPARATOR)
				.append("0")
				.append(AntConstants.INTERVAL_RIGHT_PARENTHESIS);
		}
		else if (AntConstants.MATCH_GREATER_OR_EQUAL.equals(match)) {
			sb.append(AntConstants.INTERVAL_LEFT_SQUARE_BRACKET)
				.append(major)
				.append(AntConstants.VERSION_SEPARATOR)
				.append(minor)
				.append(AntConstants.VERSION_SEPARATOR)
				.append(service);
			if (qualifier != null) {
				sb.append(AntConstants.VERSION_SEPARATOR)
					.append(qualifier);
			}
			sb.append(AntConstants.INTERVAL_COMMA)
				.append(major)
				.append(AntConstants.VERSION_SEPARATOR)
				.append(minor+2)
				.append(AntConstants.VERSION_SEPARATOR)
				.append("0")
				.append(AntConstants.INTERVAL_RIGHT_PARENTHESIS);
		}
		else {
			throw new BuildException("Unsupported match type '"+ match +"'");
		}
		sb.append(AntConstants.INTERVAL_QUOTE);
		return sb.toString();
	}

	private static void removeNodes(Node base, String xPathSelector, XPath xPath) throws XPathExpressionException {
		NodeList nl = (NodeList) xPath.evaluate(
				xPathSelector, 
				base, 
				XPathConstants.NODESET
			);
		if (nl != null) {
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				n.getParentNode().removeChild(n);
			}
		}
	}

	private static void setAttribute(Node node, String key, String value) {
		Attr attr = node.getOwnerDocument().createAttribute(key);
		attr.setNodeValue(value);
		node.getAttributes().setNamedItem(attr);
	}

	private static List<String> split(String str, char separator)  {
		ArrayList<String> result = new ArrayList<String>(4);
		StringTokenizer st = new StringTokenizer(str, String.valueOf(separator));
		while (st.hasMoreTokens()) {
			result.add(st.nextToken());
		}
		return result;
	}

	private String basedir;

	private String bundleids;

	private String prodgeneration;

	/* (non-Javadoc)
	 * @see org.apache.tools.ant.Task#execute()
	 */
	@Override
	public void execute() throws BuildException {
		
		validateInput();
		
		File baseDirFile = new File(basedir);
		if (!baseDirFile.exists()) {
			throw new BuildException("basedir '"+ basedir +"' does not exist.");
		}
		if (!baseDirFile.isDirectory()) {
			throw new BuildException("basedir '"+ basedir +"' is not a directory.");
		}
		
		StringTokenizer st = new StringTokenizer(bundleids, AntConstants.LIST_SEPARATOR_STRING);
		
		while (st.hasMoreTokens()) {
			String bundleId = st.nextToken().trim();
			if (bundleId.length() > 0) {
				
				File bundleProjectFile = new File(baseDirFile, bundleId);
				if (!bundleProjectFile.exists() || !bundleProjectFile.isDirectory()) {
					throw new BuildException("'"+ bundleProjectFile.getAbsolutePath() +"' does not exist or is not a directory.");
				}

				String versionPropsFileName = MessageFormat.format(AntConstants.VERSION_PROPERTIES_FILE_NAME, prodgeneration);
				File versionPropsFile = new File(bundleProjectFile, versionPropsFileName);
				if (!versionPropsFile.exists() || !versionPropsFile.isFile()) {
					throw new BuildException("'"+ versionPropsFile.getAbsolutePath() +"' does not exist or is not a directory.");
				}
				
				Properties props = new Properties();
				InputStream in = null;
				try {
					in = new BufferedInputStream(new FileInputStream(versionPropsFile)); 
					props.load(in);
				} catch (FileNotFoundException e) {
					throw new BuildException(e);
				} catch (IOException e) {
					throw new BuildException(e);
				}
				finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException ignored) {
						}
					}
				}

				try {
					process(bundleId, props, versionPropsFile, baseDirFile);
				} catch (Exception e) {
					e.printStackTrace();
					throw new BuildException(e);
				}

			}
		}
	}
	
	public String getBasedir() {
		return basedir;
	}

	public String getBundleids() {
		return bundleids;
	}
	
	public String getProdgeneration() {
		return prodgeneration;
	}
	private String lookUpVersion(String bundleId, File baseDirFile) {
		
		File bundleProjectFile = new File(baseDirFile, bundleId);
		if (!bundleProjectFile.exists() || !bundleProjectFile.isDirectory()) {
			throw new BuildException("'"+ bundleProjectFile.getAbsolutePath() +"' does not exist or is not a directory.");
		}

		String versionPropsFileName = MessageFormat.format(AntConstants.VERSION_PROPERTIES_FILE_NAME, prodgeneration);
		File versionPropsFile = new File(bundleProjectFile, versionPropsFileName);
		if (!versionPropsFile.exists() || !versionPropsFile.isFile()) {
			throw new BuildException("'"+ versionPropsFile.getAbsolutePath() +"' does not exist or is not a directory.");
		}
		
		Properties props = new Properties();
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(versionPropsFile)); 
			props.load(in);
		} catch (FileNotFoundException e) {
			throw new BuildException(e);
		} catch (IOException e) {
			throw new BuildException(e);
		}
		finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {
				}
			}
		}
		String bundleVersion = props.getProperty(AntConstants.COM_DRGARBAGE_VERSION);
		
		if (bundleVersion == null || bundleVersion.trim().length() == 0) {
			throw new BuildException("'"+ AntConstants.COM_DRGARBAGE_VERSION +"' not available in '"+ versionPropsFile.getAbsolutePath() +"'");
		}
		bundleVersion = bundleVersion.trim();
		
		return bundleVersion;
	}
	
	private void process(String bundleId, Properties props, File versionPropsFile, File baseDirFile) throws IOException, XPathExpressionException, TransformerFactoryConfigurationError, TransformerException {
		
		String bundleVersion = props.getProperty(AntConstants.COM_DRGARBAGE_VERSION);
		
		if (bundleVersion == null || bundleVersion.trim().length() == 0) {
			throw new BuildException("'"+ AntConstants.COM_DRGARBAGE_VERSION +"' not available in '"+ versionPropsFile.getAbsolutePath() +"'");
		}
		
		bundleVersion = bundleVersion.trim();
		
		File bundleProjectFile = new File(baseDirFile, bundleId);
		if (!bundleProjectFile.exists() || !bundleProjectFile.isDirectory()) {
			throw new BuildException("'"+ bundleProjectFile.getAbsolutePath() +"' does not exist or is not a directory.");
		}
		String manifestPath = MessageFormat.format(
				AntConstants.META_INF_MANIFEST_MF, 
				new Object[]{
						bundleProjectFile.getAbsolutePath(),
						File.separator
				}
			);
		
		InputStream mfIn = null;
		Manifest mf = new Manifest();
		File manifestFile = new File(manifestPath);
		try {
			mfIn = new BufferedInputStream(new FileInputStream(manifestFile));
			mf.read(mfIn);
		} finally {
			if (mfIn != null) {
				try {
					mfIn.close();
				} catch (Exception ignored) {
				}
			}
		}
		
		String bundleVersionQ = bundleVersion + AntConstants.VERSION_SEPARATOR + AntConstants.VERSION_QUALIFIER;
		Attributes attrs = mf.getMainAttributes();
		attrs.putValue(
				AntConstants.BUNDLE_VERSION, 
				bundleVersionQ);
		
		String featureXmlPath = MessageFormat.format(
				AntConstants.PATH_FEATURE_XML, 
				new Object[]{
						baseDirFile.getAbsolutePath(),
						File.separator,
						bundleId
				}
			);
		File featureXmlFile = new File(featureXmlPath);
		FeatureXmlModifier featureXmlModifier = new FeatureXmlModifier(featureXmlFile, bundleId, bundleVersionQ);
		
		StringBuilder sb = new StringBuilder();
		
		Set<Object> sortedKeys = new TreeSet<Object>(props.keySet());
		
		for (Object o : sortedKeys) {
			String key = (String) o;
			if (!AntConstants.COM_DRGARBAGE_VERSION.equals(key)) {
				String val = props.getProperty(key);
				String match = AntConstants.MATCH_EQUIVALENT;
				String target = AntConstants.TARGET_default;
				
				List<String> fields = split(val, AntConstants.VERSION_PROPERTIES_FIELD_SEPARATOR);
				if (fields.size() > 1) {
					match = fields.get(1);
					if (fields.size() > 2) {
						target = fields.get(2);
					}
				}
				
				String version = fields.get(0);
				if (key.startsWith(AntConstants.COM_DRGARBAGE_)) {
					/* lookup the version in the bundle project */
					version = lookUpVersion(key, baseDirFile);
				}

				
				if (target.indexOf(AntConstants.TARGET_m) >= 0) {
					/* mf is a target */
					if (sb.length() > 0) {
						sb.append(AntConstants.MANIFEST_COMMA);
					}
					String interval = createMfInterval(version, match);
					sb.append(key)
						.append(AntConstants.MANIFEST_SEMICOLON)
						.append(AntConstants.MANIFEST_BUNDLE_VERSION)
						.append(AntConstants.MANIFEST_EQUAL)
						.append(interval);
				}

				if (target.indexOf(AntConstants.TARGET_f) >= 0
						|| target.indexOf(AntConstants.TARGET_i) >= 0
						|| target.indexOf(AntConstants.TARGET_p) >= 0
				) {
					/* modify feature.xml */
					boolean visited = false;
					String versionQ = version;
					if (AntConstants.MATCH_PERFECT.equals(match) && key.startsWith(AntConstants.COM_DRGARBAGE_)) {
						versionQ += AntConstants.VERSION_SEPARATOR + AntConstants.VERSION_QUALIFIER;
					}

					if (target.indexOf(AntConstants.TARGET_f) >= 0) {
						featureXmlModifier.addImportFeature(key, match, versionQ);
						visited = true;
					}
					if (target.indexOf(AntConstants.TARGET_i) >= 0) {
						if (visited) {
							throw new BuildException("Cannot import '"+ key +"' multiple times into '"+ featureXmlPath +"'.");
						}
						featureXmlModifier.addImportPlugin(key, match, versionQ);
						visited = true;
					}
					if (target.indexOf(AntConstants.TARGET_p) >= 0) {
						if (visited) {
							throw new BuildException("Cannot import '"+ key +"' multiple times into '"+ featureXmlPath +"'.");
						}
						featureXmlModifier.addPlugin(key, versionQ);
					}
				}
			}
		}
		
		if (sb.length() == 0) {
			attrs.remove(AntConstants.REQUIRE_BUNDLE);
		}
		else {
			attrs.putValue(AntConstants.REQUIRE_BUNDLE, sb.toString());
		}
		
		
		featureXmlModifier.write();
		
		OutputStream mfOut = null;
		try {
			mfOut = new BufferedOutputStream(new FileOutputStream(manifestFile));
			mf.write(mfOut);
		} finally {
			if (mfOut != null) {
				try {
					mfOut.close();
				} catch (Exception ignored) {
				}
			}
		}

	}
	public void setBasedir(String basedir) {
		this.basedir = basedir;
	}
	public void setBundleids(String bundleids) {
		this.bundleids = bundleids;
	}
	public void setProdgeneration(String prodgeneration) {
		this.prodgeneration = prodgeneration;
	}

	
	private void validateInput() throws BuildException {
		if (bundleids == null || bundleids.length() == 0) {
			throw new BuildException("You must provide a comma-separated list of bundle ID in the bundleid attribute.");
		}
		
		if (basedir == null || basedir.length() == 0) {
			throw new BuildException("You must provide the path to the workspace base directory in the basedir attribute.");
		}

		if (prodgeneration == null || prodgeneration.length() == 0) {
			throw new BuildException("You must provide the target product generation version number like '3.5' in the prodgeneration attribute.");
		}

	}
	
}
