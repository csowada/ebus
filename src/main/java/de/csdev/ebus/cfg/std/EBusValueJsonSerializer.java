/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg.std;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.csdev.ebus.cfg.std.dto.EBusValueDTO;

/**
 * A hack to allow unknown properties like in Jackson2
 *
 * @author Christian Sowada - Initial contribution
 * @see de.csdev.ebus.cfg.std.dto.EBusCommandMethodDTO
 *
 */
public class EBusValueJsonSerializer implements JsonSerializer<List<EBusValueDTO>> {

    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(EBusValueJsonSerializer.class);

    @Override
    public JsonElement serialize(List<EBusValueDTO> list, Type arg1, JsonSerializationContext context) {

        JsonArray result = new JsonArray();

        for (EBusValueDTO element : list) {
            JsonObject serialize = context.serialize(element).getAsJsonObject();

            // remove if false, it is the default
            if (serialize.has("reverseByteOrder")) {
                if (!serialize.get("reverseByteOrder").getAsBoolean()) {
                    serialize.remove("reverseByteOrder");
                }
            }

            // normalize properties in json tree
            if (serialize.has("properties")) {
                JsonObject properties = serialize.getAsJsonObject("properties");
                for (Entry<String, JsonElement> elem : properties.entrySet()) {
                    serialize.add(elem.getKey(), properties.get(elem.getKey()));
                }
                serialize.remove("properties");
            }

            result.add(serialize);
        }

        return result;
    }

}