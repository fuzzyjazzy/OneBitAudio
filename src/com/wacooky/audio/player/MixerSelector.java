package com.wacooky.audio.player;

import java.util.ArrayList;
import java.util.Optional;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import javafx.scene.control.ChoiceDialog;

class MixerSelector {
	public static Mixer.Info mixerFromUser( String side, Mixer.Info lastSelection ) {
		MixerSelector selector = new MixerSelector(side);
		return selector.mixerFromUser(lastSelection);
	}

	public static Mixer.Info mixerFromUserJ( String side, Mixer.Info lastSelection ) {
		MixerSelector selector = new MixerSelector(side);
		return selector.mixerFromUserJ(lastSelection);
	}	
	String[] selection;
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
			selection[i] = mixerInfoList.get(i).getName(); //- vendor happens to use local encoding 
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