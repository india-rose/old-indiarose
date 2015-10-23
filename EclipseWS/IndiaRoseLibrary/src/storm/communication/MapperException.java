package storm.communication;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

/**
 * Exception class for all Mapper relative error.
 * 
 * @author Storm <j-storm@hotmail.fr>
 */
public class MapperException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3329151155034899436L;

	/**
	 * Construct an empty exception.
	 */
	public MapperException()
	{
		super();
	}
	
	/**
	 * Construct an exception with a message.
	 * @param _message : the message of the exception.
	 */
	public MapperException(String _message)
	{
		super(_message);
	}

}
