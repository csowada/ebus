/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg.std.dto;

import java.util.List;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCommandTemplatesDTO {

	private String name;
	private List<EBusValueDTO> template;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<EBusValueDTO> getTemplate() {
		return template;
	}
	public void setTemplate(List<EBusValueDTO> template) {
		this.template = template;
	}
}
