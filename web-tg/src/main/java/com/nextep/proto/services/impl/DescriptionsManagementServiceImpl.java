package com.nextep.proto.services.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.nextep.cal.util.helpers.CalHelper;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.descriptions.model.Description;
import com.nextep.descriptions.model.MutableDescription;
import com.nextep.proto.exceptions.GenericWebappException;
import com.nextep.proto.services.DescriptionsManagementService;
import com.nextep.proto.services.PuffService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.users.model.User;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public class DescriptionsManagementServiceImpl implements
		DescriptionsManagementService {

	private static final String PUFF_FIELD_DESC = "description";
	private static final String DEFAULT_SOURCE_ID = "1000";

	private CalPersistenceService descriptionService;
	private PuffService puffService;

	@Override
	public boolean updateDescriptions(User author, CalmObject parent,
			String[] descriptionLanguageCodes, String[] descriptionItemKeys,
			String[] descriptions, String[] descriptionSourceIds)
			throws GenericWebappException {
		return updateDescriptions(author, PUFF_FIELD_DESC, parent,
				descriptionLanguageCodes, descriptionItemKeys, descriptions,
				descriptionSourceIds);
	}

	@Override
	public boolean updateDescriptions(User author, String descFieldCode,
			CalmObject parent, String[] descriptionLanguageCodes,
			String[] descriptionItemKeys, String[] descriptions,
			String[] descriptionSourceIds) throws GenericWebappException {
		return updateDescriptions(author, descFieldCode, parent,
				descriptionLanguageCodes, descriptionItemKeys, descriptions,
				descriptionSourceIds, true);
	}

	private boolean updateDescriptions(User author, String descFieldCode,
			CalmObject parent, String[] descriptionLanguageCodes,
			String[] descriptionItemKeys, String[] descriptions,
			String[] descriptionSourceIds, boolean allDescriptions)
			throws GenericWebappException {
		final List<? extends Description> descriptionsList = parent
				.get(Description.class);
		// Hashing properties by their key for easy lookup
		final Map<String, Description> descriptionsKeyMap = new HashMap<String, Description>();
		for (Description d : descriptionsList) {
			descriptionsKeyMap.put(d.getKey().toString(), d);
		}
		// Checking that we got same number of arguments in each array
		if (descriptionItemKeys != null
				&& descriptionLanguageCodes != null
				&& (descriptionItemKeys.length != descriptionLanguageCodes.length || descriptionItemKeys.length != descriptions.length)) {
			throw new GenericWebappException(
					"Cannot update properties: incorrect number of arguments - keys:"
							+ descriptionItemKeys.length + " vs languages:"
							+ descriptionLanguageCodes.length
							+ " vs descriptions:" + descriptions.length);
		}
		boolean descriptionsChanged = false;
		if (descriptionItemKeys != null && descriptionLanguageCodes != null) {
			// Processing
			final ItemKey parentKey = parent.getKey();
			// Clearing descriptions
			try {
				if (allDescriptions) {
					descriptionService.setItemFor(parentKey);
				} else {
					if (descriptionItemKeys.length > 0
							&& descriptionItemKeys[0] != null
							&& !descriptionItemKeys[0].isEmpty()) {
						// Clearing only description that we will update
						List<ItemKey> descriptionKeys = CalHelper
								.wrapItemKeys(Arrays
										.asList(descriptionItemKeys));
						descriptionService.setItemFor(parentKey,
								descriptionKeys
										.toArray(new ItemKey[descriptionKeys
												.size()]));
					}
				}
			} catch (CalException e) {
				throw new GenericWebappException(
						"Unable to update user properties: " + e.getMessage(),
						e);
			}
			final Map<String, MutableDescription> descriptionsMap = new HashMap<String, MutableDescription>();
			for (int i = 0; i < descriptionLanguageCodes.length; i++) {
				final String key = descriptionItemKeys[i];
				final String languageCode = descriptionLanguageCodes[i];
				final String value = descriptions[i];
				final String sourceId = descriptionSourceIds != null
						&& descriptionSourceIds.length > i ? descriptionSourceIds[i]
						: DEFAULT_SOURCE_ID;
				final Locale l = new Locale(languageCode);
				final String descKey = languageCode + "_" + sourceId;
				boolean concatenateDesc = false;
				if (!"0".equals(languageCode)) {
					MutableDescription description = null;
					boolean hasChanged = false;
					if (key != null && !"0".equals(key) && !key.isEmpty()) {
						final Description oldDescription = descriptionsKeyMap
								.get(key);
						description = (MutableDescription) descriptionService
								.createTransientObject();
						if (oldDescription != null) {
							hasChanged = !oldDescription.getLocale()
									.getLanguage().equals(languageCode);
						}
					} else {
						// Have we got a description for this language / source
						// already ?
						description = descriptionsMap.get(descKey);
						if (description == null) {
							// If no, we have a new description
							description = (MutableDescription) descriptionService
									.createTransientObject();
						} else {
							concatenateDesc = true;
						}
					}

					// Updating description
					if (value != null && !"".equals(value.trim())) {
						// Logging PUFF change
						hasChanged |= puffService.log(parentKey,
								PUFF_FIELD_DESC, hasChanged ? null
										: description.getDescription(), value,
								l, author);
						descriptionsChanged |= hasChanged;
						description.setLocale(l);
						if (concatenateDesc) {
							final String previousDesc = description
									.getDescription();
							description.setDescription(value
									+ (previousDesc == null ? "" : ". "
											+ previousDesc));
						} else {
							description.setDescription(value);
						}
						description.setDescribedItemKey(parentKey);
						description.setDate(new Date());
						if (sourceId != null) {
							description.setSourceId(Integer.valueOf(sourceId));
						}
						// Storing
						ContextHolder.toggleWrite();
						descriptionService.saveItem(description);
						// Registering description map
						descriptionsMap.put(descKey, description);
					}
				}
			}
		}
		// Returning our change flag
		return descriptionsChanged;
	}

	@Override
	public boolean updateSingleDescription(User author, CalmObject parent,
			String[] descriptionLanguageCodes, String[] descriptionItemKeys,
			String[] descriptions, String[] descriptionSourceIds)
			throws GenericWebappException {
		//
		return updateDescriptions(author, PUFF_FIELD_DESC, parent,
				descriptionLanguageCodes, descriptionItemKeys, descriptions,
				descriptionSourceIds, false);
	}

	public void setDescriptionService(CalPersistenceService descriptionService) {
		this.descriptionService = descriptionService;
	}

	public void setPuffService(PuffService puffService) {
		this.puffService = puffService;
	}

}
