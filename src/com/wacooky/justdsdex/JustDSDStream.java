package com.wacooky.justdsdex;

import java.io.DataInput;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.imageio.stream.ImageInputStreamImpl;

import org.justcodecs.dsd.DSDStream;

/**
 * JustDSDStream is an different implementation of DSDStrema than
 * RandomDSDStrema defined in JustDSD project.
 * This DSDStreama support both RandamAccessFile and ImageInputStreamImpl.
 * 
 * @author fujimori
 *
 */
public class JustDSDStream implements DSDStream {
	protected byte[] buf = new byte[8];
	DataInput dataInput;
	
	public JustDSDStream(File file) throws FileNotFoundException {
		dataInput = new RandomAccessFile(file, "r");
	}
	
	public JustDSDStream(ImageInputStreamImpl peep) {
		dataInput = peep;
	}
	
	@Override
	public boolean canSeek() {
		return true;
	}

	public long readLong(boolean lsb) throws IOException {
		if (lsb)
			return readLong();
		readFully(buf, 0, 8);
		//System.out.printf("Buf 7 %d 0 - %d %n", buf[7], buf[0]);
		return ((long) (buf[7] & 255) << 56) + ((long) (buf[6] & 255) << 48) + ((long) (buf[5] & 255) << 40)
				+ ((long) (buf[4] & 255) << 32) + ((long) (buf[3] & 255) << 24) + ((long) (buf[2] & 255) << 16)
				+ ((long) (buf[1] & 255) << 8) + (buf[0] & 255);
	}

	@Override
	public int readInt(boolean lsb) throws IOException {
		if (lsb)
			return readInt();
		readFully(buf, 0, 4);
		return ((int) (buf[3] & 255) << 24) + ((int) (buf[2] & 255) << 16) + ((int) (buf[1] & 255) << 8)
				+ (buf[0] & 255);
	}

	@Override
	public long readIntUnsigned(boolean lsb) throws IOException {
		readFully(buf, 0, 4);
		long result = 0;
		if (lsb) {
			result += ((buf[0] & 255) << 24) + ((buf[1] & 255) << 16) + ((buf[2] & 255) << 8) + (buf[3] & 255);
		} else {
			result += ((buf[3] & 255) << 24) + ((buf[2] & 255) << 16) + ((buf[1] & 255) << 8) + (buf[0] & 255);
		}
		return result;
	}

	@Override
	public short readShort(boolean lsb) throws IOException {
		if (lsb)
			return readShort();
		readFully(buf, 0, 2);
		return (short) (((short) (buf[1] & 255) << 8) + (buf[0] & 255));
	}

	//---------------------------------------------------------------
	//-- dispatch
	@Override
	public void readFully(byte[] b) throws IOException {
		dataInput.readFully(b);		
	}

	@Override
	public int skipBytes(int n) throws IOException {
		return dataInput.skipBytes(n);
	}

	@Override
	public boolean readBoolean() throws IOException {
		return dataInput.readBoolean();
	}

	@Override
	public byte readByte() throws IOException {
		return dataInput.readByte();
	}

	@Override
	public int readUnsignedByte() throws IOException {
		return dataInput.readUnsignedByte();
	}

	@Override
	public short readShort() throws IOException {
		return dataInput.readShort();
	}

	@Override
	public int readUnsignedShort() throws IOException {
		return dataInput.readUnsignedByte();
	}

	@Override
	public char readChar() throws IOException {
		return dataInput.readChar();
	}

	@Override
	public int readInt() throws IOException {
		return dataInput.readInt();
	}

	@Override
	public long readLong() throws IOException {
		return dataInput.readLong();
	}

	@Override
	public float readFloat() throws IOException {
		return dataInput.readFloat();
	}

	@Override
	public double readDouble() throws IOException {
		return dataInput.readDouble();
	}

	@Override
	public String readLine() throws IOException {
		return dataInput.readLine();
	}

	@Override
	public String readUTF() throws IOException {
		return dataInput.readUTF();
	}

	@Override
	public void readFully(byte[] b, int off, int len) throws IOException {
		dataInput.readFully(b, off, len);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (dataInput instanceof RandomAccessFile)
			return ((RandomAccessFile)dataInput).read(b, off, len);
		else
			return ((ImageInputStreamImpl)dataInput).read(b, off, len);
	}

	@Override
	public long length() throws IOException {
		if (dataInput instanceof RandomAccessFile)
			return ((RandomAccessFile)dataInput).length();
		else
			return ((ImageInputStreamImpl)dataInput).length();
	}

	@Override
	public long getFilePointer() throws IOException {
		if (dataInput instanceof RandomAccessFile)
			return ((RandomAccessFile)dataInput).getFilePointer();
		else
			return ((ImageInputStreamImpl)dataInput).getStreamPosition();
	}

	@Override
	public void seek(long pointer) throws IOException {
		if (dataInput instanceof RandomAccessFile)
			((RandomAccessFile)dataInput).seek(pointer);
		else
			((ImageInputStreamImpl)dataInput).seek(pointer);
	}

	@Override
	public void close() throws IOException {
		if (dataInput instanceof RandomAccessFile)
			((RandomAccessFile)dataInput).close();
		else
			((ImageInputStreamImpl)dataInput).close();		
	}

}
