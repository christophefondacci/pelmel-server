package com.nextep.geo.services.test;

import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.nextep.geo.dao.GeoDao;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Place;
import com.nextep.geo.services.test.model.PlaceSolrItem;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:META-INF/geo-services/testContext.xml" })
public class PlacesIndexer {

	private final SolrServer placesSolrServer;

	@Autowired
	private GeoDao geoDao;

	public PlacesIndexer() throws Exception {
		placesSolrServer = new CommonsHttpSolrServer(
				"http://localhost:8983/solr-geoplaces/geocore/");
	}

	@Test
	public void indexPlaces() throws Exception {
		placesSolrServer.deleteByQuery("*:*");
		final List<Place> places = geoDao.listPlaces();
		for (Place p : places) {
			System.out.println("Indexing " + p.getPlaceType() + " "
					+ p.getName());
			PlaceSolrItem item = new PlaceSolrItem();
			final City city = p.getCity();
			item.setCityId(city.getKey().toString());
			if (city.getAdm1() != null) {
				item.setAdm1(city.getAdm1().getKey().toString());
			}
			if (city.getAdm2() != null) {
				item.setAdm2(city.getAdm2().getKey().toString());
			}
			item.setCountryId(city.getCountry().getKey().toString());
			item.setContinentId(city.getCountry().getContinent().getKey()
					.toString());
			item.setPlaceType(p.getPlaceType());
			item.setKey(p.getKey());
			item.setName(p.getName());
			item.setLat(p.getLatitude());
			item.setLng(p.getLongitude());
			placesSolrServer.addBean(item);
			placesSolrServer.commit();
		}
	}
}
