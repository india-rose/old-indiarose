package storm.xmlbinder.transformer;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

/**
 * Class to turn integer to String and vice versa.
 * @author Storm <j-storm@hotmail.fr>
 *
 */
public class IntegerTransformer implements TransformerInterface 
{
	/*
	 * (non-Javadoc)
	 * @see storm.xmlbinder.transformer.TransformerInterface#read(java.lang.String)
	 */
	public Object read(String _value) 
	{
		return Integer.parseInt(_value);
	}

	/*
	 * (non-Javadoc)
	 * @see storm.xmlbinder.transformer.TransformerInterface#write(java.lang.Object)
	 */
	public String write(Object _value) 
	{
		return ((Integer)_value).toString();
	}

}
