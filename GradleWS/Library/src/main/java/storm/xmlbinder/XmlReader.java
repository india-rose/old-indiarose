package storm.xmlbinder;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

/**
 * 
 * Class to read xml document and bind the content to object.
 * 
 * This class use Binder to associate content to element field.
 * You have to create the Binder hierarchy to use the XmlReader. 
 * 
 * @author Storm <j-storm@hotmail.fr>
 */
public class XmlReader extends DefaultHandler 
{
	/**
	 * The root element of the xml document.
	 */
	protected ArrayList<XmlElement> m_rootElements = null;
	
	/**
	 * Xml process management
	 */
	protected XmlElement m_currentElement = null;
	protected Object m_currentDataObject = null;
	
	protected Stack<XmlElement> m_elements = null;
	protected Stack<Object> m_dataObjects = null;
	/**
	 * End
	 */
	
	/**
	 * Stack SAX problem management
	 */
	protected Boolean m_isWaitingElement = false;
	protected XmlElement m_waitingElement = null;
	protected ArrayList<XmlInternalAttribute> m_waitingAttributes = null;
	protected StringBuilder m_waitingContent = null;
	protected Boolean m_isEnd = false;
	
	protected Stack<StringBuilder> m_contents = null;
	
	/**
	 * End
	 */
	
	/**
	 * Construct the instance of XmlReader with the specified root element description.
	 * @param _rootElement : the root element description.
	 */
	public XmlReader(XmlElement _rootElement)
	{
		this.m_rootElements = new ArrayList<XmlElement>();
		this.m_rootElements.add(_rootElement);
	}
	
	public XmlReader(ArrayList<XmlElement> _rootElements)
	{
		this.m_rootElements = _rootElements;
	}
	
	/**
	 * Method to read and parse the Xml Document.
	 * @param _filename : the path to the xml file.
	 * @return Object : the root bound object.
	 * @throws XmlBinderException
	 */
	public Object read(String _filename) throws XmlBinderException
	{
		initialize();
		try
		{
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			
			FileInputStream input = new FileInputStream(_filename);
			parser.parse(input, this);
			input.close();
		}
		catch(Exception ex)
		{
			Log.d("XmlReader", "Exception on file " + _filename);
			throw new XmlBinderException();
		}
		
		return this.m_currentDataObject;
	}
	
	/**
	 * Method to read and parse xml content.
	 * @param _content : the xml content to parse.
	 * @return the bound object.
	 * @throws XmlBinderException
	 */
	public Object readFromContent(String _content) throws XmlBinderException
	{
		initialize();
		try
		{
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(new InputSource(new StringReader(_content)), this);
		}
		catch(Exception ex)
		{
			throw new XmlBinderException();
		}
		
		return this.m_currentDataObject;
	}
	
	/**
	 * Method to initialize some variable before reading.
	 */
	protected void initialize()
	{
		this.m_currentElement = new XmlElement("", null);
		for(int i = 0 ; i < this.m_rootElements.size() ; ++i)
		{
			this.m_currentElement.addChild(this.m_rootElements.get(i));
		}
		this.m_currentDataObject = null;
		
		this.m_elements = new Stack<XmlElement>();
		this.m_dataObjects = new Stack<Object>();
		
		this.m_isWaitingElement = false;
		this.m_waitingElement = null;
		this.m_waitingAttributes = null;
		this.m_waitingContent = null;
		this.m_isEnd = false;
		this.m_contents = new Stack<StringBuilder>();
	}
	
	/*
	 * Deux possibilit�� : 
	 * 	C'est un text node => il ne doit ��tre trait�� que lorsque endElement est appel��
	 * 	C'est un parent node => il doit ��tre trait�� si startElement est appel�� avant endElement
	 */
	@Override
	public void startElement(String _uri, String _localName, String _qName, Attributes _attributes) throws SAXException 
	{
		if(this.m_isEnd)
		{
			return;
		}
		processElementSave();
		
		String elementName = _qName;
		m_waitingAttributes = extractAttributes(_attributes);
		
		ArrayList<XmlElement> children = this.m_currentElement.getChildren();
		XmlElement element = null;
		int length = children.size();
		
		for(int i = 0 ; i < length ; ++i)
		{
			element = children.get(i);
			if(element.getIdentifier().equalsIgnoreCase(elementName))
			{
				this.m_elements.push(this.m_currentElement);
				this.m_dataObjects.push(this.m_currentDataObject);
				
				
				this.m_isWaitingElement = true;
				this.m_waitingElement = element;
				this.m_waitingContent = new StringBuilder();
				
				break;
			}
		}
	}

	@Override
	public void characters(char[] _str, int _offset, int _length) throws SAXException 
	{
		if(this.m_isEnd)
		{
			return;
		}
		if(m_waitingContent != null)
		{
			m_waitingContent.append(_str, _offset, _length);
		}
	}

	@Override
	public void endElement(String _uri, String _localName, String _qName) throws SAXException
	{
		if(this.m_isEnd)
		{
			return;
		}
		processElementSave();
		if(m_currentElement.getIdentifier().equalsIgnoreCase(_qName))
		{
			
			//pop from stack
			
			if(this.m_dataObjects.size() > 1) // 1 : to do not process the root virtual element created by the read function
			{
				this.m_currentDataObject = this.m_dataObjects.pop();
				this.m_currentElement = this.m_elements.pop();
			}
			else
			{
				this.m_isEnd = true;
			}
			
		}
		else
		{
			throw new SAXException();
		}
	}

	
	/**
	 * Method to convert an Attributes list (SAX) to a list of XmlInternalAttributes
	 * @param _attributes : the attributes list provided by SAX
	 * @return the converted list.
	 */
	protected ArrayList<XmlInternalAttribute> extractAttributes(Attributes _attributes)
	{
		ArrayList<XmlInternalAttribute> result = new ArrayList<XmlInternalAttribute>();
		
		int length = _attributes.getLength();
		
		for(int i = 0 ; i < length ; ++i)
		{
			result.add(new XmlInternalAttribute(_attributes.getQName(i), _attributes.getValue(i)));
		}
		
		return result;
	}
	
	/**
	 * Method to save an element when end or a new start element is reached.
	 */
	protected void processElementSave()
	{
		if(this.m_isWaitingElement)
		{
			try
			{
				this.m_currentDataObject = this.m_waitingElement.read(this.m_currentDataObject, this.m_waitingContent.toString(), this.m_waitingAttributes);
			}
			catch(XmlBinderException ex)
			{
			}
			
			this.m_currentElement = this.m_waitingElement;
					
			this.m_isWaitingElement = false;
			this.m_waitingElement = null;
			this.m_waitingAttributes = null;
			this.m_waitingContent = null;
		}
	}
}
