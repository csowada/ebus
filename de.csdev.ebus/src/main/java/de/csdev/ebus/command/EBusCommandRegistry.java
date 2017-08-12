/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.datatypes.EBusTypeException;

/**
 * @author Christian Sowada
 *
 */
public class EBusCommandRegistry {

    private static final Logger logger = LoggerFactory.getLogger(EBusCommandRegistry.class);

    private List<IEBusCommand> list = new ArrayList<IEBusCommand>();

    public void addTelegramConfigurationList(List<IEBusCommand> nlist) {
        list.addAll(nlist);
    }

    public void addTelegramConfiguration(IEBusCommand telegramCfg) {
        list.add(telegramCfg);
    }

    public List<IEBusCommandChannel> find(byte[] data) {
        return find(ByteBuffer.wrap(data));
    }

    public List<IEBusCommand> getConfigurationList() {
        return Collections.unmodifiableList(list);
    }

    public IEBusCommandChannel getConfigurationById(String id, IEBusCommand.Type type) {

        for (IEBusCommand command : list) {
            if (StringUtils.equals(command.getId(), id)) {
                return command.getCommandChannel(type);
            }
        }

        return null;
    }

    public List<IEBusCommandChannel> find(ByteBuffer data) {
        ArrayList<IEBusCommandChannel> result = new ArrayList<IEBusCommandChannel>();
        for (IEBusCommand command : list) {
            for (IEBusCommandChannel commandChannel : command.getCommandChannels()) {
                if (matchesCommand(commandChannel, data)) {
                    result.add(commandChannel);
                }
            }

        }

        return result;

    }

    public boolean matchesCommand(IEBusCommandChannel command, ByteBuffer data) {

        Byte sourceAddress = (Byte) ObjectUtils.defaultIfNull(command.getSourceAddress(), Byte.valueOf((byte) 0x00));

        Byte targetAddress = (Byte) ObjectUtils.defaultIfNull(command.getDestinationAddress(),
                Byte.valueOf((byte) 0x00));

        try {
            ByteBuffer masterTelegram = EBusCommandUtils.buildMasterTelegram(command, sourceAddress, targetAddress,
                    null);

            ByteBuffer mask = command.getMasterTelegramMask();

            for (int i = 0; i < mask.position(); i++) {
                byte b = mask.get(i);

                if (b == (byte) 0xFF) {
                    if (masterTelegram.get(i) != data.get(i)) {
                        break;
                    }
                }
                if (i == mask.position() - 1) {
                    return true;
                }
            }
        } catch (EBusTypeException e) {
            logger.error("error!", e);
        }

        return false;
    }

}
