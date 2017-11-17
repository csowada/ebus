package de.csdev.ebus.service.metrics;

import java.math.BigDecimal;
import java.math.BigInteger;
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
	
	private BigInteger resolved = BigInteger.valueOf(0);
	
	private BigInteger unresolved = BigInteger.valueOf(0);
	
	private BigInteger received = BigInteger.valueOf(0);
	
	private BigInteger failed = BigInteger.valueOf(0);
	
	private BigInteger connectionFailed = BigInteger.valueOf(0);
	
	private BigInteger receivedAmount = BigInteger.valueOf(0);
	
	private Map<EBusError, BigInteger> failedMap = new EnumMap<EBusDataException.EBusError, BigInteger>(EBusError.class);
	
	public void clear() {
		resolved = BigInteger.valueOf(0);
		unresolved = BigInteger.valueOf(0);
		received = BigInteger.valueOf(0);
		failed = BigInteger.valueOf(0);
		connectionFailed = BigInteger.valueOf(0);
		receivedAmount = BigInteger.valueOf(0);
		failedMap.clear();
	}
	
	@Override
	public void onTelegramResolved(IEBusCommandMethod commandChannel, Map<String, Object> result, byte[] receivedData,
			Integer sendQueueId) {
		resolved = resolved.add(BigInteger.ONE);
	}

	@Override
	public void onTelegramResolveFailed(IEBusCommandMethod commandChannel, byte[] receivedData, Integer sendQueueId,
			String exceptionMessage) {
		unresolved = unresolved.add(BigInteger.ONE);
	}

	@Override
	public void onTelegramReceived(byte[] receivedData, Integer sendQueueId) {
		received = received.add(BigInteger.ONE);
		receivedAmount = receivedAmount.add(BigInteger.valueOf(receivedData.length));
	}

	@Override
	public void onTelegramException(EBusDataException exception, Integer sendQueueId) {
		
		BigInteger val = failedMap.get(exception.getErrorCode());
		if(val == null) {
			val = BigInteger.valueOf(0);
		}
		
		val = val.add(BigInteger.ONE);
		failedMap.put(exception.getErrorCode(), val);

		failed = failed.and(BigInteger.ONE);
	}

	@Override
	public void onConnectionException(Exception e) {
		connectionFailed = connectionFailed.add(BigInteger.ONE);
	}

	public BigInteger getReceivedAmount() {
		return receivedAmount;
	}

	public BigInteger getResolved() {
		return resolved;
	}

	public BigInteger getUnresolved() {
		return unresolved;
	}

	public BigInteger getReceived() {
		return received;
	}

	public BigInteger getFailed() {
		return failed;
	}

	public BigInteger getConnectionFailed() {
		return connectionFailed;
	}

	public BigDecimal getFailureRatio() {
		return new BigDecimal(received).divide(new BigDecimal(failed), RoundingMode.HALF_UP).multiply(HUNDRED);
	}
	
	public BigDecimal getUnresolvedRatio() {
		return new BigDecimal(failed).divide(new BigDecimal(received), RoundingMode.HALF_UP).multiply(HUNDRED);
	}
}
