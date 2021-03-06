package com.wacooky.audio.coverter;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ObjIntConsumer;

//-- Bitreverse table
import org.justcodecs.dsd.Utils;

/**
 * Byte interleaved 1-bit audio data.
 * This is used for data exchange across different formats.
 *  
 * @author fujimori
 *
 */
public class ByteInterleavedOneBitAudio {
	static public double[] bitToDouble( byte onebitByte) {
		if (bitToDoubleTable == null) {
			bitToDoubleTable = new double[256][8];
			double[] array;
			int v;
			for (int i = 0; i < 256; i++) {
				array = bitToDoubleTable[i];
				v = i;
				for( int j = 0; j < 8; j++) {
					array[j] = ((v & 0x80) >0) ? 1.0 : -1.0;
					v = v << 1;
				}
			}
		}
		int unsigned = onebitByte;
		if (unsigned < 0)
			unsigned = 127 - onebitByte;
		
		return bitToDoubleTable[unsigned]; 
	}
	
	
	static protected double[][] bitToDoubleTable = null;
	
	/**
	 * deinterleave
	 * 
	 * @param src	interleaved audio samples
	 * @param dst	deinterleaved audio samples
	 * @param map	must have same number of channels of interleved data,
	 * 				src in this methos
	 */
	static public void deinterleave(byte[] src, byte[][] dst, int... map) {
		int srcChannels = map.length; //-- channel map of interleaved data
		int frames = src.length/srcChannels;
		int dstCh;
		byte[] dstBuf;
		int nCopy;
		int idx;

		for (int srcCh = 0; srcCh < srcChannels; srcCh++ ) {
			if ((dstCh = map[srcCh]) < 0) //-- skip
				continue;

			dstBuf = dst[dstCh];
			nCopy = (dstBuf.length > frames) ? frames : dstBuf.length;	
			for( idx = 0; idx < nCopy; idx++ )
				dstBuf[idx] = src[srcChannels*idx+srcCh];
			while( idx < dstBuf.length)
				dstBuf[idx++] = 0;
		}
	}

	/**
	 * 
	 * @param src	channel block audio samples.
	 * @param dst	interleaved audio samples.
	 * @param map	must have same number of channels of interleved data,
	 * 				dst in this method.
	 */
	static public void interleave(byte[][] src, byte[] dst, int... map) {
		int dstChannels = map.length; //-- channel map of interleaved data
		int frames = dst.length / dstChannels;
		int srcCh;
		int dstCh;
		byte[] srcBuf;
		int nCopy;
		int idx;

		for (dstCh = 0; dstCh < dstChannels; dstCh++ ) {
			if ((srcCh = map[dstCh]) < 0) //-- skip
				continue;

			srcBuf = src[srcCh];
			nCopy = (srcBuf.length > frames) ? frames : srcBuf.length;	
			for( idx = 0; idx < nCopy; idx++ )
				dst[dstChannels*idx+dstCh] = srcBuf[idx];
			while( idx < frames)
				dst[dstChannels*idx+dstCh] = 0;
		}
	}

	static void reverseBit(byte[] buf) {
		for (int i = 0; i< buf.length; i++ )
			buf[i] = Utils.reverse(buf[i]);
	}

	byte[] buffer;
	int channels;

	public ByteInterleavedOneBitAudio(int byteInterleavedSamples, int numberOfChannel) {
		//System.out.println("audio["+numberOfChannel+"]["+byteInterleavedSamples+"]");
		buffer = new byte[byteInterleavedSamples*numberOfChannel];
		this.channels = numberOfChannel;
	}

	public ByteInterleavedOneBitAudio(byte[] buf, int channels) {
		this.buffer = buf;
		this.channels = channels;
	}

	public byte[] buffer() {
		return buffer;
	}
	
	public int channels() {
		return channels;
	}

	public int byteInterleavedSamples() {
		return buffer.length/channels;
	}

	public void reverseBit() {
		ByteInterleavedOneBitAudio.reverseBit(buffer);
	}
	
	public void fill( ObjIntConsumer<byte[]> func) {
		byte[] result = new byte[channels];
		int samples = buffer.length/channels;
		int idx;
		for (int i = 0; i < samples; i++) {
			func.accept(result, i);
			idx = i*channels;
			for (int ch = 0; ch < channels; ch++)
				buffer[idx+ch] = result[ch];
		}
	}
}
