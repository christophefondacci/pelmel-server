package com.videopolis.calm.model.impl;

import com.videopolis.calm.model.ItemKey;

/**
 * Default implementation of {@link ItemKey}
 *
 * @author julien
 *
 */
public class ItemKeyImpl implements ItemKey {
    /** serialization id */
    private static final long serialVersionUID = -617819539720819606L;
    /** Item type */
    private String type;
    /** Item unique id within the item type */
    private String id;

    @Override
    public String getId() {
	return id;
    }

    @Override
    public String getType() {
	return type;
    }

    @Override
    public void setId(final String id) {
	this.id = id;
    }

    @Override
    public void setType(final String type) {
	this.type = type;
    }

    @Override
    public String toString() {
	return type + id;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (id == null ? 0 : id.hashCode());
	result = prime * result + (type == null ? 0 : type.hashCode());
	return result;
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final ItemKeyImpl other = (ItemKeyImpl) obj;
	if (id == null) {
	    if (other.id != null) {
		return false;
	    }
	} else if (!id.equals(other.id)) {
	    return false;
	}
	if (type == null) {
	    if (other.type != null) {
		return false;
	    }
	} else if (!type.equals(other.type)) {
	    return false;
	}
	return true;
    }

    @Override
    public long getNumericId() {
	return Long.valueOf(id).longValue();
    }
}
