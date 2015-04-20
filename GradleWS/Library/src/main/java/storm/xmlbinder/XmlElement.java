package storm.xmlbinder;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import storm.xmlbinder.binder.AbstractBinder;
import storm.xmlbinder.binder.AbstractCollectionBinder;

/**
 * This class represent a node of the xml file.
 * Use it to define how to bind data between file and models class.
 * 
 * @author Storm <j-storm@hotmail.fr>
 */
public class XmlElement 
{
	/**
	 * The identifier of the xml element. (it is the node name)
	 */
	protected String m_identifier = "";
	
	/**
	 * The list of all children of the current element.
	 */
	protected ArrayList<XmlElement> m_children = new ArrayList<XmlElement>();
	
	/**
	 * The list of all attributes of the current element.
	 */
	protected ArrayList<XmlAttribute> m_attributes = new ArrayList<XmlAttribute>();
	
	/**
	 * The binder associated with the current element.
	 */
	protected AbstractBinder m_binder = null;
	
	/**
	 * Construct an instance with the specified identifier and binder.
	 * @param _identifier : name of the xml node.
	 * @param _binder : binder to use.
	 */
	public XmlElement(String _identifier, AbstractBinder _binder)
	{
		this.m_identifier = _identifier;
		this.m_binder = _binder;
	}
	
	/**
	 * Method to get the identifier of the element.
	 * @return the identifier.
	 */
	public String getIdentifier()
	{
		return this.m_identifier;
	}
	
	/**
	 * Method to get the list of all children of the element.
	 * @return the list of children.
	 */
	public ArrayList<XmlElement> getChildren()
	{
		return this.m_children;
	}
	
	/**
	 * Method to add a child to the element.
	 * @param _element : the element to add.
	 */
	public void addChild(XmlElement _element)
	{
		this.m_children.add(_element);
	}
	
	/**
	 * Method to add an attribute to the element.
	 * @param _attribute : the attribute to add.
	 */
	public void addAttribute(XmlAttribute _attribute)
	{
		this.m_attributes.add(_attribute);
	}

	/**
	 * Method to execute reading action on this element.
	 * @param _parentObject : the parent data object.
	 * @param _content : the content of the xml tag.
	 * @param _attributes : the list of all attributes associated with the xml tag.
	 * @return the child data object. (or the parent if no object is created)
	 * @throws XmlBinderException
	 */
	public Object read(Object _parentObject, String _content, ArrayList<XmlInternalAttribute> _attributes) throws XmlBinderException 
	{
		Object newData = this.m_binder.read(_parentObject, _content);
		
		int attributesRuleLength = this.m_attributes.size();
		int attributesLength = _attributes.size();
		
		XmlAttribute attribute;
		XmlInternalAttribute internalAttribute;
		
		for(int i = 0 ; i < attributesRuleLength ; ++i)
		{
			attribute = this.m_attributes.get(i);
			for(int j = 0 ; j < attributesLength ; ++j)
			{
				internalAttribute = _attributes.get(j);
				if(attribute.getIdentifier().equalsIgnoreCase(internalAttribute.getIdentifier()))
				{
					attribute.read(newData, internalAttribute.getValue());
				}
			}
		}
		
		return newData;
	}
	
	/**
	 * Method to execute writing action on the current node.
	 * @param _data : the data object.
	 * @param _currentNode : the current Xml node to write data.
	 * @param _document : the Xml document used to create Xml element.
	 * @throws XmlBinderException
	 */
	public void write(Object _data, Node _currentNode, Document _document) throws XmlBinderException
	{
		if(this.m_binder instanceof AbstractCollectionBinder)
		{
			// collection => iterate through it to create all element
			@SuppressWarnings("unchecked")
			Collection<Object> collection = (Collection<Object>)this.m_binder.getAffectedField(_data);
			Iterator<Object> it = collection.iterator();
			while(it.hasNext())
			{
				processWriteElement(it.next(), _currentNode, _document);
			}
		}
		else
		{
			processWriteElement(this.m_binder.getAffectedField(_data), _currentNode, _document);
			// simple element => create node and call binder
		}
	}
	
	/**
	 * Method to write the xml content.
	 * Separate from the write method because of code duplication.
	 * @param _data : the data object.
	 * @param _currentNode : the current xml node.
	 * @param _document : the document used to create xml elements.
	 * @throws XmlBinderException
	 */
	protected void processWriteElement(Object _data, Node _currentNode, Document _document) throws XmlBinderException
	{
		Element newNode = _document.createElement(this.m_identifier);
		this.m_binder.write(_data, newNode, _document);
		
		int length = this.m_attributes.size();
		for(int i = 0 ; i < length ; ++i)
		{
			this.m_attributes.get(i).write(_data, newNode, _document);
		}
		
		length = this.m_children.size();
		for(int i = 0 ; i < length ; ++i)
		{
			this.m_children.get(i).write(_data, newNode, _document);
		}
		
		_currentNode.appendChild(newNode);
	}
}
