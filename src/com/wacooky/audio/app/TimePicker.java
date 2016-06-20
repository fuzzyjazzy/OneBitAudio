package com.wacooky.audio.app;

import java.io.IOException;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class TimePicker extends AnchorPane {
	@FXML private Spinner<Double> timePickerSpinner;	
	@FXML private Label hourLabel;
	@FXML private Label minuteLabel;
	@FXML private Label secondLabel;
	private Label selectedLabel;
	private DoubleProperty time;
	//private LimitedDoubleValue time;

	public TimePicker() {
		FXMLLoader fxmlLoader = new FXMLLoader( getClass().getResource("TimePicker.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
		initialize();
	}

	//--For DEBUG
	public TimePicker(Spinner<Double> timePickerSpinner, Label hourLabel, Label minuteLabel, Label secondLabel) {
		this.timePickerSpinner = timePickerSpinner;
		this.hourLabel = hourLabel;
		this.minuteLabel = minuteLabel;
		this.secondLabel = secondLabel;
		initialize();
	}

	protected void initialize() {

		SpinnerValueFactory<Double> svf = new SpinnerValueFactory.DoubleSpinnerValueFactory(-1, 60);
		this.timePickerSpinner.setValueFactory(svf);
		this.timePickerSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
			if (oldValue != newValue)
				time.setValue(newValue);
		});

		this.hourLabel.setText("00");
		this.minuteLabel.setText("00");
		this.secondLabel.setText("00");
		//this.selectedLabel = this.secondLabel;
		selectLabel(this.secondLabel);

		//this.hourLabel.setFocusTraversable(true);
		this.hourLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				selectLabel(hourLabel);
				//System.out.println("hour");
			}
		});
		this.minuteLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				selectLabel(minuteLabel);
				//System.out.println("numute");
			}
		});
		this.secondLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				selectLabel(secondLabel);
				//System.out.println("second");
			}
		});
	
		time = new SimpleDoubleProperty(0);
		//time = new LimitedDoubleValue(0, 11*3600+59*60+56, 0); //-- min, max, init
		
		SpinnerValueFactory.DoubleSpinnerValueFactory dsvf = 
				(SpinnerValueFactory.DoubleSpinnerValueFactory) this.timePickerSpinner.getValueFactory();
		dsvf.setMin(0);
		dsvf.setMax(11*3600+59*60+56);
		dsvf.setValue(0.);

		//-- bidirectional bind time to spinner 
		time.addListener((arg0, oldValue, newValue) -> {
			//System.out.println("spinner time: " + newValue);
			if ( oldValue != newValue) {
				SpinnerValueFactory.DoubleSpinnerValueFactory dsvf2 = 
						(SpinnerValueFactory.DoubleSpinnerValueFactory) this.timePickerSpinner.getValueFactory();
				dsvf2.setValue((double)newValue);
				updateLabel((double)newValue);
			}
	    });

	}

	protected void selectLabel(Label label) {
		if (this.selectedLabel != null )
			this.selectedLabel.setStyle("-fx-background-color: transparent;");
		this.selectedLabel = label;
		this.selectedLabel.setStyle("-fx-background-color: #FFBBBB;");
		configureSpinner();
	}

	//-------------------------------------------------------
	//-- Spinner behavior
	protected void configureSpinner() {
		SpinnerValueFactory.DoubleSpinnerValueFactory svf = 
				(SpinnerValueFactory.DoubleSpinnerValueFactory) this.timePickerSpinner.getValueFactory();
		if (this.selectedLabel == this.hourLabel)
			svf.setAmountToStepBy(3600);
		else if (this.selectedLabel == this.minuteLabel)
			svf.setAmountToStepBy(60);
		else
			svf.setAmountToStepBy(1);
	}
	//-- Spinner behavior	
	//-------------------------------------------------------
	protected void updateLabel(double time) {
		int hour = (int)(time / 3600);
		int minute = (int)((time - hour*3600) / 60);
		double second = time - hour*3600 - minute*60;

		this.hourLabel.setText(String.format("%02d", hour));
		this.minuteLabel.setText(String.format("%02d", minute));
		this.secondLabel.setText(String.format("%02d", (int)second));
	}

	protected void updateTime(int hour, int minute, double second) {
		this.hourLabel.setText(String.format("%02d", hour));
		this.minuteLabel.setText(String.format("%02d", minute));
		this.secondLabel.setText(String.format("%02d", (int)second));
		configureSpinner();
	}
	public final void setMin(double min) {
		SpinnerValueFactory.DoubleSpinnerValueFactory svf = 
				(SpinnerValueFactory.DoubleSpinnerValueFactory) this.timePickerSpinner.getValueFactory();
		svf.setMin(min);
	}

	public final void setMax(double max) {
		SpinnerValueFactory.DoubleSpinnerValueFactory svf = 
				(SpinnerValueFactory.DoubleSpinnerValueFactory) this.timePickerSpinner.getValueFactory();
		if ( svf.getValue() > max )
			svf.setValue(max);
		svf.setMax(max);
	}

	//--------------------------------------------
	//-- Property
	public final double getTime() {
		return this.time.getValue();
	}

	public final void setTime(double value) {
		this.time.setValue(value);
	}
	
	public void setTime(int hour, int minute, double second) {
		setTime(hour*3600 + minute*60 + second);
	}
	
	public void setTime(String hhmmss ) {
		String[] hms = hhmmss.split(":");
		int n = hms.length;
		if (n > 3 )
			return; //-- ERROR

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
			return; //-- ERROR
		setTime( hour, min, sec );
	}

	public DoubleProperty timeProperty() {
		return this.time;
	}
	//-- Property
	//--------------------------------------------
}
