package de.csdev.ebus.service.metrics;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Test;

import de.csdev.ebus.core.EBusDataException;
import de.csdev.ebus.core.EBusDataException.EBusError;

public class MetricsTest {

    @Test
    public void testMetrics() {

        EBusMetricsService service = new EBusMetricsService();

        service.onTelegramReceived(new byte[0], 1);
        service.onTelegramResolveFailed(null, null, 1, "");

        assertEquals(BigDecimal.valueOf(0), service.getConnectionFailed());
        assertEquals(BigDecimal.valueOf(0), service.getFailed());
        assertEquals(BigDecimal.valueOf(1), service.getReceived());
        assertEquals(BigDecimal.valueOf(0), service.getResolved());
        assertEquals(BigDecimal.valueOf(1), service.getUnresolved());
        assertEquals(BigDecimal.valueOf(0), service.getFailureRatio());
        assertEquals(BigDecimal.valueOf(0), service.getUnresolvedRatio());

        service.onTelegramReceived(new byte[0], 1);
        service.onTelegramResolved(null, null, null, 1);

        assertEquals(BigDecimal.valueOf(0), service.getConnectionFailed());
        assertEquals(BigDecimal.valueOf(0), service.getFailed());
        assertEquals(BigDecimal.valueOf(2), service.getReceived());
        assertEquals(BigDecimal.valueOf(1), service.getResolved());
        assertEquals(BigDecimal.valueOf(1), service.getUnresolved());
        assertEquals(BigDecimal.valueOf(0), service.getFailureRatio());
        assertEquals(BigDecimal.valueOf(50.0f), service.getUnresolvedRatio());

        service.onTelegramException(new EBusDataException("Test", EBusError.MASTER_ACK_FAIL), 1);

        assertEquals(BigDecimal.valueOf(0), service.getConnectionFailed());
        assertEquals(BigDecimal.valueOf(1), service.getFailed());
        assertEquals(BigDecimal.valueOf(2), service.getReceived());
        assertEquals(BigDecimal.valueOf(1), service.getResolved());
        assertEquals(BigDecimal.valueOf(1), service.getUnresolved());
        assertEquals(BigDecimal.valueOf(33.3f).setScale(1, RoundingMode.HALF_UP), service.getFailureRatio());
        assertEquals(BigDecimal.valueOf(50.0f), service.getUnresolvedRatio());

        service.onTelegramReceived(new byte[0], 1);

        assertEquals(BigDecimal.valueOf(0), service.getConnectionFailed());
        assertEquals(BigDecimal.valueOf(1), service.getFailed());
        assertEquals(BigDecimal.valueOf(3), service.getReceived());
        assertEquals(BigDecimal.valueOf(1), service.getResolved());
        assertEquals(BigDecimal.valueOf(1), service.getUnresolved());
        assertEquals(BigDecimal.valueOf(25.0f), service.getFailureRatio());
        assertEquals(BigDecimal.valueOf(50.0f), service.getUnresolvedRatio());
    }

}
