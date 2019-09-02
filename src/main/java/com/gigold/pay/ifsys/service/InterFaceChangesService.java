package com.gigold.pay.ifsys.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.gigold.pay.framework.core.exception.AbortException;
import com.gigold.pay.framework.service.AbstractService;
import com.gigold.pay.framework.util.common.DateUtil;
import com.gigold.pay.ifsys.annoation.Notice;
import com.gigold.pay.ifsys.bo.InterFaceChanges;
import com.gigold.pay.ifsys.dao.InterFaceChangesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gigold.pay.framework.core.Domain;

@Service
public class InterFaceChangesService extends AbstractService {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	@Autowired
	InterFaceChangesDao interFaceChangesDao;

	/**
	 * 批量记录变更
	 * @param changesList
     */
	@Notice("recordChanges")
	public void recordChanges(List<InterFaceChanges> changesList){
		for(InterFaceChanges changes : changesList){
			try{
				interFaceChangesDao.recordChanges(changes);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * 单个记录变更
	 * @param changes
     */
	@Notice("recordChanges")
	public void recordChanges(InterFaceChanges changes){
			try{
				interFaceChangesDao.recordChanges(changes);
			}catch (Exception e){
				e.printStackTrace();
			}
	}

	/**
	 * 根据数量获取feed
	 * @param limit
     */
	public void getChangesByLimit(int limit){

	}

	/**
	 * 根据接口获取变更
	 */
	public List<InterFaceChanges> getChangesByIfid(int ifId){
		List<InterFaceChanges> changesList=null;
		try {
			changesList = interFaceChangesDao.getChangesByIfid(ifId);
		}catch (Exception e){
			e.printStackTrace();
		}
		return changesList;
	}

	/**
	 * 根据限定数,获取最近的FEED
	 * @param id
	 * @param pid
	 *@param limit  @return
     */
	public List<InterFaceChanges> getRecentChanges(int pid, int limit, int userId) {
		List<InterFaceChanges> changesList=null;
		try {
			changesList = interFaceChangesDao.getRecentChanges(pid,limit,userId);
		}catch (Exception e){
			e.printStackTrace();
		}
		return changesList;
	}

	/**
	 * 获取前日变更数据
	 * @return
	 */
	public List<InterFaceChanges> getDailyChanges(int pid, Date date) {
		List<InterFaceChanges> changesList=null;
		try {
			// 获取当天日期字符串
			String dateString = DateUtil.getNewFormatDateString(date).substring(0,10);
			// 获取昨天所有记录
			changesList = interFaceChangesDao.getDailyChanges(pid,dateString);
		}catch (Exception e){
			e.printStackTrace();
		}
		return changesList;
	}
}
