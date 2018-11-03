/**
 * Copyright (c) 2016-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core;

import java.util.concurrent.ThreadFactory;

/**
 * Simple thread factory which allows to use given prefix for created threads.
 *
 * @author Łukasz Dywicki luke@code-house.org
 * @author Christian Sowada
 */
public class EBusWorkerThreadFactory implements ThreadFactory {

    private int counter = 0;
    private String prefix = "";
    private boolean useCounter;

    public EBusWorkerThreadFactory(String prefix, boolean useCounter) {
        this.prefix = prefix;
        this.useCounter = useCounter;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        String name = useCounter ? prefix + "-" + counter++ : prefix;
        return new Thread(runnable, name);
    }
}
