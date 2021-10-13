package com.mobileleader.edoc.data.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mobileleader.edoc.data.dao.TbEdsElecDocGroupProcsMgmtVo;

public interface EdocProcessMapper {

	public int update(TbEdsElecDocGroupProcsMgmtVo edsElecDocGroupProcsMgmtVo) throws Exception;
	
	public int updateTaskInit();
	
	public int selectTaskCount(@Param("serverIp") String serverIp);
	
	public List<TbEdsElecDocGroupProcsMgmtVo> selectTask(@Param("serverIp") String serverIp);
	
	public List<HashMap<String, String>> selectFinishTask(@Param("targetDate") String targetDate); 
	
}
