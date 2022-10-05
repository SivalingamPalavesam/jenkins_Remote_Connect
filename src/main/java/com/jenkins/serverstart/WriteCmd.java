package com.jenkins.serverstart;
import java.io.InputStream;
import java.io.OutputStream;

class WriteCmd implements Runnable
{

	private final OutputStream ostrm_;
	private final InputStream istrm_;
	
	public WriteCmd(InputStream istrm, OutputStream ostrm) {
		istrm_ = istrm;
		ostrm_ = ostrm;
	}
	public void run() {
		try
		{
			final byte[] buffer = new byte[1000000]; //1 mb
			for (int length = 0; (length = istrm_.read(buffer)) != -1; )
			{
				ostrm_.write(buffer, 0, length);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}