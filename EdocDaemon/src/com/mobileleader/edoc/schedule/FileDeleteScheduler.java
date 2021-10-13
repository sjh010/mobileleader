package com.mobileleader.edoc.schedule;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mobileleader.edoc.exception.EdocException;
import com.mobileleader.edoc.util.FileUtil;

import edu.emory.mathcs.backport.java.util.Arrays;

@Component
public class FileDeleteScheduler {

	private static final Logger logger = LoggerFactory.getLogger(FileDeleteScheduler.class);
	
	@Value("${DATA_HOME_PATH}")
	private String DATA_HOME_PATH;
	
	@Value("${XML_FILE_PATH}")
	private String XML_FILE_PATH;
	
	@Value("${PDF_FILE_PATH}")
	private String PDF_FILE_PATH;
	
	@Value("${TSAPDF_FILE_PATH}")
	private String TSAPDF_FILE_PATH;
	
	@Value("${DELETE_SCHEDULE_YN}")
	private String DELETE_SCHEDULE_YN;
	
	@Value("${DAYS}")
	private int DAYS;
	
	@Scheduled(cron = "${DELETE_SCHEDULE_CRON}")
	public void removeWorkFile() throws EdocException {
		
		if ("Y".equalsIgnoreCase(DELETE_SCHEDULE_YN)) {
			logger.info("[DELETE Scheduler] Directory & File Delete");
			logger.info("[DELETE Scheduler] DAYS : {}", DAYS);
			logger.info("[DELETE Scheduler] Delete Start...");
			
			Calendar calendar = Calendar.getInstance();
			
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			
			calendar.add(Calendar.DAY_OF_MONTH, -DAYS);
			Date date = calendar.getTime();
			
			String minimumDay = format.format(date);
			
			logger.info("[DELETE Scheduler] minimumDay : {}", minimumDay);
			
			File root = new File(DATA_HOME_PATH);
			
			File[] files = root.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					
					if (Integer.parseInt(minimumDay) >= Integer.parseInt(pathname.getName())) {
						return true;
					} else {
						return false;
					}
					
				}
			});
			
			logger.info("Delete Date List : {}", Arrays.toString(files));
			
			for (File file : files) {
				if (file.isDirectory()) {
					FileUtil.deleteDir(file);
				} else {
					file.delete();
				}
			}
			
			logger.info("[DELETE Scheduler] Delete End...");
		}
		
	}
}
