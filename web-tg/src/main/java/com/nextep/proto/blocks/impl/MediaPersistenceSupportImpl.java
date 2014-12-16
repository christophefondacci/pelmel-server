package com.nextep.proto.blocks.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;

import com.googlecode.pngtastic.core.PngOptimizer;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.media.model.Media;
import com.nextep.media.model.MutableMedia;
import com.nextep.proto.blocks.MediaPersistenceSupport;
import com.nextep.proto.services.StorageService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.users.model.User;
import com.opensymphony.xwork2.ActionContext;
import com.thebuzzmedia.imgscalr.Scalr;
import com.thebuzzmedia.imgscalr.Scalr.Method;
import com.thebuzzmedia.imgscalr.Scalr.Mode;
import com.videopolis.calm.model.ItemKey;

public class MediaPersistenceSupportImpl implements MediaPersistenceSupport {

	private static final Log log = LogFactory
			.getLog(MediaPersistenceSupportImpl.class);
	private static final String KEY_MEDIA_DEFAULT_TITLE = "media.panel.mediaDescDefault";
	private MessageSource messageSource;
	private CalPersistenceService mediaService;
	private StorageService storageService;
	private PngOptimizer pngOptimizer;
	private String localMediaPath;
	private String localMediaUrlPrefix;

	private int thumbMaxWidth;
	private int thumbMaxHeight;
	private int miniThumbMaxWidth, miniThumbMaxHeight;
	private int maxWidth, maxHeight;
	private int mobileMaxWidth, mobileMaxHeight, mobileMaxWidthLandscape,
			mobileMaxHeightLandscape;
	private int mobileMaxWidthHighDef, mobileMaxHeightHighDef,
			mobileMaxWidthHighDefLandscape, mobileMaxHeightHighDefLandscape;
	private int previewWidth, previewHeight;
	private boolean defaultAutoCrop = false;

	private ImageWriter getJpegWriter() throws IOException {
		// use IIORegistry to get the available services
		IIORegistry registry = IIORegistry.getDefaultInstance();
		// return an iterator for the available ImageWriterSpi for jpeg images
		Iterator<ImageWriterSpi> services = registry.getServiceProviders(
				ImageWriterSpi.class, new ServiceRegistry.Filter() {
					@Override
					public boolean filter(Object provider) {
						if (!(provider instanceof ImageWriterSpi))
							return false;

						ImageWriterSpi writerSPI = (ImageWriterSpi) provider;
						String[] formatNames = writerSPI.getFormatNames();
						for (int i = 0; i < formatNames.length; i++) {
							if (formatNames[i].equalsIgnoreCase("JPEG")) {
								return true;
							}
						}

						return false;
					}
				}, true);
		// ...assuming that servies.hasNext() == true, I get the first available
		// service.
		ImageWriterSpi writerSpi = services.next();
		ImageWriter writer = writerSpi.createWriterInstance();
		return writer;

	}

	@Override
	public Media createMedia(User author, ItemKey parentItemKey, File tmpFile,
			String filename, String contentType, String title, boolean isVideo,
			Integer firstMediaPriority) throws IOException {
		// Extracting extension
		int dotIndex = filename.lastIndexOf(".");
		String ext = "";
		if (dotIndex > 0) {
			ext = filename.substring(dotIndex);
		}

		// Copying media
		FileInputStream inputStream = new FileInputStream(tmpFile);
		final String originalFileName = writeStream(inputStream, parentItemKey,
				ext, contentType);

		final MutableMedia mutableMedia = (MutableMedia) mediaService
				.createTransientObject();
		mutableMedia.setOriginalUrl(getLocalMediaUrlPrefix(parentItemKey)
				+ originalFileName);
		mutableMedia.setVideo(isVideo);
		mutableMedia.setRelatedItemKey(parentItemKey);
		mutableMedia.setAuthorKey(author.getKey());
		mutableMedia.setUploadDate(new Date());

		// Handling priority
		if (firstMediaPriority != null) {
			mutableMedia.setPreferenceOrder(firstMediaPriority - 1);
		}
		final String defaultTitle = messageSource.getMessage(
				KEY_MEDIA_DEFAULT_TITLE, null, ActionContext.getContext()
						.getLocale());
		if (!defaultTitle.equals(title)) {
			mutableMedia.setTitle(title);
		}
		if (isVideo) {
			mutableMedia.setThumbUrl(localMediaUrlPrefix + "mpg.png");
		} else {
			generateThumb(mutableMedia, tmpFile);
		}
		ContextHolder.toggleWrite();
		mediaService.saveItem(mutableMedia);
		return mutableMedia;
	}

	@Override
	public void generateThumb(MutableMedia mutableMedia, File localFile) {
		if (localFile != null) {
			try {
				generateThumb(mutableMedia, new FileInputStream(localFile));
			} catch (FileNotFoundException e) {
				log.error("File not found: " + localFile.getAbsolutePath());
			}
		}
	}

	@Override
	public void generateThumb(MutableMedia mutableMedia, InputStream stream) {
		try {
			final BufferedImage srcImg = ImageIO.read(stream);
			final BufferedImage srcImage = new BufferedImage(srcImg.getWidth(),
					srcImg.getHeight(), BufferedImage.TYPE_INT_RGB);
			long start = System.currentTimeMillis();
			for (int x = 0; x < srcImg.getWidth(); x++) {
				for (int y = 0; y < srcImg.getHeight(); y++) {
					srcImage.setRGB(x, y, srcImg.getRGB(x, y));
				}
			}
			long fillTime = System.currentTimeMillis() - start;
			log.info("[" + fillTime + "ms] RGB conversion OK");

			mutableMedia.setOnline(true);
			mutableMedia.setOriginalWidth(srcImage.getWidth());
			mutableMedia.setOriginalHeight(srcImage.getHeight());

			// Generating image file, cropping as the user requested
			Integer cropX = mutableMedia.getCropX();
			Integer cropY = mutableMedia.getCropY();
			Integer cropW;
			Integer cropH;
			if (defaultAutoCrop) {
				cropW = mutableMedia.getCropWidth() == null ? maxWidth
						: mutableMedia.getCropWidth();
				cropH = mutableMedia.getCropHeight() == null ? maxHeight
						: mutableMedia.getCropHeight();
			} else {
				cropW = mutableMedia.getCropWidth();
				cropH = mutableMedia.getCropHeight();
			}
			String filename = generateThumb(mutableMedia, srcImage, maxWidth,
					maxHeight, cropX, cropY, cropW, cropH);
			final String localMediaUrlPrefix = getLocalMediaUrlPrefix(mutableMedia
					.getRelatedItemKey());
			if (filename != null) {
				mutableMedia.setUrl(localMediaUrlPrefix + filename);
			}

			// Generating thumb (croping if needed)
			String thumbFileName = generateThumb(mutableMedia, srcImage,
					thumbMaxWidth, thumbMaxHeight, null, null, thumbMaxWidth,
					thumbMaxHeight);
			if (thumbFileName != null) {
				mutableMedia.setThumbUrl(localMediaUrlPrefix + thumbFileName);
			}

			// Generating mini thumb (cropping if needed)
			thumbFileName = generateThumb(mutableMedia, srcImage,
					miniThumbMaxWidth, miniThumbMaxHeight, null, null,
					miniThumbMaxWidth, miniThumbMaxHeight);
			if (thumbFileName != null) {
				mutableMedia.setMiniThumbUrl(localMediaUrlPrefix
						+ thumbFileName);
			}
			// Generating mobile optimized file
			final int imgWidth = srcImage.getWidth();
			final int imgHeight = srcImage.getHeight();
			int resizedWidth, resizedHeight;
			int resizedWidthHighDef, resizedHeightHighDef;
			if (imgWidth < imgHeight) {
				resizedWidth = mobileMaxWidth;
				resizedHeight = mobileMaxHeight;
				resizedWidthHighDef = mobileMaxWidthHighDef;
				resizedHeightHighDef = mobileMaxHeightHighDef;
			} else {
				resizedWidth = mobileMaxWidthLandscape;
				resizedHeight = mobileMaxHeightLandscape;
				resizedWidthHighDef = mobileMaxWidthHighDefLandscape;
				resizedHeightHighDef = mobileMaxHeightHighDefLandscape;
			}
			// Standard definition
			thumbFileName = generateThumb(mutableMedia, srcImage, resizedWidth,
					resizedHeight, null, null, null, null);
			// resizedWidth, resizedHeight);
			if (thumbFileName != null) {
				mutableMedia.setMobileUrl(localMediaUrlPrefix + thumbFileName);
			}

			// High definition
			thumbFileName = generateThumb(mutableMedia, srcImage,
					resizedWidthHighDef, resizedHeightHighDef, null, null,
					null, null);
			// resizedWidthHighDef, resizedHeightHighDef);
			if (thumbFileName != null) {
				mutableMedia.setMobileUrlHighDef(localMediaUrlPrefix
						+ thumbFileName);
			}

		} catch (IOException e) {
			log.error(
					"Unable to generate thumb for parent item '"
							+ mutableMedia.getRelatedItemKey().toString()
							+ "': " + e.getMessage(), e);
			mutableMedia.setOnline(false);
		}
	}

	private String generateThumb(MutableMedia media, BufferedImage srcImage,
			int width, int height) throws IOException {
		return generateThumb(media, srcImage, width, height, null, null, null,
				null);
	}

	private String generateThumb(MutableMedia media, BufferedImage srcImage,
			int width, int height, Integer cX, Integer cY, Integer cropWidth,
			Integer cropHeight) throws IOException {
		// Generating thumbnail
		int myWidth = width;
		int myHeight = height;
		final ItemKey parentItemKey = media.getRelatedItemKey();

		if (srcImage != null) {
			log.info("Generating thumb for item '" + parentItemKey.toString()
					+ "'");
			final int imgWidth = srcImage.getWidth();
			final int imgHeight = srcImage.getHeight();
			final double imgRatio = (double) imgWidth / (double) imgHeight;
			final double cropRatio = (double) width / (double) height;
			// For crop fit we fit to the smallest side and then we crop
			// what steps over
			if (cropWidth != null) {

				// If we get the same ratio for crop and image : both have wider
				// widths or both have taller heights
				if ((cropRatio >= 1 && imgRatio >= 1)
						|| (cropRatio < 1 && imgRatio < 1)) {
					// If the image ratio is greater than the crop ratio (image
					// has wider width for a same height) we fit to height
					if (imgRatio >= cropRatio) {
						myWidth = -1;
					} else {
						// If the image ratio is smaller than the crop ratio
						// (image has a taller height for same width) we fit to
						// width
						myHeight = -1;
					}
				} else if (cropRatio >= 1 && imgRatio <= 1) {
					// If we need to crop a landscape inside a portrait image we
					// fit to width
					myHeight = -1;
				} else if (cropRatio <= 1 && imgRatio >= 1) {
					// If we need to crop a portrait inside a landscape image we
					// fit to height
					myWidth = -1;
				}

				// if (imgWidth >= imgHeight) {
				// if ((imgRatio < 1 && cropRatio >= 1)
				// || (imgRatio >= 1 && cropRatio < 1)) {
				// myWidth = -1;
				// } else {
				// myHeight = -1;
				// }
				// } else {
				// if ((imgRatio < 1 && cropRatio >= 1)
				// || (imgRatio >= 1 && cropRatio < 1)) {
				// myHeight = -1;
				// } else {
				// myWidth = -1;
				// }
				// }
			}
			if (srcImage != null) {
				BufferedImage thumb = null;
				if (myHeight == -1) {
					thumb = Scalr.resize(srcImage, Method.QUALITY,
							Mode.FIT_TO_WIDTH, width);
				} else if (myWidth == -1) {
					thumb = Scalr.resize(srcImage, Method.QUALITY,
							Mode.FIT_TO_HEIGHT, height);
				} else {
					thumb = Scalr.resize(srcImage, Method.QUALITY, width,
							height);
				}
				// We just take a subset of the image by cropping resulting
				// image
				Integer cropX = null, cropY = null;
				final int thumbWidth = thumb.getWidth();
				final int thumbHeight = thumb.getHeight();
				// If initial X and Y are null that means we need auto centering
				// of the cropping rect
				if (cX == null && cY == null) {
					// In this situtation we automatically center the crop rect
					if (myHeight == -1) {
						// We sized to fit width
						cropX = 0;
						// So we center the crop rect vertically by adjusting
						// cropY
						int heightDelta = thumbHeight - cropHeight;
						if (heightDelta < 0) {
							cropY = 0;
						} else {
							cropY = heightDelta / 2;
						}
					} else if (myWidth == -1) {
						// We sized to fit height
						cropY = 0;
						// So we center the crop rect horizontally by adjusting
						// cropX
						int widthDelta = thumbWidth - cropWidth;
						if (widthDelta < 0) {
							cropX = 0;
						} else {
							cropX = widthDelta / 2;
						}
					} else {
						cropX = 0;
						cropY = 0;
					}
				} else {
					cropX = cX;
					cropY = cY;
				}
				if (cropX != null && cropWidth != null
						&& cropX + cropWidth <= thumbWidth
						&& cropY + cropHeight <= thumbHeight) {
					try {
						thumb = thumb.getSubimage(cropX, cropY,
								Math.min(thumbWidth - cropX, cropWidth),
								Math.min(thumbHeight - cropY, cropHeight));
					} catch (RuntimeException e) {
						throw e;
					}
				}
				if (width == maxWidth && height == maxHeight) {
					media.setWidth(thumb.getWidth());
					media.setHeight(thumb.getHeight());
				}

				final String filename = writeFile(thumb, parentItemKey);
				return filename;
				// Optimizing PNG
				// PngImage pngImage = new
				// PngImage(thumbFile.getCanonicalPath());
				//
				// // Generating optimized output file
				// final File optimizedFile = new File(
				// getLocalMediaPath(parentItemKey),
				// parentItemKey.toString() + System.currentTimeMillis()
				// + "-o" + ".png");
				//
				// // Optimizing
				// pngOptimizer.optimize(pngImage,
				// optimizedFile.getAbsolutePath(), false, 9);
				// return optimizedFile.getName();

			}
		}

		return null;
	}

	/**
	 * Writes the given buffered image to the underlying storage.
	 * 
	 * @param thumb
	 *            the {@link BufferedImage} to write
	 * @param parentItemKey
	 *            the {@link ItemKey} of the item owning this photo
	 * @return the location of the stored image in the current storage
	 * @throws IOException
	 */
	private String writeFile(BufferedImage thumb, ItemKey parentItemKey)
			throws IOException {

		// Writing to our byte array
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final ImageWriter writer = getJpegWriter();
		writer.setOutput(ImageIO.createImageOutputStream(outputStream));

		// writes the file with given compression level
		// from your JPEGImageWriteParam instance
		final JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
		jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		jpegParams.setCompressionQuality(0.9f);
		writer.write(null, new IIOImage(thumb, null, null), jpegParams);

		return writeStream(
				new ByteArrayInputStream(outputStream.toByteArray()),
				parentItemKey, ".jpg");
	}

	/**
	 * Writes the provided input stream using the storage service
	 * 
	 * @param stream
	 *            the input to stream contents from
	 * @param parentItemKey
	 *            the {@link ItemKey} of the parent information (to compute
	 *            target location)
	 * @return the name of the information in the underlying storage
	 * @throws IOException
	 */
	private String writeStream(InputStream stream, ItemKey parentItemKey,
			String extension) throws IOException {
		return writeStream(stream, parentItemKey, extension, null);
	}

	private String writeStream(InputStream stream, ItemKey parentItemKey,
			String extension, String contentType) throws IOException {
		// Getting relative path
		final String path = getLocalMediaUrlPrefix(parentItemKey);
		final String filename = parentItemKey.toString()
				+ System.currentTimeMillis() + extension;

		// Now storing through storage
		final String fullPath = path.substring(1) + filename;

		storageService.writeStream(fullPath, contentType, stream);

		return filename;
	}

	@Override
	public File crop(MutableMedia m, File srcImage, int x, int y, int width,
			int height) {
		try {
			final BufferedImage img = ImageIO.read(srcImage);
			// Scaling everything from preview coords to original image coords
			final int originalWidth = img.getWidth();
			final int originalHeight = img.getHeight();

			// First obtaining the bounds of the preview image (= original ratio
			// within preview bounds)
			int pWidth, pHeight;

			if (originalWidth > originalHeight) {
				pWidth = previewWidth;
				pHeight = (int) (((double) previewWidth)
						/ ((double) originalWidth) * originalHeight);
			} else {
				pHeight = previewHeight;
				pWidth = (int) (((double) previewHeight)
						/ ((double) originalHeight) * originalWidth);
			}

			// Now scaling to preview-fitted bounds
			final double widthRatio = ((double) originalWidth)
					/ ((double) pWidth);
			final double heightRatio = ((double) originalHeight)
					/ ((double) pHeight);
			final int cropX = (int) (x * widthRatio);
			final int cropY = (int) (y * heightRatio);
			final int cropWidth = (int) (width * widthRatio);
			final int cropHeight = (int) (height * heightRatio);

			// Adjusting max because of rounded doubles computation and
			// approximations
			final int finalCropWidth = Math.min(originalWidth - cropX,
					cropWidth);
			final int finalCropHeight = Math.min(originalHeight - cropY,
					cropHeight);
			// Cropping with scaled dimensions
			final BufferedImage resultImg = img.getSubimage(cropX, cropY,
					finalCropWidth, finalCropHeight);
			m.setCropX(cropX);
			m.setCropY(cropY);
			m.setCropWidth(finalCropWidth);
			m.setCropHeight(finalCropHeight);

			final String mediaPath = getLocalMediaPath(m.getRelatedItemKey());
			final String mediaName = m.getRelatedItemKey().toString()
					+ System.currentTimeMillis() + ".png";
			final File fileDir = new File(mediaPath);
			fileDir.mkdirs();
			final File resultingFile = new File(mediaPath, mediaName);
			ImageIO.write(resultImg, "png", resultingFile);

			// Setting media width and height
			final String resizedFileName = generateThumb(m, resultImg,
					maxWidth, maxHeight);
			final File resizedFile = new File(mediaPath, resizedFileName);

			return resizedFile;
		} catch (IOException e) {
			log.error(
					"Unable to read / write a media file for crop operation : "
							+ e.getMessage(), e);
			return null;
		}
	}

	@Override
	public File getMediaLocalFile(String mediaUrl) {
		final String mediaLocation = mediaUrl.replace(localMediaUrlPrefix,
				localMediaPath);
		return new File(mediaLocation);
	}

	@Override
	public String getMediaUrl(File localMediaFile) {
		return localMediaFile.getPath().replace(localMediaPath,
				localMediaUrlPrefix);
	}

	private String getLocalMediaPath(ItemKey key) {
		final String subDir = getPathSuffix(key);
		return localMediaPath + subDir;
	}

	private String getLocalMediaUrlPrefix(ItemKey key) {
		final String subDir = getPathSuffix(key);
		return localMediaUrlPrefix + subDir;
	}

	private String getPathSuffix(ItemKey key) {
		final String idStr = key.getId().toString();
		final StringBuilder buf = new StringBuilder();
		final String separator = File.separator;
		for (int i = 0; i < Math.min(4, idStr.length()); i++) {
			buf.append(separator);
			buf.append(idStr.charAt(i));
		}
		buf.append(File.separator);
		return buf.toString();
	}

	public void setMediaService(CalPersistenceService mediaService) {
		this.mediaService = mediaService;
	}

	public void setLocalMediaPath(String localMediaPath) {
		if (localMediaPath.charAt(localMediaPath.length() - 1) == File.separatorChar) {
			this.localMediaPath = localMediaPath.substring(0,
					localMediaPath.length() - 1);
		} else {
			this.localMediaPath = localMediaPath;
		}
	}

	public void setLocalMediaUrlPrefix(String localMediaUrlPrefix) {
		this.localMediaUrlPrefix = localMediaUrlPrefix;
	}

	public void setThumbMaxHeight(int thumbMaxHeight) {
		this.thumbMaxHeight = thumbMaxHeight;
	}

	public void setThumbMaxWidth(int thumbMaxWidth) {
		this.thumbMaxWidth = thumbMaxWidth;
	}

	public void setMiniThumbMaxHeight(int miniThumbMaxHeight) {
		this.miniThumbMaxHeight = miniThumbMaxHeight;
	}

	public void setMiniThumbMaxWidth(int miniThumbMaxWidth) {
		this.miniThumbMaxWidth = miniThumbMaxWidth;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setPreviewHeight(int previewHeight) {
		this.previewHeight = previewHeight;
	}

	public void setPreviewWidth(int previewWidth) {
		this.previewWidth = previewWidth;
	}

	public void setMobileMaxHeight(int mobileMaxHeight) {
		this.mobileMaxHeight = mobileMaxHeight;
	}

	public void setMobileMaxHeightHighDef(int mobileMaxHeightHighDef) {
		this.mobileMaxHeightHighDef = mobileMaxHeightHighDef;
	}

	public void setMobileMaxWidth(int mobileMaxWidth) {
		this.mobileMaxWidth = mobileMaxWidth;
	}

	public void setMobileMaxWidthHighDef(int mobileMaxWidthHighDef) {
		this.mobileMaxWidthHighDef = mobileMaxWidthHighDef;
	}

	public void setMobileMaxWidthLandscape(int mobileMaxWidthLandscape) {
		this.mobileMaxWidthLandscape = mobileMaxWidthLandscape;
	}

	public void setMobileMaxHeightLandscape(int mobileMaxHeightLandscape) {
		this.mobileMaxHeightLandscape = mobileMaxHeightLandscape;
	}

	public void setMobileMaxWidthHighDefLandscape(
			int mobileMaxWidthHighDefLandscape) {
		this.mobileMaxWidthHighDefLandscape = mobileMaxWidthHighDefLandscape;
	}

	public void setMobileMaxHeightHighDefLandscape(
			int mobileMaxHeightHighDefLandscape) {
		this.mobileMaxHeightHighDefLandscape = mobileMaxHeightHighDefLandscape;
	}

	public void setDefaultAutoCrop(boolean defaultAutoCrop) {
		this.defaultAutoCrop = defaultAutoCrop;
	}

	public void setPngOptimizer(PngOptimizer pngOptimizer) {
		this.pngOptimizer = pngOptimizer;
	}

	public void setStorageService(StorageService storageService) {
		this.storageService = storageService;
	}
}
