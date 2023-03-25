/**
 * Copyright (c) 2017-2023 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg.std.dto;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCommandTemplatesDTO {

    private @Nullable String name;
    private @Nullable List<EBusValueDTO> template;

    public @Nullable String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public @Nullable List<EBusValueDTO> getTemplate() {
        return template;
    }

    public void setTemplate(List<EBusValueDTO> template) {
        this.template = template;
    }

    @Override
    public String toString() {
        return "EBusCommandTemplatesDTO [" + (name != null ? "name=" + name + ", " : "")
                + (template != null ? "template=" + template : "") + "]";
    }

}
