package com.nextep.advertising.model.impl;

import com.videopolis.calm.model.RequestType;

public class AdvertisingRequestTypes {

	public static final RequestType USER_SUBSCRIPTIONS = new RequestType() {
		private static final long serialVersionUID = -6872436155017531972L;

		@Override
		public boolean equals(Object obj) {
			return obj == USER_SUBSCRIPTIONS;
		};
	};
	public static final RequestType USER_BANNERS = new RequestType() {
		private static final long serialVersionUID = 1L;

		@Override
		public boolean equals(Object obj) {
			return obj == USER_BANNERS;
		};
	};

}
