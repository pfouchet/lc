package com.groupeseb.logchecker;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

@Slf4j
@Service
public class Writer {

	public void write(String location, Collection<String> logs) throws FileNotFoundException {
		FileOutputStream fileOutputStream = new FileOutputStream(location);
		for (String s : logs) {
			try {
				IOUtils.write(s + "\n", fileOutputStream);
			} catch (IOException e) {
				log.error("Error while writing", e);
			}
		}
	}

}
