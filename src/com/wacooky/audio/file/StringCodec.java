package com.wacooky.audio.file;

public interface StringCodec {
	public String encode();
	public void decode(String contentString);
}