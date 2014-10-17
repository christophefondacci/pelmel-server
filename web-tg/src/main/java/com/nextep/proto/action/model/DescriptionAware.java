package com.nextep.proto.action.model;

import com.nextep.proto.blocks.ItemsListBoxSupport;

public interface DescriptionAware {

	void setDescriptionSupport(ItemsListBoxSupport descriptionSupport);

	ItemsListBoxSupport getDescriptionSupport();
}
