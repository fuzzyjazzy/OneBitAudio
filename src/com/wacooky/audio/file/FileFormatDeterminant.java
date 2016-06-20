package com.wacooky.audio.file;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.imageio.stream.ImageInputStreamImpl;
import javax.imageio.stream.MemoryCacheImageInputStream;

public class FileFormatDeterminant<T> {
	static public final int PEEP_SIZE = 128; //byte size to peep content to determin format
	//static private final List<Class<?>> subClassList = new ArrayList<Class<?>>();
	static public ImageInputStreamImpl createPeep( String path ) throws FileNotFoundException {
		ImageInputStreamImpl peep = null;
		byte[] buf = new byte[PEEP_SIZE];
		FileInputStream fileStream =new FileInputStream(path);
		try {
			fileStream.read(buf);
			fileStream.close();
			ByteArrayInputStream byteStream = new ByteArrayInputStream(buf);
			peep = new MemoryCacheImageInputStream(byteStream);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return peep;
	}

	final private String[] extensions;
	final private Constructor<?> constructorWithPath;
	final private Constructor<?> constructorWithStream;
	
	public FileFormatDeterminant(Class<?> clazz, String[] extensions) {
		Constructor<?> constructor;
		try {
			constructor = clazz.getConstructor(String.class, FileFormat.ReadProfile.class);
		} catch (NoSuchMethodException | SecurityException e1) {
			constructor = null;
			e1.printStackTrace();
		}
		constructorWithPath = constructor;

		try {
			constructor = clazz.getConstructor(ImageInputStreamImpl.class, FileFormat.ReadProfile.class);
		} catch (NoSuchMethodException | SecurityException e1) {
			constructor = null;
			e1.printStackTrace();
		}
		constructorWithStream = constructor;

		this.extensions = extensions;
	}

/*	
	public boolean matchExtension(String path) {
		if (extensions == null)
			return true;
		int n = path.length();
		if (n < 4)
			return false;
		String ext = path.substring(n-4);
		for (int i = 0; i < extensions.length; i++ ) {
			if ( ext.compareToIgnoreCase(extensions[i]) == 0 )
				return true;
		}
		return false;
	}
*/
	public boolean matchExtension(String path) {
		if (extensions == null)
			return true;

		int extLen;
		String ext;
		int n;
		for (String allowExt : extensions) {
			extLen = allowExt.length();
			n = path.length();
			if (n < extLen)
				continue;
			ext = path.substring(n-extLen);
			if ( ext.compareToIgnoreCase(allowExt) == 0 )
				return true;
		}
		return false;
	}

	public T matchFormat(String path)  {
		if (!matchExtension(path))
			return null;
//		return createFileFormat(this.clazz, path, ReadProfile.INFO);
		try {
			return (T) constructorWithPath.newInstance(path, FileFormat.ReadProfile.INFO);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean matchHeader(ImageInputStreamImpl peep ) throws IOException {
		return true;
		//return peep.readByte() == 't' && peep.readByte() == 'e' && peep.readByte() == 's' && peep.readByte() == 't';
	}
	
	public T matchFormat( ImageInputStreamImpl peep ) throws IOException {
		peep.seek(0L);
		if ( !matchHeader(peep))
			return null;

		//return createFileFormat( this.clazz, peep, ReadProfile.INFO); //-- not work but for example
		try {
			return (T) constructorWithStream.newInstance(peep, FileFormat.ReadProfile.INFO);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;

	}
}
