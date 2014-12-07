package com.nextep.proto.blocks;

import java.util.List;
import java.util.Locale;

import com.nextep.descriptions.model.Description;
import com.nextep.proto.services.UrlService;
import com.videopolis.calm.model.CalmObject;

public interface DescriptionsEditionSupport {

	void initialize(Locale locale, CalmObject parent, UrlService urlService);

	String getParentId();

	List<? extends Description> getDescriptions();

	String getDescriptionLanguage(Description d);

	String getDescription(Description d);

	List<String> getAvailableLanguages();

	String getLanguageName(String languageCode);

	String getLanguageIcon(String languageCode);

	String getSelectedAttribute(String languageCode,
			String currentDescriptionLanguageCode);
}
