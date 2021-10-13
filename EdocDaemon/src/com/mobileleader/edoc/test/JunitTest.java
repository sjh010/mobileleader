package com.mobileleader.edoc.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mobileleader.edoc.config.SpringConfigration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringConfigration.class})
public class JunitTest {

	private static final Logger logger = LoggerFactory.getLogger(JunitTest.class);
	
	@Autowired
	ThreadPoolTaskExecutor taskExecutor;
	
	@Test
	public void test() {

		
	}
}
