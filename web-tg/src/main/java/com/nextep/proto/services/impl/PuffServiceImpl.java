package com.nextep.proto.services.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.proto.services.PuffService;
import com.nextep.users.model.User;
import com.videopolis.calm.model.ItemKey;

public class PuffServiceImpl implements PuffService {
	private static final Log LOGGER = LogFactory.getLog("PUFF");
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private final MessageDigest digester;
	private String partnerId;

	public PuffServiceImpl() throws NoSuchAlgorithmException {
		digester = MessageDigest.getInstance("SHA-1");
	}

	@Override
	public boolean log(ItemKey elementKey, String fieldCode,
			String currentValue, String newValue, Locale locale, User author) {
		return logPuff(elementKey, fieldCode, currentValue, newValue, locale,
				(author == null ? "" : "author=" + author.getKey()));
	}

	@Override
	public boolean logPuff(ItemKey elementKey, String fieldCode,
			String currentValue, String newValue, Locale locale, String extra) {
		if (newValue != null && !newValue.equals(currentValue)) {
			StringBuilder buf = new StringBuilder();
			buf.append(partnerId);
			buf.append(';');
			buf.append(elementKey.toString());
			buf.append(';');
			buf.append(quote(fieldCode));
			buf.append(';');
			buf.append(DATE_FORMAT.format(new Date()));
			buf.append(";I;");
			byte[] hash = digester.digest(newValue.getBytes());
			// Outputting an hexadecimal conversion of this byte array
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xFF & hash[i]);
				if (hex.length() == 1) {
					// could use a for loop, but we're only dealing with a
					// single byte
					buf.append('0');
				}
				buf.append(hex);
			}
			buf.append(';');
			buf.append(quote(newValue));
			buf.append(';');
			buf.append(locale == null ? "" : locale.getLanguage());
			buf.append(';');
			buf.append(locale == null ? "" : locale.getCountry());
			buf.append(';');
			LOGGER.info(buf.toString());
			return true;
		}
		return false;
	}

	private String quote(String value) {
		return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}
}
