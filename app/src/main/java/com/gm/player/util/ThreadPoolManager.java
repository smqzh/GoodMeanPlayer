package com.gm.player.util;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Qzh
 * @version 1.0
 * @Description 线程池管理
 * @date 2017/12/25.
 * @Copyright: 2018 www.kind.com.cn Inc. All rights reserved.
 */
public class ThreadPoolManager {
	/**
	 * 单例设计模式
	 */
	private static ThreadPoolManager mInstance;

	public static ThreadPoolManager getInstance() {
		if(mInstance == null){
			mInstance = new ThreadPoolManager();
		}
		return mInstance;
	}

	/**
	 * 核心线程池的数量，同时能够执行的线程数量
	 */
	private int corePoolSize;
	/**
	 * 最大线程池数量，表示当缓冲队列满的时候能继续容纳的等待任务的数量
	 */
	private int maximumPoolSize;
	/**
	 * 存活时间
	 */
	private long keepAliveTime = 1;
	private TimeUnit unit = TimeUnit.HOURS;
	private ThreadPoolExecutor executor;
	private ScheduledExecutorService executorService;
	private ThreadPoolManager() {

		//给corePoolSize赋值：当前设备可用处理器核心数*2 + 1,能够让cpu的效率得到最大程度执行（有研究论证的）
		corePoolSize = Runtime.getRuntime().availableProcessors()*2+1;
		//虽然maximumPoolSize用不到，但是需要赋值，否则报错
		maximumPoolSize = corePoolSize;

		executor = new ThreadPoolExecutor(
				//当某个核心任务执行完毕，会依次从缓冲队列中取出等待任务
				corePoolSize,
				//5,先corePoolSize,然后new LinkedBlockingQueue<Runnable>(),然后maximumPoolSize,但是它的数量是包含了corePoolSize的
				maximumPoolSize,
				//表示的是maximumPoolSize当中等待任务的存活时间
				keepAliveTime,
				//时间的单位
				unit,
				//缓冲队列，用于存放等待任务，Linked的先进先出
				new LinkedBlockingQueue<Runnable>(),
				//创建线程的工厂
				Executors.defaultThreadFactory(),
				//用来对超出maximumPoolSize的任务的处理策略
				new ThreadPoolExecutor.AbortPolicy()
		);
		//延迟线程池
		executorService = new ScheduledThreadPoolExecutor(corePoolSize);
	}

	/**
	 * 执行任务
	 */
	public void execute(Runnable runnable){
		if(runnable==null) {
			return;
		}
		executor.execute(runnable);
	}

	public void executeDelay(Runnable runnable, long delay){
		if(runnable==null) {
			return;
		}
		executorService.schedule(runnable,delay, TimeUnit.MILLISECONDS);
	}

	/**
	 * 从线程池中移除任务
	 */
	public void remove(Runnable runnable){
		if(runnable==null){
			return;
		}
		executor.remove(runnable);
	}
}
