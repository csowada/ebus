# ebusd buildin data type mapping

The list below shows the mapping from ebus build data types to this library. All
values in bold are not supported yet.

You can see the list of ebusd data types in the 
[ebusd wiki](https://github.com/john30/ebusd/wiki/4.3.-Builtin-data-types).

ebusd type | eBUS lib type | add. params | xxxx
--- | --- | --- | --- 
IGN | byte | use without ``name``
STR | string | | filled up with space
**NTS** | | | | filled up with 0x00
HEX | char
BDA | date | | day first, including weekday, Sunday=0x06
BDA:3 | date | variant: short | day first, excluding weekday
HDA   | date | variant: hex | dd.mm.yyyy | day first, including weekday, Sunday=0x07
HDA:3 | date | variant: hex_short | day first, excluding weekday
DAY   | date | variant: days | days since 01.01.1900
BTI | time | | seconds first
BTM | time | variant: short | minute first
VTI | time | variant: hex | seconds first
VTM | time | variant: hex_short | minute first
HTI | time | variant: hex, reverseByteOrder: true | hours first
HTM | time | variant: hex_short, reverseByteOrder: true | hours first
MIN | time | variant: minutes | minutes since midnight
TTM | time | variant: minutes, minuteMultplier: 10 | multiple of 10 minutes since midnight
TTH | time | variant: minutes, minuteMultplier: 30 | multiple of 30 minutes since midnight
TTQ | time | variant: minutes, minuteMultplier: 15 | multiple of 15 minutes since midnight
**BDY** | | | | Weekday, Sunday=0x06
**HDY** | | | | Weekday, Sunday=0x07
BCD | bcd | | BCD value
BCD:2 | bcd | length: 2 | BCD value
BCD:3 | bcd | length: 3 | BCD value
BCD:4 | bcd | length: 4 | BCD value
**HCD** |||each BCD byte converted to hex
**HCD:1** |||each BCD byte converted to hex
**HCD:2** |||each BCD byte converted to hex
**HCD:3** |||each BCD byte converted to hex
PIN | bcd | length: 2 | BCD value
UCH | byte/(uchar) | | unsigned char, primary type 
SCH | char | | signed char, primary type 
D1C | data1c | | same as ``char``
D2B | data2b | | divisor 2
D2C | data2c | | divisor 256
FLT | int | factor: 0.001
FLR | int | factor: 0.001, reverseByteOrder: true
**EXP** | |
**EXR** | | reverseByteOrder: true
UIN | word/(uint) | | low byte first
UIR | word/(uint) | reverseByteOrder: true | high byte first
SIN | int | | low byte first
SIR | int | reverseByteOrder: true | high byte first
U3N | byte/(uchar) | length: 3 | low byte first
U3R | byte/(uchar) | length: 3, reverseByteOrder: true | high byte first
S3N | char | length: 3 | low byte first
S3R | char | length: 3, reverseByteOrder: true | high byte first
ULG | byte/(uchar) | length: 4 | low byte first
ULR | byte/(uchar) | length: 4, reverseByteOrder: true | high byte first
SLG | char | length: 4 | low byte first
SLR | char | length: 4, reverseByteOrder: true | high byte first
DI0-7 | byte | children of ``bit``
