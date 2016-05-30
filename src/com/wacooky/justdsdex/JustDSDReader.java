package com.wacooky.justdsdex;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;

import org.justcodecs.dsd.Decoder.DecodeException;

import com.wacooky.audio.coverter.ByteInterleavedOneBitAudio;
import com.wacooky.audio.coverter.OneBitAudioReader;

public class JustDSDReader extends OneBitAudioReader {
	org.justcodecs.dsd.DSDFormat<?> dsdFormat;
	//DSDFormatExposer<B> exposer;
	org.justcodecs.dsd.Decoder decoder;
	byte[] sampleBuf;
	//long currentPosition; //-- valid byte boundary current position
	//long endPosition;	//-- valid byte boundary end position
	//boolean isByteInterleaved = true;

	public JustDSDReader(org.justcodecs.dsd.DSDFormat<?> format, File file) throws IOException {
		super(file);
		dsdFormat = format;
		//if (dsdFormat instanceof org.justcodecs.dsd.DSFFormat )
		//	isByteInterleaved = false;

		try {
			dsdFormat.init(new JustDSDStream(inputStream));
		} catch (DecodeException e) {
			e.printStackTrace();
			throw new IOException(e.toString());
		}
		//System.out.println(dsdFormat.getClass() + " " + inputStream.getByteOrder());
		samplingFreq = dsdFormat.getSampleRate();
		channels = dsdFormat.getNumChannels();
		sampleCountTotal = dsdFormat.getSampleCount();
		endSampleCount = sampleCountTotal;
		currentSampleCount = 0;
	
		//currentPosition = 0;
		//endPosition = sampleCountTotal; //-- byte interleaved data

		//currentPosition = countToByte(currentSampleCount);
		//endPosition = countToByte(endSampleCount);
		if (DEBUG) {
			System.out.println("JustDSDReader");
			System.out.println("sampling: "+samplingFreq);
			System.out.println("channels: "+channels);
			System.out.println("sampleCountTotal: "+sampleCountTotal);
		}

		decoder = new org.justcodecs.dsd.Decoder();
		try {
			decoder.init(dsdFormat);
			decoder.setOutputFormat(null);
		} catch (DecodeException e) {
			e.printStackTrace();
			throw new IOException(e.toString());			
		}
		
		//exposer = new DSDFormatExposer<B>(dsdFormat);
		//exposer.initBuffers(0);
	}

	@Override
	public ByteOrder byteOrder() {
		if (dsdFormat instanceof org.justcodecs.dsd.DSFFormat)
			return ByteOrder.LITTLE_ENDIAN;
		return ByteOrder.BIG_ENDIAN;
	}
	
	@Override
	public int frameLength() {
		if (dsdFormat instanceof org.justcodecs.dsd.DSFFormat)
			return 4096;
		if (dsdFormat instanceof org.justcodecs.dsd.DFFFormat)
			return 2048;		
		return 1; //-- because decoder outputs byte interleaved samples. 
	}
	
	@Override
	public void seekCount(long start, long end) throws IOException {
		try {
			decoder.seek(start);
			currentSampleCount = start;
			endSampleCount = end;
			if (DEBUG) 
				System.out.println("seekCount: " + start + " " + end + "(" + (end - start) + ")" );

			//currentPosition = countToByte(currentSampleCount);
			//endPosition = countToByte(endSampleCount);
		} catch (DecodeException e) {
			e.printStackTrace();
			throw new IOException(e.toString());
		}
	}
/*
	@Override
	int read(ByteInterleavedOneBitAudio audio) throws IOException {
		if (currentPosition >= endPosition)
			return 0;
		int n = 0;
		try {
			if( (n = decoder.decodeDSD(channels, audio.buffer())) > 0) {
				if (currentPosition + n > endPosition)
					n = (int)(endPosition - currentPosition);
				currentPosition += n;
			}
			//-- decoder retunrs byte length or -1 if no data is read.
			if (n < 0)
				n = 0;
			currentSampleCount = byteToCount(currentPosition);
		} catch (DecodeException e) {
			e.printStackTrace();
			throw new IOException(e.toString());
		}
		return n;
	}
*/
	
	@Override
	public int read(ByteInterleavedOneBitAudio audio) throws IOException {
		if (currentSampleCount >= endSampleCount)
			return 0;
		
		int samples = 0;
		try {
			int n;
			if( (n = decoder.decodeDSD(channels, audio.buffer())) > 0) {
/*
				if (!isByteInterleaved)
					samples = n * 8 / channels;
				else
					samples = n * 8;
*/
				samples = n * 8 / channels;
				if (currentSampleCount + samples > endSampleCount)
					samples = (int)(endSampleCount - currentSampleCount);
				currentSampleCount += samples;
			}
			//-- decoder retunrs byte length or -1 if no data is read.
			if (n < 0)
				samples = 0;
			//currentSampleCount = byteToCount(currentPosition);
		} catch (DecodeException e) {
			e.printStackTrace();
			throw new IOException(e.toString());
		}
		return samples;
	}

}