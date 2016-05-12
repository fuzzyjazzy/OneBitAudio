package com.wacooky.audio.onebit;
/**
 * AudioDeviceSelector.java
 *
 * @author Jun-ichi Fujimori
 * @version 1.0
 * History
 *  2009/07/20 original
 *
 */

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import javafx.scene.control.ChoiceDialog;

/**
 * AudioDeviceSelector
 * Ver. 2.0 2016-04-03
 * 
 * @author fujimori
 *
 */
public class AudioDeviceSelector {
	//*********************************************************
	public static void main(String[] args) {
//		SystemOut.setVerbose( true );
//		AudioDeviceSelector.test();
//		AudioDeviceSelector.listMixers();
//		AudioDeviceSelector.listSupportedFormats();


		System.out.println( "Mixer.Info = " +
			MixerSelector.mixerFromUserJ( "in", null ) );


/*
		boolean in = false;
		System.out.println( "Line.Info = " +
				AudioDeviceSelector.chooseDataLine( in ) );
*/

/*
		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				createAndShowGUI( exampleN );
			}
		});
*/
	}

/*
	private static void createAndShowGUI( int id ) {
		JFrame frame = new JFrame("Audio Device Test");

		frame.setVisible(true);
    }
*/
	//*********************************************************
	//ArrayList<String> stringList = new ArrayList<String>();
	String[] selection;

	public AudioDeviceSelector() {
	}

/*
	public AudioDeviceSelector(String side) {
		collectMixerInfo(side);
		int n = mixerInfoList.size();
		selection = new String[n];
		for (int i = 0; i < n; i++ )
			selection[i] = AudioDeviceSelector.convertShiftJisString( mixerInfoList.get(i).getName() );
	}
*/
	
	public String[] getSelection() {
		return selection;
	}
	
/*	
	static protected int indexOf( String val, String[] selection ) {
		for(int i = 0; i < selection.length; i++) {
			if (val == selection[i])
				return i;
		}
		return -1;
	}
*/	

	
	//--------------------------------------------------------
	//-- Legacy interface
	public static Mixer.Info chooseMixerJ( boolean in ) {
		return MixerSelector.mixerFromUserJ(in ? "in" : "out", null);
	}

	public static Line.Info chooseDataLineJ( boolean in ) {
		return DataLineSelector.dataLineFromUserJ(in ? "in" : "out");
	}

	protected static String convertShiftJisString( String shiftJisString ) {
		String unicodeString;
		try {
			unicodeString = new String(
				shiftJisString.getBytes("iso-8859-1"), "Shift_JIS");
		} catch( UnsupportedEncodingException e ) {
			return( "" );
		}
		return( unicodeString );
	}
	//-- Legacy interface
	//--------------------------------------------------------


	static public void listSupportedFormats() {
		//source data line format
		AudioFormat format;

		int channels = 2;
		boolean signed = true;
		boolean bigEndian = false;
		float[] sampleRateSet = {
			11024f, 22050f, 24000f, 44100f, 48000f, 88200f, 96000f, 176000f, 192000f, 352000f};
		for( int i = 0; i < sampleRateSet.length; i++ ) {
			format = new AudioFormat(
						sampleRateSet[i], 16, channels, signed, bigEndian);

			// Open a data line to play our type of sampled audio.
			// Use SourceDataLine for play and TargetDataLine for record.
			DataLine.Info info = new DataLine.Info(
									SourceDataLine.class, format );
			if( AudioSystem.isLineSupported( info ) ) {
				System.out.println(
					String.format("%d[Hz] %dbit %dch",
									(int)(sampleRateSet[i]), 16, channels ) );
			}

		}

	}

	static public void listMixers() {
/*
		//source data line format
		int channels = 2;
		boolean signed = true;
		boolean bigEndian = false;
		format = new AudioFormat(sampleRate, 16, channels, signed, bigEndian);

		// Open a data line to play our type of sampled audio.
		// Use SourceDataLine for play and TargetDataLine for record.
		DataLine.Info info = new DataLine.Info( SourceDataLine.class, format );	*/
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		for( int i = 0; i < mixerInfos.length; i++ ) {
			System.out.println("Mixer " +
				AudioDeviceSelector.convertShiftJisString( mixerInfos[i].getName() ) );
			//
			Mixer mixer = AudioSystem.getMixer( mixerInfos[i] );

			Line.Info[] lineInfos = mixer.getSourceLineInfo();
			for( int j = 0; j < lineInfos.length; j++ ) {
				System.out.println(" -Sounce line " +
				AudioDeviceSelector.convertShiftJisString( lineInfos[j].toString() ) );

/*
				if( lineInfos[j] instanceof DataLine.Info ) {
					DataLine.Info dataLineInfo = (DataLine.Info)lineInfos[j];
					AudioFormat[] supportedFormats =
						 dataLineInfo.getFormats();
					for( int k = 0; k < supportedFormats.length; k++ )
						System.out.println("Auio Format " +
												supportedFormats[k] );
				}
*/
			}

			lineInfos = mixer.getTargetLineInfo();
			for( int j = 0; j < lineInfos.length; j++ )
				System.out.println(" -Target line " +
				AudioDeviceSelector.convertShiftJisString( lineInfos[j].toString() ) );

		}
	}

	protected void test() {
		SourceDataLine line;

		//source data line format
		int sampleRate = 22050;
		int channels = 2;
		boolean signed = true;
		boolean bigEndian = false;
		AudioFormat format = new AudioFormat(
									sampleRate, 16, channels, signed, bigEndian
								 );
		DataLine.Info info = new DataLine.Info( SourceDataLine.class, format );
		try {
			line = (SourceDataLine) AudioSystem.getLine( info );
			System.out.println("line="+line);
		} catch ( LineUnavailableException e ) {
				e.printStackTrace();
		}

		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		for( int i = 0; i < mixerInfos.length; i++ )
			System.out.println("Mixer="+mixerInfos[i]);

		for( int i = 0; i < mixerInfos.length; i++ )
//			if( mixerInfos[i].getName().compareToIgnoreCase(
//				"Java Sound Audio Engine") == 0 ) {
//				System.out.println("found");
			if( i == 2 ) {
				System.out.println("Mixer "+mixerInfos[i]);
				Mixer mixer = AudioSystem.getMixer( mixerInfos[i] );
				try {
					line = (SourceDataLine) mixer.getLine( info );
					System.out.println("line="+line);
				} catch ( LineUnavailableException e ) {
					e.printStackTrace();
				}

			}

	}

}

class MixerSelector extends AudioDeviceSelector {
	public static Mixer.Info mixerFromUser( String side, Mixer.Info lastSelection ) {
		MixerSelector selector = new MixerSelector(side);
		return selector.mixerFromUser(lastSelection);
	}

	public static Mixer.Info mixerFromUserJ( String side, Mixer.Info lastSelection ) {
		MixerSelector selector = new MixerSelector(side);
		return selector.mixerFromUserJ(lastSelection);
	}


	ArrayList<Mixer.Info> mixerInfoList;
	String side;

	public MixerSelector(String side) {
		this.side = side;
		collectMixerInfo(this.side);
	}
	
	protected void collectMixerInfo(String side) {
		mixerInfoList = new ArrayList<Mixer.Info>();
		boolean in = (side.equalsIgnoreCase("in")) ? true : false;

		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();

		Line.Info[] lineInfos;
		for( int i = 0; i < mixerInfos.length; i++ ) {
			Mixer mixer = AudioSystem.getMixer( mixerInfos[i] );
			if( in )
				lineInfos = mixer.getTargetLineInfo();
			else
				lineInfos = mixer.getSourceLineInfo();

			for( int j = 0; j < lineInfos.length; j++ ) {
				if( lineInfos[j] instanceof DataLine.Info ) {
					mixerInfoList.add( mixerInfos[i] );
					break;
				}
			}
		}

		int n = mixerInfoList.size();
		selection = new String[n];
		for (int i = 0; i < n; i++ )
			selection[i] = mixerInfoList.get(i).getName();
			//selection[i] = AudioDeviceSelector.convertShiftJisString( mixerInfoList.get(i).getName() );

	}

	public int indexOf(Mixer.Info mixerInfo) {
		return mixerInfoList.indexOf(mixerInfo);
	}
	
	public Mixer.Info getMixer( String val ) {
		for(int i = 0; i < selection.length; i++) {
			if (val == selection[i])
				return mixerInfoList.get(i);
		}
		return null;
	}

	public Mixer.Info mixerFromUser( Mixer.Info lastSelection ) {
		String msg;
		if( side.equalsIgnoreCase("in") )
			msg = "Audio In";
		else
			msg = "Audio Out";

		int idx = (lastSelection == null) ? -1 : indexOf(lastSelection);
		String selected = (idx == -1) ? selection[0] : selection[idx];
	
		ChoiceDialog<String> dialog = new ChoiceDialog<>(selected, selection);
		dialog.setTitle("Choice Dialog");
		dialog.setHeaderText(msg);
		dialog.setContentText("Select device:");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) 
			return getMixer(result.get());
		else
			return null;
	}
	
	public Mixer.Info mixerFromUserJ( Mixer.Info lastSelection ) {
		String msg;
		if( side.equalsIgnoreCase("in") )
			msg = "Audio In";
		else
			msg = "Audio Out";
		
		int idx = (lastSelection == null) ? -1 : indexOf(lastSelection);
		String selected = (idx == -1) ? selection[0] : selection[idx];
		ImageIcon icon = null;
		//icon = new ImageIcon("./img/reo1s.gif");
		Object value = JOptionPane.showInputDialog(null, msg,
   										 		"Audio Line Chooser",
												JOptionPane.INFORMATION_MESSAGE, 												icon,
												selection, selected);
		return getMixer((String)value);
	}

}

class DataLineSelector extends AudioDeviceSelector {
	public static Line.Info dataLineFromUser( String side ) {
		DataLineSelector selector = new DataLineSelector(side);
		return selector.dataLineFromUser();
	}

	public static Line.Info dataLineFromUserJ( String side ) {
		DataLineSelector selector = new DataLineSelector(side);
		return selector.dataLineFromUserJ();
	}

	List<Line.Info> lineInfoList = new ArrayList<Line.Info>();
	String side;

	public DataLineSelector(String side) {
		this.side = side;
		collectDataLine(this.side);
	}
	
	public Line.Info getDataLine( String val ) {
		for(int i = 0; i < selection.length; i++) {
			if (val == selection[i])
				return lineInfoList.get(i);
		}
		return null;
	}
	
	protected void collectDataLine( String side ) {
		boolean in = (side.equalsIgnoreCase("in")) ? true : false;
		lineInfoList = new ArrayList<Line.Info>();
		List<String> stringList = new ArrayList<String>();

		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();

		Line.Info[] lineInfos;
		DataLine dataLine;
		int lineIndex;

		for( int i = 0; i < mixerInfos.length; i++ ) {
			Mixer mixer = AudioSystem.getMixer( mixerInfos[i] );
			if( in )
				lineInfos = mixer.getTargetLineInfo();
			else
				lineInfos = mixer.getSourceLineInfo();

			//Line.Info: DataLine.Info or Port.Info
			lineIndex = 1;
			for( int j = 0; j < lineInfos.length; j++ ) {
/*
				if( lineInfos[j] instanceof Port.Info )
					System.out.println("Port "+
							AudioDeviceSelector.convertShiftJisString(
								((Port.Info)lineInfos[j]).getName() ) );
*/
				if( lineInfos[j] instanceof DataLine.Info ) {
					try {
						dataLine = (DataLine)AudioSystem.getLine(lineInfos[j] );
						if( (in && dataLine instanceof TargetDataLine) ||
							(!in && dataLine instanceof SourceDataLine)	) {
							lineInfoList.add( lineInfos[j] );
							stringList.add(
								mixerInfos[i].getName()
								//AudioDeviceSelector.convertShiftJisString( mixerInfos[i].getName() ) 
								+ ": " + lineIndex++
							);
						}
					} catch( LineUnavailableException el ) {
						el.printStackTrace();
						continue;
					} catch( SecurityException es ) {
						es.printStackTrace();
						continue;
					} catch( IllegalArgumentException ei ) {
						ei.printStackTrace();
						continue;
					}
				}
			}
		}

		selection = new String[ lineInfoList.size() ];
		stringList.toArray( selection );
	}
	
	public Line.Info dataLineFromUser() {
		String msg;
		if( side.equalsIgnoreCase("in") )
			msg = "Audio In";
		else
			msg = "Audio Out";
	
		ChoiceDialog<String> dialog = new ChoiceDialog<>(selection[0], selection);
		dialog.setTitle("Choice Dialog");
		dialog.setHeaderText(msg);
		dialog.setContentText("Select device:");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) 
			return getDataLine(result.get());
		else
			return null;
	}

	public Line.Info dataLineFromUserJ() {
		String msg;
		if( side.equalsIgnoreCase("in") )
			msg = "Audio In";
		else
			msg = "Audio Out";
		ImageIcon icon = null;
		//icon = new ImageIcon("./img/reo1s.gif");
		Object value = JOptionPane.showInputDialog(null, msg,
   										 		"Audio Line Chooser",
												JOptionPane.INFORMATION_MESSAGE, 												icon,
												selection, selection[0]);

		return getDataLine((String)value);
	}

}

/*
class SystemOut {
	static private boolean verbose = false;

	final static void setVerbose( boolean b ) { verbose = b; }

	final static void println( String s ) {
		if( verbose )
			System.out.println( s );
	}
}
*/