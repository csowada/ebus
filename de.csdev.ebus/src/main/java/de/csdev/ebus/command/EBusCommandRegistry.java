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

/**
 * @author Christian Sowada
 *
 */
public class EBusCommandRegistry {

    private List<EBusCommand> list = new ArrayList<EBusCommand>();

    public void addTelegramConfigurationList(List<EBusCommand> nlist) {
        list.addAll(nlist);
    }

    public void addTelegramConfiguration(EBusCommand telegramCfg) {
        list.add(telegramCfg);
    }

    public List<EBusCommand> find(byte[] data) {
        return find(ByteBuffer.wrap(data));
    }

    public List<EBusCommand> getConfigurationList() {
        return Collections.unmodifiableList(list);
    }

    public EBusCommand getConfigurationById(String id) {
        return null;
    }
    
    public List<EBusCommand> find(ByteBuffer data) {

        ArrayList<EBusCommand> result = new ArrayList<EBusCommand>();

        for (EBusCommand telegramCfg : list) {

            ByteBuffer masterTelegram = EBusCommandUtils.buildMasterTelegram(telegramCfg, (byte) 0x00, (byte) 0xFF, null);
            ByteBuffer mask = telegramCfg.getMasterTelegramMask();

            for (int i = 0; i < mask.position(); i++) {
                byte b = mask.get(i);

                if (b == (byte) 0xFF) {
                    if (masterTelegram.get(i) != data.get(i)) {
                        break;
                    }
                }
                if (i == mask.position() - 1) {
                    result.add(telegramCfg);
                }
            }
        }

        return result;

    }

}
