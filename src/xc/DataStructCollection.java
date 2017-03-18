package xc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MapNode {
	private int nodeId;
	private double x, y;

	public MapNode(int nodeId, double x, double y) {
		this.nodeId = nodeId;
		this.x = x;
		this.y = y;
	}

	public int getNodeId() {
		return nodeId;
	}

	public double getxPoint() {
		return x;
	}

	public double getyPoint() {
		return y;
	}

	public void printData() {
		System.out.println(nodeId + " " + x + " " + y);
	}
}

class MapEdge {
	private int edgeId;
	private int startNode, endNode;

	// private double weight;

	public MapEdge(int edgeId, int startNode, int endNode) {
		this.edgeId = edgeId;
		this.startNode = startNode; //
		this.endNode = endNode;//
		// this.weight = weight; //
	}

	public int getStartNode() {
		return startNode;
	}

	public int getEndNode() {
		return endNode;
	}

	public int getEdgeId() {
		return edgeId;
	}

	public void printData() {
		System.out.println(edgeId + " " + startNode + " " + endNode);
	}
}

class MapResult {
	private int roadid;
	private Timestamp time;

	public MapResult(int roadid, Timestamp time) {
		this.roadid = roadid;
		this.time = time;
	}

	public int getRoadid() {
		return roadid;
	}

	public Timestamp getTime() {
		return time;
	}
}

class landmarkedge {
	String landmarkedgeID;
	int startEdgeID;
	int endEdgeID;
	double[] weekendTimeSplit;
	double[] workdayTimeSplit;

	public landmarkedge(String landmarkedgeID, List<String> timelist) {
		weekendTimeSplit = new double[24];
		workdayTimeSplit = new double[24];
		for (int i = 0; i < 24; i++) {
			weekendTimeSplit[i] = -1;
			workdayTimeSplit[i] = -1;
		}
		this.landmarkedgeID = landmarkedgeID;
		startEdgeID = Integer.parseInt(landmarkedgeID.split("\\+")[0]);
		endEdgeID = Integer.parseInt(landmarkedgeID.split("\\+")[1]);
		CountTimeSplit(timelist);
		System.out.println(landmarkedgeID + "  " + startEdgeID + "  "
				+ endEdgeID);
		String line = "";
		String line2="";
//		for (String s : timelist) {
//			System.out.println(s);
//		}
		for (int i = 0; i < 24; i++) {
				line = line + "time" + i + "=" + weekendTimeSplit[i] + " ";
				line2 = line2 + "time" + i + "=" + workdayTimeSplit[i] + " ";
		}
		System.out.println(line);
		System.out.println(line2);
		System.out.println();
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
		double averageweekendtime =Double.parseDouble(String.format("%1$.2f", t1 / count1) );
		double averageworkdaytime =Double.parseDouble(String.format("%1$.2f", t2 / count2) );;
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
}