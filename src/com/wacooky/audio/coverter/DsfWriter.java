package com.wacooky.audio.coverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteOrder;

/**
 * OneBitAudioWriter for DSF format.
 * 
 * @author fujimori
 * @version 1.0		2016-05-30
 * 
 */
class DsfWriter extends OneBitAudioWriter {
	static final public int FRAME_LENGTH = 4096;
	byte[][] bufo;
	int bufoWritePointer;
	

	public DsfWriter(File file) throws FileNotFoundException, IOException {
		super(file);
	}
	
	@Override
	public ByteOrder byteOrder() {
		return ByteOrder.LITTLE_ENDIAN;
	}
	
	@Override
	public int frameLength() {
		return FRAME_LENGTH; //- byte for each channel
	}
	
	
	@Override
	public void createStereo(int samplingFrequency) throws IOException {
		channels = 2;
		
		outputStream.seek(0L);
		//
		outputStream.writeBytes("DSD ");
		outputStream.writeLong(28L); //- chunk size
		outputStream.writeLong(0L); //- file size
		outputStream.writeLong(0L); //- metadata pointere
			
		outputStream.writeBytes("fmt ");
		outputStream.writeLong(52L); //- chunk size
		outputStream.writeInt(1); //- Format version
		outputStream.writeInt(0); //- Format ID = DSD raw
		outputStream.writeInt(2); //- Channel type = 2 stereo
		outputStream.writeInt(2); //- Channel number = 2 stereo
		outputStream.writeInt(samplingFrequency); //- Sampling freq
		//JRiver, DA-3000 missinterprete this field
		//accessor.intTo4bytes(8); //- Bits per sample
		outputStream.writeInt(1); //- Bits per sample
		outputStream.writeLong(0L); //- Sample count
		outputStream.writeInt(FRAME_LENGTH); //- Block size per channel
		outputStream.writeInt(0); //- Reserved

		outputStream.writeBytes("data");
		outputStream.writeLong(0L); //- Chunk size
		//-- sample data
		//-- metadata ID3v2
	}

	@Override
	public void write(ByteInterleavedOneBitAudio audio, int samples, int...map) throws IOException {
		if (bufo == null) {
			//-- audio.byteInterleavedSamples() and  FRAME_LENGTH must be harmonic
			bufo = new byte[channels][FRAME_LENGTH];
			bufoWritePointer = 0;
		}
		int srcChannels = map.length; //-- must be equal to audio.channels()
		int dstCh;
		byte[] src = audio.buffer();
		int srcPos;
		int bytes = samples/8;

		for (int i = 0; i < bytes; i++) {
			if (bufoWritePointer >= FRAME_LENGTH)
				writeBlock();
			srcPos = i*srcChannels;
			for (int srcCh = 0; srcCh < srcChannels; srcCh++) {
				if ((dstCh = map[srcCh]) < 0)
					continue;
				bufo[dstCh][bufoWritePointer] = src[srcPos+srcCh];
			}
			bufoWritePointer++;
		}
		sampleCount += samples;
	}
	
	protected void writeBlock() throws IOException {
		byte[] buf;
		int channels = bufo.length;
		for (int i = 0; i < channels; i++) {
			buf = bufo[i];
			outputStream.write(buf, 0, buf.length);
			osize += buf.length;
		}
		bufoWritePointer = 0;
	}

	@Override
	public void close() throws IOException {
		if (bufoWritePointer > 0) //-- neewd flush
			writeBlock();

		if (DEBUG) {
			System.out.println("byte count=" + osize);
			System.out.println("sample count=" + sampleCount);
			
			System.out.println("total=" + (osize+80+12) );
		}
		outputStream.seek(12L);
		outputStream.writeLong(osize+80+12);//- total file size
		outputStream.seek(64L);
		outputStream.writeLong(sampleCount);//- Smaple count
		outputStream.seek(84L);
		outputStream.writeLong(osize+12);//- data chunk size
		outputStream.close();

		bufo = null;
	}

}