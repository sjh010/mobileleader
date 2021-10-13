package com.mobileleader.edoc.daemon;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.mobileleader.edoc.config.SpringConfigration;
import com.mobileleader.edoc.data.mapper.EdocProcessMapper;

@Component("edocDaemon")
public class EdocDaemon {

	private static final Logger logger = LoggerFactory.getLogger(EdocDaemon.class);

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	@Autowired
	private EdsTaskWorker edocTaskWorker;

	@Autowired
	private EdsTaskWorkerResultProcessor edocTaskWorkerProcessor;

	@Autowired
	private EdocTaskSearcher edocTaskSearcher;

	@Autowired
	private EdocProcessMapper edocProcessMapper;
	
	/**
	 * 데몬이 재기동 되었을 경우, 진행중 상태인 task 들을 초기화 상태로 변경함.
	 */
	@PostConstruct
	private void initTask() {
		logger.info("[EdocDaemon Start - Init Task]");
		edocProcessMapper.updateTaskInit();
	}

	/**
	 * 전자문서 생성 처리 업무 스케쥴러
	 */
	@Scheduled(cron = "*/10 * * * * *")
	public void process() {
		// 처리 업무 검색
		List<EdsTask> tasks = edocTaskSearcher.search();
		
		if (tasks != null && tasks.size() > 0) {
			for (final EdsTask task : tasks) {
				taskExecutor.execute(new Runnable() {
					@Override
					@Async
					public void run() {
						EdsTaskWorkerResult result = edocTaskWorker.work(task);
						edocTaskWorkerProcessor.process(result);
					}
				});
			}
		}	
	}

	public static void main(String[] args) {
		@SuppressWarnings({ "unused", "resource" })
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfigration.class);
	}
}
