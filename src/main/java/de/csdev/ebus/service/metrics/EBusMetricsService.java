/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
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
    @SuppressWarnings({"null"})
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    @SuppressWarnings({"null"})
    private BigDecimal resolved = BigDecimal.valueOf(0);
    @SuppressWarnings({"null"})
    private BigDecimal unresolved = BigDecimal.valueOf(0);
    @SuppressWarnings({"null"})
    private BigDecimal received = BigDecimal.valueOf(0);
    @SuppressWarnings({"null"})
    private BigDecimal failed = BigDecimal.valueOf(0);
    @SuppressWarnings({"null"})
    private BigDecimal connectionFailed = BigDecimal.valueOf(0);
    @SuppressWarnings({"null"})
    private BigDecimal receivedAmount = BigDecimal.valueOf(0);

    private Map<EBusError, @Nullable BigDecimal> failedMap = new EnumMap<>(EBusError.class);

    @SuppressWarnings({"null"})
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
    @SuppressWarnings({"null"})
    public void onTelegramResolved(IEBusCommandMethod commandChannel,
            Map<String, @Nullable Object> result, byte @NonNull [] receivedData,
            @Nullable Integer sendQueueId) {
        resolved = resolved.add(BigDecimal.ONE);
    }

    @Override
    @SuppressWarnings({"null"})
    public void onTelegramResolveFailed(@Nullable IEBusCommandMethod commandChannel, byte @Nullable [] receivedData,
            @Nullable Integer sendQueueId, @Nullable String exceptionMessage) {
        unresolved = unresolved.add(BigDecimal.ONE);
    }

    @Override
    @SuppressWarnings({"null"})
    public void onTelegramReceived(byte[] receivedData, @Nullable Integer sendQueueId) {
        received = received.add(BigDecimal.ONE);
        receivedAmount = receivedAmount.add(BigDecimal.valueOf(receivedData.length));
    }

    @Override
    @SuppressWarnings({"null"})
    public void onTelegramException(EBusDataException exception, @Nullable Integer sendQueueId) {

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
    @SuppressWarnings({"null"})
    public void onConnectionException(Exception e) {
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

    @SuppressWarnings({"null"})
    public BigDecimal getFailureRatio() {
        BigDecimal all = received.add(failed);
        if (!failed.equals(BigDecimal.ZERO) && !all.equals(BigDecimal.ZERO)) {

            return failed.setScale(3, RoundingMode.HALF_UP).divide(all, RoundingMode.HALF_UP).multiply(HUNDRED)
                    .setScale(1, RoundingMode.HALF_UP);
        } else {
            return BigDecimal.ZERO;
        }
    }

    @SuppressWarnings({"null"})
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
