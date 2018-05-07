## Alpha 0.9.18 (2018-??-??) WIP - SNAPSHOT

Features:

  - add bruteforce analyzer to decode data with all data types (internal only)
  - add watchdog timer to all eBus connections, default is 5mins at the moment

Bugfixes:

  - fix java 6 compatibility
  - fix Replace Value for `bit` data type
  - fix NPE for data type decode
  - fix some smaller issues

## Alpha 0.9.17 (2018-??-??)

Never released, migrated internal development branch to github

## Alpha 0.9.16 (2018-01-30)

Features:

  - large internal update, see git commits for details
  - move all configuration file to an extra bundle, easier to update
  - add new data type `float`
  - Add variant `minute_short` for 8bit minute values
  - Enhance unit tests
  
Bugfixes:

  - Fix template block for local and global id
  - Swap, `hex` and `hex_short` in decodeInt()
  - fix data type `minutes_short`
  - fix missing master length in csv file
  
Configurations:

  - All configurations are moved! See ebus-configuration bundle.

## Alpha 0.0.15 (2017-12-04)

Features:

  - add support for global templates in json configuration files
  - clean-up source code
  - enhance documentation

Bugfixes:

  - smaller bug fixes

Configurations:

  - add device Vaillant VC206
  - change Vaillant VRC430/470 label  `Outside temperature` 
    to to `Controller Datetime`
  - add Vaillant BAI `datetime`, `setopdata`and `getopdata`

## Alpha 0.0.14 (2017-11-20)

Features:

  - add support to load complete configuration bundles via URL
  - add device scan function, rename inquire function
  - add CSV writer for resolved and unresolved telegrams
  - add metrics service to collect eBUS data

Bugfixes:

  - also use master addresses for device table
  - activate unused resolve failed events
  - delete unused projects from source
  - remove closed connection streams on close

Configurations:

  - add identification for Wolf MM
  - add outside temperature and datetime to Vaillant VRC 4307470

## Alpha 0.0.13 (2017-11-02)

Features:

  - restructure internal configuration handling
  - removed outdated maven modules from source

Configurations:

  - add identification for Vaillant VRC430f and VRC470f
  - add pressure for Vailant BAI00
  - fix outside_temp for Vailant BAI00

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