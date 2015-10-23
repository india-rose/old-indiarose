package storm.xmlbinder;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import android.util.Log;

/**
 * Class to manage data writing to xml file.
 * 
 * This class use the same XmlElement form than the XmlReader one.
 * 
 * @author Storm <j-storm@hotmail.fr>
 */
public class XmlWriter 
{
	/**
	 * The root XmlElement to describe how to bind the data to xml file.
	 */
	protected XmlElement m_rootElement = null;
	
	/**
	 * Boolean to enable the pretty output mode.
	 * This mode enable tabs and newline to produce readable xml.
	 */
	protected Boolean m_prettyOutput = false;
	
	/**
	 * Construct an instance of XmlWriter with the specified xml root element.
	 * @param _rootElement : the root element of the document.
	 */
	public XmlWriter(XmlElement _rootElement)
	{
		this.m_rootElement = _rootElement;
	}
	
	/**
	 * Method to change the pretty output mode.
	 * @param _enable : if true, enable pretty output mode.
	 */
	public void setPrettyOutput(Boolean _enable)
	{
		this.m_prettyOutput = _enable;
	}
	
	/**
	 * Method to write data to file using rules used in initialization.
	 * @param _data : the data object to write.
	 * @param _filename : the name of the xml file to create.
	 * @throws XmlBinderException
	 */
	public void write(Object _data, String _filename) throws XmlBinderException
	{
		try
		{
			Document xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			
			//in write, write content first then write all attributes and all child
			m_rootElement.write(_data, xmlDocument, xmlDocument);
	 
			DOMSource source = new DOMSource(xmlDocument);
			boolean b = new File(_filename).createNewFile();
			if(!b){
				//Log.d("file - XMLWRITER", "��a cr��e pas cette merde "+_filename);
			}
			FileOutputStream output = new FileOutputStream(_filename);
			StreamResult result = new StreamResult(output);
	 
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			if(this.m_prettyOutput)
			{
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			}
			
			transformer.transform(source, result);
			output.flush();
			output.close();
		}
		catch(Exception ex)
		{
			Log.wtf("XmlWriter", ex);
			throw new XmlBinderException();
		}
	}
	
	/**
	 * Method to get the generated xml content produced by the conversion of data.
	 * @param _data : the data object to convert into xml.
	 * @return the xml generated content.
	 * @throws XmlBinderException
	 */
	public String getXml(Object _data) throws XmlBinderException
	{
		try
		{
			Document xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			
			//in write, write content first then write all attributes and all child
			m_rootElement.write(_data, xmlDocument, xmlDocument);
	 
			DOMSource source = new DOMSource(xmlDocument);
			StringWriter stringResult = new StringWriter();
			StreamResult result = new StreamResult(stringResult);
			// StreamResult result = new StreamResult(System.out);
	 
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			if(this.m_prettyOutput)
			{
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			}
			transformer.transform(source, result);
			return stringResult.toString();
		}
		catch(Exception ex)
		{
			throw new XmlBinderException();
		}
	}
}
