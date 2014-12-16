package com.nextep.proto.services.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.StorageObject;
import com.nextep.proto.services.StorageService;

public class GoogleCloudStorageServiceImpl implements StorageService {

	private static final Log LOGGER = LogFactory
			.getLog(GoogleCloudStorageServiceImpl.class);

	private Storage storage;
	private String accountId;
	private String privateKeyPath;
	private String appName;
	private String bucketName;
	private String mediaBaseUrl;

	public void init() throws IOException, GeneralSecurityException {
		HttpTransport httpTransport = new NetHttpTransport();
		JsonFactory jsonFactory = new JacksonFactory();

		List<String> scopes = new ArrayList<String>();
		scopes.add(StorageScopes.DEVSTORAGE_FULL_CONTROL);

		Credential credential = new GoogleCredential.Builder()
				.setTransport(httpTransport)
				.setJsonFactory(jsonFactory)
				.setServiceAccountId(accountId)
				.setServiceAccountPrivateKeyFromP12File(
						new File(privateKeyPath))
				.setServiceAccountScopes(scopes).build();

		storage = new Storage.Builder(httpTransport, jsonFactory, credential)
				.setApplicationName(appName).build();
	}

	@Override
	public void writeStream(String name, InputStream stream) throws IOException {

		writeStream(name, null, stream);
	}

	@Override
	public InputStream readStream(String name) throws IOException {
		final URL url = new URL(mediaBaseUrl + name);
		return url.openStream();
	}

	@Override
	public void writeStream(String name, String contentType, InputStream stream)
			throws IOException {
		// Creating our new object
		StorageObject object = new StorageObject();
		object.setBucket(bucketName);
		object.setName(name);
		object.setCacheControl("public, max-age=31536000");

		if (contentType == null) {
			contentType = URLConnection.guessContentTypeFromStream(stream);
		}
		InputStreamContent content = new InputStreamContent(contentType, stream);

		// Prerparing our insert request
		Storage.Objects.Insert insert = storage.objects().insert(bucketName,
				object, content);
		insert.setName(name);

		// For now we want files to be readable publicly, change this to
		// constraint visibility
		insert.setPredefinedAcl("publicread");

		// Optimization (without this uploads could take up to 25 secondes)
		insert.getMediaHttpUploader().setDisableGZipContent(true);
		insert.getMediaHttpUploader().setDirectUploadEnabled(true);

		// Processing upload
		long startTime = System.currentTimeMillis();
		LOGGER.info("[Cloud] Uploading '" + name + "' to Google cloud gs://"
				+ bucketName);
		insert.execute();
		long uploadTime = System.currentTimeMillis() - startTime;
		LOGGER.info("[Cloud] Uploaded successfully in " + uploadTime + "ms");

	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public void setPrivateKeyPath(String privateKeyPath) {
		this.privateKeyPath = privateKeyPath;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public void setMediaBaseUrl(String mediaBaseUrl) {
		this.mediaBaseUrl = mediaBaseUrl;
	}
}
