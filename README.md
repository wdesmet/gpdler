gpdler: download and parse genome project info
==============================================

gpdler is a maven based java module that makes it easy to parse and download
all of NCBI's genome project related information and most importantly the
links that are available in LinkOut. Its original goal is to have a quick and
dirty way to get a copy of all links submitted to LinkOut as part of the
Genomic Rosetta Stone.

Usage
-----
Once you've cloned the repository, you should be able to simply build the jar
with:

    mvn package
    java -Dgrs.email=your@email.address -jar idloader*-executable.jar

This will run the code with defaults, downloading all files from FTP and
mappings from LinkOut (which will take a *long* time). The result is stored
in an apache derby DB called "grsdb" in the current directory. You can run the
genomic rosetta stone web application against this database (once it's
published).

If you just want to use this download the data and do your own database
loading you can either extend the existing functionality or take a look at the
code in `net.straininfo2.grs.idloader.bioproject.eutils.DumpTest`.

Known Issues
------------
Sometimes a connection might take a while to time out (even though both
connection and read timeouts have been set). The code can recover but
you might see some slowdown as a result.

License
-------
The code is licensed under the terms of the Apache License, Version 2.0. A
copy should be in the LICENSE file of this repository.
