package com.koen.tca.server.state;

import java.util.ArrayList;
import java.util.List;

import com.koen.tca.server.UEInfo;

/**
 * Holds the result of the UE 'Detection' state.
 * 
 */
public class DetectResult {

	private static DetectResult self;

	private DetectResult() {
		// Only accessible by the class itself

	}

	public static DetectResult SINGLETON() {

		if (self == null) {
			self = new DetectResult();
		}
		return self;
	}

	/**
	 * All the founded UE's in the Detect state, are stored in this list.
	 */
	private List<UEInfo> validUEList = new ArrayList<UEInfo>();

	/**
	 * @return the validUEList
	 */
	public synchronized List<UEInfo> getValidUEList() {
		return validUEList;
	}

	/**
	 * @param validUEList
	 *            the validUEList to set
	 */
	public synchronized void setValidUEList(List<UEInfo> validUEList) {
		this.validUEList = validUEList;
	}

	public synchronized void resetUEList() {
		validUEList.clear();
	}

	@Override
	public String toString() {
		
		if(getValidUEList().isEmpty()){
			return "No UE's detected :-(";
		}
		
		StringBuilder sb = new StringBuilder();
		for (UEInfo ueinfo : getValidUEList()) {
			sb.append("\n" + ueinfo);
		}
		return sb.toString();
	}

}
