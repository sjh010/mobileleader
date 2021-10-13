package com.mobileleader.edoc.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.ComponentScan.Filter;

@Configuration
@ComponentScan(basePackages = {"com.mobileleader.edoc"}, excludeFilters = {
        @Filter(type=FilterType.REGEX, pattern="com.mobileleader.edoc.test.*")})
@PropertySource(value = {"edocDaemon.properties", "Config.properties", "db.properties"})
@Import({DatabaseConfiguration.class, SchedulerConfig.class})
public class SpringConfigration {
	
}