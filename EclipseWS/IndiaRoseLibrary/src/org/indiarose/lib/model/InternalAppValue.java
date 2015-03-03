package org.indiarose.lib.model;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */


/**
 * This class store internal data relative to application data.
 * Such as login, password and database version.
 * 
 * @author Julien Mialon <mialon.julien@gmail.com>
 */
public class InternalAppValue
{
	/**
	 * User login.
	 */
	public String login = "";
	/**
	 * User password.
	 */
	public String password = "";
	/**
	 * Database version.
	 */
	public int version = 0;
	
	@Override
	public boolean equals(Object _other)
	{
		if(!(_other instanceof InternalAppValue))
		{
			return false;
		}
		
		InternalAppValue other = (InternalAppValue) _other;
		
		return  (other.login == this.login) &&
				(other.password == this.password) &&
				(other.version == this.version);
	}
	
	@Override
	public InternalAppValue clone()
	{
		InternalAppValue other = new InternalAppValue();
		
		other.login = this.login;
		other.password = this.password;
		other.version = this.version;
		
		return other;
	}
	
	/**
	 * Increment the current version of the database version by one.
	 */
	public void incrementVersion()
	{
		this.version += 1;
	}
}
