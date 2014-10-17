package com.nextep.proto.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

import com.nextep.cal.util.model.Named;
import com.nextep.descriptions.model.Description;
import com.nextep.geo.model.AlternateName;
import com.nextep.users.model.User;
import com.opensymphony.xwork2.ActionContext;
import com.videopolis.calm.model.CalmObject;

/**
 * This class helps with displaying information
 * 
 * @author cfondacci
 * 
 */
public final class DisplayHelper {

	private static final String TRANSLATION_HEIGHT = "height.label";
	private static final String TRANSLATION_WEIGHT = "weight.label";

	private DisplayHelper() {
	}

	public static String getName(CalmObject o) {
		return getName(o, ActionContext.getContext().getLocale());
	}

	public static String getName(CalmObject o, Locale l) {
		if (o != null) {
			final String language = l.getLanguage();
			final List<? extends AlternateName> alternates = o
					.get(AlternateName.class);
			if (alternates != null) {
				for (AlternateName alternate : alternates) {
					if (alternate.getLanguage().equals(language)) {
						return alternate.getAlternameName();
					}
				}
			}
		}
		if (o instanceof Named) {
			return ((Named) o).getName();
		} else if (o instanceof User) {
			return ((User) o).getPseudo();
		} else {
			return "";
		}
	}

	@Deprecated
	public static String getNoMiniThumbUrl() {
		return MediaHelper.getNoThumbUrl();
	}

	@Deprecated
	public static String getNoMiniThumbUserUrl() {
		return MediaHelper.getNoThumbUserUrl();
	}

	public static String getDefaultAjaxContainer() {
		return "mainContent";
	}

	public static String getHeight(MessageSource messageSource,
			Integer heightInCm, Locale l) {
		if (heightInCm != null) {
			double feetVal = Math.floor(heightInCm * 0.0328084d);
			double inchVal = Math.round(heightInCm * 0.3937008 - feetVal * 12);
			return messageSource.getMessage(TRANSLATION_HEIGHT, new Object[] {
					heightInCm, feetVal, inchVal }, l);
		} else {
			return "n/a";
		}
	}

	public static String getWeight(MessageSource messageSource,
			Integer weightInKg, Locale l) {
		if (weightInKg != null) {
			double lbsVal = Math.round(weightInKg * 2.204623);
			return messageSource.getMessage(TRANSLATION_WEIGHT, new Object[] {
					weightInKg, lbsVal }, l);
		} else {
			return "n/a";
		}
	}

	public static Description getMainLocaleDescription(CalmObject o,
			Locale locale) {
		Description description = null;
		if (o != null) {
			final List<? extends Description> descriptions = o
					.get(Description.class);
			if (descriptions != null) {
				for (Description d : descriptions) {
					if (locale.getLanguage()
							.equals(d.getLocale().getLanguage())) {
						description = d;
						break;
					}
				}
			}
		}
		return description;
	}

	/**
	 * Provides the most appropriate single description for the specified
	 * locale. It will look for a description in the provided language, if not
	 * available it will fallback on english, if not available it will fallback
	 * to the first available description and if no description can be found it
	 * will return <code>null</code>
	 * 
	 * @param o
	 *            the object for which we want a description
	 * @param l
	 *            the current language
	 * @return the most appropriate single description for this element or <
	 */
	public static Description getSingleDescription(CalmObject o, Locale l) {
		// Building the ordered list on which we'll base our description search
		final List<String> languages = Arrays.asList(l.getLanguage(), "en",
				null);
		for (String language : languages) {
			final Description d = getLocaleDescription(o, language);
			if (d != null) {
				return d;
			}
		}
		return null;
	}

	private static Description getLocaleDescription(CalmObject o,
			String language) {
		final List<? extends Description> descs = o.get(Description.class);
		if (descs == null) {
			return null;
		} else {
			for (Description d : descs) {
				if (language == null
						|| d.getLocale().getLanguage().equals(language)) {
					return d;
				}
			}
			return null;
		}
	}

	/**
	 * Builds a list of page numbers for the specified pagination information.
	 * This method will generate appropriate dots (...) to split page numbers,
	 * leaving specified leading and trailing pages around the current page,
	 * handling start and end sequences.
	 * 
	 * @param pageCount
	 *            total number of pages in the pagination
	 * @param currentPage
	 *            current page number (starts from 1)
	 * @param pagesBeforeAfter
	 *            number of pages that MUST be displayed before and after the
	 *            current page
	 * @return a list of the appropriate page number sequence where
	 *         <code>null</code> represents the dots.
	 */
	public static List<Integer> buildPagesList(int pageCount, int currentPage,
			int pagesBeforeAfter) {
		final List<Integer> pagesList = new ArrayList<Integer>();
		final List<Integer> pagesTail = new ArrayList<Integer>();
		int start = 1;
		int end = pageCount;

		// When current page is under 2xPAGES SIZE
		if (currentPage <= pagesBeforeAfter + 1) {
			// We display a 2xPAGES SIZE block so that the number of displayed
			// page numbers remain constant
			start = 1;
			end = Math.min(2 * pagesBeforeAfter, pageCount);
			// Optionnally we add the last page if above the displayed pages
			if (pageCount > end) {
				pagesTail.add(null);
				pagesTail.add(pageCount);
			}
		} else if (currentPage >= pageCount - pagesBeforeAfter - 1) {
			// When current page is near the MAX - 2xPAGES SIZE
			// We display a 2xPAGES SIZE block (from the end) so that the number
			// of displayed page numbers remain constant
			start = Math.max(1, pageCount - 2 * pagesBeforeAfter);
			end = pageCount;
			// We know that we need to display the start page if inferior to
			// start
			if (start > 1) {
				pagesList.add(1);
			}
			if (start > 2) {
				pagesList.add(null);
			}
		} else {
			// Else we are in between, needing :
			// first page ... pageslist ... last page
			start = currentPage - pagesBeforeAfter;
			end = currentPage + pagesBeforeAfter;
			pagesList.add(1);
			pagesList.add(null);
			pagesTail.add(null);
			pagesTail.add(pageCount);
		}

		for (int i = start; i <= end; i++) {
			pagesList.add(i);
		}
		pagesList.addAll(pagesTail);
		return pagesList;
	}

	/**
	 * Transforms the provided string into a title compliant string (typically a
	 * string with first-character upper-cased).
	 * 
	 * @param title
	 *            the string to transform
	 * @return the proper title string
	 */
	public static String titleCased(String title) {
		// We force the uppercase of first letter in resulting title
		if (title != null && !title.isEmpty()) {
			final String firstLetter = title.substring(0, 1).toUpperCase();
			title = firstLetter + title.substring(1);
		}
		return title;
	}
}
