package com.gigold.pay.ifsys.dao;

import com.gigold.pay.ifsys.bo.InterFaceEdition;

import java.util.List;


public interface InterFaceEditionDao {
	/**
	 * 更新接口版本信息
     */
	void saveEdition(InterFaceEdition edition);

	/**
	 * @author chenkuan
	 * @date 2016/11/06
	 * @param interFaceEdition
	 * @return
     */
	InterFaceEdition getIfEditionByVerNo(InterFaceEdition interFaceEdition);

	/**
	 * 根据接口ID获取接口所有的版本
	 * @param ifId
	 * @return
     */
	List<InterFaceEdition> getEditionsByIfId(int ifId);
}
