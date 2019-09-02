package com.gigold.pay.ifsys.ResponseDto;

import com.gigold.pay.framework.core.ResponseDto;
import com.gigold.pay.ifsys.bo.InterFaceInvoker;

public class InterFaceInvokerAddResDto extends ResponseDto {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	private InterFaceInvoker invoker;
	/**
	 * @return the invoker
	 */
	public InterFaceInvoker getInvoker() {
		return invoker;
	}
	/**
	 * @param invoker the invoker to set
	 */
	public void setInvoker(InterFaceInvoker invoker) {
		this.invoker = invoker;
	}

	

}
