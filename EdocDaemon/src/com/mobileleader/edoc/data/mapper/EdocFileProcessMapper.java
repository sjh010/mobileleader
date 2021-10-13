package com.mobileleader.edoc.data.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mobileleader.edoc.data.dao.TbEdsElecDocFileProcsMgmtVo;

public interface EdocFileProcessMapper {

	public List<TbEdsElecDocFileProcsMgmtVo> selectListByEdocIndexNo(@Param("elecDocGroupInexNo") String elecDocGroupInexNo);
	
	public int update(TbEdsElecDocFileProcsMgmtVo edsElecDocFileProcsMgmtVo);
}
