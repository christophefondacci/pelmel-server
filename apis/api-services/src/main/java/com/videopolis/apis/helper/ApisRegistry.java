package com.videopolis.apis.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.videopolis.apis.model.ApisRequest;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.service.CalService;

/**
 * The APIS registry. This class holds references to all services currently
 * handled by APIS and provides convenience conversion methods to translate
 * classes to type and type to classes
 * 
 * @author Christophe Fondacci
 * 
 */
public final class ApisRegistry {

	/** {@link CalService} hashed by their type */
	private static Map<String, CalService> typedServiceMap = new HashMap<String, CalService>();
	private static Map<CalService, String> serviceTypeMap = new HashMap<CalService, String>();
	private static Map<Class<? extends CalmObject>, String> classTypeMap = new HashMap<Class<? extends CalmObject>, String>();
	private static Map<String, Class<? extends CalmObject>> typeClassMap = new HashMap<String, Class<? extends CalmObject>>();

	private ApisRegistry() {
	}

	/**
	 * Registers a common abstract layer service to the aggregated layer. Once
	 * registered, the service becomes eligible to be called by the aggregated
	 * layer (APIS) when an {@link ApisRequest} reference the <i>type</i> that
	 * this provided {@link CalService} handled (
	 * {@link CalService#getProvidedType()} )
	 * 
	 * @param calService
	 *            the {@link CalService} to register
	 */
	public static void registerCalService(CalService calService) {
		if (calService == null) {
			throw new IllegalArgumentException(
					"Cannot register a null CalService");
		}
		final String providedType = calService.getProvidedType();
		typedServiceMap.put(providedType, calService);
		serviceTypeMap.put(calService, providedType);
		classTypeMap.put(calService.getProvidedClass(), providedType);
		typeClassMap.put(providedType, calService.getProvidedClass());
		if (calService.getSupportedInputTypes() != null) {
			for (String inputType : calService.getSupportedInputTypes()) {
				typedServiceMap.put(inputType, calService);
			}
		}
	}

	/**
	 * Retrieves the {@link CalService} registered for this type. This method is
	 * designed for internal use of the APIS framework. <br>
	 * <b>Warning:</b> This method is for internal use of the APIS framework.
	 * 
	 * @param type
	 *            type of the {@link CalService} to retrieve
	 * @return the {@link CalService}
	 * 
	 */
	public static CalService getCalService(String type) {
		final CalService calService = typedServiceMap.get(type);
		if (calService == null) {
			throw new IllegalArgumentException(
					"No Cal service has been defined for type: " + type);
		}
		return calService;
	}

	/**
	 * Retrieves the type associated with the specified model class.
	 * 
	 * @param typeClass
	 *            model class of desired elements
	 * @return the type string for CAL services call
	 */
	public static String getTypeFromModel(Class<? extends CalmObject> typeClass) {
		return classTypeMap.get(typeClass);
	}

	/**
	 * Retrieves the model class associated with the specified type.
	 * 
	 * @param type
	 *            type string for CAL services call
	 * @return the model class
	 */
	public static Class<? extends CalmObject> getModelFromType(String type) {
		return typeClassMap.get(type);
	}

	/**
	 * Returns all the declared model classes
	 * 
	 * @return Model classes
	 */
	public static Set<Class<? extends CalmObject>> getDeclaredModelClasses() {
		return classTypeMap.keySet();
	}

	/**
	 * Retrieves the type provided by the specified service. This method should
	 * be preferred to <code>service.getProvidedType()</code> as calling the
	 * service may trigger remote calls to get this information.<br>
	 * This method caches the type provided by all registered service to avoid
	 * performance and network overheads when a caller need to get the CAL type
	 * associated to a service.
	 * 
	 * @param service
	 *            {@link CalService} to get the CAL type for
	 * @return the associated CAL type or <code>null</code> if this
	 *         {@link CalService} has not been registered
	 */
	public static String getTypeForCalService(CalService service) {
		return serviceTypeMap.get(service);
	}
}
