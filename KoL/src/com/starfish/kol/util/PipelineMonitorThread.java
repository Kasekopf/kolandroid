package com.starfish.kol.util;

public abstract class PipelineMonitorThread<E> extends Thread {
	private final Pipeline<E> input;
	
	public PipelineMonitorThread(Pipeline<E> input){
		this.input = input;
	}
	
	@Override
	public void run(){
		while (true) {
			E obj = input.take();
			if (obj == null)
				break;
			recieve(obj);
		}
	}
	
	protected abstract void recieve(E obj);
}
