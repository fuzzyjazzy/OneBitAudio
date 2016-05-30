package com.wacooky.audio.file;

import java.util.HashMap;


/**
 * 
 * @author fujimori
 *
 */
public class AudioFileFormatInfo extends FileFormatInfo {
	public int samplingFrequency;
	public int channelNumber;
	public long sampleCount;
	public int sampleBitLength;
	public int blockSize;

	
	public HashMap<String, String> textAttributes = new HashMap<String, String>();
	
	public AudioFileFormatInfo() {
	}

	public double timeCount() {
		return (double)sampleCount/samplingFrequency;
	}
	
	public String getString() {
		return String.format("%s, %d, %d\n%d %d %d", 
				formatName, samplingFrequency, channelNumber, 
				sampleCount, sampleBitLength, blockSize);
	}
}
