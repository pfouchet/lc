package com.groupeseb.logchecker;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@SpringBootApplication
public class Application implements CommandLineRunner {

	@Autowired
	private Reader reader;

	@Autowired
	private Writer writer;

	public static void main(String[] args) {
		System.exit(SpringApplication.exit(SpringApplication.run(Application.class, args)));
	}

	@Override
	public void run(String... strings) throws Exception {

		Pattern pattern = Pattern.compile("No '([^']*)'.*'(.*)'\\s:\\sLKV\\s(.*)\\sdiscarded");

		for (String s : strings) {
			List<String> logs = reader.readFile(s);

			logs = logs.stream()
					.filter((l) -> l.startsWith("[WARN"))
					.filter((l) -> !l.matches(".*steps\\[(\\d)+\\]\\.sequences\\[(\\d)+\\]\\.operations\\[(\\d)+\\]\\.program\\.(para|appliance).*"))
					.filter((l) -> !l.matches(".*steps\\[(\\d)+\\]\\.sequences\\[(\\d)+\\]\\.applianceGroup.*"))
					.filter((l) -> !l.matches(".*lang\\.defaultMarket.*"))
					.filter((l) -> !l.matches(".*topRecipe\\.domain.*"))
					.filter((l) -> !l.matches(".*unit\\.type.*"))
					.filter((l) -> !l.matches(".*market\\.langs.*"))
					.filter((l) -> !l.matches(".*langs\\[\\d*\\]\\.defaultMarket.*"))
					.collect(Collectors.toList());
			logs.replaceAll((l) -> StringUtils.substringAfter(l, "]"));
			logs.replaceAll((l) -> {
						String mid;
						Matcher matcher = pattern.matcher(l);
						if (matcher.find()) {
							String[] split1 = matcher.group(2).split("\\.");
							if (split1.length == 1) {
								mid = matcher.group(2);
							} else {
								mid = split1[split1.length - 2] + "." + split1[split1.length - 1];
							}
							return String.format("type : %s, LKV : %s, translation : %s", mid, matcher.group(3), matcher.group(1));
						} else {
							return l;
						}


					}
			);

			FileOutputStream fileOutputStream = new FileOutputStream("C:\\logs\\output.txt");
			logs.stream().collect(Collectors.toSet()).stream().sorted().collect(Collectors.toList()).forEach(
					(l) -> {
						try {
							log.info(l);
							IOUtils.write(l + "\n", fileOutputStream);
						} catch (IOException e) {
							log.error("Error while writing", e);
						}
					}

			);
		}
	}
}
