package com.nextep.proto.blocks.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.nextep.descriptions.model.Description;
import com.nextep.proto.blocks.DescriptionsEditionSupport;
import com.videopolis.calm.model.CalmObject;

public class DescriptionsEditionSupportImpl implements
		DescriptionsEditionSupport {

	private Locale locale;
	private CalmObject parent;

	@Override
	public void initialize(Locale locale, CalmObject parent) {
		this.locale = locale;
		this.parent = parent;
	}

	@Override
	public String getParentId() {
		return parent.getKey().toString();
	}

	@Override
	public List<? extends Description> getDescriptions() {
		if (parent != null) {
			return parent.get(Description.class);
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public String getDescriptionLanguage(Description d) {
		return d.getLocale().getLanguage();
	}

	@Override
	public String getDescription(Description d) {
		return d.getDescription();
	}

	@Override
	public List<String> getAvailableLanguages() {
		return Arrays.asList("en", "fr", "de", "es", "pt", "jp");
	}

	@Override
	public String getLanguageName(String languageCode) {
		final Locale langLocale = new Locale(languageCode);
		return langLocale.getDisplayLanguage(locale);
	}

	@Override
	public String getLanguageIcon(String languageCode) {
		return "background-image: url('/images/flags/" + languageCode
				+ ".png');";
	}

	@Override
	public String getSelectedAttribute(String languageCode,
			String currentDescriptionLanguageCode) {
		final List<? extends Description> descs = getDescriptions();
		if (!descs.isEmpty()) {
			return languageCode.equals(currentDescriptionLanguageCode) ? "selected"
					: "";
		} else {
			return languageCode.equals(locale.getLanguage()) ? "selected" : "";
		}
	}

}
