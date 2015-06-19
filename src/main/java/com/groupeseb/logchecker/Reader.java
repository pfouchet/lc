package com.groupeseb.logchecker;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class Reader {

	public List<String> readFile(String resource) throws IOException {
		return IOUtils.readLines(new InputStreamReader(new FileInputStream(resource)));
	}
}
