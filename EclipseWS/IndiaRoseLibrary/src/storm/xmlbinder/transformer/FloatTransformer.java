package storm.xmlbinder.transformer;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

public class FloatTransformer implements TransformerInterface
{
	public Object read(String _value)
	{
		return Float.parseFloat(_value);
	}

	public String write(Object _value)
	{
		return ((Float)_value).toString();
	}

}
