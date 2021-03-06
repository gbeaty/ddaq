## Problems:
- Gathering mechanical data from external sources (OBD2 interfaces, ADCs, CAN buses, etc).
- Saving said data in standard formats.
- Displaying only the data the operator needs to see.

## Goals:
- Modular, open-source dash system targeting Android and potentially other JVM-capable platforms.
- Leverage code contributions for sensor definitions, front-ends, log file formats, and data-source drivers.

## Projects:

- ddaq: The main library. Defines channels, dashes, controllers, sources, etc. and tools to compose them.
- source: Data source drivers, such as OBD2 interfaces, ADCs, etc.
- controller: Controllers for dashes and loggers. These handle warnings, refresh rates, rolling averages, etc.
- logger: Handles saving gathered data to various log file formats.
- sensor: Sensor definitions.
- channel: Channel type definitions and composition (e.g. low oil pressure warnings, combining RPM and pulse with to get duty cycle, etc.)

## Platforms:

- test: The development test platform. Prints output to System.out.
- android: The first target platform.