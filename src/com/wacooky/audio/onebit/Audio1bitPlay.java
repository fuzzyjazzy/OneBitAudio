package com.wacooky.audio.onebit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.Mixer;

import org.justcodecs.dsd.Decoder.DecodeException;

import com.wacooky.task.SimpleTask;
import com.wacooky.task.TaskCallback;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
/**
 * 
 * @author fujimori
 * @version 0.9 2016-05-05
 */
public class Audio1bitPlay {
	static public boolean DEBUG = false;

	@FXML private Button play;
	@FXML private Button stop;
	@FXML private Button device;
	@FXML public Label time;
	@FXML public Label duration;
	@FXML public Slider locationSlider;	
	@FXML private TextField path;
	
	private DAC dac;
	private SimpleTask<Object> task;
	private MixerSelector mixerSelector = new MixerSelector("out");
	private Mixer.Info mixerinfo;
	private List<String> listDoP;
	private int outputSampleFreq = 0;
	private AudioLocation audioLocation = new AudioLocation();
	private ChangeListener<Number> samplesPropertyListener;

	public Audio1bitPlay() {
		if (DEBUG) System.out.println("Scan Audio Devices..");
		listDoP = new ArrayList<String>();
		listDoP.add("iFi (by AMR) HD USB Audio Output");
	}

	/**
	 * Initialize controller
	 * @param obj
	 * @param stage
	 */
	public void initialize(Stage stage) {
		//this.stage = stage;
		stage.setOnCloseRequest((WindowEvent wevent) -> {
			stop();
		});
		
		path.textProperty().addListener((observable, oldValue, newValue) -> {
		    prepareDAC(newValue);
		});
		
		locationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			double ratio = newValue.doubleValue(); //-- ratio 0 - 1.0
			if (locationSlider.isValueChanging()) {
				try {
					long outputSamples = audioLocation.convertRatioToSample(ratio);
					dac.setOutputSampleCount(outputSamples);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		samplesPropertyListener = new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> obs,
                Number oldValue, Number newValue) {
            	long samples = newValue.longValue();
    			time.setText(audioLocation.sampleToString(samples));
    			locationSlider.setValue(audioLocation.convertSampleToRatio(samples));
            }
        };

	}

	protected void stop() {
		if (task != null )
			task.cancel();
	}
	
	protected boolean isPlaying() {
		return task != null;
	}
	
	@FXML
	public void onPlayClicked(ActionEvent event) {
		if (DEBUG) System.out.println("play");
		//-- extra samplesProperty lister is null and callback is null
		task = dac.run(null, new TaskCallback() {
			@Override
			public void complete() { task = null; }
		});
	}

	@FXML
	public void onStopClicked(ActionEvent event) {
		if (DEBUG) System.out.println("stop");
		if (task != null)
			task.cancel();
	}

	@FXML
	public void onDeviceClicked(ActionEvent event) {
		mixerinfo = mixerSelector.mixerFromUser(mixerinfo);
		if (DEBUG) System.out.println(mixerinfo.getName());
		configureDevice();
	}

	//-----------------------------------------
	//-- Drop files
	@FXML
	private void pathDragOver(DragEvent event) {
		Dragboard board = event.getDragboard();
		if(!isPlaying() && board.hasFiles()) {
			event.acceptTransferModes(TransferMode.COPY);
		}
	}

	@FXML
	private void pathDropped(DragEvent event) {
		Dragboard board = event.getDragboard();
		if(board.hasFiles()) {
			board.getFiles().stream().forEach((file) -> {
				path.setText(file.getPath());
				//System.out.println(file.getPath());
			});
			event.setDropCompleted(true);
		} else {
			//- not my type
			event.setDropCompleted(false);
		}
	}
	//-- Drop files
	//-----------------------------------------
	
	protected void configureDevice() {
		String os = System.getProperty("os.name");
		if (DEBUG) System.out.println( os );
		if (os.startsWith("Mac OS X") ) {
			Runtime rt = Runtime.getRuntime();
			try {
				rt.exec(new String[] { "open", "/Applications/Utilities/Audio MIDI Setup.app" });
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected void prepareDAC(String path) {
		//-- remove samplesProperty listener to prevent from memory leak
		if (dac != null)
			((ObservableValue<? extends Number>) dac.samplesProperty()).removeListener(samplesPropertyListener);

		dac = null;
		if (path == null || path.isEmpty())
			return;

		boolean canDoP =  (mixerinfo != null && listDoP.contains(mixerinfo.getName()) ) ? true : false;
		try {
			dac = DAC.createDAC(mixerinfo, canDoP, path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DecodeException e) {
			e.printStackTrace();
		}

		if (DEBUG) System.out.println("dac:" + dac);
		if( dac == null)
			return;
		
		audioLocation.setSampleFrequency(dac.getOutputSampleFrequency());
		outputSampleFreq = dac.getOutputSampleFrequency();
		int inputSampleFreq = dac.getInputSampleFrequency();
		long inputSampleCount = dac.getInputSampleCount();
		long outputSampleCount = AudioLocation.convertSampleCount(inputSampleCount, inputSampleFreq, outputSampleFreq);
		if (DEBUG) System.out.println(AudioLocation.sampleToTimeString(outputSampleCount, outputSampleFreq));
		audioLocation.setSampleCountTotal(outputSampleCount);
		//Decoder decoder = dac.getDecoder();
		duration.setText(
			AudioLocation.sampleToTimeString(inputSampleCount, inputSampleFreq)
		);
		if (DEBUG) System.out.println(outputSampleFreq + " " + inputSampleFreq);
		
		((ObservableValue<? extends Number>) dac.samplesProperty()).addListener(samplesPropertyListener);
		dac.seekToOutputSampleCount(0L);
		//-----------------------------------------------------
		//TODO: remove next line.
		//dac.seekToOutputSampleCount() updates samplesProperty but 
		//the listener never be called bufore run().
		samplesPropertyListener.changed(null, null,new Long(0));
		//-----------------------------------------------------
		
		//--- TEST
		//dac.setOutputSampleCountEnd(audioLocation.convertTimeToSample(5.0));
		//DAC.audition(null, false, path, 0L, inputSampleFreq*5, null);
	}
}

class AudioLocation {
	static public String sampleToTimeString( long samples, int samplingFreq ) {
		if (samplingFreq == 0 )
			return String.format("%d", samples);
		double time = samples/(double)samplingFreq;
		int hour = (int)(time / 3600);
		time -= (hour*3600);
		int min = (int)(time / 60);
		time -= (min*60);
		int sec = (int)time;
		time -= sec;
		return String.format("%02d:%02d:%02d %02d", hour, min, sec, (int)(time*100));
	}
	
	static public long convertSampleCount( long samples, int sampleFreq, int newSampleFreq ) {
		return (long)(samples * (double)newSampleFreq/sampleFreq);
	}
	
	protected int sampleFrequency = 0;
	protected long sampleCountTotal = 0;
	protected int stringFormat = 1;
	
	public AudioLocation() {
	}

	public AudioLocation setSampleFrequency(int sampleFrequency) {
		this.sampleFrequency = sampleFrequency;
		return this;
	}

	public AudioLocation setSampleCountTotal(long sampleCountTotal) {
		this.sampleCountTotal = sampleCountTotal;
		return this;
	}

	public double convertSampleToRatio(long samples) {
		return (double)samples/this.sampleCountTotal;
	}

	public long convertRatioToSample(double ratio) {
		return (long)(this.sampleCountTotal*ratio);
	}
	
	public long convertTimeToSample(double time) {
		return (long)(this.sampleFrequency * time + 0.5);
	}
	
	public String sampleToString(long samples) {
		if (this.stringFormat == 0 )
			return sampleToSampleString(samples);
		else
			return sampleToTimeString(samples);
	}

	
	public String sampleToSampleString(long samples) {
		return String.format("%,d", samples);
	}
	
	public String sampleToTimeString(long samples) {
		return AudioLocation.sampleToTimeString(samples, this.sampleFrequency);
	}
	
}
