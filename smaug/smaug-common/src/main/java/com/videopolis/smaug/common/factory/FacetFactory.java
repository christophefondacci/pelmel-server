package com.videopolis.smaug.common.factory;

import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.FacetRange;
import com.videopolis.smaug.common.model.impl.FacetImpl;
import com.videopolis.smaug.common.model.impl.FacetRangeImpl;
import com.videopolis.smaug.common.model.impl.PlaceholderFacetRangeImpl;

/**
 * A Factory used to create {@link Facet} instances
 *
 * @author julien
 *
 */
public final class FacetFactory {

	private FacetFactory() {
	}

	/**
	 * Creates a {@link Facet}
	 *
	 * @param facetCategory
	 *            Facet category
	 * @param facetCode
	 *            Facet code
	 * @return {@link Facet}
	 */
	public static Facet createFacet(final FacetCategory facetCategory,
			final String facetCode) {
		final FacetImpl facet = new FacetImpl();
		facet.setFacetCategory(facetCategory);
		facet.setFacetCode(facetCode);
		return facet;
	}

	/**
	 * Creates a {@link FacetRange}
	 *
	 * @param facetCategory
	 *            Facet category
	 * @param format
	 *            Format of the code (as in String.format method), used to
	 *            generate the facet's code given the two bounds
	 * @param lowerBound
	 *            Range's lower bound
	 * @param higherBound
	 *            Range's upper bound
	 * @return {@link FacetRange}
	 */
	public static FacetRange createFacetRange(
			final FacetCategory facetCategory, final String format,
			final long lowerBound, final long higherBound) {
		final FacetRangeImpl facet = new FacetRangeImpl();
		facet.setFacetCategory(facetCategory);
		facet.setLowerBound(lowerBound);
		facet.setHigherBound(higherBound);
		facet.setRangeFormat(format);
		return facet;
	}

	public static FacetRange createFacetRange(FacetCategory facetCategory,
			String lowerBound, String higherBound) {
		final FacetRangeImpl facet = new FacetRangeImpl();
		facet.setFacetCategory(facetCategory);
		facet.setLowerBoundExpr(lowerBound);
		facet.setHigherBoundExpr(higherBound);
		return facet;
	}

	/**
	 * Creates a {@link FacetRange} which has the same category & format that
	 * another {@link FacetRange}, but differents boundaries
	 *
	 * @param originalRange
	 *            The original range
	 * @param lowerBound
	 *            The new lower bound
	 * @param higherBound
	 *            The new higher bound
	 * @return The new {@link FacetRange}
	 */
	public static FacetRange createFacetRange(final FacetRange originalRange,
			final long lowerBound, final long higherBound) {
		final FacetRangeImpl facet = new FacetRangeImpl();
		facet.setFacetCategory(originalRange.getFacetCategory());
		facet.setRangeFormat(originalRange.getRangeFormat());
		facet.setLowerBound(lowerBound);
		facet.setHigherBound(higherBound);
		return facet;
	}

	/**
	 * <p>
	 * Creates a {@link FacetRange} which uses placeholders as lower/higher
	 * bound codes.
	 * </p>
	 * <p>
	 * This is intended to be used to generate URLs with placeholders, to
	 * dynamically inject values (who said price slider?)
	 * </p>
	 *
	 * @param facetCategory
	 *            Facet category
	 * @param lowerBoundPlaceholder
	 *            Lower bound place holder
	 * @param higherBoundPlaceholder
	 *            Higher bound place holder
	 * @return {@link FacetRange}
	 */
	public static FacetRange createPlaceHodlerFacetRange(
			final FacetCategory facetCategory,
			final String lowerBoundPlaceholder,
			final String higherBoundPlaceholder) {
		final PlaceholderFacetRangeImpl facet = new PlaceholderFacetRangeImpl(
				lowerBoundPlaceholder, higherBoundPlaceholder);
		facet.setFacetCategory(facetCategory);
		return facet;
	}
}
