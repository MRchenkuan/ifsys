package com.gigold.pay.ifsys.dao;

import com.gigold.pay.ifsys.bo.InterFaceChanges;
import com.gigold.pay.ifsys.bo.InterFaceInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface InterFaceChangesDao {
	/**
	 * 记录一条变更
	 * @param interFaceChanges
     */
	void recordChanges(InterFaceChanges interFaceChanges);

	/**
	 * 根据接口ID获取变更
	 * @param ifId
	 * @return
     */
	List<InterFaceChanges> getChangesByIfid(int ifId);

	/**
	 * 根据限定数,获取最近的FEED
	 *
     * @param pid
     * @param limit
     * @param userId
* @return
	 */
	List<InterFaceChanges> getRecentChanges(@Param("pid")int pid, @Param("limit") int limit, @Param("userId") int userId);

	/**
	 * 获取当日变更数据
	 * @return
	 * @param pid
	 * @param date
	 */
    List<InterFaceChanges> getDailyChanges(@Param("pid")int pid, @Param("date")String date);
}
