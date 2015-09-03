package com.nextep.proto.spring;

import com.videopolis.calm.model.ItemKey;

public final class ContextHolder {

	private static final ThreadLocal<Boolean> isReadOnly = new ThreadLocal<Boolean>();
	private static final ThreadLocal<ItemKey> currentUser = new ThreadLocal<ItemKey>();

	public static void toggleReadonly() {
		isReadOnly.set(true);
	}

	public static void toggleWrite() {
		isReadOnly.set(false);
	}

	public static boolean isWritable() {
		final Boolean b = isReadOnly.get();
		if (b == null) {
			return false;
		} else {
			return !b;
		}
	}

	public static void setCurrentUserItemKey(ItemKey currentUserKey) {
		currentUser.set(currentUserKey);
	}

	public static ItemKey getCurrentUserItemKey() {
		return currentUser.get();
	}
}
