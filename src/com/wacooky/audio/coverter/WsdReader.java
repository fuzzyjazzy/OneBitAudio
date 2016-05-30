package com.wacooky.audio.coverter;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.zip.DataFormatException;
/**
 * This is an example of a OneBitAudioReader.
 * 
 * FormatConverter uses com.wacooky.jistdsdex.JustDSDReader that utilize
 * JustDSD DSDFormat.
 *  
 * @author fujimori
 * @version 0.1	2016-05-30
 * 
 */
public class WsdReader extends OneBitAudioReader {
	long dataSize; //-- total data size in byte
	long dataPosition;	 //-- position in input file where audio samples start
	long currentPosition; //-- valid byte boundary current position
	long endPosition;	//-- valid byte boundary end position

	byte[] bufi;
	
	public WsdReader(File file) throws IOException, DataFormatException {
		super(file);

		byte[] fourc = new byte[4];
		inputStream.readFully(fourc);
		String chunkId = new String(fourc);
		if (chunkId.compareTo("1bit") != 0)
			throw new DataFormatException();
	
		inputStream.seek(12L);
		long s = inputStream.readLong();
		long lo = (s >> 32) & 0x00000000FFFFFFFFL;
		long hi = s & 0x00000000FFFFFFFFL;
		//sz = (((hi << 32) | lo) - 2048) / (8 * formatInfo.channelNumber);	
		dataSize = (((hi << 32) | lo) - 2048);
		//sz = inputStream.readLong() - 2048;
		inputStream.seek(24L);
		dataPosition = inputStream.readInt();
		inputStream.seek(36L);
		samplingFreq = inputStream.readInt();
		inputStream.seek(44L);
		channels = inputStream.readByte();
		inputStream.seek(dataPosition);		
		currentPosition = 0L;
		//currentPosition = secToBytes(10.) * wsdChannels;
		inputStream.seek(dataPosition + currentPosition);
		endPosition = dataSize;
		//endPosition = secToBytes(20.) * wsdChannels;
	
		sampleCountTotal = byteToCount(dataSize); 
		currentSampleCount = byteToCount(currentPosition);
		endSampleCount = byteToCount(endPosition);

		if (DEBUG) {
			System.out.println("WsdReader");			
			System.out.println("ID: " + chunkId);
			System.out.println("Data position: " + dataPosition);
			System.out.println("Samplimg Frequency: " + samplingFreq);
			System.out.println("Channels: " + channels);
			System.out.println("Data Size: " + dataSize);
		}
	}

	@Override
	public long getCountNow() {
		return byteToCount(currentPosition);
	}

	@Override
	public ByteOrder byteOrder() {
		return ByteOrder.BIG_ENDIAN;
	}
	
	@Override
	public int frameLength() {
		return 1;
	}
	
	@Override
	public int read(ByteInterleavedOneBitAudio audio) throws IOException {
		if ( audio.channels() != channels ) {
			System.out.println("Buffer lenth mismatch.");
			return 0;
		}

		if (currentPosition >= endPosition)
			return 0;
		int n;
		if ( (n = inputStream.read(audio.buffer)) > 0 ) {
			if (currentPosition + n > endPosition)
				n = (int)(endPosition - currentPosition);
			currentPosition += n;
		}
		return (int)byteToCount((long)n);
	}

	@Override
	public void seekCount(long start, long end) throws IOException {
		this.currentPosition = countToByte( (start < 0) ? 0 : start);
		if ( start >= 0 && end >= start)
			this.endPosition = countToByte(end);
		inputStream.seek(dataPosition + currentPosition);

	}

}