package org.indiarose.net;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLSocketFactory;

public class MySSLSocketFactory extends SSLSocketFactory
{
	private SSLContext _sslContext;
	
	
	public MySSLSocketFactory(KeyStore truststore, SSLContext sslContext)
			throws NoSuchAlgorithmException, KeyManagementException,
			KeyStoreException, UnrecoverableKeyException {
		super(truststore);
		
		_sslContext = sslContext;
	}
	
	@Override
	public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
	    return _sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
	}

	@Override
	public Socket createSocket() throws IOException {
	    return _sslContext.getSocketFactory().createSocket();
	}
	
}