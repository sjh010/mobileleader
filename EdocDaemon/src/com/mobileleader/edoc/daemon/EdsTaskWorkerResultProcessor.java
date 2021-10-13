package com.mobileleader.edoc.daemon;

/**
 * 전자문서서버 데몬에서 주어진 임무를 EdsTaskWorker 클래스가 처리한 결과에 대해 처리하는 인터페이스
 */
public interface EdsTaskWorkerResultProcessor {

	public void process(EdsTaskWorkerResult result);
}
