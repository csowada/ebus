/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg.std;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import de.csdev.ebus.cfg.std.dto.EBusValueDTO;

/**
 * A hack to allow unknown properties like in Jackson2
 *
 * @author Christian Sowada - Initial contribution
 * @see de.csdev.ebus.cfg.std.dto.EBusCommandMethodDTO
 *
 */
public class EBusValueJsonDeserializer implements JsonDeserializer<List<EBusValueDTO>> {

    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(EBusValueJsonDeserializer.class);

    @Override
    public List<EBusValueDTO> deserialize(JsonElement jElement, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        JsonArray asJsonArray = jElement.getAsJsonArray();
        ArrayList<EBusValueDTO> result = new ArrayList<EBusValueDTO>();

        ArrayList<String> fields = new ArrayList<String>();
        for (Field field : EBusValueDTO.class.getDeclaredFields()) {
            SerializedName annotation = field.getAnnotation(SerializedName.class);

            if (annotation != null) {
                fields.add(annotation.value());

            } else {
                fields.add(field.getName());
            }
        }

        for (JsonElement jsonElement : asJsonArray) {
            JsonObject jObject = jsonElement.getAsJsonObject();
            EBusValueDTO valueDTO = context.deserialize(jObject, EBusValueDTO.class);

            for (Entry<String, JsonElement> entry : jObject.entrySet()) {
                if (!fields.contains(entry.getKey())) {
                    valueDTO.setProperty(entry.getKey(), entry.getValue().getAsString());
                }
            }

            result.add(valueDTO);
        }

        return result;
    }

}