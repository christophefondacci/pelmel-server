package com.nextep.proto.apis.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.nextep.messages.model.MessageRecipientsGroup;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.ApisCustomAdapter;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * This custom adapter provides the list of {@link ItemKey} associated with a
 * {@link MessageRecipientsGroup} so that users from a group can be retrieved in
 * an APIS query.
 * 
 * @author cfondacci
 *
 */
public class ApisMessageRecipientsGroupItemKeyAdapter implements
		ApisCustomAdapter {

	@Override
	public List<ItemKey> adapt(ApisContext context, CalmObject... parents) {
		// Only supported use case: 1 parent of type recipients group
		if (parents.length == 1 && parents[0] instanceof MessageRecipientsGroup) {
			return new ArrayList<ItemKey>(
					((MessageRecipientsGroup) parents[0]).getRecipients());
		}
		return Collections.emptyList();
	}
}
