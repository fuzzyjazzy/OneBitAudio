package com.wacooky.audio.file;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

import javafx.collections.ObservableList;

/**
 * FileCollector collects specified file type from given folder.
 * 
 * @author fujimori
 *
 * {@link FileCollector}&lt;E extends {@link AudioFileInfo}&gt;
 */
public class FileCollector<E extends AudioFileInfo> extends SimpleFileVisitor<Path> {
	private ObservableList<E> record;
	private FileFormatDeterminant[] determinants;
	private Constructor<?> fileInfoConstructor;
	private Set<E> set;
	/**
	 * 
 	 * @param fileInfoClass Class
	 * @param record	ObservableList&lt;E&gt;
	 * @param determinant FileFormatDeterminant
	 */
	public FileCollector(Class<?> fileInfoClass, ObservableList<E> record, FileFormatDeterminant determinant) {
		this.determinants = new FileFormatDeterminant[1];
		this.determinants[0] = determinant;
		initialize(fileInfoClass, record);
	}

	public FileCollector(Class<?> fileInfoClass, ObservableList<E> record, FileFormatDeterminant[] determinants) {
		this.determinants = determinants;
		initialize(fileInfoClass, record);
	}

	protected void initialize(Class<?> fileInfoClass, ObservableList<E> record) {
		Constructor<?> constructor;
		try {
			constructor = fileInfoClass.getConstructor(Path.class, FileFormat.class);
		} catch (NoSuchMethodException | SecurityException e) {
			constructor = null;
			e.printStackTrace();
		}
		this.fileInfoConstructor = constructor;
		
		this.record = record;
		this.set = new HashSet<E>();
		this.set.addAll(this.record);
		this.record.clear();
		//this.determinamt = determinant;
	}

	public void terminate() {
		this.record.addAll(this.set);
	}

	@Override
	public FileVisitResult visitFile(Path file,BasicFileAttributes attr) {
		if (attr.isRegularFile()) {
			//String name = file.getFileName().toString();
			String path = file.toString();
			
			for (FileFormatDeterminant determinant : this.determinants ) {
				FileFormat fmt = (FileFormat)determinant.matchFormat(path);
				if (fmt != null) {
					try {
						//FileInfo info = (FileInfo)fileInfoConstructor.newInstance(file, fmt);
						E info = (E)fileInfoConstructor.newInstance(file, fmt);
						//record.add(info);
						set.add(info);
						break;
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
								| InvocationTargetException e) {
						e.printStackTrace();
					}
					//FileInfo info = new FileInfo(file, fmt);
					//record.add(info);
				}
			}
		}
		return CONTINUE;
	}
/*
	// Print each directory visited.
	@Override
	public FileVisitResult postVisitDirectory(Path dir,IOException exc) {
		System.out.format("Directory: %s%n", dir);
		return CONTINUE;
	}
*/
	// If there is some error accessing
	// the file, let the user know.
	// If you don't override this method
	// and an error occurs, an IOException 
	// is thrown.
	@Override
	public FileVisitResult visitFileFailed(Path file,IOException exc) {
		//System.err.println(exc);
		return CONTINUE;
	}	
}