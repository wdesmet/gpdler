package net.straininfo2.grs.idloader.bioproject.eutils;

import net.straininfo2.grs.idloader.TargetIdExtractor;
import net.straininfo2.grs.idloader.bioproject.domain.mappings.Mapping;
import net.straininfo2.grs.idloader.bioproject.domain.mappings.Provider;

import java.util.Collection;
import java.util.Map;

/**
 * Implementors can implement their own method to store or process BioProject
 * LinkOut mappings.
 */
public interface MappingHandler {

    public void addMapping(long bioProjectId, Mapping mapping, TargetIdExtractor extractor);

    public void handleMappings(long bioProjectId, Collection<Mapping> mappings, Map<Provider, TargetIdExtractor> extractors);
}
