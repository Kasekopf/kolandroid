package com.starfish.kol.util;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Pipeline<E> implements Reciever<E>{
	private BlockingQueue<Object> queue;
	private static final Object POISON = new Object();
	private boolean open;

	public Pipeline() {
		this(true);
	}

	public Pipeline(boolean opened) {
		open = false;
		queue = null;
		
		if (opened)
			open();
	}

	public Pipeline(Pipeline<E> toCopy) {
		queue = toCopy.queue;
		open = false;
	}

	public void add(E object) {
		queue.add(object);
	}

	@SuppressWarnings("unchecked")
	public E take() {
		if (!open) {
			return null;
		}
		try {
			Object object = queue.take();
			if (object == POISON) {
				queue.add(POISON);
			} else {
				return (E) object;
			}
		} catch (InterruptedException e) {
		}
		return null;
	}

	public int size(){
		return queue.size();
	}
	
	public void open() {
		if (!open) {
			if (queue == null)
				queue = new LinkedBlockingQueue<Object>();
			else {
				Iterator<Object> i = queue.iterator();
				while (i.hasNext())
					if (i.next() == POISON)
						i.remove();
			}

			open = true;
		}
	}

	public void close() {
		if (open) {
			open = false;
			queue.add(POISON);
		}
	}

	public boolean isOpen() {
		return open;
	}

	@Override
	public void pass(E object) {
		queue.add(object);
	}
}
