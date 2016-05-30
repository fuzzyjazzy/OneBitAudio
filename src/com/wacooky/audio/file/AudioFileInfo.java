package com.wacooky.audio.file;

import java.nio.file.Path;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AudioFileInfo {
	private Path path;
	private int sampleFrequency;
	private int channels;
	private double totalTime;
	
	private StringProperty name;
	private StringProperty sf;
	private StringProperty ch;
	private StringProperty time;
	private StringProperty type;
	//
	private StringProperty absPath;

	public AudioFileInfo(Path path, FileFormat fmt) {
		this.path = path;
		String fname = path.getFileName().toString();
		this.name = new SimpleStringProperty(fname);
		if (fmt instanceof AudioFileFormat) {
			AudioFileFormat afmt = (AudioFileFormat)fmt;
			sampleFrequency = afmt.samplingFrequency();
			channels = afmt.channelNumber();
			totalTime = afmt.timeCount();
			this.sf = new SimpleStringProperty( PrintDataUtility.samplingFrequencyString(sampleFrequency));
			this.ch = new SimpleStringProperty(String.valueOf(channels));
			this.time = new SimpleStringProperty(PrintDataUtility.timeString(totalTime));			
		} else {
			sampleFrequency = 0;
			channels = 0;
			totalTime = 0;
			this.sf = new SimpleStringProperty("-");
			this.ch = new SimpleStringProperty("-");
			this.time = new SimpleStringProperty("-");
		}

		this.absPath = new SimpleStringProperty(this.path.toAbsolutePath().toString());
		int idx = fname.lastIndexOf('.');
		String ext;
		if (idx > 0)
			ext = fname.substring(idx+1);
		else
			ext = "";
		this.type = new SimpleStringProperty(ext);
		
	}

	@Override
	public boolean equals(Object obj) {
		return(obj instanceof AudioFileInfo && this.path.equals(((AudioFileInfo)obj).path));
	}
	
	@Override
	public int hashCode() {
		return this.path.hashCode();
	}
	
	public Path getPath() {
		return path;
	}
	
	public int getSampleFrequency() {
		return sampleFrequency;
	}
	
	public int getChannels() {
		return channels;
	}
	
	public double getTotalTime() {
		return totalTime;
	}
	
	public void setName(String name) {
		this.name = new SimpleStringProperty(name);
	}
	
	public StringProperty nameProperty() {
		return name;
	}
	public StringProperty sfProperty() {
		return sf;
	}
	public StringProperty chProperty() {
		return ch;
	}
	public StringProperty timeProperty() {
		return time;
	}
	public StringProperty absPathProperty() {
		return absPath;
	}
	public StringProperty typeProperty() {
		return type;
	}

}