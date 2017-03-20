package datastruct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class landmarkedge {
	String landmarkedgeID;
	int startEdgeID;
	int endEdgeID;
	double time;
	double[] weekendTimeSplit;
	double[] workdayTimeSplit;

	public landmarkedge(String landmarkedgeID, List<String> timelist) {
		this.landmarkedgeID = landmarkedgeID;
		startEdgeID = Integer.parseInt(landmarkedgeID.split("\\+")[0]);
		endEdgeID = Integer.parseInt(landmarkedgeID.split("\\+")[1]);
		time = 2.33;
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

	private void CountTimeSplit(List<String> timelist) {
		// 遍历所有符合条件的时间，分配到对应的时间段
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
				time = modetime;
			}
			if (workdaymap.containsKey(i)) {
				List<Integer> hourtimelist = workdaymap.get(i);
				double modetime = getModeTime(hourtimelist);
				workdayTimeSplit[i] = modetime;
				time = modetime;
			}
		}
		// 没有众数的以平均时间来替代
		fixTimeSplit();

	}

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
		double averageweekendtime = Double.parseDouble(String.format("%1$.2f",
				t1 / count1));
		double averageworkdaytime = Double.parseDouble(String.format("%1$.2f",
				t2 / count2));
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

	private double getModeTime(List<Integer> hourtimelist) {
		// TODO Auto-generated method stub
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

	public int getStartEdgeID() {
		return startEdgeID;
	}

	public int getEndEdgeID() {
		return endEdgeID;
	}

	public double getTime() {
		return time;
	}
}
