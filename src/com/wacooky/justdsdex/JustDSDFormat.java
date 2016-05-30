package com.wacooky.justdsdex;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteOrder;

import javax.imageio.stream.ImageInputStreamImpl;
import javax.imageio.stream.MemoryCacheImageInputStream;

import org.justcodecs.dsd.DSDFormat;
import org.justcodecs.dsd.Decoder.DecodeException;

import com.wacooky.audio.file.AudioFileFormat;
import com.wacooky.audio.file.AudioFileFormatInfo;
import com.wacooky.audio.file.FileFormat;
import com.wacooky.audio.file.FileFormatDeterminant;
import com.wacooky.audio.file.FileFormat.ReadProfile;

/**
 * JustDSDFormat wraps DSDFileFormat class defined in JustDSD project to be
 * an AudioFileFormat in terms of FileFormatDeterminant provider.
 * See also JustDSDStream class.
 *  
 * @author fujimori
 *
 * @param <E>
 */
public abstract class JustDSDFormat<E extends DSDFormat<?>> extends AudioFileFormat {
	protected Class<E> dsdFormatClazz;
	protected E dsdFormat;

	public JustDSDFormat() {
		super();
	}

	public JustDSDFormat(String path, ReadProfile profile) throws IOException {
		super(path, profile);
	}

	public JustDSDFormat(ImageInputStreamImpl inputStream) throws IOException {
		super( inputStream, ReadProfile.INFO);
	}
	
	public JustDSDFormat(ImageInputStreamImpl inputStream, ReadProfile profile) throws IOException {
		super( inputStream, profile);
	}

/*
	@Override	
	protected void initFileFormatInfo() {
		this.formatInfo = new AudioFileFormatInfo();
		this.formatInfo.formatName = "WSD";		
	}
*/

	@Override
	protected void initForRead(ImageInputStreamImpl peep, ReadProfile profile) throws IOException {
		JustDSDStream dsdStream = new JustDSDStream(peep);
		//E dsdFormat = null;
		try {
			dsdFormat = dsdFormatClazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return;
		}

		try {
			dsdFormat.init(dsdStream);
		} catch (DecodeException e) {
			e.printStackTrace();
			return;
		}
		
		AudioFileFormatInfo formatInfo = getAudioFileFormatInfo();		
		formatInfo.channelNumber = dsdFormat.getNumChannels();
		formatInfo.samplingFrequency = dsdFormat.getSampleRate();
		formatInfo.sampleBitLength = 1;
		formatInfo.sampleCount = dsdFormat.getSampleCount();	
		formatInfo.blockSize = 0; //--not specified
		
		//System.out.println(formatInfo.getString());
	}


	@Override
	public ByteOrder getByteOrder() {
		return ByteOrder.BIG_ENDIAN;
	}

/*
	@Override
	protected DataField[] getPrimaryDataFields() {
		return WsdDataField.values();
	}
*/
	
	/**
	 * Wrapper for org.justcodecs.dsd.WSDFormat
	 * @author fujimori
	 *
	 */
	public static class WSD extends JustDSDFormat<org.justcodecs.dsd.WSDFormat> {
		static public final FileFormatDeterminant<JustDSDFormat.WSD> determinant = 
				new FileFormatDeterminant<JustDSDFormat.WSD>(JustDSDFormat.WSD.class, new String[]{".wsd"}) {
			@Override
			public boolean matchHeader(ImageInputStreamImpl peep ) throws IOException {
				return peep.readByte() == '1' && peep.readByte() == 'b' && peep.readByte() == 'i' && peep.readByte() == 't';
			}
		};
	
		public WSD() {
			super();
		}
	
		public WSD(String path, ReadProfile profile) throws IOException {
			super(path, profile);
		}

		public WSD(ImageInputStreamImpl inputStream) throws IOException {
			super( inputStream, ReadProfile.INFO);
		}
		
		public WSD(ImageInputStreamImpl inputStream, ReadProfile profile) throws IOException {
			super( inputStream, profile);
		}

		@Override	
		protected void initFileFormatInfo() {
			this.formatInfo = new AudioFileFormatInfo();
			this.formatInfo.formatName = "WSD";		
			dsdFormatClazz = org.justcodecs.dsd.WSDFormat.class;
		}

		@Override
		public ImageInputStreamImpl createInputStream(String path) {
			ImageInputStreamImpl inputStream = null;
			try {
				byte[] buf = new byte[2048]; //-- fixed size are
				ByteArrayInputStream byteStream = new ByteArrayInputStream(buf);
				inputStream = new MemoryCacheImageInputStream(byteStream);
				FileInputStream fileStream =new FileInputStream(path);
				fileStream.read(buf);
				fileStream.close();

				byte[] fourc = new byte[4];
				inputStream.readFully(fourc);
				String str = new String(fourc);
				//String str = (String)WsdDataField.FIELD_ID.readFieldValue(inputStream);
				//System.out.println(str);
				//inputStream.close();
				inputStream.seek(0L);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return inputStream;
		}	
	}
	
	/**
	 * Wrapper for org.justcodecs.dsd.DSFFormat
	 * @author fujimori
	 *
	 */
	public static class DSF extends JustDSDFormat<org.justcodecs.dsd.DSFFormat> {
		static public final FileFormatDeterminant<JustDSDFormat.DSF> determinant = 
				new FileFormatDeterminant<JustDSDFormat.DSF>(JustDSDFormat.DSF.class, new String[]{".dsf"}) {
			@Override
			public boolean matchHeader(ImageInputStreamImpl peep ) throws IOException {
				return peep.readByte() == 'D' && peep.readByte() == 'S' && peep.readByte() == 'D' && peep.readByte() == ' ';
			}
		};
	
		public DSF() {
			super();
		}
	
		public DSF(String path, ReadProfile profile) throws IOException {
			super(path, profile);
		}

		public DSF(ImageInputStreamImpl inputStream) throws IOException {
			super( inputStream, ReadProfile.INFO);
		}
		
		public DSF(ImageInputStreamImpl inputStream, ReadProfile profile) throws IOException {
			super( inputStream, profile);
		}

		@Override	
		protected void initFileFormatInfo() {
			this.formatInfo = new AudioFileFormatInfo();
			this.formatInfo.formatName = "DSF";		
			dsdFormatClazz = org.justcodecs.dsd.DSFFormat.class;
		}
	}

	/**
	 * Wrapper for org.justcodecs.dsd.DFFFormat
	 * @author fujimori
	 *
	 */
	public static class DFF extends JustDSDFormat<org.justcodecs.dsd.DFFFormat> {
		static public final FileFormatDeterminant<JustDSDFormat.DFF> determinant = 
				new FileFormatDeterminant<JustDSDFormat.DFF>(JustDSDFormat.DFF.class, new String[]{".dff"}) {
			@Override
			public boolean matchHeader(ImageInputStreamImpl peep ) throws IOException {
				return peep.readByte() == 'F' && peep.readByte() == 'R' && peep.readByte() == 'M' && peep.readByte() == '8';
			}
		};
	
		public DFF() {
			super();
		}
	
		public DFF(String path, ReadProfile profile) throws IOException {
			super(path, profile);
		}

		public DFF(ImageInputStreamImpl inputStream) throws IOException {
			super( inputStream, ReadProfile.INFO);
		}
		
		public DFF(ImageInputStreamImpl inputStream, ReadProfile profile) throws IOException {
			super( inputStream, profile);
		}

		@Override	
		protected void initFileFormatInfo() {
			this.formatInfo = new AudioFileFormatInfo();
			this.formatInfo.formatName = "DFF";		
			dsdFormatClazz = org.justcodecs.dsd.DFFFormat.class;
		}
	}
}

