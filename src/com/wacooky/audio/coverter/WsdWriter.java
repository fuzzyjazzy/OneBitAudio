package com.wacooky.audio.coverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteOrder;
/**
 *  OneBitAudioWriter for WSD format.
 *  
 * @author fujimori
 * @version 1.0		2016-05-30*
 */
class WsdWriter extends OneBitAudioWriter {
	static final public int PREFERRED_FRAME_LENGTH = 4096;
	byte[] bufo;
	int bufoFrames;
	int samplingFrequency;
	

	public WsdWriter(File file) throws FileNotFoundException, IOException {
		super(file);
	}
	
	@Override
	public ByteOrder byteOrder() {
		return ByteOrder.BIG_ENDIAN;
	}
	
	@Override
	public int frameLength() {
		return 1; //- byte for each channel
	}
	
	
	@Override
	public void createStereo(int samplingFrequency) throws IOException {
		channels = 2;
		this.samplingFrequency = samplingFrequency;
	
		byte[] work = new byte[512];
		for (int i = 0; i < work.length; i++)
			work[i] = 0;
	
		outputStream.seek(0L);
		//-- General Information	
		outputStream.writeBytes("1bit");
		outputStream.writeInt(0); //- Reserved
		outputStream.writeByte(0x11); //- Vestion_N
		outputStream.writeByte(0); //- Reserved
		outputStream.writeShort(0); //- Reserved
		outputStream.writeLong(0L); //- File size
		outputStream.writeInt(0x80); //- Text start address
		outputStream.writeInt(0x800); //- Data start address
		outputStream.writeInt(0); //- Reserved
		//-- Data Spec. Information	
		outputStream.writeInt(0); //Play back time
		outputStream.writeInt(samplingFrequency); //Sampling frequency
		outputStream.writeInt(0); //Reserved
		outputStream.writeByte((byte)channels); //- Number of channe;s
		outputStream.write(work, 0, 3); //- Reserved
		outputStream.writeInt(0); //- Channel assignment
		outputStream.write(work, 0, 12); //- Reserved
		outputStream.writeInt(0); //- E,phasis
		outputStream.writeInt(0); //- Reserved
		outputStream.write(work, 0, 16); //- Time Reference
		outputStream.write(work, 0, 40); //- Reserved

		//-- Text Data
		for (int i = 0; i < work.length; i++)
			work[i] = ' ';
		outputStream.write(work, 0, 128); //- Title
		outputStream.write(work, 0, 128); //- Composer
		outputStream.write(work, 0, 128); //- Song Writer
		outputStream.write(work, 0, 128); //- Artist
		outputStream.write(work, 0, 128); //- Album
		outputStream.write(work, 0, 32); //- Genre
		outputStream.write(work, 0, 32); //- Date&Time
		outputStream.write(work, 0, 32); //- Location
		outputStream.write(work, 0, 512); //- Comment
		outputStream.write(work, 0, 512); //- User Specific
		outputStream.write(work, 0, 160); //- Reserved
		//-- Stream Data

	}

	@Override
	public void write(ByteInterleavedOneBitAudio audio, int samples, int...map) throws IOException {
		if (bufo == null) {
			//-- audio.byteInterleavedSamples() and  FRAME_LENGTH must be harmonic
			bufo = new byte[PREFERRED_FRAME_LENGTH*channels];
			bufoFrames = 0;
		}

		int srcChannels = map.length; //-- must be equal to audio.channels()
		int dstCh;
		int dstPos;
		byte[] src = audio.buffer();
		int srcPos;
		int bytes = samples/8;

		for (int i = 0; i < bytes; i++) {
			if (bufoFrames >= PREFERRED_FRAME_LENGTH)
				writeBlock();
			srcPos = i*srcChannels;
			dstPos = bufoFrames*channels;
			for (int srcCh = 0; srcCh < srcChannels; srcCh++) {
				if ((dstCh = map[srcCh]) < 0)
					continue;
				bufo[dstPos+dstCh] = src[srcPos+srcCh];
			}
			bufoFrames++;
		}
		sampleCount += samples;
	}
	
	protected void writeBlock() throws IOException {
		outputStream.write(bufo, 0, bufo.length);
		osize += bufo.length;
		bufoFrames = 0;
	}

	@Override
	public void close() throws IOException {
		if (bufoFrames > 0) { //-- need flush]
			int n = bufoFrames*channels;
			outputStream.write(bufo, 0, n);
			osize += n;	
		}
		
		if (DEBUG) {
			System.out.println("WsdWriter");			
			System.out.println("byte count=" + osize);
			System.out.println("sample count=" + sampleCount);			
			System.out.println("total=" + (osize+2048) );
		}
		long fileSize = osize+2048;
		long lo = fileSize & 0x00000000FFFFFFFFL;
		long hi = (fileSize >> 32) & 0x00000000FFFFFFFFL;
		fileSize = ((lo << 32) | hi);

		outputStream.seek(12L);
		outputStream.writeLong(fileSize);

		double time = samplingFrequency * sampleCount;
		int hour = (int)(time / 3600);
		int min = (int)((time - 3600*hour) / 60);
		int sec = (int)(time - 3600*hour - 60*min);
		outputStream.seek(32L);
		outputStream.writeByte((byte)( (hour/10)<<4 | hour%10));
		outputStream.writeByte((byte)( (min/10)<<4 | min%10));
		outputStream.writeByte((byte)( (sec/10)<<4 | sec%10));

		outputStream.close();

		bufo = null;
	}

}