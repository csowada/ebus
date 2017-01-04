/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Sowada
 *
 */
public class EBusConfigurationJsonReader {
	
	private static final Logger logger = LoggerFactory.getLogger(EBusConfigurationJsonReader.class);
	
	// filter: ??
	private static Pattern P_PLACEHOLDER = Pattern.compile("\\?\\?");

	// filter: (00)
	private static Pattern P_BRACKETS_VALS = Pattern.compile("(\\([0-9A-Z]{2}\\))");

	// filter: (|)
	private static Pattern P_BRACKETS_CLEAN = Pattern.compile("(\\(|\\))");
	
	private Map<String, String> loadedFilters = new HashMap<String, String>();

	private EBusConfigurationProvider configurationProvider;

	public EBusConfigurationJsonReader(EBusConfigurationProvider configurationProvider) {
		this.configurationProvider = configurationProvider;
	}

	/**
	 * Loads a JSON configuration file by url
	 * 
	 * @param url The url to a configuration file
	 * @throws IOException Unable to read configuration file
	 * @throws ParseException A invalid json file
	 */
	public void loadConfigurationFile(URL url) throws IOException {

		final ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
		final InputStream inputStream = url.openConnection().getInputStream();

		final List<EBusConfigurationTelegram> loadedTelegramRegistry = mapper.readValue(inputStream,
				new TypeReference<List<EBusConfigurationTelegram>>() {
		});

		for (Iterator<EBusConfigurationTelegram> iterator = loadedTelegramRegistry.iterator(); iterator.hasNext();) {
			EBusConfigurationTelegram object = iterator.next();
			transformDataTypes(object);

			// check if this filter pattern is already loaded
			String filter = object.getFilterPattern().toString();
			String fileComment = StringUtils.substringAfterLast(url.getFile(), "/") + " >>> " + object.getComment();

			if (loadedFilters.containsKey(filter)) {
				logger.info("Identical filter already loaded ... {} AND {}", loadedFilters.get(filter), fileComment);
			} else {
				loadedFilters.put(filter, fileComment);
			}
		}

		if (loadedTelegramRegistry != null && !loadedTelegramRegistry.isEmpty()) {
			configurationProvider.add(loadedTelegramRegistry);
		}
	}

	/**
	 * @param configurationEntry
	 */
	protected void transformDataTypes(EBusConfigurationTelegram configurationEntry) {

		// Use filter property if set
		if (StringUtils.isNotEmpty(configurationEntry.getFilter())) {
			String filter = configurationEntry.getFilter();
			filter = P_PLACEHOLDER.matcher(filter).replaceAll("[0-9A-Z]{2}");
			logger.trace("Compile RegEx filter: {}", filter);
			configurationEntry.setFilterPattern(Pattern.compile(filter));

		} else {
			// Build filter string

			// Always ignore first two hex bytes
			String filter = "[0-9A-Z]{2} [0-9A-Z]{2}";

			// Add command to filter string
			if (StringUtils.isNotEmpty(configurationEntry.getCommand())) {
				filter += " " + configurationEntry.getCommand();
				filter += " [0-9A-Z]{2}";
			}

			// Add data to filter string
			if (StringUtils.isNotEmpty(configurationEntry.getData())) {
				Matcher matcher = P_BRACKETS_VALS.matcher(configurationEntry.getData());
				filter += " " + matcher.replaceAll("[0-9A-Z]{2}");
			}

			// Finally add .* to end with everything
			filter += " .*";

			logger.trace("Compile RegEx filter: {}", filter);
			configurationEntry.setFilterPattern(Pattern.compile(filter));
		}

		// remove brackets if used
		if (StringUtils.isNotEmpty(configurationEntry.getData())) {
			Matcher matcher = P_BRACKETS_CLEAN.matcher(configurationEntry.getData());
			configurationEntry.setData(matcher.replaceAll(""));
		}
	}
}
