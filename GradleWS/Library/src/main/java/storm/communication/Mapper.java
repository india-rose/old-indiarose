package storm.communication;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Class to manage signal/slots mapping.
 * Once a mapping is made from a signal to a slot, the signal can be raised to call the slot.
 * 
 * @author Storm <j-storm@hotmail.fr>
 */
public class Mapper
{
	// Hashtable<Object sender, Hashtable<String signal, ArrayList<Connection object>>>
	/**
	 * Nested hash table to store signal / slot mapping.
	 */
	protected static Hashtable<Object, Hashtable<String, ArrayList<Connection>>> m_mapping = new Hashtable<Object, Hashtable<String, ArrayList<Connection>>>();
	
	/**
	 * Method to connect a signal (sender, signal) to a slot (receiver, method).
	 * @param _sender : the object which will send the signal.
	 * @param _signal : the name of the signal.
	 * @param _receiver : the object which will receive the signal and which from the slot method will be called.
	 * @param _slotMethod : the name of the slot method.
	 * @throws MapperException if the mapping can not be established. 
	 * 		This error generally happened if the slot method name does not exists or is not visible in the receiver object.
	 */
	public static void connect(Object _sender, String _signal, Object _receiver, String _slotMethod) throws MapperException
	{
		if(!m_mapping.containsKey(_sender))
		{
			m_mapping.put(_sender, new Hashtable<String, ArrayList<Connection>>());
		}
		
		if(!m_mapping.get(_sender).containsKey(_signal))
		{
			m_mapping.get(_sender).put(_signal, new ArrayList<Connection>());
		}
		
		Connection c = new Connection(_receiver, _slotMethod);
		m_mapping.get(_sender).get(_signal).add(c);
	}
	
	/**
	 * Method to raise a signal.
	 * @param _sender : the sender object, usually you use this.
	 * @param _signal : the signal name.
	 * @param param : the parameters for the slot method. Optional if no parameters is needed.
	 * @throws MapperException usually due to the non method existence with this parameters or an exception in the slot method.
	 */
	public static void emit(Object _sender, String _signal, Object... param) throws MapperException
	{
		if(m_mapping.containsKey(_sender) && m_mapping.get(_sender).containsKey(_signal))
		{
			Connection [] connections = m_mapping.get(_sender).get(_signal).toArray(new Connection[]{});
			for(Connection c : connections)
			{
				c.call(param);
			}
		}
	}
	
	/**
	 * Method to disconnect all slot connected to an object.
	 * @param _sender : the sender object.
	 */
	public static void disconnect(Object _sender)
	{
		if(m_mapping.containsKey(_sender))
		{
			m_mapping.remove(_sender);
		}
	}
	
	/**
	 * Method to disconnect all slot connected to a specified signal.
	 * @param _sender : the sender object.
	 * @param _signal : the signal name.
	 */
	public static void disconnect(Object _sender, String _signal)
	{
		if(m_mapping.containsKey(_sender) && m_mapping.get(_sender).containsKey(_signal))
		{
			m_mapping.get(_sender).remove(_signal);
		}
	}
	
	/**
	 * Method to disconnect all slot from a specified object connected to a specified signal.
	 * @param _sender : the sender object.
	 * @param _signal : the signal name.
	 * @param _receiver : the receiver object.
	 */
	public static void disconnect(Object _sender, String _signal, Object _receiver)
	{
		if(m_mapping.containsKey(_sender) && m_mapping.get(_sender).containsKey(_signal))
		{
			while(m_mapping.get(_sender).get(_signal).remove(Connection.create(_signal)));
		}
	}
	
	/**
	 * Method do disconnect a slot from a signal.
	 * @param _sender : the sender object.
	 * @param _signal : the signal name.
	 * @param _receiver : the receiver object.
	 * @param _slotMethod : the slot method name.
	 */
	public static void disconnect(Object _sender, String _signal, Object _receiver, String _slotMethod)
	{
		if(m_mapping.containsKey(_sender) && m_mapping.get(_sender).containsKey(_signal))
		{
			while(m_mapping.get(_sender).get(_signal).remove(Connection.create(_signal, _slotMethod)));
		}
	}
}
