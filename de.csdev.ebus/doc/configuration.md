# eBUS Configuration


## Configuration format


### Header block

```javascript
{
    "id":           "device_id",
    "vendor":       "Vendor Name",
    "label":        "A nice label",
    "description":  "A longer description",
    
    "authors":      ["Your author name"],
    "identification": ["FF FF FF FF FF"],
    "properties":   {
                        "key" : "value"
                    },
    "templates":    [
        {"name":"template_block_1", "template": [
            <value>,
            <value>,
            ...
        ]}
	],
                    
    "commands":     [
        <command>,
        <command>,
        ...
    ]
}
```

Key            | Required | Description
---            | ---      | ---
id             | x        | The unique short id of this file
vendor         | x        | Then device vendor
label          | x        | The device label
description    | x        | A longer description
authors        |          | Multiple authors of this file as array
identification |          | The device identifications as hex string with five bytes. Multiple values allowed.
properties     |          | Additional key-values
commands       | x        | The array of templates, see template block below
commands       | x        | The array of commands, see command block below


### Template block

Key            | Required | Description
---            | ---      | ---
name           | x        | The name of the template block. Must be unique within the file
template       | x        | An array of value blocks


### Command block

```javascript
{
    "label":    "Command label",
    "id":       "groupdId.commandId",
    "command":  "FF FF",
    
    "template": [<template_value>, <template_value>, ...],
    
    <method-block>,
    <method-block>,
    ...
}
```

Key       | Required | Description
---       | ---      | ---
label     | x        | A label for this command
id        | x        | A unique identifier within this file
command   |          | The two byte hex string for the eBUS command
template  |          | 
get       |          | 
set       |          | 
broadcast |          | 


### Method block

```javascript
"set": {
    "command": "FF FF",
    "master": [<value>, <value>, ...]
},

"get": {
    "command": "FF FF",
    "master": [<value>, <value>, ...],
    "slave": [<value>, <value>, ...]
},

"broadcast": {
    "command": "FF FF",
    "master": [<value>, <value>, ...]
}
```

Key       | Required | Description
---       | ---      | ---
set       |          | The command configuration for the setter method
get       |          | The command configuration for the getter method
broadcast |          | The command configuration for the broadcast method
master    |          | The value list for master data, see value block below
slave     |          | The value list for slave data, see value block below
command   |          | The value list for slave data, see value block below


### Value block

```javascript
{
    "name":"value_name",
    "type": "typeId",
    "label": "A nice label",
    "format":"%.1f°C",
    "min": 0,
    "max": 100,
    "factor": 0.1,
    "step": 0.5,
    "length": 2,
    "default": "FF",
    "children": [<value>, <value>, ...]
}
```

Key              | Required | Description
---              | ---      | ---
name             | (x)      | The unique name within this command. Use underscore ``_`` for multiple-word names. Use a leading ``_`` to mark this value as advanced.
type             | x        | The data type of this value
label            |          | The label of this value
format           |          | Formatter for this value, see String.format
min              |          | A minimal allowed value
max              |          | A maximal allowed value
factor           |          | Multiply the result value with this factor
step             |          | The allowed step size for this value
length           |          | The length for variable data types like ``bytes``, ``string`` and ``mword``
children         |          | sub values for type ``byte``
id               |          | Currently used by `template` and `template-block`
variant          |          | Currently used by ``datetime``, ``date`` and ``time``
default          |          | A default value, usually as hex string
reverseByteOrder |          | Reverse the byte order of some datatypes


## Data types


### Standard data types

Key     | Alias | Len | Description
------- | ----- | --- | -----------
bcd     |       | 1(*)| BCD number, ``length`` for larger values
bit     |       | 1   | Bit as child of ``byte``
byte    | uchar | 1   | Byte
char    |       | 1   | Char
data1b  |       | 1   | DATA1B
data1c  |       | 1   | DATA1C
data2b  |       | 2   | DATA2B
data2c  |       | 2   | DATA2C
int     |       | 2   | Integer
word    | uint  | 2   | Unsigned Word
number  |       | *   | Signed number, variable length
unumber |       | *   | Unsigned number, variable length


### Advanced data types

Key            | Alias | Len   | Add. Param       | Description
---            | ---   | ---   | ---              | ---
bytes          | Byte Array, requires ``length``
datetime       | A combination of type ``date`` and ``time``. Use their variants. Switch Time/date order with ``timeFirst``
date           | A Date value (default), ``std``, see list below
time           | A Time value (default), ``std``, see list below
kw-crc         | Kromschröder/Wolf CRC, often seen as ``0xCC``
version        | A BCD encoded version number
mword          | Multiple word, requires ``length`` and allows to set ``multiplier`` (default: 1000)
string         | ASCII String, requires ``length``
static         | A static byte array with the value of ``default``
template       | Adds the template with the id,
template-block | Adds the complete template block on this position

For more deails see [Advance Data Types](./types.md)


## Example
```javascript
{
    "id":           "common",
    "vendor":       "eBUS Group",
    "label":        "eBUS Standard",
    "description":  "eBUS Group Standard commands",
    
    "authors":      ["Christian Sowada, opensource@cs-dev.de"],
    "identification": [],
    
    "commands":
    [
        {
            "label":    "Outside temperature",
            "id":       "burner.temp_outside",
            "command":  "03 01",
    
            "broadcast": {
                "master": [
                    {"name":"temp_outside", "type": "word", "label": "Outside temperature", "factor": 0.1, "format":"%.1f°C"}
                ]
            }
        },
    
        {
            "label":    "Operational Data of Burner Control unit to Room Control Unit - Block 1",
            "id":       "auto_stroker.op_data_bc2tc_b1",
            "command":  "05 03",

            "get": {
                "master": [
                    {"name":"block_number", "type": "static", "default": "01"},
                    {"name":"status_auto_stroker", "type": "byte", "label": "Status indication"},
                    {"type": "byte", "children": [
                        {"name":"state_air_pressure", "type": "bit", "label": "Air pressure monitor"},
                        {"name":"state_gas_pressure", "type": "bit", "label": "Gas pressure monitor"},
                        {"name":"state_water_flow", "type": "bit", "label": "Water flow"},
                        {"name":"state_flame", "type": "bit", "label": "Flame"},
                        {"name":"state_valve1", "type": "bit", "label": "Valve1"},
                        {"name":"state_valve2", "type": "bit", "label": "Valve2"},
                        {"name":"state_pump", "type": "bit", "label": "State heating pump"},
                        {"name":"state_alarm", "type": "bit", "label": "Alarm"}
                    ]},                
                    {"name":"performance_burner", "type": "uchar", "label": "Setting degree between min. and max. boiler performance (%)","min":0.0, "max":100.0,  "format":"%d%%"},
                    {"name":"temp_boiler", "type": "data1c", "label": "Boiler temperature (°C)", "min":0, "max":100, "format":"%.1f°C"},
                    {"name":"temp_return", "type": "uchar", "label": "Return temperature (°C)", "min":0, "max":100, "format":"%d°C"},
                    {"name":"temp_dhw", "type": "uchar", "label": "DHW temperature (°C)", "min":0, "max":100, "format":"%d°C"},
                    {"name":"temp_outside", "type": "char", "label": "Outside temperature (°C)", "min":-30, "max":50, "format":"%d°C"}
                ]
            }
        },
        ...
        
    ]
}
```
