package com.wacooky.audio.coverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStreamImpl;

abstract public class OneBitAudioWriter {
	static public boolean DEBUG = false;

	ImageOutputStreamImpl outputStream;
	long osize;
	long sampleCount;
	int channels;

	public OneBitAudioWriter() {
	}
	public OneBitAudioWriter(File file) throws FileNotFoundException, IOException {
		RandomAccessFile rafile = new RandomAccessFile(file, "rw");
		rafile.setLength(0L); //-- Prevent from appending
		outputStream = new FileImageOutputStream(rafile);
		outputStream.setByteOrder(byteOrder());
		osize = 0;
		sampleCount = 0;
	}
	
	public int channels() {
		return channels;
	}

	abstract public ByteOrder byteOrder();
	abstract public int frameLength();
	abstract public void write(ByteInterleavedOneBitAudio audio, int samples, int...map) throws IOException;
	abstract public void createStereo(int samplingFrequency) throws IOException;
	abstract public void close() throws IOException;

}