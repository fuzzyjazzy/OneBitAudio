package com.wacooky.audio.file;

public class PrintDataUtility {
	static public String hexDump( byte[] buf, int offset, int len) {
		int max = ( buf.length >  (offset + len) ) ? buf.length : (offset + len);
		String hex = "";
		for( int i = offset; i < max; i++ )
			hex += String.format("%02x ", buf[i]);
		return hex;
	}
	
	static public String fileSizeString(long size) {
		if ( size > 1000000000L) //--GB
			return String.format("%4.1fGB", (float)((double)size/1000000000L)+0.05);
		if ( size > 1000000) //--MB
			return String.format("%4.1fMB", (float)((double)size/1000000)+0.05);
		if ( size > 1000) //--KB
			return String.format("%4.1fKB", (float)((double)size/1000)+0.05);
		return String.format("%dB", size);	
	}
	
	static public String timeString( double timeInSec ) {
		//System.out.println(timeInSec);
		int hour = (int)(timeInSec / 3600);
		int min = (int)((timeInSec - 3600*hour)/60);
		int sec = (int)(timeInSec - 3600*hour - 60*min);
		return String.format("%02d:%02d:%02d", hour, min, sec);
	}

	static public String samplingFrequencyString(int sf) {
		if( sf > 1000000)
			return String.format("%4.2fMHz", (float)sf/1000000);
		if( sf > 1000)
			return String.format("%4.2fkHz", (float)sf/1000);
		return String.format("%dHz", sf);
			
	}

}