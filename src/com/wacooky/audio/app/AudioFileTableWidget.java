package com.wacooky.audio.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import com.wacooky.audio.file.AudioFileInfo;
import com.wacooky.audio.file.FileCollector;
import com.wacooky.audio.file.FileFormat;
import com.wacooky.audio.file.FileFormatDeterminant;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
/**
 * 	Property: currentFileInfo
 *  Method: scanFiles(List<File> files, boolean append)
 *  
 * @author fujimori
 * @version 1.0
 */
public class AudioFileTableWidget extends HBox {
	@FXML private TableView<AudioFileInfo> fileInfoTable;
	@FXML private TableColumn<AudioFileInfo, String> nameColumn;
	@FXML private TableColumn<AudioFileInfo, String> sfColumn;
	@FXML private TableColumn<AudioFileInfo, String> chColumn;
	@FXML private TableColumn<AudioFileInfo, String> timeColumn;
	@FXML private TableColumn<AudioFileInfo, String> typeColumn;
	
	//------------------------------------------------------------------------
	//http://stackoverflow.com/questions/35187145/javafx-tableview-items-prevent-duplicates
	//-- TableView can't accept ObservableSet
	private ObservableList<AudioFileInfo> record = FXCollections.observableArrayList();
	private ObjectProperty<AudioFileInfo> currentFileInfo = new SimpleObjectProperty<AudioFileInfo>();
	private FileFormatDeterminant<?>[] determinants;
	private DragEvent dragEvent;
	private boolean shiftKeyDown = false;

	public AudioFileTableWidget() {
		FXMLLoader fxmlLoader = new FXMLLoader( getClass().getResource("AudioFileTableWidget.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
		initialize();
	}

	protected void initialize() {
		//-- fileInfoTable 
		nameColumn.setCellValueFactory(new PropertyValueFactory<AudioFileInfo, String>("name"));
		sfColumn.setCellValueFactory(new PropertyValueFactory<AudioFileInfo, String>("sf"));
		chColumn.setCellValueFactory(new PropertyValueFactory<AudioFileInfo, String>("ch"));
		timeColumn.setCellValueFactory(new PropertyValueFactory<AudioFileInfo, String>("time"));
		typeColumn.setCellValueFactory(new PropertyValueFactory<AudioFileInfo, String>("type"));
		fileInfoTable.setItems(record);
		fileInfoTable.getSelectionModel().selectedItemProperty().addListener(
			(ObservableValue<? extends AudioFileInfo> ov, AudioFileInfo oldInfo, AudioFileInfo newInfo) -> {
				currentFileInfo.set(newInfo);
		});
		fileInfoTable.setPlaceholder(new Text("Drop Files/Folders Here."));
		setFileFormatDeterminants(FileFormat.determinant); //-- AnyFileFormat
		
		this.widthProperty().addListener(observable -> {
			fileInfoTable.setPrefWidth(this.getWidth());
		});

	}

	public void setFileFormatDeterminants(FileFormatDeterminant<?>... determinants) {
		this.determinants = determinants;
	}
	
	public TableView<AudioFileInfo> getTableViewDevalop() {
		return this.fileInfoTable;
	}
	
	public void removeSelection() {
		AudioFileInfo selection = fileInfoTable.getSelectionModel().getSelectedItem();
		if (selection != null) {
			record.remove(selection);
			currentFileInfo.set(null);
		}
	}

	public AudioFileInfo selectFirst() {
		if (record.size() > 0) {
			fileInfoTable.getSelectionModel().clearAndSelect(0);
			AudioFileInfo selection = fileInfoTable.getSelectionModel().getSelectedItem();
			currentFileInfo.set(selection);
			return selection;
		}
		return null;
	}
	
	//---------------------------------------------------------
	//-- Drop files 
	//-- Bind fileInfoTable by SceneBuilder
	@FXML
	private void pathDragOver(DragEvent event) {
		Dragboard board = event.getDragboard();
		if(board.hasFiles()) {
			event.acceptTransferModes(TransferMode.COPY);
			dragEvent = event;
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					fileInfoTable.requestFocus();
					//table.getSelectionModel().select(0);
					//table.getFocusModel().focus(0);
				}
			});
		}
	}

	@FXML
	private void pathDropped(DragEvent event) {
		dragEvent = null;
		Dragboard board = event.getDragboard();
		if(board.hasFiles()) {
			
			scanFiles(board.getFiles(), shiftKeyDown);
			event.setDropCompleted(true);
		} else {
			//- not my type
			event.setDropCompleted(false);
		}
	}
	//-- Drop files
	//---------------------------------------------------------

	@FXML
    private void keyPressed(KeyEvent evt) {
		//-- NOTE: 
		if (evt.getCode() == KeyCode.ESCAPE)
			dragEvent.setDropCompleted(false);

		shiftKeyDown = evt.isShiftDown();
		if (dragEvent != null)
			setCursor(Cursor.CROSSHAIR);
		//System.out.println("Shift " + shiftKeyDown );
    }

	@FXML
    private void keyReleased(KeyEvent evt) {
		shiftKeyDown = evt.isShiftDown();
		if (dragEvent != null)
			setCursor(Cursor.DEFAULT);		
		//System.out.println("Shift " + shiftKeyDown );
    }

	//---------------------------------------------------------
	//-- Property
	public AudioFileInfo getCurrentFileInfo() {
		return currentFileInfo.get();
	}

	public ObjectProperty<AudioFileInfo> currentFileInfoProperty() {
		return currentFileInfo;
	}
	//-- Property
	//---------------------------------------------------------
	
	public void scanFiles(List<File> files, boolean append) {
		currentFileInfo.set(null);
		//currentFileLabel.setText("");
		if (!append)
			record.clear();

		FileCollector<AudioFileInfo> fileCollector = new FileCollector<AudioFileInfo>(AudioFileInfo.class, record,  this.determinants);
		for( File file : files) {
			Path path = file.toPath();
			//FileCollector<FileInfo> fileCollector = new FileCollector<FileInfo>(FileInfo.class, record,  this.determinants);
			if (file.isDirectory()) {
				try {
					Files.walkFileTree(path, fileCollector);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
					fileCollector.visitFile(path, attr);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		fileCollector.terminate();
	}

}

