package com.livinglogic.utils;

import java.util.TimerTask;

/*
** Interrupt the given task when the timer runs.
*/
public class InterruptTimerTask extends TimerTask
{
	private Thread tread;

	public InterruptTimerTask(Thread tread)
	{
		this.tread = tread;
	}

	@Override
	public void run()
	{
		tread.interrupt();
	}
}