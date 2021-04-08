package com.tdp.ms.sales;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import junit.framework.Assert;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SalesApplicationTests {

	@Test
	void main() {
		SalesApplication.main(new String[] {});
		Assert.assertTrue(true);
	}


}
