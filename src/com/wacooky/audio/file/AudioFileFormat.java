package com.wacooky.audio.file;

import java.io.IOException;

import javax.imageio.stream.ImageInputStreamImpl;

abstract public class AudioFileFormat extends FileFormat {
	public AudioFileFormat() {
		super();
	}

	public AudioFileFormat(String path, ReadProfile profile) throws IOException {
		super(path, profile);
	}
	
	public AudioFileFormat(ImageInputStreamImpl inputStream, ReadProfile profile) throws IOException {
		super(inputStream, profile);
	}
	
	public int samplingFrequency() {
		return ((AudioFileFormatInfo)formatInfo).samplingFrequency;
	}
	
	public int channelNumber() {
		return ((AudioFileFormatInfo)formatInfo).channelNumber;
	}

	public double timeCount() {
		return ((AudioFileFormatInfo)formatInfo).timeCount();
	}

	protected AudioFileFormatInfo getAudioFileFormatInfo() {
		return (AudioFileFormatInfo)formatInfo;
	}
}
