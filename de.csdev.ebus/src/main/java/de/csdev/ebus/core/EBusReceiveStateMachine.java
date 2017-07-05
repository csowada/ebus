package de.csdev.ebus.core;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.utils.EBusUtils;

public class EBusReceiveStateMachine {

	private static final Logger logger = LoggerFactory.getLogger(EBusReceiveStateMachine.class);

    public enum State {
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
        ACK2,
        SYN,
        UNKNOWN
    }
	
    private State state = State.UNKNOWN;
    
    private int len = 0;
    
	private ByteBuffer bb = ByteBuffer.allocate(50);
	
	private boolean isEscapedByte = false;
	
	private byte crc = 0;
	
    
	public void update(byte data) {
		
		if(data == EBusConsts.SYN) {
			if(state != State.UNKNOWN && state != State.SYN) {
				logger.warn("Reset state machine because SYN byte!");
				setState(State.UNKNOWN);
			}
		}
		
		switch (state) {
		
		case UNKNOWN:
			// unknwon > syn
			
			// waiting for next sync byte
			if(data == EBusConsts.SYN) {
				setState(State.SYN);			
			}
			
			break;

		case SYN:
			// syn > source address
			if(EBusUtils.isMasterAddress(data)) {
				// start telegram
				bb.clear();
				bb.put(data);
				crc = EBusUtils.crc8_tab(data, (byte)0);
				setState(State.SRC_ADDR);
				
			} else if(data == EBusConsts.SYN) {
				logger.info("Leave in state SYN");
				
			} else {
				setState(State.UNKNOWN);
			}
			
			break;
			
		case SRC_ADDR:
			// source address > target address
			bb.put(data);
			crc = EBusUtils.crc8_tab(data, crc);
			setState(State.TGT_ADDR);
			break;
			
		case TGT_ADDR:
			// target address > primary command
			bb.put(data);
			crc = EBusUtils.crc8_tab(data, crc);
			setState(State.PRIMARY_CMD);
			break;
			
		case PRIMARY_CMD:
			// primary command > secondary command
			bb.put(data);
			crc = EBusUtils.crc8_tab(data, crc);
			setState(State.SECONDARY_CMD);
			break;

		case SECONDARY_CMD:
			// secondary command > nn1
			
			if(data > 16) {
				setState(State.UNKNOWN);
				logger.warn("Master Data len too large!");
				break;
			}
			
			len = data;
			logger.info("Data Length: " + len);
			bb.put(data);
			crc = EBusUtils.crc8_tab(data, crc);
			setState(State.LENGTH1);
			break;
			
		case LENGTH1:
			// nn1 > db1 (end)
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
			if(data == crc) {
				logger.info("Jehaaaaaaaaaaaaaaaaaaaaaaa");
				setState(State.CRC1);
			} else {
				logger.warn("Wrong Master CRC! " + EBusUtils.toHexDumpString(bb));
				
				setState(State.UNKNOWN);
			}
			
			break;

			
		case CRC1:
			// CRC > SYN / NACK / ACK
			logger.info("nuuuuuuuuuuuuuuuuuuuuuuuuu?");
			
			if(data == EBusConsts.SYN) {
				logger.info("telegram end");
				setState(State.SYN);
				
			} else if(data == EBusConsts.ACK_OK) {
				setState(State.ACK1);
				
				
			} else if(data == EBusConsts.ACK_FAIL) {
				
				
			}
			break;

		case ACK1:
			// ACK1 > NN2
			crc = EBusUtils.crc8_tab(data, (byte)0);
			
			if(data > 16) {
				setState(State.UNKNOWN);
				logger.warn("Master Data len too large!");
				break;
			}
			
			len = data;
			logger.info("xData Length: " + len);
			
			setState(State.LENGTH2);
			break;
			
		case LENGTH2:
			// NN2 > DB2 (end)
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
			if(data == crc) {
				logger.info("Jehaaaaaaaaaaaaaaaaaaaaaaa");
				setState(State.CRC2);
			} else {
				logger.warn("Wrong Master CRC! " + EBusUtils.toHexDumpString(bb));
				
				setState(State.UNKNOWN);
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
					setState(State.UNKNOWN);
				}

			} else if(data == EBusConsts.ACK_OK) {
				setState(State.ACK2);
				
				
			} else if(data == EBusConsts.ACK_FAIL) {
				logger.warn("slave answerd with NACK!");
				setState(State.SYN);
				
			}
			break;

		case ACK2:
			// ACK1 > SYN
			setState(State.SYN);
			break;
			
		default:
			logger.warn("Unnown state!");
			setState(State.UNKNOWN);
			break;
		}

	}
	
	private void setState(State newState) {
		logger.info("Update state from " + state.name() + " to " + newState.name());
		state = newState;
	}

}
