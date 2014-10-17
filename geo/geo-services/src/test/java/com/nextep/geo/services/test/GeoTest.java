package com.nextep.geo.services.test;

import java.util.List;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.nextep.geo.model.Admin;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Continent;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.impl.AdmImpl;
import com.nextep.geo.model.impl.CityImpl;
import com.nextep.geo.model.impl.CountryImpl;
import com.nextep.geo.services.GeoService;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.ItemKey;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:META-INF/geo-services/testContext.xml" })
public class GeoTest {

	private final static Log log = LogFactory.getLog(GeoTest.class);
	@Autowired
	private GeoService geoService;

	@Test
	public void testListContinents() {
		System.out.println(">>>>>>>>> LISTING CONTINENTS <<<<<<<<<<");
		List<GeographicItem> continents = geoService.listContinents();
		for (GeographicItem continent : continents) {
			Assert.assertNotNull("Continent must have a name",
					continent.getName());
			final ItemKey key = continent.getKey();
			Assert.assertNotNull("Continent must have a key", key);
			Assert.assertEquals("Continent must have a key of type continent",
					Continent.CAL_ID, key.getType());
			System.out.println("==============================");
			System.out.println("Continent name : " + continent.getName());
			System.out.println("Continent key : " + continent.getKey());
		}
	}

	@Test
	public void testListCountries() {
		System.out.println("");
		System.out.println(">>>>>>>>> LISTING COUNTRIES <<<<<<<<<<");
		// Listing countries for every continent
		List<GeographicItem> continents = geoService.listContinents();
		for (GeographicItem continent : continents) {
			System.out.println(">>>>>>>>> CONTINENT " + continent.getName());
			final List<GeographicItem> countries = geoService
					.listCountries(continent);
			for (GeographicItem country : countries) {
				Assert.assertNotNull("Country must have a name",
						country.getName());
				final ItemKey key = country.getKey();
				Assert.assertNotNull("Country must have a key", key);
				Assert.assertEquals("Country must have a key of type country",
						CountryImpl.CAL_ID, key.getType());
				System.out.println("Country key/name : " + country.getKey()
						+ "/" + country.getName());
			}
		}
	}

	@Test
	public void testGetCountry() {
		System.out.println("");
		System.out.println(">>>>>>>>> GET COUNTRY <<<<<<<<<<");
		final GeographicItem france = geoService.getCountry("FR");
		Assert.assertNotNull(france);
		Assert.assertNotNull(france.getName());
		Assert.assertNotNull(france.getKey());
		System.out.println("Country key/name : " + france.getKey() + "/"
				+ france.getName());
	}

	@Test
	public void testGetAdmin() {
		System.out.println("");
		System.out.println(">>>>>>>>> GET ADMIN <<<<<<<<<<");
		final Admin ainDep = geoService.getAdmin("FR", "1");
		Assert.assertNotNull(ainDep);
		Assert.assertNotNull(ainDep.getName());
		Assert.assertNotNull(ainDep.getKey());
		System.out.println("Admin key/name : " + ainDep.getKey() + "/"
				+ ainDep.getName());
	}

	@Test
	public void testListAdmins() throws CalException {
		System.out.println("");
		System.out.println(">>>>>>>>> LISTING ADMINS <<<<<<<<<<");
		// Initiating FR country
		final GeographicItem france = new CountryImpl("CA");
		// Listing admins
		List<Admin> admins = geoService.listAdmins(france);
		for (Admin admin : admins) {
			Assert.assertNotNull("Admin must have a name", admin.getName());
			final ItemKey key = admin.getKey();
			Assert.assertNotNull("Admin must have a key", key);
			Assert.assertEquals("Admin must have a key of type admin",
					AdmImpl.CAL_ID, key.getType());
			System.out.println("Admin key/name : " + admin.getKey() + "/"
					+ admin.getName());
			// Retrieving children of this admin
			List<Admin> subAdmins = geoService.listAdmins(admin);
			for (Admin subAdmin : subAdmins) {
				Assert.assertNotNull("SubAdmin must have a name",
						subAdmin.getName());
				final ItemKey subKey = subAdmin.getKey();
				Assert.assertNotNull("SubAdmin must have a key", subKey);
				Assert.assertEquals("SubAdmin must have a key of type admin",
						AdmImpl.CAL_ID, subKey.getType());
				System.out.println("    > SubAdmin key/name : "
						+ subAdmin.getKey() + "/" + subAdmin.getName());
			}
		}
	}

	@Test
	public void testListCities() throws CalException {
		System.out.println("");
		System.out.println(">>>>>>>>> LISTING CITIES <<<<<<<<<<");
		final Admin ain = geoService.getAdmin("FR", "1");
		final List<City> cities = geoService.listCities(ain.getKey(), 100);
		for (City city : cities) {
			Assert.assertNotNull("City must have a name", city.getName());
			final ItemKey key = city.getKey();
			Assert.assertNotNull("City must have a key", key);
			Assert.assertEquals("City must have a key of type admin",
					CityImpl.CAL_ID, key.getType());
			System.out.println("City key/name : " + city.getKey() + "/"
					+ city.getName());
		}
	}

	@Test
	public void testFindCities() {
		System.out.println("");
		System.out.println(">>>>>>>>> CITIES FINDER <<<<<<<<<<");

		final List<City> cities = geoService.findCities("paris");
		for (City city : cities) {
			Assert.assertNotNull("City must have a name", city.getName());
			final ItemKey key = city.getKey();
			Assert.assertNotNull("City must have a key", key);
			Assert.assertEquals("City must have a key of type admin",
					CityImpl.CAL_ID, key.getType());
			Assert.assertNotNull(city.getCountry());
			StringBuilder b = new StringBuilder();
			b.append(city.getCountry().getName() + " / ");
			if (city.getAdm1() != null) {
				b.append(city.getAdm1().getName() + " / ");
			}
			if (city.getAdm2() != null) {
				b.append(city.getAdm2().getName() + " / ");
			}
			b.append(city.getName());
			System.out.println(b.toString());
		}
	}
}
