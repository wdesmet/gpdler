package net.straininfo2.grs.idloader.bioproject.xmlparsing;

import net.straininfo2.grs.idloader.bioproject.bindings.TypePackage;

/**
 * Parsers of Packages from the bioproject XML can pass an implementation of
 * this interface to DocumentChunker to receive packages during XML parsing.
 *
 * @see DocumentChunker
 */
public interface PackageProcessor {

    public void processPackage(TypePackage nextPackage);

    /**
     * Called when last project has been parsed and passed on.
     */
    public void endParsing();

}
