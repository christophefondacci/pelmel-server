package com.videopolis.smaug.common.model;

/**
 * This interface defines a translated facet, which is literally a facet which
 * has a defined translation.
 * 
 * @author Christophe Fondacci
 * 
 */
public interface TranslatedFacet extends Facet {

    /**
     * Retrieves the translation of the facet.
     * 
     * @return the translation of this facet
     */
    String getTranslation();

}
