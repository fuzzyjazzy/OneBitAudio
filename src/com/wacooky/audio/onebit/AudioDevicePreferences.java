package com.wacooky.audio.onebit;

import java.io.IOException;

public class AudioDevicePreferences {

	protected void configureDevice() {
		String os = System.getProperty("os.name");
		System.out.println( os );
		if (os.startsWith("Mac OS X") ) {
			Runtime rt = Runtime.getRuntime();
			try {
				rt.exec(new String[] { "open", "/Applications/Utilities/Audio MIDI Setup.app" });
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
