/*
 * Copyright 2015-2017 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package org.apiguardian.report;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.1
 */
@API(status = API.Status.STABLE, since = "1.1")
public final class ApiReport {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApiReport.class);

	private final List<Class<?>> types;

	private final Map<Status, List<Class<?>>> declarationsMap;

	public ApiReport(List<Class<?>> types, Map<Status, List<Class<?>>> declarationsMap) {
		this.types = types;
		this.declarationsMap = declarationsMap;
	}

	public List<Class<?>> getTypes() {
		return this.types;
	}

	public Map<Status, List<Class<?>>> getDeclarationsMap() {
		return this.declarationsMap;
	}

	private static String eol() {
		return System.lineSeparator();
	}

	private static String scanResultMessage(final int namesSize, final ScanResult scanResult) {
		StringBuilder builder = new StringBuilder(
				namesSize + " @API declarations (including meta) found in class-path:");
		builder.append(eol());
		scanResult.getUniqueClasspathElements().forEach(e -> builder.append(e).append(eol()));
		return builder.toString();
	}

	private static String annotatedTypesMessage(final List<Class<?>> types) {
		StringBuilder builder = new StringBuilder("Listing of all " + types.size() + " annotated types:");
		builder.append(eol());
		types.forEach(e -> builder.append(e).append(eol()));
		return builder.toString();
	}

	public static ApiReport generateReport(String... packages) {
		// Scan packages
		ScanResult scanResult = new FastClasspathScanner(packages).scan();

		// Collect names
		List<String> names = new ArrayList<>();
		names.addAll(scanResult.getNamesOfClassesWithAnnotation(API.class));
		names.addAll(scanResult.getNamesOfAnnotationsWithMetaAnnotation(API.class));

		LOGGER.debug(scanResultMessage(names.size(), scanResult));

		// Collect types
		List<Class<?>> types = scanResult.classNamesToClassRefs(names);
		// only retain directly annotated types
		types.removeIf(c -> !c.isAnnotationPresent(API.class));
		types.sort(Comparator.comparing(type -> type.getName()));

		LOGGER.debug(annotatedTypesMessage(types));

		// Build map
		Map<Status, List<Class<?>>> declarationsMap = new EnumMap<>(Status.class);
		for (Status status : Status.values()) {
			declarationsMap.put(status, new ArrayList<>());
		}
		types.forEach(type -> declarationsMap.get(type.getAnnotation(API.class).status()).add(type));

		// Create report
		return new ApiReport(types, declarationsMap);
	}
}
