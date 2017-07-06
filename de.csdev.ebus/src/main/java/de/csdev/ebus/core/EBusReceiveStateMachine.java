package de.csdev.ebus.core;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.utils.EBusUtils;

public class EBusReceiveStateMachine {

	private static final Logger logger = LoggerFactory.getLogger(EBusReceiveStateMachine.class);

    public enum State {
        UNKNOWN,
        SYN,
        SRC_ADDR,
        TGT_ADDR,
        PRIMARY_CMD,
        SECONDARY_CMD,
        LENGTH1,
        DATA1,
        CRC1,
        ACK1,
        LENGTH2,
        DATA2,
        CRC2,
        ACK2
    }
	
    private State state = State.UNKNOWN;
    
    private int len = 0;
    
	private ByteBuffer bb = ByteBuffer.allocate(50);
	
	private boolean isEscapedByte = false;
	
	private boolean telegramAvailable = false;
	
	private byte crc = 0;
	
	public String toDumpString() {
		return EBusUtils.toHexDumpString(bb).toString();
	}
	
	public boolean isSync() {
		return state == State.SYN;
	}
	
	public boolean isReceivingTelegram() {
		return state.compareTo(State.SYN) > 1;
	}
	
	public boolean isReadyForAnswer() {
		// after master crc byte
		return state == State.CRC1 && bb.get(1) != EBusConsts.BROADCAST_ADDRESS;
	}
	
	public boolean isTelegramAvailable() {
		return telegramAvailable;
	}

	public byte[] getTelegramData() {
		
		byte[] receivedRawData = new byte[bb.position()];
		bb.position(0);
		bb.get(receivedRawData);
		
		return receivedRawData;
	}
	
	public State getState() {
		return state;
	}
    
	private void reset(boolean ignoreState) {
		len = 0;
		crc = 0;
		isEscapedByte = false;
		telegramAvailable = false;
		
		bb.clear();
		
		if(!ignoreState) {
			setState(State.UNKNOWN);
		}
	}
	
	public void reset() {
		reset(false);
	}
	
	private boolean checkSYN(byte data) {
		if(data == EBusConsts.SYN) {
			logger.warn("Reset state machine because SYN byte!");
			reset();
			return true;
		}
		
		return false;
	}
	
	public void update(byte data) throws EBusDataException {
		
		// check syn bytes
//		if(data == EBusConsts.SYN) {
//			if(state != State.UNKNOWN && state != State.SYN && state != State.CRC1) {
//				logger.warn("Reset state machine because SYN byte!");
//				reset();
//			}
//		}
		
		if(!bb.hasRemaining()) {
			reset();
			logger.warn("Input buffer full, reset!");
			throw new EBusDataException("", EBusDataException.EBusError.INDEX_OUT_OF_BOUNDS);
		}
		
		// state machine
		
		switch (state) {
		
		case UNKNOWN:
			// unknown > syn
			
			// waiting for next sync byte
			if(data == EBusConsts.SYN) {
				setState(State.SYN);			
			}
			
			break;

		case SYN:
			// syn > source address
			if(EBusUtils.isMasterAddress(data)) {
				// start telegram, reset old data
				reset(true);
				
				bb.put(data);
				crc = EBusUtils.crc8_tab(data, (byte)0);
				setState(State.SRC_ADDR);
				
			} else if(data == EBusConsts.SYN) {
				logger.trace("Auto-SYN byte received");
				// keep in this state
				
			} else {
				// unknown data
				reset();
				
			}
			
			break;
			
		case SRC_ADDR:
			// source address > target address
			
			if(checkSYN(data)) {
				break;
			}
			
			bb.put(data);
			crc = EBusUtils.crc8_tab(data, crc);
			setState(State.TGT_ADDR);
			break;
			
		case TGT_ADDR:
			// target address > primary command
			
			if(checkSYN(data)) {
				break;
			}
			
			bb.put(data);
			crc = EBusUtils.crc8_tab(data, crc);
			setState(State.PRIMARY_CMD);
			break;
			
		case PRIMARY_CMD:
			// primary command > secondary command
			
			if(checkSYN(data)) {
				break;
			}
			
			bb.put(data);
			crc = EBusUtils.crc8_tab(data, crc);
			setState(State.SECONDARY_CMD);
			break;

		case SECONDARY_CMD:
			// secondary command > nn1
			
			if(checkSYN(data)) {
				break;
			}
			
			if(data > 16) {
				logger.warn("Master Data len too large!");
				reset();
				break;
			}
			
			len = data;
			logger.info("Data Length: " + len);
			bb.put(data);
			crc = EBusUtils.crc8_tab(data, crc);
			
//			if(len == 0)
			
			setState(len == 0 ? State.DATA1 : State.LENGTH1);
			break;
			
		case LENGTH1:
			// nn1 > db1 (end)
			
			if(checkSYN(data)) {
				break;
			}
			
			crc = EBusUtils.crc8_tab(data, crc);
			
			if(data == EBusConsts.ESCAPE) {
				isEscapedByte = true;
				
			} else {
				
				if(isEscapedByte) {
					data = data == (byte) 0x00 ? EBusConsts.ESCAPE : 
						data == (byte) 0x01 ? EBusConsts.SYN : data;
					isEscapedByte = false;
				}
				
				bb.put(data);
				len--;
			}

			if(len == 0) {
				setState(State.DATA1);
			} else {
				logger.info("Data " + len);
			}

			break;
			
		case DATA1:
			// after data
			
			if(checkSYN(data)) {
				break;
			}
			
			// escaped crc value
			if(!isEscapedByte && crc == EBusConsts.ESCAPE) {
				isEscapedByte = true;
				break;
			}
			
			// overwrite data with new value
			if(isEscapedByte) {
				data = data == (byte) 0x00 ? EBusConsts.ESCAPE : 
					data == (byte) 0x01 ? EBusConsts.SYN : data;
				isEscapedByte = false;
			}
			
			if(data == crc) {
				logger.info("Jehaaaaaaaaaaaaaaaaaaaaaaa");
				setState(State.CRC1);
			} else {
				logger.warn("Wrong Master CRC! " + EBusUtils.toHexDumpString(bb));
				reset();
			}
			
			break;

			
		case CRC1:
			// CRC > SYN / NACK / ACK
			logger.info("nuuuuuuuuuuuuuuuuuuuuuuuuu?");
			
			if(data == EBusConsts.SYN) {
				if(bb.get(1) == EBusConsts.BROADCAST_ADDRESS) {
					logger.info("broadcast end");
					fireTelegramAvailable();
					
				} else {
					logger.warn("unexpected telegram end!");
				}
				setState(State.SYN);
				
			} else if(data == EBusConsts.ACK_OK) {
				setState(State.ACK1);
				
				
			} else if(data == EBusConsts.ACK_FAIL) {
				logger.warn("Slave answered NACK!");
				reset();
			}
			break;

		case ACK1:
			// ACK1 > NN2
			
			if(checkSYN(data)) {
				break;
			}
			
			crc = EBusUtils.crc8_tab(data, (byte)0);
			
			if(data > 16) {
				setState(State.UNKNOWN);
				logger.warn("Master Data len too large!");
				break;
			}
			
			len = data;
			logger.info("xData Length: " + len);
			
//			setState(State.LENGTH2);
			setState(len == 0 ? State.DATA2 : State.LENGTH2);
			break;
			
		case LENGTH2:
			// NN2 > DB2 (end)
			
			if(checkSYN(data)) {
				break;
			}
			
			crc = EBusUtils.crc8_tab(data, crc);
			
			if(data == EBusConsts.ESCAPE) {
				isEscapedByte = true;
				
			} else {
				
				if(isEscapedByte) {
					data = data == (byte) 0x00 ? EBusConsts.ESCAPE : 
						data == (byte) 0x01 ? EBusConsts.SYN : data;
					isEscapedByte = false;
				}
				
				bb.put(data);
				len--;
			}

			if(len == 0) {
				setState(State.DATA2);
			} else {
				logger.info("Data " + len);
			}

			break;
			
		case DATA2:
			// after data
			
			if(checkSYN(data)) {
				break;
			}
			
			if(data == crc) {
				logger.info("Jehaaaaaaaaaaaaaaaaaaaaaaa");
				setState(State.CRC2);
			} else {
				logger.warn("Wrong Master CRC! " + EBusUtils.toHexDumpString(bb));
				reset();
			}
			
			break;
			
		case CRC2:
			// CRC > SYN / NACK / ACK

			if(data == EBusConsts.SYN) {
				if(bb.get(1) == EBusConsts.BROADCAST_ADDRESS) {
					logger.info("telegram end (broadcast)");
					setState(State.SYN);
				} else {
					logger.warn("no answer from master or slave!");
					reset();
				}

			} else if(data == EBusConsts.ACK_OK) {
				setState(State.ACK2);
				
				
			} else if(data == EBusConsts.ACK_FAIL) {
				logger.warn("slave answerd with NACK!");
				reset();
				
			}
			break;

		case ACK2:
			// ACK1 > SYN
			setState(State.SYN);
			
			fireTelegramAvailable();
			
			break;
			
		default:
			logger.warn("Unknown state!");
			setState(State.UNKNOWN);
			break;
		}

	}
	
	private void fireTelegramAvailable() {
		logger.info("fireTelegramAvailable ...");
		telegramAvailable = true;
	}
	
	private void setState(State newState) {
		logger.info("Update state from " + state.name() + " to " + newState.name());
		state = newState;
	}

}
