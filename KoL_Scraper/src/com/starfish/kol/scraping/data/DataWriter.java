package com.starfish.kol.scraping.data;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class DataWriter {
	private PrintWriter writer;
	public DataWriter(String filename, String tablename, int version) {
		try {
			this.writer = new PrintWriter(filename);
			writer.println(tablename + " v" + version);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
		
	public void write(Data d) {
		writer.println(d.getValues());
	}
	
	public void write(Iterable<? extends Data> data) {
		for(Data d : data)
			this.write(d);
	}
	public void close() {
		writer.close();
	}
}
