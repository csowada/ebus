/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
@NonNullByDefault
public interface IEBusType<T> {

    public static final String LENGTH = "length";

    public static final String FACTOR = "factor";

    public static final String VARIANT = "variant";

    public static final String REVERSED_BYTE_ORDER = "reverseByteOrder";

    /**
     * Decodes the byte-array extract from the telegram to valid result
     *
     * @param data
     * @return
     * @throws EBusTypeException
     */
    public @Nullable T decode(byte @Nullable [] data) throws EBusTypeException;

    /**
     * Encodes the given object to a byte-array value
     *
     * @param data
     * @return
     * @throws EBusTypeException
     */
    public byte[] encode(@Nullable Object data) throws EBusTypeException;

    /**
     * Returns the support types of this type
     *
     * @return
     */
    public String[] getSupportedTypes();

    /**
     * Internal only
     *
     * @param types
     */
    public void setTypesParent(EBusTypeRegistry types);

    /**
     * Returns the byte length of this type
     *
     * @return
     */
    public int getTypeLength();

    /**
     * Creates a new instance based on the properties. For simple cases the
     * returned object is a shared singleton object.
     *
     * @param properties
     * @return
     */
    public @Nullable IEBusType<T> getInstance(@Nullable Map<String, Object> properties);

}
