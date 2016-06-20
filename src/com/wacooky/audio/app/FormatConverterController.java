package com.wacooky.audio.app;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

import com.wacooky.audio.coverter.FormatConverter;
import com.wacooky.audio.coverter.InteractiveProcess;
import com.wacooky.audio.file.AudioFileInfo;
import com.wacooky.audio.player.DAC;
import com.wacooky.justdsdex.JustDSDFormat;
import com.wacooky.task.SimpleTask;
import com.wacooky.task.TaskCallback;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

@Version("0.2")
public class FormatConverterController implements Initializable {
	static public boolean DEBUG = true;

	@FXML private BorderPane mainBorderPane;	
	@FXML private Label currentFileLabel;
	@FXML private SimpleTimeRegionWidget simpleTimeRegion;

	@FXML private ComboBox<String> outComboBox;
	@FXML private Button convertOneButton;
	@FXML private Button convertAllButton;
	@FXML private Button stopButton;
	@FXML private Button auditionButton;
	@FXML private Button removeButton;

	//-----------------------------
	//timePicker
	@FXML private TimePicker endTimePicker;	
	@FXML private TimePicker startTimePicker;
	DoubleProperty currentTime;
	
	private AudioFileTableWidget audioFileTableWidget;

	private SimpleTask<Object> conversionTask;
	private boolean singleStep;
	private String outputFileExtension;
	private Pattern namePattern = Pattern.compile("(.*)((__)(\\d+))?\\.\\w+$");
	
	private SimpleTask<Object> auditionTask;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	 	Version version = FormatConverterController.class.getAnnotation(Version.class);
		if (DEBUG) System.out.println(this.getClass() + " init " + version);

		audioFileTableWidget = new AudioFileTableWidget();
		audioFileTableWidget.setFileFormatDeterminants(
			//WsdFormat.determinant, DsfFormat.determinant
			JustDSDFormat.WSD.determinant, JustDSDFormat.DSF.determinant
		);

		//------------------------------------------------------
		//-- When a row of table is selected
		audioFileTableWidget.currentFileInfoProperty().addListener(
			(ObservableValue<? extends AudioFileInfo> ov, AudioFileInfo oldInfo, AudioFileInfo newInfo) -> {

				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						Double time = null;
						if (newInfo == null)
							currentFileLabel.setText("");
						else {
							currentFileLabel.setText(newInfo.getPath().toAbsolutePath().toString());
							time = timeFromStringHHMMSS(newInfo.timeProperty().get());
						}
						if (time != null)
							setDurationTime(time.doubleValue());
						else
							setDurationTime(0.);

					}
				});
		});
	
		mainBorderPane.setCenter(audioFileTableWidget);
		
		startTimePicker.timeProperty().bindBidirectional(simpleTimeRegion.startTimeProperty());
		endTimePicker.timeProperty().bindBidirectional(simpleTimeRegion.endTimeProperty());
		
		singleStep = false;
		
		outComboBox.getItems().addAll(new String[]{"DSF","WSD"});
		outComboBox.getSelectionModel().select(0);
		updateOutputFileExtension();

		//-- test
		//setDurationTime(400.);
		/*
		currentTime = new SimpleDoubleProperty(0);
		currentTime.addListener((arg0, oldValue, newValue) -> {
			System.out.println("currentTime: " + newValue);
	    });
	    */
	}
	
	public void setOnCloseRequestTo(Stage stage) {
		stage.setOnCloseRequest((WindowEvent wevent) -> {
			if (conversionTask == null && auditionTask == null )
				return;
	
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirmation Dialog");
			alert.setHeaderText("Window is about to close.");
			alert.setContentText("Are you ok with this?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK){
				stopConversion();
				stopAudition();
			} else {
				wevent.consume();
			}
		});
	}
	
	protected Double timeFromStringHHMMSS(String hhmmss) {
		String[] hms = hhmmss.split(":");
		int n = hms.length;
		if (n > 3 )
			return null; //-- ERROR

		int hour = 0;
		int min = 0;
		double sec = 0;
		if (n == 3 )
			hour = Integer.parseInt(hms[n-3]);
		if ( n >= 2 )
			min = Integer.parseInt(hms[n-2]);
		if ( n >= 1 )
			sec = Float.parseFloat(hms[n-1]);
		if (hour > 11 || min > 59 || sec > 59.99 )
			return null; //-- ERROR
		return new Double(hour*3600 + min*60 + sec);
	}

	//-- Sync three controls
	public void setDurationTime(double time) {
		simpleTimeRegion.setDurationTime(time);
		startTimePicker.setTime(0.);
		startTimePicker.setMax(time);
		endTimePicker.setMax(time);
		endTimePicker.setTime(time);
	}

	protected void updateOutputFileExtension() {
		String ext = outComboBox.getSelectionModel().getSelectedItem();
		if (ext != null)
			outputFileExtension = ext.toLowerCase();
	}
	
	@FXML
	public void onOutComboBoxClicked(ActionEvent event) {
		updateOutputFileExtension();
	}
	
	@FXML
	public void onConvertOneClicked(ActionEvent event) {
		singleStep = true;
		runConverter();
	}

	@FXML
	public void onConvertAllClicked(ActionEvent event) {
		singleStep = false;
		runConverter();
	}

	@FXML
	public void onStopButtonClicked(ActionEvent event) {
		stopConversion();
	}

	@FXML
	public void onAuditionButtonClicked(ActionEvent event) {
		//System.out.println("audition");
		if (auditionTask != null) {
			stopAudition();
			return;
		}
		runAuditioner();
	}

	@FXML
	public void onRemoveButtonClicked(ActionEvent event) {
		audioFileTableWidget.removeSelection();
	}

	protected Path currentDst() {
		AudioFileInfo info = audioFileTableWidget.getCurrentFileInfo();
		if (info == null)
			return null;
		String name = info.getPath().getFileName().toString();
		Matcher m = namePattern.matcher(name);
		if (!m.matches())
			return null;
		name = m.group(1);
		
		//for (int i = 0; i < m.groupCount(); i++)
		//	System.out.println("[" + m.group(i) +"]");
		
		int idx = 1;
		if (m.group(3) != null)
			idx = Integer.parseInt(m.group(3));
	
		//name = name.substring(0, name.length()-4);
		String ext = "." + outputFileExtension;
		Path dir =  info.getPath().getParent();
		String guess = name + ((m.group(2) != null) ? m.group(2) : ""); //-- prefix to index
		String result;
		while ( Files.exists(dir.resolve((result = guess + ext))) )
			guess = name + "__" + String.valueOf(idx++);

		if (DEBUG) System.out.println("dst " + result);
		return dir.resolve(result);
/*		
		File file = new File("/Volumes/WDMyPassport/DSD/");
		Path parent = file.toPath();
		//Path parent = currentFileInfo.path.getParent();
		return parent.resolve(name);
*/
	}

	protected void stopAudition() {
		if (DEBUG) System.out.println("stop audition");
		if (auditionTask != null) {
			auditionTask.cancel();
			auditionTask = null;
		}
	}

	protected void stopConversion() {
		if (DEBUG) System.out.println("stop conversion");
		if (conversionTask != null) {
			conversionTask.cancel();
			conversionTask = null;
		}
	}
	
	protected void clearSelection() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				audioFileTableWidget.removeSelection();
				simpleTimeRegion.setDurationTime(0.);
			}
		});
		
		while (audioFileTableWidget.getCurrentFileInfo() != null ) {
			//System.out.println("Wait");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	protected void runAuditioner() {
		AudioFileInfo fileInfo = audioFileTableWidget.getCurrentFileInfo();
		if ( fileInfo == null )
			return;
		int sampleFrequency = fileInfo.getSampleFrequency();
		long fromSampleCount = (long)(simpleTimeRegion.getStartTime()*sampleFrequency);
		long toSampleCount = (long)(simpleTimeRegion.getEndTime()*sampleFrequency);
		
		ChangeListener<Number> inputSamplesListener = new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> obs,
					Number oldValue, Number newValue) {
				double time = (double)newValue.longValue()/sampleFrequency;
				simpleTimeRegion.currentTimeProperty().set(time);
			}
		};

		auditionTask = DAC.audition(null, false, fileInfo.getPath().toString(),
			fromSampleCount, toSampleCount-fromSampleCount, inputSamplesListener,
			new TaskCallback() {
				@Override
				public void complete() { stopAudition(); }
			}
		);
		if (DEBUG)
			System.out.println("audition " +
					simpleTimeRegion.getStartTime() + "->" + simpleTimeRegion.getEndTime() +
					" : " + fromSampleCount + "->" + toSampleCount);
	}
	
	protected void runConverter() {
		conversionTask = new SimpleTask<Object>() {
			private boolean TASK_DEBUG = false;
			//-----------------------------------
			//-- additional property named "time"
			private ReadOnlyDoubleWrapper time = new ReadOnlyDoubleWrapper(this, "time", 0.);		
			public ReadOnlyDoubleWrapper timeProperty() {
				return time;
			}
			//-- additional property named "time"
			//-----------------------------------
				
			@Override
			protected Object call() throws Exception  {
				//Path dstPath;
				InteractiveProcess converter;

				//--------------------------------------
				//-- Scanning Table Loop
				while( !isCancelled() ) {
					AudioFileInfo fileInfo = audioFileTableWidget.getCurrentFileInfo();
					if ( fileInfo == null ) {
						if ( audioFileTableWidget.selectFirst() == null )
							break;
					}

					//-- Process Initialize
					if (TASK_DEBUG)
						converter = new InteractiveProcessDummy();
					else
						converter = createConverter();
					if ( converter == null )
						break;
					converter.seekTime(simpleTimeRegion.getStartTime(), simpleTimeRegion.getEndTime());
					//wsd2dsd.setPosition(0., wsd2dsd.getDurationTime());
					//wsd2dsd = createConverter();
					//long size = 0;
					//int n = 0;
					int i = 0;
					//-------------------------------------------------------
					//-- Process Loop
					while( !isCancelled() && (converter.process()) > 0 ) {
						//size += n;
						//updateProgress(wsd2dsd.getCurrentPosition(), wsd2dsd.getDurationTime());
						if (i++ > 1000) {
							i = 0;
							updateProperty(time, converter.getTimeNow());
							//System.out.print(".");
							//updateMessage( printSampleLocation(dac.getSampleCount()) );
						}					
					};
					//-- Process Loop
					//-------------------------------------------------------

					//-- Process Terminate
					updateProperty(time, converter.getTimeNow());
					converter.terminate();
					clearSelection();
					updateProperty(time, 0.);
					if (singleStep)
						break;
				}
				//-- Scanning Table Loop
				//--------------------------------------
				return "OK";
			}

			@Override
			public ReadOnlyProperty<?>[] getBindProperties() {
				ReadOnlyProperty<?>[] properties = new ReadOnlyProperty<?>[2];
				properties[0] = titleProperty();
				//properties[1] = messageProperty();
				//properties[1] = progressProperty();
				properties[1] = timeProperty();
				return properties;
			}
		};
	
		/*
		//-- PropertyBinder can bind an anonymous class's property.
		PropertyBinder binder = new PropertyBinder();
		//binder.add("title", currentFileLabel.textProperty());
		//binder.add("progress", currentTime);
		binder.add("time", timeRangeContol.currentTimeProperty());
		binder.bind(task); //-- bind anonymous subclass of Task
		//
		*/

		//-- Or manually bind this way
		ReadOnlyProperty<?>[] props = conversionTask.getBindProperties();
		simpleTimeRegion.currentTimeProperty().bind((ObservableValue<? extends Number>) props[1]);

		Thread th = new Thread(conversionTask);
		th.setDaemon(true);
		th.start();    
	}

	
	protected InteractiveProcess createConverter() {
		AudioFileInfo fileInfo = audioFileTableWidget.getCurrentFileInfo();
		if (fileInfo == null)
			return null;
		Path dstPath = currentDst();
		File srcFile = fileInfo.getPath().toFile();
		File dstFile = dstPath.toFile();
		FormatConverter converter = new FormatConverter();			
		try {
			converter.initializeFileIO(srcFile, dstFile);
			return converter;
		} catch (DataFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	//-------------------------------------------------------------------

}
