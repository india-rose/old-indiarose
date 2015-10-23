package storm.communication;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.lang.reflect.Method;

import android.util.Log;

/**
 * Internal class to manage slot in Mapper object.
 * @see storm.communication.Mapper
 * 
 * @author Storm <j-storm@hotmail.fr>
 */
class Connection
{
	/**
	 * The object from which called the slot method.
	 */
	protected Object m_receiver;
	
	/**
	 * The name of the slot method.
	 */
	protected String m_slotMethod;
	
	/**
	 * Create an empty connection.
	 * Used through static method to create connection.
	 */
	protected Connection()
	{
		this.m_receiver = null;
		this.m_slotMethod = null;
	}
	
	/**
	 * Create a connection object.
	 * @param _receiver : the object from which invoke the slot method.
	 * @param _slotMethod : the slot method name.
	 * @throws MapperException usually due to the non method existence.
	 */
	public Connection(Object _receiver, String _slotMethod) throws MapperException
	{
		this.m_receiver = _receiver;
		this.m_slotMethod = _slotMethod;
		
		if(!checkMethodExistence())
		{
			throw new MapperException("Method " + _slotMethod + " does not exists in object of type " + _receiver.getClass());
		}
	}
	
	/**
	 * Method to call the slot with specified parameters.
	 * @param _param : parameters to pass to the slot method.
	 * @throws MapperException usually happened if the method does not exists with the specified parameters or if an exception happened in the slot method.
	 */
	public void call(Object[] _param) throws MapperException
	{
		//Retrieve type of parameters.
		Class<?> [] paramType = new Class[_param.length];
		int i = 0;
		for(Object o : _param)
		{
			paramType[i++] = o.getClass();
		}
		
		try
		{
			Method method = null;
			Exception exception = null;
			try
			{
				//try to get the method with complete match of parameters.
				method = this.m_receiver.getClass().getMethod(this.m_slotMethod, paramType);
			}
			catch(Exception ex)
			{
				//if an exact method does not exist, check if one exist and can accept parameters.
				exception = ex;
				
				Method [] methods = this.m_receiver.getClass().getMethods();
				for(Method m : methods)
				{
					if(m.getName().equals(this.m_slotMethod))
					{
						//check if param is ok
						Class<?> [] parameters = m.getParameterTypes();
						if(parameters.length == paramType.length)
						{
							boolean ok = true;
							for(int j = 0 ; j < parameters.length ; ++j)
							{
								if(!parameters[j].isAssignableFrom(paramType[j]))
								{
									ok = false;
									break;
								}
							}
							
							if(ok)
							{
								method = m;
								break;
							}
						}
					}
				}
			}
			
			if(method == null)
			{
				//if no method has been found, throw exception as method as not been found.
				throw exception;
			}
			else
			{
				//invoke method with parameters.
				method.invoke(this.m_receiver, _param);
			}
		} 
		catch (Exception e)
		{
			String strType = "";
			
			Log.wtf("Connection", "Error exception", e);
			
			for(Class<?> c : paramType)
			{
				strType += c + " ; ";
			}
			
			throw new MapperException("Method " + this.m_slotMethod + " in object of type " + this.m_receiver.getClass() + " is not applicable for param type " + strType);
		}
	}
	
	/**
	 * Check if a method with the slot method name exists in the object.
	 * @return true if exists, false otherwise.
	 */
	protected boolean checkMethodExistence()
	{
		for(Method m : this.m_receiver.getClass().getMethods())
		{
			if(m.getName().equals(this.m_slotMethod))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Create a connection object with a receiver.
	 * Used to remove easily in Mapper.disconnect
	 */
	public static Connection create(Object _receiver)
	{
		Connection c = new Connection();
		c.m_receiver = _receiver;
		return c;
	}
	
	/**
	 * Create a connection object with a receiver and a slot method.
	 * Used to remove easily in Mapper.disconnect
	 */
	public static Connection create(Object _receiver, String _slotMethod)
	{
		Connection c = new Connection();
		c.m_receiver = _receiver;
		c.m_slotMethod = _slotMethod;
		return c;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object _other)
	{
		if(_other instanceof Connection)
		{
			Connection other = (Connection)_other;
			boolean ok = true;
			if(this.m_slotMethod != null && other.m_slotMethod != null)
			{
				ok = ok & this.m_slotMethod.equals(other.m_slotMethod);
			}
			
			if(this.m_receiver != null && other.m_receiver != null)
			{
				ok = ok & this.m_receiver.equals(other.m_receiver);
			}
			return ok;
		}
		return false;
	}
}
