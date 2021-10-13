package com.mobileleader.edoc.action;

import com.mobileleader.edoc.daemon.EdsTask;
import com.mobileleader.edoc.daemon.EdsTaskWorkerResult;

public interface EdocAction {

    /**
     * Action 인터페이스
     */
	public EdsTaskWorkerResult execute(EdsTask task) throws Exception;

}
