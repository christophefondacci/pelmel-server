package com.videopolis.apis.service.impl;

import org.junit.Assert;
import org.junit.Test;

import com.videopolis.apis.exception.ApisException;

public class ApiResponseImplTest {

    @Test
    public void testAll() throws ApisException {
	final ApiResponseImpl response = new ApiResponseImpl();
	Assert.assertTrue("Initial elements list should be empty", response
		.getElements().isEmpty());
	Assert.assertNull(
		"An getUniqueElement() on an empty list should return null",
		response.getUniqueElement());
    }
}
