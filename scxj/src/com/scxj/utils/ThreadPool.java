/*
 * 
 */
package com.scxj.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
// TODO: Auto-generated Javadoc
/**
 * 
 * Copyright (c) 2012 All rights reserved
 * @version v1.0
 */

public class ThreadPool{
	
	/** The tag. */
	private static String TAG = "ThreadPool";
	
	
	/** The http pool. */
	private static ThreadPool threadPool = null; 
	
	/** The n threads. */
	private static int nThreads  = 2;
	// �̶�����߳���ִ������?
	/** The executor service. */
	private ExecutorService executorService = null; 
	
	
	/**
	 * ����ͼƬ������.
	 *
	 * @param nThreads the n threads
	 */
    protected ThreadPool(int nThreads) {
    	executorService = Executors.newFixedThreadPool(nThreads); 
    } 
	
	/**
	 * @return single instance of ThreadPool
	 * 考虑到我们都是小计算线程单元，如果把USB并行计算线程加到线程池里面，�?��cpu * 6;
	 */
    public static ThreadPool getInstance() { 
        if (threadPool == null) { 
        	nThreads = getNumCores();
        	threadPool = new ThreadPool(nThreads*6); 
        } 
        return threadPool;
    } 
    
    
    public void excuteThread(Runnable t){
    	executorService.submit(t);
    }
    
    
   
    
    public static int getNumCores() { 
		try { 
			//Get directory containing CPU info 
			File dir = new File("/sys/devices/system/cpu/"); 
			//Filter to only list the devices we care about 
			File[] files = dir.listFiles(new FileFilter(){

				@Override
				public boolean accept(File pathname) {
					//Check if filename is "cpu", followed by a single digit number 
					if(Pattern.matches("cpu[0-9]", pathname.getName())) { 
					   return true; 
				    } 
				    return false; 
				}
				
			}); 
			//Return the number of cores (virtual CPU devices) 
			return files.length; 
		} catch(Exception e) { 
			//Default to return 1 core 
			return 1; 
		} 
	} 
    
   
    
    public void shutdownNow(){
    	if(!executorService.isTerminated()){
    		executorService.shutdownNow();
    		listenShutdown();
    	}
    	
    }
    
    /**
     */
    public void shutdown(){
    	if(!executorService.isTerminated()){
    	   executorService.shutdown();
    	   listenShutdown();
    	}
    }
    
    /**
     */
    public void listenShutdown(){
    	try {
			while(!executorService.awaitTermination(1, TimeUnit.MILLISECONDS)) { 
			}  
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
