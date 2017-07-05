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

import de.csdev.ebus.cfg.datatypes.IEBusType;
import de.csdev.ebus.cfg.datatypes.ext.EBusTypeBytes;
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
        
        if(command == null) throw new IllegalArgumentException("Parameter command is null!");
        if(source == null) throw new IllegalArgumentException("Parameter source is null!");
        if(target == null) throw new IllegalArgumentException("Parameter target is null!");
        
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
                byte[] b = null;
                
                // use the value from the values map if set
                if (values != null && values.containsKey(entry.getName())) {
                    b = type.encode(values.get(entry.getName()));
                    
                } else {
                    if (entry.getDefaultValue() == null) {
                        b = type.encode(null);
                    } else {
                    	b = type.encode(entry.getDefaultValue());
                    }

                }
                
                if(b == null) {
                	throw new RuntimeException("Encoded value is null!");              	
                }
                
                buf.put(b); 
                len += type.getTypeLenght();
            }
        }

        

        // set len
        buf.put(4, len);

        byte crc8 = EBusUtils.crc8(buf.array(), buf.position());
        
        buf.put(crc8);

        return buf;
    }
    
    public static Map<String, Object> decodeTelegram(IEBusCommand command, byte[] data) {

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
                    
                    if(decode instanceof BigDecimal) {
                    	
                        BigDecimal multiply = (BigDecimal) decode;

                        if (nev.getFactor() != null) {
                            multiply = multiply.multiply(nev.getFactor());
                            decode = multiply;
                        }

                        if (nev.getMin() != null && multiply.compareTo(nev.getMin()) == -1) {
                            System.out.println("EBusCommand.encode() >> MIN");
                            decode = null;
                        }

                        if (nev.getMax() != null && multiply.compareTo(nev.getMax()) == 1) {
                            System.out.println("EBusCommand.encode() >> MAX");
                            decode = null;
                        }

//                        decode = multiply;
                    }

                    // nev.getMax()
                }

                result.put(ev.getName(), decode);
                pos += ev.getType().getTypeLenght();
            }
        }

        return result;
    }
    
    public static ByteBuffer getMasterTelegramMask(IEBusCommand command) {
    	
        // byte len = 0;
        ByteBuffer buf = ByteBuffer.allocate(50);
        buf.put(command.getSourceAddress() == null ? (byte) 0x00 : (byte)0xFF); // QQ - Source
        buf.put(command.getDestinationAddress() == null ? (byte) 0x00 : (byte)0xFF); // ZZ - Target
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
