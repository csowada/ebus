package de.csdev.ebus.service.metrics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.Map;

import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.core.EBusDataException;
import de.csdev.ebus.core.EBusDataException.EBusError;
import de.csdev.ebus.core.IEBusConnectorEventListener;
import de.csdev.ebus.service.parser.IEBusParserListener;

public class EBusMetricsService implements IEBusParserListener, IEBusConnectorEventListener {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private BigDecimal resolved = BigDecimal.valueOf(0);

    private BigDecimal unresolved = BigDecimal.valueOf(0);

    private BigDecimal received = BigDecimal.valueOf(0);

    private BigDecimal failed = BigDecimal.valueOf(0);

    private BigDecimal connectionFailed = BigDecimal.valueOf(0);

    private BigDecimal receivedAmount = BigDecimal.valueOf(0);

    private Map<EBusError, BigDecimal> failedMap = new EnumMap<EBusDataException.EBusError, BigDecimal>(
            EBusError.class);

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
    public void onTelegramResolved(IEBusCommandMethod commandChannel, Map<String, Object> result, byte[] receivedData,
            Integer sendQueueId) {
        resolved = resolved.add(BigDecimal.ONE);
    }

    @Override
    public void onTelegramResolveFailed(IEBusCommandMethod commandChannel, byte[] receivedData, Integer sendQueueId,
            String exceptionMessage) {
        unresolved = unresolved.add(BigDecimal.ONE);
    }

    @Override
    public void onTelegramReceived(byte[] receivedData, Integer sendQueueId) {
        received = received.add(BigDecimal.ONE);
        receivedAmount = receivedAmount.add(BigDecimal.valueOf(receivedData.length));
    }

    @Override
    public void onTelegramException(EBusDataException exception, Integer sendQueueId) {

        BigDecimal val = failedMap.get(exception.getErrorCode());
        if (val == null) {
            val = BigDecimal.valueOf(0);
        }

        val = val.add(BigDecimal.ONE);
        failedMap.put(exception.getErrorCode(), val);

        failed = failed.add(BigDecimal.ONE);
    }

    @Override
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
        if (!resolved.equals(BigDecimal.ZERO) && !all.equals(BigDecimal.ZERO)) {
            return resolved.setScale(3, RoundingMode.HALF_UP).divide(all, RoundingMode.HALF_UP).multiply(HUNDRED)
                    .setScale(1, RoundingMode.HALF_UP);
        } else {
            return BigDecimal.ZERO;
        }
    }
}
