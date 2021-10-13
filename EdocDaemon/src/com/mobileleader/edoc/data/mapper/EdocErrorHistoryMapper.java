package com.mobileleader.edoc.data.mapper;

import com.mobileleader.edoc.data.dao.TbEdsElecDocGroupProcsErrHstrVo;

public interface EdocErrorHistoryMapper {
	
	public int selectDuplicateError(TbEdsElecDocGroupProcsErrHstrVo ecmRegInfoVo);
	
	public int insert(TbEdsElecDocGroupProcsErrHstrVo ecmRegInfoVo);
}
