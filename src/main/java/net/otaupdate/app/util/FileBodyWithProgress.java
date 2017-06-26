package net.otaupdate.app.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.mime.content.FileBody;

import net.otaupdate.app.util.OutputStreamWithCounter.ProgressOutputStreamListener;


public class FileBodyWithProgress extends FileBody implements ProgressOutputStreamListener
{
	public interface ProgessFileEntityListener
	{
		public void onUpdate(long totalNumBytesWrittenIn, long totalNumBytesExpected);
	}
	
	
	private final List<ProgessFileEntityListener> listeners = new ArrayList<FileBodyWithProgress.ProgessFileEntityListener>();
	private OutputStreamWithCounter os = null;
	

	public FileBodyWithProgress(File file)
	{
		super(file);
	}
	
	
	public void addListener(ProgessFileEntityListener listenerIn)
	{
		this.listeners.add(listenerIn);
	}
	
	
	@Override
	public void writeTo(OutputStream osIn) throws IOException
	{
		this.os = new OutputStreamWithCounter(osIn);
		this.os.addListener(this);
		super.writeTo(this.os);
	}


	@Override
	public void onUpdate(long totalNumBytesWrittenIn)
	{
		for( ProgessFileEntityListener currListener : this.listeners )
		{
			currListener.onUpdate(totalNumBytesWrittenIn, this.getContentLength());
		}
	}

}
