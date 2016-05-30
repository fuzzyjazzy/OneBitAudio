package com.wacooky.audio.app;

import java.io.IOException;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

/**
 * Property: currentTimeProperty, startTimeProperty, endTimeProperty
 * Method: setDurationTime
 * 
 * TODO: Support sample count.
 * 
 * @author fujimori
 * @version 1.0	2016-04-22
 */
public class SimpleTimeRegionWidget extends AnchorPane {
	@FXML private Canvas totalRegion;

	//-- model data
	private DoubleProperty currentTime = new SimpleDoubleProperty(0);
	private double durationTime;
	private DoubleProperty startTime = new SimpleDoubleProperty(0);
	private DoubleProperty endTime = new SimpleDoubleProperty(0);

	
	//-- view data cache
	double left; //-- startTime
	double right; //-- endTime
	double length; //-- durationTime
	double cursorX; //-- currentTime
	double height;

	
	public SimpleTimeRegionWidget() {
		FXMLLoader fxmlLoader = new FXMLLoader( getClass().getResource("SimpleTimeRegionWidget.fxml"));
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
	public SimpleTimeRegionWidget(Canvas canvas) {
		totalRegion = canvas;
		setLeftAnchor(totalRegion, 0.0);
		setRightAnchor(totalRegion, 0.0);
		getChildren().add(totalRegion);
		initialize();
	}

	protected void initialize() {
		left = 0.;
		right = 0.;
		cursorX = 0.;
		
		//-- Listening totalRegion (a Canvas) is bad idea here
		//-- Canvas size will not follow this AnchorPane if the anchor point is
		//-- specified in fxml file. May be a bug?
		//-- Anyway it is necessary to listen size changes to draw correctly.
		this.widthProperty().addListener(observable -> sizeChanged());
		this.heightProperty().addListener(observable -> sizeChanged());

		//System.out.println("SimpleTimeRegionWidget: initialize");
		totalRegion.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				updateRange( event.getX() );

/*
				MouseButton button = event.getButton();
				if(button == MouseButton.PRIMARY){
					System.out.println("PRIMARY button clicked");
				}else if(button == MouseButton.SECONDARY){
					System.out.println("SECONDARY button clicked");
				}else if(button == MouseButton.MIDDLE){
					System.out.println("MIDDLE button clicked");
				}
*/
			}
		});
		
		totalRegion.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				updateRange( event.getX() );
			}
		});

		durationTime = -1;
		
		startTime.addListener((arg0, oldValue, newValue) -> {
			if (oldValue != newValue) {
				double v = (double)newValue;
				if (v > endTime.get())
					endTime.set(v);
				else
					drawDisplay();
			}
	    });

		endTime.addListener((arg0, oldValue, newValue) -> {
			if (oldValue != newValue) {
				double v = (double)newValue;
				if (v < startTime.get())
					startTime.set(v);
				else
					drawDisplay();
			}
	    });

		currentTime.addListener((arg0, oldValue, newValue) -> {
			if (oldValue != newValue) {
				//System.out.println(newValue);
				drawDisplay();
			}
	    });

	}

	//--------------------------------------------
	//-- Property
	public final double getCurrentTime() {
		return this.currentTime.get();
	}

	public final void setCurrentTime(double value) {
		if (!hasDurationTime())
			return;

		double newValue = ( value > durationTime ) ? durationTime : value;
		if ( newValue != this.currentTime.get() ) {
			this.currentTime.set(newValue);
			drawDisplay();
		}
	}

	public DoubleProperty currentTimeProperty() {
		return this.currentTime;
	}
	
	public final double getStartTime() {
		return this.startTime.get();
	}

	public final void setStartTime(double value) {
		this.startTime.set(value);
/*
		if (!hasDurationTime())
			return;
		double newValue = ( value < 0) ? 0 : ((value > durationTime) ? durationTime : value);
		this.startTime.setValue(newValue);
		if (newValue > this.endTime.get())
			this.endTime.setValue(newValue);
		drawDisplay();
*/
	}

	public DoubleProperty startTimeProperty() {
		return this.startTime;
	}

	public final double getEndTime() {
		return this.endTime.get();
	}

	public final void setEndTime(double value) {
		this.endTime.set(value);
/*
		if (!hasDurationTime())
			return;
		double newValue = ( value < 0) ? 0 : ((value > durationTime) ? durationTime : value);
		this.startTime.setValue(newValue);
		if (newValue < this.startTime.get())
			this.startTime.setValue(newValue);
		drawDisplay();
*/
	}

	public DoubleProperty endTimeProperty() {
		return this.endTime;
	}
	//-- Property
	//--------------------------------------------

	protected void sizeChanged() {
		//System.out.println(totalRegion.getWidth());
		//System.out.println(totalRegion.getHeight());
		length = this.getWidth();
		height = this.getHeight();
		totalRegion.setWidth(length);
		totalRegion.setHeight(height);
		drawDisplay();
/*
		GraphicsContext gc = totalRegion.getGraphicsContext2D();
		gc.setFill(Color.BLUE);
		gc.fillRect(0, 0, totalRegion.getWidth(), totalRegion.getHeight());
*/
	}

	protected boolean hasDurationTime() {
		return this.durationTime > 0;
	}

	public double getDurationTime() {
		return this.durationTime;
	}
	
	public void setDurationTime(double durationTime) {
		this.durationTime = durationTime;
		if (this.startTime.get() != 0 )
			this.startTime.set(0);
		if (this.endTime.get() != this.durationTime )
			this.endTime.set(this.durationTime);
		
		if (!this.currentTime.isBound() && this.currentTime.get() > this.durationTime )
			this.currentTime.set(this.durationTime);
	}

	protected void updateRange( double x ) {
		//if (!hasDurationTime())
		//	return;
		//System.out.println(x);
		double center = (right + left)/2;
		if (x < center) {
			left = (int)x;
			this.startTime.setValue(durationTime*x/length);
		} else if (x > center) {
			right = (int)x;
			this.endTime.setValue(durationTime*x/length);
		}
		//-- display is invoked in startTime/endTime property listener
		//updateDisplay();
	}

	protected void updateDisplay() {
		GraphicsContext gc = totalRegion.getGraphicsContext2D();
		gc.setFill(Color.LIGHTGRAY);
		gc.fillRect(0, 0, length, height);
		if (hasDurationTime()) {
			gc.setFill(Color.GREEN);
			//gc.setStroke(Color.BLUE);
			double w = right - left;
			gc.fillRect(left - ((left >= length) ? 1 : 0), 0, (w <= 0) ? 1 : w, height);
			//
			gc.setFill(Color.WHITE);
			gc.fillRect(cursorX, 0, 1, height);
		}
	}
	
	protected void drawDisplay() {
		if (hasDurationTime()) {
			left = (int)(this.startTime.get()*length/durationTime);
			right = (int)(this.endTime.get()*length/durationTime);
			cursorX = (int)(this.currentTime.get()*length/durationTime);
		}
		updateDisplay();

/*
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				updateDisplay();
			}
		});
*/
	}
}

