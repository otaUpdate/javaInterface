package net.otaupdate.app.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class OutputStreamWithCounter extends OutputStream
{
	public interface ProgressOutputStreamListener
	{
		public void onUpdate(long totalNumBytesWrittenIn);
	}
	
	
	private long numBytesWritten = 0;
	private final OutputStream os;
	
	private final List<ProgressOutputStreamListener> listeners = new ArrayList<OutputStreamWithCounter.ProgressOutputStreamListener>();
	
	
	public OutputStreamWithCounter(OutputStream osIn)
	{
		this.os = osIn;
	}
	
	
	public void addListener(ProgressOutputStreamListener listenerIn)
	{
		this.listeners.add(listenerIn);
	}
	

	@Override
    public void write(int b) throws IOException
	{
		os.write(b);
		this.numBytesWritten++;
        for( ProgressOutputStreamListener currListener : this.listeners )
        {
        	currListener.onUpdate(this.numBytesWritten);
        }
    }
	

    @Override
    public void write(byte[] b) throws IOException
    {
    	os.write(b);
    	this.numBytesWritten += b.length;
    	for( ProgressOutputStreamListener currListener : this.listeners )
        {
        	currListener.onUpdate(this.numBytesWritten);
        }
    }
    

    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
    	os.write(b, off, len);
    	this.numBytesWritten += len;
    	for( ProgressOutputStreamListener currListener : this.listeners )
        {
        	currListener.onUpdate(this.numBytesWritten);
        }
    }
}
