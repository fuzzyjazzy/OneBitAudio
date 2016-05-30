package com.wacooky.audio.coverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.DataFormatException;

import com.wacooky.justdsdex.JustDSDReader;

/**
 * Audio format converter.
 * 
 * Extract time region and map channels.
 * 
 * @author fujimori
 * @version 0.1	2016-05-30  WSD <--> DSF conversion is supporter.
 */
public class FormatConverter implements InteractiveProcess {
	static public boolean DEBUG = true;
	//--------------------------------------------------------
	//-- For Test
	public static void convert(String srcPath, String dstPath, Double fromSec, Double toSec) {
		File src = new File(srcPath);
		File dst = new File(dstPath);
		FormatConverter converter = new FormatConverter();
		try {
			converter.convert( src, dst, fromSec, toSec);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DataFormatException e) {
			e.printStackTrace();			
		}
	}
	//-- For Test
	//--------------------------------------------------------

	//--------------------------------------------------	
	protected OneBitAudioReader reader;
	protected OneBitAudioWriter writer;
	protected int[] channelMap;
	protected ByteInterleavedOneBitAudio audio;
	protected boolean needBitReverse;
	protected long samples = 0;
	protected String srcExt;
	protected String dstExt;
	
	public FormatConverter() {
	}

	protected String formatTime( double timeInSec ) {
		int hour = (int)(timeInSec / 3600);
		int min = (int)((timeInSec - 3600*hour)/60);
		int sec = (int)(timeInSec - 3600*hour - 60*min);
		return String.format("%02d:%02d:%02d", hour, min, sec);
	}
		
	public void initializeFileIO( File src , File dst, int... map ) throws FileNotFoundException, DataFormatException, IOException {
		reader = getJuDSDReader(src);
		writer = getWriter(dst);
		needBitReverse = (srcExt.compareTo(dstExt) != 0 && 
							(srcExt.compareTo(".wsd") == 0 || dstExt.compareTo(".wsd") == 0));
		
		if (DEBUG) System.out.println("bitReverse " + needBitReverse);

		if (writer == null)
			throw new DataFormatException("Writer note found.");
		writer.createStereo(reader.samplingFreq());
		int srcChannels = reader.channels();
		int dstChannels = writer.channels();
		//-----------------------------------------
		//-- reader's frame length has priority over writer's
		//-- Writer must have capable of consue all give data from reader.
		int frameLength = reader.frameLength();
		if (frameLength == 1) //-- WSD format returns 1 which allows arbitrary buffer size
			frameLength = 4096;
		if (DEBUG)
			System.out.println("audio " + frameLength);
		audio = new ByteInterleavedOneBitAudio(frameLength, reader.channels());
		//-----------------------------------------
		
		//------------------------------------
		//-- create channel map for stereo output
		if (map == null || map.length == 0 || map.length != srcChannels ) {
			channelMap = new int[srcChannels];
			for (int i = 0; i < channelMap.length; i++) {
				if (i >= dstChannels)
					channelMap[i] = -1;
				else
					channelMap[i] = i;
			}
		} else {
			channelMap = map;
		}
		//------------------------------------
		
	}


	protected OneBitAudioWriter getWriter(File file) throws FileNotFoundException, IOException {
		String path = file.getPath();
		dstExt = path.substring(path.length() - 4).toLowerCase();
		if (dstExt.compareTo(".wsd") == 0)
			return new WsdWriter(file);
		else if (dstExt.compareTo(".dsf") == 0)
			return new DsfWriter(file);
		return null;
	}

	
	protected OneBitAudioReader getJuDSDReader(File file) throws IOException {
		org.justcodecs.dsd.DSDFormat<?> format = null;
		String path = file.getPath();
		srcExt = path.substring(path.length() - 4).toLowerCase();
		
		if (srcExt.compareTo(".wsd") == 0)
			format = new org.justcodecs.dsd.WSDFormat();
		else if (srcExt.compareTo(".dsf") == 0)
			format = new org.justcodecs.dsd.DSFFormat();
		else if (srcExt.compareTo(".dff") == 0)
			format = new org.justcodecs.dsd.DFFFormat();
		
		if (format == null)
			return null;
		return new JustDSDReader(format, new File(path));			
	}

	protected double byteToSec( long byteCount) {
		return reader.byteToSec(byteCount);
	}
	
	protected long secToByte( double sec ) {
		return reader.secToByte(sec); //-- byte
	}

	protected long countToByte( long sampeCount) {
		return reader.countToByte(sampeCount);
	}

	protected long byteToCount( long byteCount ) {
		return reader.byteToCount(byteCount);
	}

	//---------------------------------------------------------------
	//-- InteractiveProcess Interface
	@Override
	public double getTimeTotal() {
		return (double)getCountTotal()/reader.samplingFreq();
	}

	@Override
	public void seekTime(double start, double end) throws IOException {
		int samplingFreq = reader.samplingFreq();
		long startSample = (long)(samplingFreq * start);
		long endSample = (long)(samplingFreq * end);
		seekCount(startSample, endSample);
	}

	@Override
	public double getTimeNow() {
		return (double)getCountNow()/reader.samplingFreq();
	}
	
	@Override
	public long getCountTotal() {
		return reader.getCountTotal();
	}

	@Override
	public void seekCount(long start, long end ) throws IOException {
		reader.seekCount(start, end);
	}

	@Override
	public long getCountNow() {
		return reader.getCountNow();
	}
	
	@Override
	public int process() throws IOException {
		int samples = reader.read(audio);
		if (needBitReverse)
			audio.reverseBit();
		if (samples == 0)
			return samples;
		writer.write(audio, samples, channelMap);
		samples += samples;
		return samples;
	}

	@Override
	public void terminate() throws IOException {
		reader.close();
		writer.close();
	}
	//-- InteractiveProcess Interface
	//---------------------------------------------------------------
	
	/**
	 * fron CUI
	 * 
	 * @param src
	 * @param dst
	 * @param fromSec	null or start sec
	 * @param toSec		null or end sec
	 * @throws FileNotFoundException
	 * @throws DataFormatException
	 * @throws IOException
	 */
	public void convert( File src , File dst, Double fromSec, Double toSec  ) throws FileNotFoundException, DataFormatException, IOException {
		initializeFileIO( src, dst );
		if (fromSec != null && toSec != null)
			seekTime(fromSec, toSec);

		long size = 0;
		int n = 0;
		int i = 0;
		while ((n = process()) > 0){
			size += n;
			if (i++ > 1000) {
				i = 0;
				System.out.print(".");
			}
		}
		
		terminate();
		System.out.println("conver completed: " + size);

	}
	
}
