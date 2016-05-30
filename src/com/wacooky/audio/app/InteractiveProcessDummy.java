package com.wacooky.audio.app;

import java.io.IOException;

import com.wacooky.audio.coverter.InteractiveProcess;

public class InteractiveProcessDummy implements InteractiveProcess {
	long wsdDataSize = 5640000; //-- byte size
	int readSize = 100000000;  //-- byte siz
	long wsdSamplingFreq = 5640000;
	int wsdChannels = 2;

	long currentPosition;
	long endPosition;
	
	public InteractiveProcessDummy() {
		wsdDataSize = secToByte(4*60 + 14); 
		try {
			seekTime(0., getTimeTotal());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected double byteToSec( long byteCount) {
		return (double)byteCount*8/(wsdSamplingFreq*wsdChannels);
	}
	
	protected long secToByte( double sec ) {
		return (long)(wsdSamplingFreq * sec * wsdChannels / 8); //-- byte
	}
	
	protected long countToByte( long sampeCount) {
		return (long)(sampeCount * wsdChannels / 8);
	}

	protected long byteToCount( long byteCount ) {
		return (long)(byteCount * 8 / wsdChannels);
	}

	@Override
	public void seekTime(double start, double end) throws IOException {
		currentPosition = secToByte(start);
		endPosition = secToByte(end);
	}

	@Override
	public double getTimeTotal() {
		return byteToSec(wsdDataSize);
	}

	@Override
	public double getTimeNow() {
		return byteToSec(currentPosition);
	}

	@Override
	public long getCountTotal() {
		return byteToCount(wsdDataSize);
	}

	@Override
	public void seekCount(long start, long end ) throws IOException {
		currentPosition = countToByte(start);
		endPosition = countToByte(end);
	}

	@Override
	public long getCountNow() {
		return byteToCount(currentPosition);
	}

	@Override
	public int process() throws IOException {
		if (currentPosition >= endPosition )
			return 0;
				
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return 0;
		}
		int n;
		if ((currentPosition + readSize) < endPosition )
			n = readSize;
		else
			n = (int)((currentPosition + readSize) - endPosition);
			
		currentPosition += n;
		System.out.println("Dummy process " + currentPosition );
		return n;
	}

	@Override
	public void terminate() throws IOException {
	}	
}