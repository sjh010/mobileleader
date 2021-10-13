package com.mobileleader.edoc.daemon;

/**
 * 전자문서생성 데몬에서 처리할 업무를 수행하는 인터페이스
 */
public interface EdsTaskWorker {
	
	public EdsTaskWorkerResult work(EdsTask task);

}
