ebusd type | eBUS binding type | add. params | xxxx
--- | --- | --- | --- 
IGN | byte | use without ``name``
STR | string | | filled up with space
**NTS** | | | | filled up with 0x00
HEX | char
BDA | date | | day first, excluding weekday
BDA:3 | date | variant: short | day first, including weekday, Sunday=0x06
HDA   | date | variant: hex | dd.mm.yyyy | day first, including weekday, Sunday=0x07
HDA:3 | date | variant: hex_short | day first, excluding weekday
DAY   | date | variant: days | days since 01.01.1900
BTI | time | | seconds first
BTM | time | variant: short | minute first
VTI | time | variant: hex | seconds first
VTM | time | variant: hex_short | minute first
HTI | time | variant: hex, reverseByteOrder: true | hours first
HTM | time | variant: hex_short, reverseByteOrder: true | hours first
MIN | time | variant: min | minutes since midnight
**TTM** | time | variant: min, factor: 10 | multiple of 10 minutes since midnight
**TTH** | time | variant: min, factor: 30 | multiple of 30 minutes since midnight
**TTQ** | time | variant: min, factor: 15 | multiple of 15 minutes since midnight
**BDY** | | | | Weekday, Sunday=0x06
**HDY** | | | | Weekday, Sunday=0x07
BCD | bcd | | BCD value
**BCD:2* | bcd | length: 2 | BCD value
**BCD:3** | bcd | length: 3 | BCD value
**BCD:4** | bcd | length: 4 | BCD value
**HCD** |||each BCD byte converted to hex
**HCD:1** |||each BCD byte converted to hex
**HCD:2** |||each BCD byte converted to hex
**HCD:3** |||each BCD byte converted to hex
**PIN** | bcd | length: 2 | BCD value
UCH | byte/(uchar) | | unsigned char, primary type 
SCH | char | | signed char, primary type 
D1C | data1c | | same as ``char``
D2B | data2b | | divisor 2
D2C | data2c | | divisor 256
FLT | int | factor: 0.001
FLR | int | factor: 0.001, reverseByteOrder: true
**EXP** | |
**EXR** | |
UIN | word/(uint) | | low byte first
UIR | word/(uint) | reverseByteOrder: true | high byte first
SIN | int | | low byte first
SIR | int | reverseByteOrder: true | high byte first
**U3N** | word/(uint) | length: 3 | low byte first
**U3R** | word/(uint) | length: 3, reverseByteOrder: true | high byte first
**S3N** | int | length: 3 | low byte first
**S3R** | int | length: 3, reverseByteOrder: true | high byte first
**ULG** | word/(uint) | length: 4 | low byte first
**ULR** | word/(uint) | length: 4, reverseByteOrder: true | high byte first
**SLG** | int | length: 4 | low byte first
**SLR** | int | length: 4, reverseByteOrder: true | high byte first
DI0-7 | byte | children of ``bit``
