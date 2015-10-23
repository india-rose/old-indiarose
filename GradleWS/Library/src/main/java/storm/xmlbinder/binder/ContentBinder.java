package storm.xmlbinder.binder;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import storm.xmlbinder.XmlBinderException;
import storm.xmlbinder.transformer.TransformerInterface;

/**
 * Binder to bind content of a xml tag to a field.
 * 
 * @author Storm <j-storm@hotmail.fr>
 */
public class ContentBinder extends AbstractBinder 
{
	/**
	 * The transformer to apply on data before storing it.
	 */
	protected TransformerInterface m_transformer = null;
	
	/**
	 * Construct an instance of the binder.
	 * @param _fieldName : the name of the field.
	 * @param _transformer : the transformer instance to use.
	 */
	public ContentBinder(String _fieldName, TransformerInterface _transformer)
	{
		super(_fieldName);
		this.m_transformer = _transformer;
	}
	
	/*
	 * (non-Javadoc)
	 * @see storm.xmlbinder.binder.AbstractBinder#read(java.lang.Object, java.lang.String)
	 */
	@Override
	public Object read(Object _data, String _content) throws XmlBinderException 
	{
		Object processedContent = this.m_transformer.read(_content);
		setField(_data, this.m_fieldName, processedContent);
		return _data;
	}

	/*
	 * (non-Javadoc)
	 * @see storm.xmlbinder.binder.AbstractBinder#write(java.lang.Object, org.w3c.dom.Node, org.w3c.dom.Document)
	 */
	@Override
	public void write(Object _data, Node _node, Document _document) throws XmlBinderException 
	{
		// _node.appendChild(_document.createTextNode(this.m_transformer.write(getField(_data, this.m_fieldName))));
		_node.appendChild(_document.createTextNode(this.m_transformer.write(_data)));
	}

}
