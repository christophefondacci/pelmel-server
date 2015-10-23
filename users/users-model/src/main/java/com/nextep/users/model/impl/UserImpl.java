package com.nextep.users.model.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Type;

import com.nextep.users.model.MutableUser;
import com.nextep.users.model.PushProvider;
import com.nextep.users.model.User;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@Table(name = "USERS")
public class UserImpl extends AbstractCalmObject implements User, MutableUser {

	private static final Log LOGGER = LogFactory.getLog(UserImpl.class);

	@Id
	@GeneratedValue
	@Column(name = "USER_ID", nullable = false, unique = true)
	private Long id;

	@Column(name = "PSEUDO")
	private String pseudo;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "PASSWORD")
	private String password;

	@Column(name = "CREDITS_COUNT")
	private int credits = 0;

	@Column(name = "USER_STATUS")
	private String status;

	@Column(name = "BIRTHDAY")
	private Date birthday = new Date();

	@Column(name = "HEIGHT")
	private Integer height;

	@Column(name = "WEIGHT")
	private Integer weight;

	@Column(name = "UNIT_SYSTEM")
	private String unitSystemCode;

	@Column(name = "ONLINE_TIMEOUT")
	private Date onlineTimeout = new Date();

	@Column(name = "GEO_LATITUDE")
	private Double latitude;

	@Column(name = "GEO_LONGITUDE")
	private Double longitude;

	@Column(name = "LAST_LOCATION_KEY")
	private String lastLocationKey;

	@Column(name = "LAST_LOCATION_DATE")
	private Date lastLocationTime;

	private transient ItemKey statLocationKey;

	@Column(name = "FACEBOOK_ID")
	private String facebookId;

	// @Column(name = "FACEBOOK_TOKEN")
	// private String facebookToken;
	private transient String token;

	@Column(name = "PUSH_PROVIDER")
	private String pushProvider;

	@Column(name = "PUSH_DEVICE_ID")
	private String pushDeviceId;

	@Column(name = "DEVICE_INFO")
	private String deviceInfo;

	@Column(name = "EMAIL_LAST_DATE")
	private Date lastEmailDate;

	@Column(name = "EMAIL_VALIDATION_TOKEN")
	private String emailValidationToken;

	@Column(name = "EMAIL_VALIDATED")
	private boolean emailValidated;

	@Column(name = "IS_ANONYMOUS")
	@Type(type = "yes_no")
	private boolean anonymous;

	private static final long serialVersionUID = 4236481091171015199L;

	public UserImpl() {
		super(null);
	}

	public UserImpl(ItemKey key) {
		super(key);
		this.id = key.getNumericId();
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public int getCredits() {
		return credits;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nextep.users.model.impl.MutableUser#setEmail(java.lang.String)
	 */
	@Override
	public void setEmail(String email) {
		this.email = email;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nextep.users.model.impl.MutableUser#setCredits(int)
	 */
	@Override
	public void setCredits(int credits) {
		this.credits = credits;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nextep.users.model.impl.MutableUser#setPseudo(java.lang.String)
	 */
	@Override
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	@Override
	public String getPseudo() {
		return pseudo;
	}

	@Override
	public ItemKey getKey() {
		if (id == null) {
			return null;
		} else {
			final ItemKey key = super.getKey();
			if (key == null) {
				try {
					return CalmFactory.createKey(CAL_TYPE, id);
				} catch (CalException e) {
					e.printStackTrace();
				}
			}
			return key;
		}
	}

	public Long getId() {
		return id;
	}

	@Override
	public String getStatusMessage() {
		return status;
	}

	@Override
	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public Date getBirthday() {
		return birthday;
	}

	@Override
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	@Override
	public Integer getHeightInCm() {
		return height;
	}

	@Override
	public void setHeightInCm(Integer height) {
		this.height = height;
	}

	@Override
	public Integer getWeightInKg() {
		return weight;
	}

	@Override
	public void setWeightInKg(Integer weight) {
		this.weight = weight;
	}

	@Override
	public void setUnitSystemCode(String unitSystemCode) {
		this.unitSystemCode = unitSystemCode;
	}

	@Override
	public String getUnitSystemCode() {
		return unitSystemCode;
	}

	@Override
	public Date getOnlineTimeout() {
		return onlineTimeout;
	}

	public void setOnlineTimeout(Date onlineTimeout) {
		this.onlineTimeout = onlineTimeout;
	}

	@Override
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public double getLongitude() {
		if (longitude != null) {
			return longitude;
		} else {
			return 0;
		}
	}

	@Override
	public double getLatitude() {
		if (latitude != null) {
			return latitude;
		} else {
			return 0;
		}
	}

	@Override
	public ItemKey getLastLocationKey() {
		if (lastLocationKey != null) {
			try {
				return CalmFactory.parseKey(lastLocationKey);
			} catch (CalException e) {
				LOGGER.error("Unable to parse last location key : " + lastLocationKey);
			}
		}
		return null;
	}

	@Override
	public Date getLastLocationTime() {
		return lastLocationTime;
	}

	@Override
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@Override
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@Override
	public void setLastLocationKey(ItemKey lastLocationKey) {
		this.lastLocationKey = lastLocationKey == null ? null : lastLocationKey.toString();
	}

	@Override
	public void setLastLocationTime(Date lastLocationTime) {
		this.lastLocationTime = lastLocationTime;
	}

	@Override
	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	@Override
	public String getFacebookId() {
		return facebookId;
	}

	// @Override
	// public void setFacebookToken(String facebookToken) {
	// this.facebookToken = facebookToken;
	// }
	//
	// @Override
	// public String getFacebookToken() {
	// return facebookToken;
	// }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserImpl other = (UserImpl) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public PushProvider getPushProvider() {
		try {
			return PushProvider.valueOf(pushProvider);
		} catch (IllegalArgumentException e) {
			return null;
		} catch (RuntimeException e) {
			return null;
		}
	}

	@Override
	public String getPushDeviceId() {
		return pushDeviceId;
	}

	@Override
	public void setPushProvider(PushProvider provider) {
		this.pushProvider = provider == null ? null : provider.name();
	}

	@Override
	public void setPushDeviceId(String pushDeviceId) {
		this.pushDeviceId = pushDeviceId;
	}

	@Override
	public ItemKey getStatLocationKey() {
		return statLocationKey;
	}

	@Override
	public void setStatLocationKey(ItemKey statLocationKey) {
		this.statLocationKey = statLocationKey;
	}

	@Override
	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	@Override
	public String getDeviceInfo() {
		return deviceInfo;
	}

	@Override
	public void setLastEmailDate(Date lastEmailDate) {
		this.lastEmailDate = lastEmailDate;
	}

	@Override
	public Date getLastEmailDate() {
		return lastEmailDate;
	}

	@Override
	public String getEmailValidationToken() {
		return emailValidationToken;
	}

	@Override
	public boolean isEmailValidated() {
		return emailValidated;
	}

	@Override
	public void setEmailValidationToken(String emailValidationToken) {
		this.emailValidationToken = emailValidationToken;
	}

	@Override
	public void setEmailValidated(boolean emailValidated) {
		this.emailValidated = emailValidated;
	}

	@Override
	public boolean isAnonymous() {
		return anonymous;
	}

	@Override
	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}
}
