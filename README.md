# denon4j Java API

[![Build Status](https://travis-ci.org/stheves/denon4j.svg?branch=master)](https://travis-ci.org/stheves/denon4j)

The denon4j project provides a java API for communicating with a Denon AVR (currently only AVR1912 is supported and tested)
via TCP/IP. The project is intended to be easily extended for those who want to write an API for their own AVR model.

Contributions are welcome!

# Prerequisites

- Java 8
- Maven 3.2+ (only for building)
- You must be connected to the same network as your AV receiver
- Your receiver must be either turned on or you have to enable the network standby feature

# Basic API Usage

```
Avr1912 avr = new Avr1912(192.168.0.102, 23);
avr.connect(1000);
Slider masterVolume = avr.masterVolume();
masterVolume.slideUp();
avr.disconnect();
```

Take a look at the class Avr1912Demo.java in the test directory to view a complete example.

# Command line usage

```
java -jar denon4j-[version]-cli.jar 192.168.0.12 23
Enter a command ('?' for help, 'q' for quit):
> ?

The following options are available:

?			Prints this help
q			Quits the program
<cmd>		Executes a generic command e.g. PW?
PW?			Returns the power state
PWON		Turns power on

See the Denon AVR protocol for a full list of commands.
> MU?
OFF
```

# About the API

- All packages which have 'internal' in their name are not intended to be used from outside
- Changes to internal packages are made without further notice

# Building

This is a maven style project so `mvn` needs to be on your `$PATH`. To build
from scratch just type:

> mvn clean package

# License

Apache License Version 2.0, January 2004 (see LICENSE file)

# Contributing

1. Fork it.
2. Create a branch (`git checkout -b my_plugin`)
3. Commit your changes (`git commit -am "Added feature"`)
4. Push to the branch (`git push origin my_plugin`)
5. Create a new [Issue](https://github.com/sath1982/denon4j/issues/new) with a link to your branch, or just make a Pull Request.
6. Enjoy a refreshing Diet Coke and wait

# Denon AVR Control Protocol Specs

- [AVR-2112CI/AVR-1912](denon-avr-1912-protokoll.pdf)
