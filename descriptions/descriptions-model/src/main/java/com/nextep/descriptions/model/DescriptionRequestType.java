package com.nextep.descriptions.model;

import com.videopolis.calm.model.RequestType;

public interface DescriptionRequestType extends RequestType {

	int getCode();

	DescriptionRequestType SINGLE_DESC = new DescriptionRequestType() {
		private static final long serialVersionUID = 9194970921743515347L;

		@Override
		public int hashCode() {
			return getCode();
		};

		@Override
		public boolean equals(Object obj) {
			return (obj instanceof DescriptionRequestType)
					&& ((DescriptionRequestType) obj).getCode() == getCode();
		};

		@Override
		public int getCode() {
			return 1;
		};
	};

	DescriptionRequestType SORT_BY_LENGTH_DESC = new DescriptionRequestType() {
		private static final long serialVersionUID = -8762349014146586751L;

		@Override
		public int hashCode() {
			return getCode();
		};

		@Override
		public boolean equals(Object obj) {
			return (obj instanceof DescriptionRequestType)
					&& ((DescriptionRequestType) obj).getCode() == getCode();
		};

		@Override
		public int getCode() {
			return 2;
		};
	};
	DescriptionRequestType SORT_BY_DATE_DESC = new DescriptionRequestType() {

		private static final long serialVersionUID = -2489217633412968608L;

		@Override
		public int hashCode() {
			return getCode();
		};

		@Override
		public boolean equals(Object obj) {
			return (obj instanceof DescriptionRequestType)
					&& ((DescriptionRequestType) obj).getCode() == getCode();
		};

		@Override
		public int getCode() {
			return 3;
		};
	};
}
