/**
 * Copyright (c) 2017-2021 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.service.metrics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.core.EBusConnectorEventListener;
import de.csdev.ebus.core.EBusDataException;
import de.csdev.ebus.core.EBusDataException.EBusError;
import de.csdev.ebus.service.parser.IEBusParserListener;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
@NonNullByDefault
public class EBusMetricsService extends EBusConnectorEventListener implements IEBusParserListener {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private BigDecimal resolved = BigDecimal.valueOf(0);

    private BigDecimal unresolved = BigDecimal.valueOf(0);

    private BigDecimal received = BigDecimal.valueOf(0);

    private BigDecimal failed = BigDecimal.valueOf(0);

    private BigDecimal connectionFailed = BigDecimal.valueOf(0);

    private BigDecimal receivedAmount = BigDecimal.valueOf(0);

    private Map<EBusError, @Nullable BigDecimal> failedMap = new EnumMap<>(EBusError.class);

    public void clear() {
        resolved = BigDecimal.valueOf(0);
        unresolved = BigDecimal.valueOf(0);
        received = BigDecimal.valueOf(0);
        failed = BigDecimal.valueOf(0);
        connectionFailed = BigDecimal.valueOf(0);
        receivedAmount = BigDecimal.valueOf(0);
        failedMap.clear();
    }

    @Override
    public void onTelegramResolved(@NonNull IEBusCommandMethod commandChannel,
            @NonNull Map<@NonNull String, @Nullable Object> result, byte @NonNull [] receivedData,
            @Nullable Integer sendQueueId) {
        resolved = resolved.add(BigDecimal.ONE);
    }

    @Override
    public void onTelegramResolveFailed(@Nullable IEBusCommandMethod commandChannel, byte @Nullable [] receivedData,
            @Nullable Integer sendQueueId, @Nullable String exceptionMessage) {
        unresolved = unresolved.add(BigDecimal.ONE);
    }

    @Override
    public void onTelegramReceived(byte @NonNull [] receivedData, @Nullable Integer sendQueueId) {
        received = received.add(BigDecimal.ONE);
        receivedAmount = receivedAmount.add(BigDecimal.valueOf(receivedData.length));
    }

    @Override
    public void onTelegramException(@NonNull EBusDataException exception, @Nullable Integer sendQueueId) {

        EBusError errorCode = exception.getErrorCode();

        if (errorCode != null && failedMap.containsKey(errorCode)) {
            BigDecimal val = failedMap.get(errorCode);
            if (val == null) {
                val = BigDecimal.valueOf(0);
            }

            val = val.add(BigDecimal.ONE);
            failedMap.put(errorCode, val);

            failed = failed.add(BigDecimal.ONE);
        }
    }

    @Override
    public void onConnectionException(@NonNull Exception e) {
        connectionFailed = connectionFailed.add(BigDecimal.ONE);
    }

    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }

    public BigDecimal getResolved() {
        return resolved;
    }

    public BigDecimal getUnresolved() {
        return unresolved;
    }

    public BigDecimal getReceived() {
        return received;
    }

    public BigDecimal getFailed() {
        return failed;
    }

    public BigDecimal getConnectionFailed() {
        return connectionFailed;
    }

    public BigDecimal getFailureRatio() {
        BigDecimal all = received.add(failed);
        if (!failed.equals(BigDecimal.ZERO) && !all.equals(BigDecimal.ZERO)) {

            return failed.setScale(3, RoundingMode.HALF_UP).divide(all, RoundingMode.HALF_UP).multiply(HUNDRED)
                    .setScale(1, RoundingMode.HALF_UP);
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getUnresolvedRatio() {
        BigDecimal all = unresolved.add(resolved);
        if (!unresolved.equals(BigDecimal.ZERO) && !all.equals(BigDecimal.ZERO)) {
            return unresolved.setScale(3, RoundingMode.HALF_UP).divide(all, RoundingMode.HALF_UP).multiply(HUNDRED)
                    .setScale(1, RoundingMode.HALF_UP);
        } else {
            return BigDecimal.ZERO;
        }
    }
}
