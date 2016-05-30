package com.wacooky.audio.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStreamImpl;
import javax.imageio.stream.ImageOutputStreamImpl;


public abstract class FileFormat {
	public static enum ReadProfile { INFO, DATA, FIELD }
	//--- Example
	//--static public final FileFormatDeterminant<FileFormat> determinant = 
	//--	new FileFormatDeterminant<FileFormat>(FileFormat.class, null);

	static public final FileFormatDeterminant<AnyFileFormat> determinant = 
			new FileFormatDeterminant<AnyFileFormat>(AnyFileFormat.class, null);
	
	//------------------------------------------------------
	//-- Abstract class
	protected ImageInputStreamImpl currentStream;
	protected byte[] currentBuf;
	protected FileFormatInfo formatInfo;
	
	public FileFormat() {
		initFileFormatInfo();
		System.out.println("FileFormat");
	}

	public FileFormat(String path, ReadProfile profile) throws IOException {
		this(new FileImageInputStream(new RandomAccessFile(path, "r")), profile);
	}

	public FileFormat(ImageInputStreamImpl inputStream, ReadProfile profile) throws IOException {
		initFileFormatInfo();
		initForRead(inputStream, profile);
	}

	abstract protected void initFileFormatInfo();
	abstract protected void initForRead(ImageInputStreamImpl inputStream, ReadProfile profile) throws IOException;
	public abstract ByteOrder getByteOrder();
	
	public byte[] getCurrentBuf() {
		return currentBuf;
	}
	
	public void writeRawData(byte[] buf, int offset, int len ) throws IOException {
		//if (currentStream instanceof ImageOutputStreamImpl) {
		ImageOutputStreamImpl outputStream = (ImageOutputStreamImpl)currentStream;
		outputStream.write(buf, offset, len);
	}

	public void readRawData(byte[] buf, int offset, int len ) throws IOException {
		//if (currentStream instanceof ImageInputStreamImpl) {
		currentStream.read(buf, offset, len);
	}

	public void close() throws IOException {
		if (currentStream != null)
			currentStream.close();
		currentStream = null;
	}

	public ImageInputStreamImpl createInputStream(String path) throws FileNotFoundException {
		ImageInputStreamImpl inputStream = null;
		try {
			inputStream = new FileImageInputStream(new File(path));
			//inputStream.seek(0L);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inputStream;
	}
}