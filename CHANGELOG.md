## Alpha 0.0.12 (2017-10-22)

Features:

  - new data types ``date`` and ``time`` for variable length signed / unsigned numbers
  - more source code documentation
  - enhance logging on firing event handler s

Bugfixes:

  - fix issue if master/slave CRCs are escaped
  - fix ignored properties for some non-standard properties in json files
  - adjust some maven build properties 
  - fix and enhance manufacture id/name mapping

Configurations:

  - add date and time to Vaillant VRC470
  - fix identifier for Vailant BAI00


## Alpha 0.0.11 (2017-10-15)

Features:

  - change device table from master address to slave address indexed
  - complete rewritten internal data types
    - simplified class hierarchy
    - junit tests for all data types
    - property ``reverseByteorder`` for nearly all data types
    - replaceValue is now available for all data types
  - new data type ``date`` and ``time``, ``datetime`` rewritten
  - enhance data type ``bcd`` to support variable length

Bugfixes:

  - fix a bug where ``factor`` was used twice for data type ``mword``
  - fix all data type to not modify the input byte array
  - fix data type ``bcd`` to only allow value in range of 0-99
  - fix data type ``bcd`` returned as signed value
  - fix common configuration label, add mappings to status_heat_reqX
  - fix id in configuration wolf mm
  - fix manufacture name lookup issue