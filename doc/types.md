# Advanced data types


## Type `bytes`

Byte array of given length

Property | Required | Default | Information
---      | ---      | ---     | ---
length   | x        |         | The byte array length


## Type `date`

A date value.

Property         | Required | Default   | Information
---              | ---      | ---       | ---
variant          |          | `std`   | Use `std`, `short`, `hex`, `hex_short` or `days` 
reverseByteOrder |          | `false` | Reverse the byte order of this value

List of possible `variant` values

Variant   | Length | Desciption
---       | ---    | ---
std       | 4      | BCD date include weekday
short     | 3      | BCD date without weekday
hex       | 4      | HEX date without weekday
hex_short | 3      | HEX date without weekday
days      | 2      | Days since 01.0.1970


## Type `time`

A time value.

Property         | Required | Default | Information
---              | ---      | ---     | ---
variant          |          | `std`   | Use `std`, `short`, `hex`, `hex_short` or `minutes` 
minuteMultplier  |          |         | multiple of n minutes
reverseByteOrder |          | `false` | Reverse the byte order of this value

List of possible `variant` values

Variant   | Length | Desciption
---       | ---    | ---
std       | 4      | BCD date include seconds
short     | 3      | BCD date without seconds
hex       | 4      | HEX date without seconds
hex_short | 3      | HEX date without seconds
minutes   | 2      | Minutes since midnight


## Type `datetime`

A combination of data type `date` and `time`.

Property    | Required | Default | Information
---         | ---      | ---     | ---
timeFirst   |          | `true`  | The first bytes are the time (default), to change set `false`
variantTime |          | `std`   | Use variant from data type `date`
variantDate |          | `std`   | Use variant from data type `time`


## Type `template`

A template value will be replaced with a single template value and all it's properties. You can overwrite nearly all properties wwith other values. This is not possible for `template-block` values. The id is required to identifiy the correct template from this or any other file.

Property         | Required | Default          | Information
---              | ---      | ---              | ---
id               | x        |                  | The id of the template value in in form of `collectionId`.`templateBlockName`.`templateValueName`
name             |          |                  | Overwrites the `name` from the template
label            |          |                  | Overwrites the `label` from the template
(value property) |          | (template value) | Overwrites any property from a `value block`


### Additional information to template ids

_Notice: command level templates will be removed in further releases!_

You can define templates within any configuration file. It's also possible to create configuration files without any command blocks. If you want to address a template within the same configuration file use the short id format like `templateBlockName`.`templateValueName`. Add the `collectionId` if you want to use template from the global scope. Example: `collectionId`.`templateBlockName`.`templateValueName`.

_Notice: If you want to use global templates, keep the loading order of the files in mind. The template must be loaded before it is referenced._

**Examples**

Use id `temp_sensor` within the same file or use `vtempl.temp_sensor` to use a template from another file.


## Type `template-block`

A template block will be replaced with all values within the template block. You are not able to replace single properties! In this case use multiple `template` values instead of one block. The id is required to identifiy the correct template from this or any other file.

Property | Required | Default | Information
---      | ---      | ---     | ---
id       | x        |         | The id of the template value in in form of `collectionId`.`templateBlockName`


### Additional information to template ids

_Notice: command level templates will be removed in further releases!_

You can define templates within any configuration file. It's also possible to create configuration files without any command blocks. If you want to address a template within the same configuration file use the short id format like `templateBlockName`. Add the `collectionId` if you want to use template from the global scope. Example: `collectionId`.`templateBlockName`

_Notice: If you want to use global templates, keep the loading order of the files in mind. The template must be loaded before it is referenced._

## Type `string`

ASCII String

Property | Required | Default | Information
---      | ---      | ---     | ---
length   | x        |         | The characters length


## Type `mword`

Multiple word, each word value will be multiplied

Property         | Required | Default | Information
---              | ---      | ---     | ---
length           |          | `1`     | The characters length
multiplier       | x        | `1000`  | The characters length
reverseByteOrder |          | `false` | Reverse the byte order of this value

## Type `static`

A static byte array with the value of `default`

Property | Required | Default | Information
---      | ---      | ---     | ---
default  | x        |         | The hex byte array as string


## Type `version`

A BCD encoded version number


## Type `kw-crc`

Kromschröder/Wolf CRC, often seen as `0xCC`