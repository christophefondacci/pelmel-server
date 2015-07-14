//package com.nextep.messages.model.impl;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.Id;
//import javax.persistence.IdClass;
//import javax.persistence.Table;
//
//import com.videopolis.calm.factory.CalmFactory;
//import com.videopolis.calm.model.ItemKey;
//
//@Entity
//@IdClass()
//@Table(name = "MESSAGES_RECIPIENTS_ITEMS")
//public class MessageRecipientsItemKeyImpl implements ItemKey {
//
//	@Id
//	@Column(name = "MRCPT_ID")
//	private int recipientsGroupId;
//
//	@Id
//	@Column(name = "ITEM_KEY")
//	private String itemKey;
//
//	@Override
//	public String getType() {
//		final ItemKey key = CalmFactory.parseKey(itemKey);
//		return key.getType();
//	}
//
//	@Override
//	public void setType(String type) {
//		throw new UnsupportedOperationException("setType() not supported");
//	}
//
//	@Override
//	public String getId() {
//		final ItemKey key = CalmFactory.parseKey(itemKey);
//		return key.getId();
//	}
//
//	@Override
//	public void setId(String id) {
//		throw new UnsupportedOperationException("setType() not supported");
//	}
//
//	@Override
//	public long getNumericId() {
//		final ItemKey key = CalmFactory.parseKey(itemKey);
//		return key.getNumericId();
//	}
//
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((itemKey == null) ? 0 : itemKey.hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		MessageRecipientsItemKeyImpl other = (MessageRecipientsItemKeyImpl) obj;
//		if (itemKey == null) {
//			if (other.itemKey != null)
//				return false;
//		} else if (!itemKey.equals(other.itemKey))
//			return false;
//		return true;
//	}
//
// }
