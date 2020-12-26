# Changelog
All notable changes to this project will be documented in this file.

## Unreleased 
### Fixed
- The new "Change Status" check (from v1.0.8) does not work, since the old status is already set previously

## [1.1.3] - 2020-12-25
### Changed
- Changed many line of code to fix sonarlint/cloud issues
- Add more ``final`` to parameters
- Changed to Java 8 syntax like Lambda expressions or Multi-Exceptions
- Fixed one or two wrong handled Interrupts

## [1.1.2] - 2020-12-17
### Added
- Added ``sonarcloud`` to build pipeline
### Removed
- Removed all old CI files from the project

## [1.1.1] - 2020-12-17
### Added
- Added ``gitflow-maven-plugin`` to manage release process, see BUILD-HELP.md
### Changed
- Switched from Travis CI to GitHub Actions

## [1.1.0] - 2020-12-12
### Added
- Add Eclipse Null Annotionas to project
### Changed
- Changed a lot of code lines to fix several ``null`` issues etc.
- Declare all dependencies in ``pom.xml`` as ``provided``

## [1.0.8] - 2020-06-21
### Fixed
- Fixed several ``NullPointException``

## [1.0.7] - 2020-05-17

### Changed
- Limit the ThreadPoolExecutor to max. 30 threads

### Fixed
- Add sender thread restart to main loop
- Fix a NullPoint Exception
   
## [1.0.6] - 2020-02-09

### Added
- Add a send preparation function incl. auto attach crc if missing

### Change
- Enhance fireOn... events, including some sendId fixes
   
### Fixed
- Disable ``InterruptedIOException`` to prevent ebusd connector from closing after a short time

## [1.0.5] - 2020-01-29

### Changed
- Enhance telegram matcher in Command registry
- Add (debug) warning if master-slave command configuration is without slave part
- Throw an exception on ``master-master`` commands with slave addresses that have no master address pair
   
### Fixed
- Fix matcher in Command registry
- Fix several nested template bugs
  - Fix  internal clone() of nested templates
  - Set type and default value for nested types
  - Use list for ``template-block`` to also copy values without ``name``
  - Rename template to valueDto, better variable name
  - Add nested values to ``composeMasterData`` incl. test case
  - Fix parent method for nested values

## [1.0.4] - 2020-01-21

### Added
- Add Version class to identify build version, commit etc.

### Fixed
- Use timestamps for Bundle-Version

## [1.0.3] - 2020-01-20

### Changed
- enhance ebusd controller with version check
- update all unit tests
- improve NRJavaSerial connector
- add a warning if the received data doesn't match to the found config method.

### Fixed
- fix ``FF`` byte issue on sending
- add a warning if a template-block element has no name (more a bug workaround!)
- revert default telegram type to ``master-slave`` if not specified
- harden slave length function, expect no slave data
  
## [1.0.2] - 2020-01-12

### Changed
- check every send byte for LowLevelController
- enhance error and parsing logging
- enhance queue for ebusd controller
- adjust NRJavaSerial driver for low latency

### Fixed
- block send queue while controller is not connected
- reduce Device Table Service log messages
  
## [1.0.1] - 2020-01-03

### Changed
- add more details on connection errors
- add more details on parser errors
- add connection status incl. listeners to controllers

### Fixed
- fix thread interrupt handling
- allow null values for date and/or time for "datetime" type
- fix wrong replace broadcast message on building a telegram
- block table services if connection is not established
- add slave length to command match function to only process expected slave answer


## [1.0.0] - 2019-12-28
### Changed
- Just a new label for the Alpha 0.9.22 release
  
## Alpha 0.9.22 (2019-12.28)

Bugfixes:

  - fix critical Master-Master send issue
  - change error handling for "End of stream reached"
  - replace and warn wrong addresses for buildMasterTelegram
  - enhance Wolf/Kromschröder CRC calculation for type "kw-crc"
 
## Alpha 0.9.21 (2019-05-22)

Project Enhancements:

   - fix project setup
   - remove useless MANIFEST.MF file
   - update all file headers to year 2019

## Alpha 0.9.20 (2019-05-22)

Features:

  - add ebusd connector
  
Bugfixes:

  - fix NPE
  - synchronize log writers
  
## Alpha 0.9.19 (2018-11-03)

Features:

  - add JSerialComm as additional serial driver
  - add send/receive roundtrip time to controller
  - Update DTO objects
  - add divider property to DTO
  - change java level to 1.8
  
Bugfixes:

  - fix "indirectly referenced" issue on extern bundles

## Alpha 0.9.18 (2018-06-10)

Features:

  - add bruteforce analyzer to decode data with all data types (internal only)
  - add watchdog timer to all eBus connections, default is 5mins at the moment
  - add new controller exception to harden thread handling

Bugfixes:

  - fix java 6 compatibility
  - fix Replace Value for `bit` data type
  - fix NPE for data type decode
  - fix NPE because of already closed thread pool
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