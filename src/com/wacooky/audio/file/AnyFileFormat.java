package com.wacooky.audio.file;

import java.io.IOException;
import java.nio.ByteOrder;

import javax.imageio.stream.ImageInputStreamImpl;


class AnyFileFormat extends FileFormat {

	public AnyFileFormat(String path, ReadProfile profile) throws IOException {
		super(path, profile);
	}
	
	public AnyFileFormat(ImageInputStreamImpl inputStream, ReadProfile profile) throws IOException {
		initFileFormatInfo();
		initForRead(inputStream, profile);
	}

	
	@Override
	protected void initFileFormatInfo() {
		formatInfo = new FileFormatInfo();
		formatInfo.formatName = "";		
	}

	@Override
	protected void initForRead(ImageInputStreamImpl inputStream, ReadProfile profile) throws IOException {
	}

	@Override
	public ByteOrder getByteOrder() {
		return null;
	}

}