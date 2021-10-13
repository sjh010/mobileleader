package com.mobileleader.edoc.data.mapper;

import org.apache.ibatis.annotations.Param;

import com.mobileleader.edoc.data.dao.TbEdsElecDocGroupBzwkInfoVo;

public interface EdocBizMapper {

	public TbEdsElecDocGroupBzwkInfoVo select(@Param("elecDocGroupInexNo") String elecDocGroupInexNo);
}
