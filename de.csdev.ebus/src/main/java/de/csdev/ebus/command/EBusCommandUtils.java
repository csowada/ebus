/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.csdev.ebus.cfg.datatypes.EBusTypeBytes;
import de.csdev.ebus.cfg.datatypes.IEBusType;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada
 *
 */
public class EBusCommandUtils {

	public static byte[] buildMasterTelegram2(IEBusCommand command, Byte source, Byte target, Map<String, Object> values) {
		return null;
	}
	
    public static ByteBuffer buildMasterTelegram(IEBusCommand command, Byte source, Byte target, Map<String, Object> values) {

        byte len = 0;
        ByteBuffer buf = ByteBuffer.allocate(50);

        buf.put(source); // QQ - Source
        buf.put(target); // ZZ - Target
        buf.put(command.getCommand()); // PB SB - Command
        buf.put((byte) 0x00); // NN - Length, will be set later

        if (command.getExtendCommandValue() != null) {
            for (IEBusValue entry : command.getExtendCommandValue()) {
                IEBusType type = entry.getType();
                buf.put(type.encode(entry.getDefaultValue()));
                len += type.getTypeLenght();
            }
        }

        if (command.getMasterTypes() != null) {
            for (IEBusValue entry : command.getMasterTypes()) {
                IEBusType type = entry.getType();

                // use the value from the values map if set
                if (values != null && values.containsKey(entry.getName())) {
                    buf.put(type.encode(values.get(entry.getName())));
                    
                } else {
                    if (entry.getDefaultValue() == null) {
                        buf.put(type.encode(0));
                    } else {
                        buf.put(type.encode(entry.getDefaultValue()));
                    }

                }

                len += type.getTypeLenght();
            }
        }

        byte crc8 = EBusUtils.crc8(buf.array(), len);

        // set len
        buf.put(4, len);

        buf.put(crc8);

        return buf;
    }
    
    public static Map<String, Object> encode(IEBusCommand command, byte[] data) {

        HashMap<String, Object> result = new HashMap<String, Object>();
        int pos = 6;

        if(command == null) {
        	throw new IllegalArgumentException("Parameter command is null!");
        }
        
        if (command.getExtendCommandValue() != null) {
            for (IEBusValue ev : command.getExtendCommandValue()) {
                pos += ev.getType().getTypeLenght();
            }
        }

        if (command.getMasterTypes() != null) {
            for (IEBusValue ev : command.getMasterTypes()) {
            	
                byte[] src = new byte[ev.getType().getTypeLenght()];
                System.arraycopy(data, pos - 1, src, 0, src.length);
                Object decode = ev.getType().decode(src);
                
                
                if(ev instanceof IEBusNestedValue) {
                	IEBusNestedValue evc = (IEBusNestedValue)ev;
                	if(evc.hasChildren()) {
                		
                		for (IEBusValue child : evc.getChildren()) {
                			
							Object decode2 = child.getType().decode(src);
							if(StringUtils.isNoneEmpty(child.getName())) {
								result.put(child.getName(), decode2);								
							}
						}
                	}
                }
                
                
                System.out.println("EBusTelegram.encode()" + decode.toString());
                
                if(StringUtils.isNoneEmpty(ev.getName())) {
                	result.put(ev.getName(), decode);                	
                }
                
                pos += ev.getType().getTypeLenght();
            }
        }

        pos += 3;

        if (command.getSlaveTypes() != null) {

            for (IEBusValue ev : command.getSlaveTypes()) {
                byte[] src = new byte[ev.getType().getTypeLenght()];
                System.arraycopy(data, pos - 1, src, 0, src.length);
                Object decode = ev.getType().decode(src);

                if (ev instanceof EBusCommandValue) {
                    EBusCommandValue nev = (EBusCommandValue) ev;
                    BigDecimal multiply = (BigDecimal) decode;

                    if (nev.getFactor() != null) {
                        multiply = multiply.multiply(nev.getFactor());
                    }

                    if (nev.getMin() != null && multiply.compareTo(nev.getMin()) == -1) {
                        System.out.println("EBusCommand.encode() >> MIN");
                    }

                    if (nev.getMax() != null && multiply.compareTo(nev.getMax()) == 1) {
                        System.out.println("EBusCommand.encode() >> MAX");
                    }

                    decode = multiply;
                    // nev.getMax()
                }

                System.out.println("EBusTelegram.encode()" + decode);
                result.put(ev.getName(), decode);
                pos += ev.getType().getTypeLenght();
            }
        }

        return result;
    }
    
    public static ByteBuffer getMasterTelegramMask(IEBusCommand command) {
    	
        // byte len = 0;
        ByteBuffer buf = ByteBuffer.allocate(50);
        buf.put((byte) 0x00); // QQ - Source
        buf.put((byte) 0x00); // ZZ - Target
        buf.put(new byte[] { (byte) 0xFF, (byte) 0xFF }); // PB SB - Command
        buf.put((byte) 0xFF); // NN - Length

        if (command.getExtendCommandValue() != null) {

            for (IEBusValue entry : command.getExtendCommandValue()) {
                IEBusType type = entry.getType();

                if (entry instanceof KWCrcMValue) {
                    buf.put(new byte[type.getTypeLenght()]);
                } else {
                    for (int i = 0; i < type.getTypeLenght(); i++) {
                        buf.put((byte) 0xFF);

                    }
                }

                // buf.put(new byte[type.getTypeLenght()]);

            }
        }

        if (command.getMasterTypes() != null) {
            for (IEBusValue entry : command.getMasterTypes()) {
                IEBusType type = entry.getType();

//                boolean x = type instanceof EBusTypeBytes;

                if (entry.getName() == null && type instanceof EBusTypeBytes && entry.getDefaultValue() != null) {
                    for (int i = 0; i < type.getTypeLenght(); i++) {
                        buf.put((byte) 0xFF);

                    }
                } else {
                    for (int i = 0; i < type.getTypeLenght(); i++) {
                        buf.put((byte) 0x00);

                    }
                }

                // buf.put(type.encode(entry.getDefaultValue()));

            }
        }

        buf.put((byte) 0x00); // Master CRC

        return buf;
    }
	
}
