package com.wacooky.audio.coverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStreamImpl;

abstract public class OneBitAudioReader {
	static public boolean DEBUG = false;

	protected ImageInputStreamImpl inputStream;
	protected int samplingFreq;
	protected int channels;
	protected long sampleCountTotal;
	protected long currentSampleCount;
	protected long endSampleCount;

	public OneBitAudioReader() {
	}

	public OneBitAudioReader(File file) throws FileNotFoundException, IOException {
		inputStream = new FileImageInputStream(new RandomAccessFile(file, "r"));
		inputStream.setByteOrder(byteOrder());		
	}

	abstract public ByteOrder byteOrder();
	
	public int samplingFreq() {
		return samplingFreq;
	}
	
	public int channels() {
		return channels;
	}

	public void close() throws IOException {
		inputStream.close();
	}
	
	protected double byteToSec( long byteCount) {
		return (double)byteCount*8/(samplingFreq*channels);
	}
	
	protected long secToByte( double sec ) {
		return (long)(samplingFreq * sec * channels / 8); //-- byte
	}

	protected long countToByte( long sampeCount) {
		return (long)(sampeCount * channels / 8);
	}

	protected long byteToCount( long byteCount ) {
		return (long)(byteCount * 8 / channels);
	}

	public long getCountTotal() {
		return sampleCountTotal;
	}

	public long getCountNow() {
		return currentSampleCount;
	}

	abstract public int frameLength();
	abstract public int read(ByteInterleavedOneBitAudio audio) throws IOException;
	abstract public void seekCount(long start, long end ) throws IOException;

}