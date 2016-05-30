package com.wacooky.audio.player;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLDecoder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

import org.justcodecs.dsd.DFFFormat;
import org.justcodecs.dsd.DISOFormat;
import org.justcodecs.dsd.DSDFormat;
import org.justcodecs.dsd.DSFFormat;
import org.justcodecs.dsd.Decoder;
import org.justcodecs.dsd.Utils;
import org.justcodecs.dsd.WSDFormat;
import org.justcodecs.dsd.Decoder.DecodeException;
import org.justcodecs.dsd.Decoder.PCMFormat;

import com.wacooky.task.SimpleTask;
import com.wacooky.task.TaskCallback;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * DAC
 *
 * File ->[Decoder]->[DAC] -> SourceDataLine
 *
 * @author fujimori
 * @version 0.9 2016-05-05
 * 
 */
public abstract class DAC {
	static public boolean DEBUG = false;
	static public DAC createDAC(Mixer.Info mixerinfo, boolean canDoP, String f ) throws FileNotFoundException, DecodeException {
		Decoder decoder = new Decoder();
		DSDFormat<?> dsd;
		if (f.toLowerCase().endsWith(".dsf")) {
			dsd = new DSFFormat();
		} else if (f.toLowerCase().endsWith(".iso")) {
			dsd = new DISOFormat();
		} else if (f.toLowerCase().endsWith(".wsd")) {
			dsd = new WSDFormat();			
		} else if (f.toLowerCase().endsWith(".dff")) {
			dsd = new DFFFormat();
		} else
			return null;
		
		if (f.toUpperCase().startsWith("FILE:/")) {
			try {
				f = new URL(URLDecoder.decode(f, "UTF-8")).getFile();
			} catch (Exception e) {
				// ignore
			}	
		}
		if (DEBUG) System.out.println("init " + dsd.getClass());
		dsd.init(new Utils.RandomDSDStream(new File(f)));

		decoder.init(dsd); 
		if (DEBUG) System.out.printf("Play using %s%n", dsd);
		DAC dac= new DsdDAC(mixerinfo, decoder, dsd);
		if ( dac.isActive() )
			return dac;

		if (canDoP) {
			if (DEBUG) System.out.println("DoP mode");
			dac = new DopDAC(mixerinfo, decoder, dsd);
			if ( dac.isActive() )
				return dac;
		}

		if (DEBUG) System.out.println("PCM mode");
		dac = new PcmDAC(mixerinfo, decoder );
		if ( dac.isActive() )
			return dac;
		return null;
	}
	
	/*
	//-- FOR TEST
	public static void main(String[] args) {
		play(null, false, "/Users/fujimori/Downloads/HiResolutionAudio/kristof20051030.dsf");
	}
*/
	static public void play(Mixer.Info mixerinfo, boolean canDoP, String path) {
		try {
			DAC dac = createDAC(mixerinfo, canDoP, path);
			dac.start(false); //-- background = false
			while( dac.convert() > 0 );
			dac.stop();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DecodeException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * audition
	 * 
	 * @param mixerinfo		mixer info or null for default mixer
	 * @param canDoP		DoP capability of the given mixer
	 * @param path			path to audio file
	 * @param fromSamples 	audition start sample count of input file.
	 * @param samples		audition duration sample count of input file.
	 * @param theListener	samplesProperty listener (output side)
	 * @return				SimpleTask<Object> 
	 */
	static public SimpleTask<Object>  audition(Mixer.Info mixerinfo, boolean canDoP, String path, long fromSamples, long samples, final ChangeListener<Number> theListener, TaskCallback callback ) {
		try {
			DAC dac = createDAC(mixerinfo, canDoP, path);
			
			//-- DAC's natural sample count is output one but
			//-- caller of audition's natural sample count is input one
			//-- since it only knows input file info.
			//-- So, so convert output sample count to input sample count here
			ChangeListener<Number> outputSamplesListener = null;
			if( theListener != null ) {
				outputSamplesListener = new ChangeListener<Number>() {
					public void changed(ObservableValue<? extends Number> obs,
							Number oldValue, Number newValue) {
						theListener.changed(obs, 
							dac.toInputSampleCount(oldValue.longValue()),
							dac.toInputSampleCount(newValue.longValue())
						);
					}
				};
			}

			dac.setOutputSampleCount( dac.toOutputSampleCount(fromSamples));
			if (samples <= 0)
				dac.setOutputSampleCountEnd(dac.toOutputSampleCount(dac.getInputSampleCount()));
			else
				dac.setOutputSampleCountEnd(dac.toOutputSampleCount(fromSamples + samples));
			return dac.run(outputSamplesListener, callback);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DecodeException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}


	//-------------------------------------------------
	//-- Instance variables
	Decoder decoder;
	int blockSize = 4096; //default
	 //--decorder's input side
	int inputSampleFrequency;
	long inputSampleCountTotal;
	//-- decoder's output side
	long outputSampleCount; //-- current sample count
	long prevSampleCount;
	long outputSampleCountEnd; //-- play until this sample count
	
	PCMFormat pcmf;
	Mixer.Info mixerinfo;
	SourceDataLine dl;

	private SimpleTask<Object> task;
	//-- seek control
	boolean background;
	long sampleCountToSeek;


	//-------------------------------------------------------
	//-- samplesProperty indicates current output sample count
	private ReadOnlyLongWrapper samples = new ReadOnlyLongWrapper(this, "samples", 0L);		
	public ReadOnlyLongWrapper samplesProperty() {
		return samples;
	}

	private ReadOnlyLongWrapper inputSamples = new ReadOnlyLongWrapper(this, "inputSamples", 0L);		
	public ReadOnlyLongWrapper inputSamplesProperty() {
		return inputSamples;
	}

	public void updateSamplesProperty() {
		Platform.runLater(new Runnable() {
			public void run() {
				samples.set(outputSampleCount);
				inputSamples.set(toInputSampleCount(outputSampleCount));
			}
		});
	}
	//-- samplesProperty
	//-----------------------------------			

	public DAC() {
		this(null);
	}

	public DAC(Mixer.Info mixerinfo) {
		outputSampleCount = 0;
		prevSampleCount = 0;
		this.mixerinfo = mixerinfo;
		//
		background = false;
		sampleCountToSeek = -1L;
	}

	protected void setDecoder(Decoder decoder) {
		this.decoder = decoder;
		inputSampleFrequency = this.decoder.getSampleRate();
		inputSampleCountTotal = this.decoder.getSampleCount();
		setOutputSampleCountEnd(toOutputSampleCount(inputSampleCountTotal));
	}
	
	//-------------------------------------------------------
	//-- sample location
	public void setOutputSampleCount(long outputSampleCount ) {
		if (background) {
			if (sampleCountToSeek == -1L)
				sampleCountToSeek = outputSampleCount;
			return;
		}
		seekToOutputSampleCount(outputSampleCount);
	}
	
	protected void seekToOutputSampleCount(long outputSampleCount) {
		long inputSampleCount = toInputSampleCount(outputSampleCount);
		try {
			decoder.seek(inputSampleCount);
			this.outputSampleCount = outputSampleCount;
			updateSamplesProperty();
		} catch (DecodeException e) {
			e.printStackTrace();
		}

	}

	public void seekInTask() {
		if (sampleCountToSeek != -1L) {
			seekToOutputSampleCount(sampleCountToSeek);
			sampleCountToSeek = -1L;
		}
	}

	protected long toOutputSampleCount(long inputSamples) {
		int outputSampleFrequency = getOutputSampleFrequency();
		return (long)(inputSamples * (double)outputSampleFrequency/inputSampleFrequency);
	}

	protected long toInputSampleCount(long outputSamples) {
		int outputSampleFrequency = getOutputSampleFrequency();
		//System.out.println("toInputSampleCount:" + outputSamples + " " + inputSampleFrequency + "/" + outputSampleFrequency);
		return (long)(outputSamples * (double)inputSampleFrequency/outputSampleFrequency);
	}
	//-- sample location
	//-------------------------------------------------------
	
	abstract protected SourceDataLine getDataLine();
	
	public boolean isActive() {
		return dl != null;
	}
/*
	public void seek( long samples ) throws DecodeException {
		if (decoder == null)
			return;
		decoder.seek(samples);
		outputSampleCount = AudioLocation.convertSampleCount(samples,  decoder.getSampleRate(), getOutputSampleFrequency() );
		System.out.println("seek " + outputSampleCount);
		
	}
*/
	
	public long getInputSampleCount() {
		if (decoder == null)
			return 0;
		return decoder.getSampleCount();
	}

	public int getInputSampleFrequency() {
		if (decoder ==  null)
			return 0;			
		return decoder.getSampleRate();
	}

	public int getOutputSampleFrequency() {
		//-- PCM or DoP 
		if ( pcmf != null )
			return pcmf.sampleRate;
		//-- DSD native (not tested)
		if (decoder != null)
			return decoder.getSampleRate();
		return 0;
	}
	
	public long getSampleCountInterval() {
		return outputSampleCount - prevSampleCount;
	}

	public long getCurrentSampleCount() {
		prevSampleCount = outputSampleCount;
		return outputSampleCount;
	}

	public void setOutputSampleCountEnd(long outputSampleCountEnd) {
		this.outputSampleCountEnd = outputSampleCountEnd;
	}
	
	//-- start must save background value to this.backgroung
	abstract public void start( boolean background) throws LineUnavailableException, DecodeException;
	abstract public int convert() throws DecodeException;
	//-- stop must reset this.background to false;
	abstract public void stop();

	public void play() throws LineUnavailableException, DecodeException {
		start(false);
		while( convert() > 0 );
		stop();
	}
	
	/**
	 * 
	 * @param theListener
	 * @return
	 */
	public SimpleTask<Object> run(final ChangeListener<Number> theListener, TaskCallback callback) {
		final int tickSamples = getOutputSampleFrequency()/10; //-- 1/10 sec
		task = new SimpleTask<Object>(callback) {
			@Override
			protected Object call() throws Exception  {
				if (theListener != null)
					((ObservableValue<? extends Number>)samplesProperty()).addListener(theListener);

				try {
					start(true); //-- background = true
					while( !isCancelled() ) {
						seekInTask();
						if (convert() > 0) {
							if ( getSampleCountInterval() > tickSamples ) {
								updateSamplesProperty();
							}
						} else {
							updateSamplesProperty();
							break; //-- auto stop
						}
					};
				} catch (LineUnavailableException e) {
					System.out.println("LineUnavailableException" + e);
					//e.printStackTrace();
				} catch (DecodeException e) {
					System.out.println("DecodeException" + e);
					//e.printStackTrace();
				} catch (RuntimeException e) {
					//System.out.println("DecodeException" + e);
					e.printStackTrace();
				}
				stop();

				if (theListener != null)
					((ObservableValue<? extends Number>)samplesProperty()).removeListener(theListener);

				return "OK";
			}
		};
		//task.DEBUG = true;
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
		return task;
	}

}

class DsdDAC extends DAC {
	DSDFormat<?> dsd;
	byte[] samples;
	int nsampl;
	
	public DsdDAC(Mixer.Info mixerinfo, Decoder decoder, DSDFormat<?> dsd) {
		super(mixerinfo);
		this.dsd = dsd;

		if (dsd instanceof DSFFormat || dsd instanceof WSDFormat)
			blockSize = 4096; //-- see DFFFormat bloc
		else if (dsd instanceof DFFFormat)
			blockSize = 2048; //-- see DFFFormat block

		setDecoder(decoder);
		//this.decoder = decoder;
		this.dl = getDataLine();
	}

	@Override
	public int getOutputSampleFrequency() {
		if( dsd != null )
			return dsd.getSampleRate();
		return 0;
	}

	/**
	 * Modified by fujimori
	 * @param dsd
	 * @param mixerinfo
	 * @return
	 */
	@Override
	protected SourceDataLine getDataLine() {
		try {
			return AudioSystem.getSourceDataLine(new AudioFormat(new AudioFormat.Encoding("DSD_UNSIGNED"),
					dsd.getSampleRate(), 1, dsd.getNumChannels(), 4, dsd.getSampleRate()/32,
					true), mixerinfo);
		} catch (IllegalArgumentException e) {
			if (DEBUG) System.out.printf("No DSD %s%n", e);
		} catch (LineUnavailableException e) {
			if (DEBUG) System.out.printf("No DSD %s%n", e);
		}
		return null;
	}

	@Override
	public void start(boolean background) throws LineUnavailableException {
		this.background = background;
		samples = new byte[dsd.getNumChannels() * blockSize];
		dl.open();
		dl.start();
	}

	@Override
	public int convert() throws DecodeException {
		nsampl = decoder.decodeDSD(dsd.getNumChannels(), samples);
		if (nsampl <= 0)
			return -1;
		
		if ((outputSampleCount + nsampl) >= outputSampleCountEnd) {
			nsampl =  (int)(outputSampleCountEnd - outputSampleCount);
		}
		
		dl.write(samples, 0, nsampl);
		outputSampleCount += nsampl;
		return nsampl;
	}
	
	@Override
	public void stop() {
		dl.stop();
		dl.close();
		background = false;
	}

}

class DopDAC extends DsdDAC {
	byte[] dopBuffer;
	int dopBufferSize;
	
	public DopDAC(Mixer.Info mixerinfo, Decoder decoder, DSDFormat<?> dsd) {
		super(mixerinfo, decoder, dsd);
	}

	@Override
	protected SourceDataLine getDataLine() {
		int pcmSampleRate = 0;
		int sf = dsd.getSampleRate();
		switch( sf ) {
			case 2822400: pcmSampleRate = 176400; break; //-- DSD64
			case 5644800: pcmSampleRate = 352800; break; //-- DSD128
		}
		if ( pcmSampleRate == 0 )
			return null;

		pcmf = new PCMFormat();
		pcmf.sampleRate = pcmSampleRate; //44100* 2 * 2 * 2; //-176.4kHz
		pcmf.bitsPerSample = 24;
		pcmf.channels = 2;
		//AudioFormat af = new AudioFormat(pcmf.sampleRate, pcmf.bitsPerSample, pcmf.channels, true, pcmf.lsb);
		AudioFormat af = new AudioFormat(pcmf.sampleRate, pcmf.bitsPerSample, pcmf.channels, true, !pcmf.lsb);
		if (DEBUG) System.out.println(af);
		try {
			//pcmf.bitsPerSample = 24;
			//decoder.setPCMFormat(pcmf);
			if (DEBUG) {
				if (mixerinfo != null)
					System.out.println("DoPDAC " + mixerinfo.getName());
				else
					System.out.println("DoPDAC Default Audio Output");
			}
			decoder.setOutputFormat(null);	
			return AudioSystem.getSourceDataLine(af, mixerinfo);
		} catch (LineUnavailableException e) {
			System.out.println(pcmSampleRate/1000f + "KHz 24bit is not supported.");
			e.printStackTrace();
		} catch (DecodeException e) {
			System.out.println("Decoder setOutputFormat Error");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void start(boolean background) throws LineUnavailableException {
/*
		samples = new byte[2 * 2048];
		dl.open();
		dl.start();
*/
		//-- TODO: cupport odd or multiple of 3 channles
		super.start(background);
		if (DEBUG) System.out.println("create DoP buffer " + dl.getBufferSize());
		int nchs = dsd.getNumChannels();
		
		int nwords = blockSize * nchs / 2;
		dopBufferSize = 3 * nwords;
		dopBuffer = new byte[dopBufferSize]; //- 24bit(3bytes)/sample
		if (DEBUG) System.out.println("create DoP buffer " + dopBufferSize + " for" + samples.length);

		int nchs2 = nchs * 2;
		for(int i = 0, j = 0; i < nwords; i++ ) {
			dopBuffer[j++] = (i % nchs2 < nchs) ? 0x05 : (byte)0xFA;
			dopBuffer[j++] = (byte)0xAA;			
			dopBuffer[j++] = (byte)0xAA;			
		}
	}

	@Override
	public int convert() throws DecodeException {
		//-- nsampl bytes
		nsampl = decoder.decodeDSD(dsd.getNumChannels(), samples);
		//nsampl = 2048*16;
		if (nsampl <= 0)
			return -1;

		if ((outputSampleCount + (nsampl * 8)) >= outputSampleCountEnd) {
			nsampl =  (int)((outputSampleCountEnd - outputSampleCount)/8);
		}
	
		int nchs = dsd.getNumChannels();
		//int nwords = CHANNEL_BUFFER_SIZE * nchs / 2;
		int nchs2 = nchs * 2;
		int j = 0;
		for(int i = 0; i < nsampl; i += nchs2) {
			for(int c = 0; c < nchs; c++ ) {
				//-- c = 0: L c = 1: R
				j++;
				//dopBuffer[j++] = Utils.reverse(samples[i+c]);			
				//dopBuffer[j++] = Utils.reverse(samples[i+c+nchs]);
				dopBuffer[j++] = samples[i+c];			
				dopBuffer[j++] = samples[i+c+nchs];

/*
				if (c==1) {
					dopBuffer[j++] = (byte)0xAA; //samples[i+c+nchs];
					dopBuffer[j++] = (byte)0xAA; //samples[i+c];
				} else {
					dopBuffer[j++] = samples[i+c];			
					dopBuffer[j++] = samples[i+c+nchs];
				}
*/
			}
		}

		//dl.write(samples, 0, nsampl);
		dl.write(dopBuffer, 0, dopBufferSize);
		outputSampleCount += (nsampl*8);
		return nsampl;
	}

}

class PcmDAC extends DAC {
	int[][] samples;
	int channels;
	int bytesChannelSample;
	byte[] playBuffer;
	//int testSeek;
	int nsampl;

	public PcmDAC(Mixer.Info mixerinfo, Decoder decoder) {
		super(mixerinfo);
		setDecoder(decoder);
		//this.decoder = decoder;
		dl = getDataLine();
	}
	
	@Override
	protected SourceDataLine getDataLine() {
		//System.out.printf("Samples %d duration %ds%n",  decoder.getSampleCount(), decoder.getSampleCount()/decoder.getSampleRate());
		pcmf = new PCMFormat();
		pcmf.sampleRate = 44100 * 2 * 2;
		pcmf.bitsPerSample = 16;
		//System.out.printf("clip: %x %x  %x-%x%n",((1 << pcmf.bitsPerSample) - 1) >> 1, 1 << pcmf.bitsPerSample, Short.MAX_VALUE, Short.MIN_VALUE); 
		pcmf.channels = 2;
		AudioFormat af = new AudioFormat(pcmf.sampleRate, pcmf.bitsPerSample, pcmf.channels, true, pcmf.lsb);
		if (DEBUG) System.out.println(af);
		try {
			pcmf.bitsPerSample = 24;
			decoder.setOutputFormat(pcmf);
			return AudioSystem.getSourceDataLine(af, mixerinfo);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (DecodeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void start(boolean background) throws LineUnavailableException, DecodeException {
		this.background = background;
		dl.open();
		dl.start();
		samples = new int[pcmf.channels][2048];
		channels = (pcmf.channels > 2 ? 2 : pcmf.channels);
		bytesChannelSample = 2; //pcmf.bitsPerSample / 8;
		int bytesSample = channels * bytesChannelSample;
		playBuffer = new byte[bytesSample * 2048];
		//decoder.seek(0);
		//testSeek = 0;		
	}

	@Override
	public int convert() throws DecodeException {
		nsampl = decoder.decodePCM(samples);
		if (nsampl <= 0)
			return -1;
		int bp = 0;

		if ((outputSampleCount + nsampl) >= outputSampleCountEnd) {
			nsampl =  (int)(outputSampleCountEnd - outputSampleCount);
		}
		
		for (int s = 0; s < nsampl; s++) {
			for (int c = 0; c < channels; c++) {
				//System.out.printf("%x", samples[c][s]);
				samples[c][s] >>=8;
				for (int b = 0; b < bytesChannelSample; b++)
					playBuffer[bp++] = (byte) ((samples[c][s] >> (b * 8)) & 255);
			}
		}
		//for (int k=0;k<bp; k++)
		//System.out.printf("%x", playBuffer[k]);
		dl.write(playBuffer, 0, bp);
		outputSampleCount += nsampl;
		/*
		if (testSeek > 0 && outputSampleCount > pcmf.sampleRate * 10) {
			decoder.seek((long) decoder.getSampleRate() * (testSeek));
			testSeek = 0;
		}
		*/
		return nsampl;
	}
	
	@Override
	public void stop() {
		dl.stop();
		dl.close();
		background = false;
	}

	
}