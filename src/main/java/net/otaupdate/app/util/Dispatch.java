package net.otaupdate.app.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Dispatch
{
	private static ExecutorService executor = Executors.newFixedThreadPool(5);
	
	
	public static void async(Runnable runnableIn)
	{
		executor.execute(runnableIn);
	}
}
