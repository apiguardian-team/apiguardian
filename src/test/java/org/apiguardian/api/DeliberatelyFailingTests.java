package org.apiguardian.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DeliberatelyFailingTests {

	@Test
	void deliberateFailure() {
		assertEquals(API.Status.STABLE, API.Status.EXPERIMENTAL);
	}

}
