/**
 * Copyright (c) 2017-2023 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core;

import java.io.IOException;
import java.util.jar.Manifest;

import org.eclipse.jdt.annotation.NonNullByDefault;

@NonNullByDefault
public class EBusVersion {

    protected EBusVersion() {
        throw new IllegalStateException("Utility class");
    }

    private static String getAttribute(final Class<?> rootClass, final String name) {
        try {
            Manifest manifest = new Manifest(rootClass.getResourceAsStream("/META-INF/MANIFEST.MF"));
            return manifest.getMainAttributes().getValue(name);
        } catch (IOException e) {
            // noop
        }
        return "";
    }

    public static String getVersion() {
        return getVersion(EBusVersion.class);
    }

    protected static String getVersion(final Class<?> rootClass) {
        return getAttribute(rootClass, "Bundle-Version");
    }

    public static String getBuildCommit() {
        return getBuildCommit(EBusVersion.class);
    }

    protected static String getBuildCommit(final Class<?> rootClass) {
        return getAttribute(rootClass, "Build-Commit");
    }

    public static String getBuildTimestamp() {
        return getBuildTimestamp(EBusVersion.class);
    }

    protected static String getBuildTimestamp(final Class<?> rootClass) {
        return getAttribute(rootClass, "Build-Timestamp");
    }

    public static String getBuildNumber() {
        return getBuildNumber(EBusVersion.class);
    }

    protected static String getBuildNumber(final Class<?> rootClass) {
        return getAttribute(rootClass, "Build-Number");
    }

}
