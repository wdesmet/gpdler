gpdler: download and parse genome project info
==============================================

gpdler is a maven based java module that makes it easy to parse and download
all of NCBI's bioproject (genome project) related information and most
importantly the links that are available in LinkOut. Its original goal is to
have a quick and dirty way to get a copy of all links submitted to LinkOut as
part of the Genomic Rosetta Stone. 

To this end, it contains an XML parser for the NCBI BioProject XML format (for
extra information), a nearly complete domain mapping of all entities described
in that format, and various utility classes to download LinkOut entries using
the eutils tools.

Usage
-----
Once you've cloned the repository, you should be able to simply build and run
the jar with:

    mvn package
    java -Dgrs.email=your@email.address -jar target/gpdler*-executable.jar

This will run the code with defaults, downloading all files from FTP and
mappings from LinkOut (which will take a *long* time, due to bandwidth
restrictions and the size of stuff necessary). The result is stored in a H2 DB
called "grsdb2.h2.db" in the current directory.

If you just want to use this download the data and do your own database
loading you can either extend the existing functionality or use standard
database tools to load the data from there into the database of your choice.

Known Issues
------------
Sometimes a connection might take a while to time out (even though both
connection and read timeouts have been set). The code can recover but
you might see some slowdown as a result.

Not everything has been mapping in the XML, but most of what is missing would
be easy to add.

Loading mappings is currently somewhat slow because it uses a transaction per
mapping.

License
-------
The code is licensed under the terms of the Apache License, Version 2.0. A
copy should be in the LICENSE file of this repository.
