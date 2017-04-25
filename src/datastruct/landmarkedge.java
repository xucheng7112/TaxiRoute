package datastruct;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LandMarkEdge {
	String landmarkedgeID;
	int startEdgeID;
	int endEdgeID;
	double[] weekendTimeSplit;
	double[] workdayTimeSplit;

	public LandMarkEdge(String landmarkedgeID, List<String> timelist) {
		this.landmarkedgeID = landmarkedgeID;
		startEdgeID = Integer.parseInt(landmarkedgeID.split("\\+")[0]);
		endEdgeID = Integer.parseInt(landmarkedgeID.split("\\+")[1]);
		weekendTimeSplit = new double[24];
		workdayTimeSplit = new double[24];
		for (int i = 0; i < 24; i++) {
			weekendTimeSplit[i] = -1;
			workdayTimeSplit[i] = -1;
		}
		CountTimeSplit(timelist);
		// System.out.println(landmarkedgeID + "  " + startEdgeID + "  "
		// + endEdgeID);
	}

	public LandMarkEdge() {
		weekendTimeSplit = new double[24];
		workdayTimeSplit = new double[24];
	}

	/**
	 * 遍历所有符合条件的时间，分配到对应的时间段
	 * 
	 * @param timelist
	 */
	private void CountTimeSplit(List<String> timelist) {
		HashMap<Integer, List<Integer>> weekendmap = new HashMap<Integer, List<Integer>>();
		HashMap<Integer, List<Integer>> workdaymap = new HashMap<Integer, List<Integer>>();
		List<Integer> weekend = new ArrayList<Integer>() {
			{
				add(3);
				add(4);
				add(10);
				add(11);
				add(18);
				add(17);
				add(25);
				add(25);
			}
		};
		for (String dayhourtime : timelist) {
			String[] s = dayhourtime.split("\\+");
			if (weekend.contains(Integer.parseInt(s[0]))) {
				if (weekendmap.containsKey(Integer.parseInt(s[1]))) {
					weekendmap.get(Integer.parseInt(s[1])).add(
							Integer.parseInt(s[2]));
				} else {
					List<Integer> hourtimelist = new ArrayList<Integer>();
					hourtimelist.add(Integer.parseInt(s[2]));
					weekendmap.put(Integer.parseInt(s[1]), hourtimelist);
				}
			} else {
				if (workdaymap.containsKey(Integer.parseInt(s[1]))) {
					workdaymap.get(Integer.parseInt(s[1])).add(
							Integer.parseInt(s[2]));
				} else {
					List<Integer> hourtimelist = new ArrayList<Integer>();
					hourtimelist.add(Integer.parseInt(s[2]));
					workdaymap.put(Integer.parseInt(s[1]), hourtimelist);
				}
			}
		}
		// 计算所有时间段的众数
		for (int i = 0; i < 24; i++) {
			if (weekendmap.containsKey(i)) {
				List<Integer> hourtimelist = weekendmap.get(i);
				double modetime = getModeTime(hourtimelist);
				weekendTimeSplit[i] = modetime;
			}
			if (workdaymap.containsKey(i)) {
				List<Integer> hourtimelist = workdaymap.get(i);
				double modetime = getModeTime(hourtimelist);
				workdayTimeSplit[i] = modetime;
			}
		}
		// 没有众数的以平均时间来替代
		fixTimeSplit();
	}

	/**
	 * 对于未被覆盖的时间段，用其他时间的平均时间来表示
	 */
	private void fixTimeSplit() {
		double t1 = 0, t2 = 0;
		int count1 = 0, count2 = 0;
		for (int i = 0; i < 24; i++) {
			if (weekendTimeSplit[i] != -1) {
				t1 += weekendTimeSplit[i];
				count1++;
			}
			if (workdayTimeSplit[i] != -1) {
				t2 += workdayTimeSplit[i];
				count2++;
			}
		}
		double averageweekendtime, averageworkdaytime;
		if (count1 == 0) {
			averageweekendtime = 10.0;
		} else {
			averageweekendtime = Double.parseDouble(String.format("%1$.2f", t1
					/ count1));
		}
		if (count2 == 0) {
			averageworkdaytime = 10.0;
		} else {
			averageworkdaytime = Double.parseDouble(String.format("%1$.2f", t2
					/ count2));
		}
		;
		for (int i = 0; i < 24; i++) {
			if (weekendTimeSplit[i] == -1) {
				weekendTimeSplit[i] = averageweekendtime;
			}
			if (workdayTimeSplit[i] == -1) {
				workdayTimeSplit[i] = averageworkdaytime;
			}
		}
	}

	/**
	 * 获取当前时间段的中位数
	 * 
	 * @param hourtimelist
	 * @return
	 */
	private double getModeTime(List<Integer> hourtimelist) {
		Map<Integer, Integer> modetimemap = new HashMap<Integer, Integer>();
		for (Integer time : hourtimelist) {
			if (modetimemap.containsKey(time)) {
				modetimemap.put(time, modetimemap.get(time) + 1);
			} else {
				modetimemap.put(time, 1);
			}
		}
		int count = 0;
		double modetime = 0;
		for (Integer time : modetimemap.keySet()) {
			if (modetimemap.get(time) > count) {
				modetime = time;
				count = modetimemap.get(time);
			} else if (modetimemap.get(time) == count) {
				modetime = (modetime + time) / 2;
			}
		}
		return modetime;
	}

	/**
	 * 返回当前路段，当前日期，当前时间通过所需时间
	 * 
	 * @return
	 */
	public double getTime() {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		int Week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		if (Week == 1 || Week == 0) {
			return weekendTimeSplit[hour];
		} else {
			return workdayTimeSplit[hour];
		}
	}

	public int getStartEdgeID() {
		return startEdgeID;
	}

	public int getEndEdgeID() {
		return endEdgeID;
	}

	public double getWeekendTimeSplit(int i) {
		return weekendTimeSplit[i];
	}

	public double getWorkdayTimeSplit(int i) {
		return workdayTimeSplit[i];
	}

	public void setWeekendTimeSplit(int i, double time) {
		this.weekendTimeSplit[i] = time;
	}

	public void setWorkdayTimeSplit(int i, double time) {
		this.workdayTimeSplit[i] = time;
	}

	public String getLandmarkedgeID() {
		return landmarkedgeID;
	}

	public void setLandmarkedgeID(String landmarkedgeID) {
		this.landmarkedgeID = landmarkedgeID;
	}

	public void setStartEdgeID(int startEdgeID) {
		this.startEdgeID = startEdgeID;
	}

	public void setEndEdgeID(int endEdgeID) {
		this.endEdgeID = endEdgeID;
	}

}
