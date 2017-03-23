package datastruct;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class LandMarkGraph {
	private List<Integer> LandMarknode;
	private Map<String, landmarkedge> landmarkedge;
	private List<Integer> result;
	Map<Integer, Integer> edgeDensity;
	Map<Integer, Integer> ntonodeid; // 1,2...n与n个点的映射
	Map<Integer, Integer> nodeidton; // n个点与1,2...n的映射
	int[][] path;
	double[][] dist;
	int nodelength;

	public LandMarkGraph() {
		initLandMarkNode();
		initMember();
		// ConvertTraToLandMarkSeq();
		// CountLandMarksnode();
		initLandMarkEdge();
		initFloyd();
	}

	private void initMember() {
		// TODO Auto-generated method stub
		landmarkedge = new HashMap<String, landmarkedge>();
		edgeDensity = new HashMap<Integer, Integer>();
		ntonodeid = new HashMap<Integer, Integer>(); // 1,2...n与n个点的映射
		nodeidton = new HashMap<Integer, Integer>(); // n个点与1,2...n的映射
		path = new int[nodelength][nodelength];
		dist = new double[nodelength][nodelength];
	}

	public List<Integer> getRoughRoute(Integer startroadid, Integer endroadid) {
		// floyd算法
		// startroadid = Integer.parseInt("243612");
		// endroadid = Integer.parseInt("272565");
		result = new ArrayList<Integer>();
		result.add(nodeidton.get(startroadid));
		findPath(nodeidton.get(startroadid), nodeidton.get(endroadid), path);
		result.add(nodeidton.get(endroadid));
		List<Integer> tmp = result;
		result = new ArrayList<Integer>();
		for (Integer i : tmp) {
			result.add(ntonodeid.get(i));
		}
		return result;
	}

	private void initFloyd() {
		int INF = Integer.MAX_VALUE;
		for (int i = 0; i < nodelength; i++) {
			ntonodeid.put(i, LandMarknode.get(i));
			nodeidton.put(LandMarknode.get(i), i);
		}
		double[][] matrix = new double[nodelength][nodelength];
		for (int i = 0; i < nodelength; i++) {
			for (int j = 0; j < nodelength; j++) {
				matrix[i][j] = INF;
			}
		}
		for (String s : landmarkedge.keySet()) {
			String[] roadid = s.split("\\+");
			Integer road1 = Integer.parseInt(roadid[0]);
			Integer road2 = Integer.parseInt(roadid[1]);
			matrix[nodeidton.get(road1)][nodeidton.get(road2)] = landmarkedge
					.get(s).getTime();
		}

		int size = matrix.length;
		// initialize dist and path
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				path[i][j] = -1;
				dist[i][j] = matrix[i][j];
			}
		}
		for (int k = 0; k < size; k++) {
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					if (dist[i][k] != INF && dist[k][j] != INF
							&& dist[i][k] + dist[k][j] < dist[i][j]) {
						dist[i][j] = dist[i][k] + dist[k][j];
						path[i][j] = k;
					}
				}
			}
		}
	}

	private void findPath(int i, int j, int[][] path) {
		int k = path[i][j];
		if (k == -1)
			return;
		findPath(i, k, path); // 递归
		result.add(k);
		findPath(k, j, path);
	}

	/**
	 * get top-4000 land
	 */
	public void CountLandMarksnode() {
		try {
			String path = "G:/taxidata/mapMatchingResult/RoadSeqence";
			File file = new File(path);
			File[] filelist = file.listFiles();
			// 统计密度
			for (int i = 0; i < filelist.length; i++) {
				String filename = filelist[i].getAbsolutePath();
				String thisLine = null;
				BufferedReader br = new BufferedReader(new FileReader(filename));
				while ((thisLine = br.readLine()) != null) {
					if (thisLine.length() != 1) {
						String[] a = thisLine.split(";");
						int roadid = Integer.parseInt(a[0].trim());
						if (edgeDensity.containsKey(roadid)) {
							edgeDensity
									.put(roadid, edgeDensity.get(roadid) + 1);
						} else {
							edgeDensity.put(roadid, 1);
						}
					}
				}
				br.close();
				System.out.println(filelist[i].getName() + "  统计结束");
			}
			// 密度排序
			List<Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer, Integer>>(
					edgeDensity.entrySet());
			Collections.sort(list,
					new Comparator<Map.Entry<Integer, Integer>>() {
						public int compare(Entry<Integer, Integer> o1,
								Entry<Integer, Integer> o2) {
							return (o2.getValue() - o1.getValue());
						}
					});
			// 遍历前4000，保存
			String filename = "G:/taxidata/mapMatchingResult/top-4000.txt";
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			for (int i = 0; i < 4000; i++) {
				Entry<Integer, Integer> mapping = list.get(i);
				bw.write(mapping.getKey().toString());
				bw.newLine();
			}
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("密度统计完毕");
		}
	}

	/**
	 * roadseqence转换成landmarkseqence
	 */
	public void ConvertTraToLandMarkSeq() {
		try {
			String path = "G:/taxidata/mapMatchingResult/RoadSeqence";
			File file = new File(path);
			File[] filelist = file.listFiles();
			for (int i = 0; i < filelist.length; i++) {
				String filename = filelist[i].getAbsolutePath();
				String thisLine = null;
				BufferedReader br = new BufferedReader(new FileReader(filename));
				BufferedWriter bw = new BufferedWriter(new FileWriter(
						"G:/taxidata/mapMatchingResult/LandMarkSeqence/LandSeqence "
								+ filelist[i].getName()));
				while ((thisLine = br.readLine()) != null) {
					if (thisLine.length() != 1) {
						String[] a = thisLine.split(";");
						int roadid = Integer.parseInt(a[0].trim());
						if (LandMarknode.contains(roadid)) {
							System.out.println(roadid);
							bw.write(a[0] + ";" + a[1]);
							bw.newLine();
						}
					} else {
						bw.write(" ");
						bw.newLine();
					}
				}
				bw.flush();
				bw.close();
				br.close();
				System.out.println(filelist[i].getName() + " 转换完毕");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

	}

	/**
	 * 初始化landmarknode地标信息
	 * 
	 */
	private void initLandMarkNode() {
		try {
			LandMarknode = new ArrayList<Integer>();
			String thisLine = null;
			BufferedReader br = new BufferedReader(new FileReader(
					"G:/taxidata/mapMatchingResult/top-4000.txt"));
			while ((thisLine = br.readLine()) != null) {
				int a = Integer.parseInt(thisLine);
				LandMarknode.add(a);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			nodelength = LandMarknode.size();
		}
	}

	/**
	 * 根据landmark和所有轨迹 初始化landmarkedge
	 */
	public void initLandMarkEdge() {
		// 统计candidate密度
		Map<String, List<String>> candidateedge = new HashMap<String, List<String>>();
		try {
			String path = "G:/taxidata/mapMatchingResult/LandMarkSeqence";
			File file = new File(path);
			File[] filelist = file.listFiles();
			for (int i = 0; i < filelist.length; i++) {
				String filename = filelist[i].getAbsolutePath();
				String oneLine = null;
				String twoLine = null;
				BufferedReader br = new BufferedReader(new FileReader(filename));
				oneLine = br.readLine();
				String[] s1 = oneLine.split(";");
				while ((br.readLine()).length() == 1) {
					br.readLine();
				}
				while ((twoLine = br.readLine()) != null) {
					if (twoLine.length() != 1) {
						String[] s2 = twoLine.split(";");
						int time = getTimeBetweenPoi(s1[1], s2[1]);
						if (time <= 30) {
							String candidateid = s1[0].trim() + "+"
									+ s2[0].trim();
							String day = (s1[1].split(" ")[1]).split("-")[2];
							String hour = (s1[1].split(" ")[2]).split(":")[0];
							if (candidateedge.containsKey(candidateid)) {
								candidateedge.get(candidateid).add(
										day + "+" + hour + "+" + time);
							} else {
								List<String> timelist = new ArrayList<String>();
								timelist.add(day + "+" + hour + "+" + time);
								candidateedge.put(candidateid, timelist);
							}
						}
						oneLine = twoLine;
						s1 = oneLine.split(";");
					} else {
						oneLine = br.readLine();
						twoLine = br.readLine();
					}
				}
				br.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// for (String key : candidateedge.keySet()) {
			// System.out.println(key);
			// List<String> value = candidateedge.get(key);
			// for (int i = 0; i < value.size(); i++) {
			// System.out.println(value.get(i));
			// }
			// System.out.println();
			// System.out.println();
			// }
			// System.out.println(candidateedge.size());
		}
		// System.out.println("~~~~~~~~~~~~~");
		// 从candidateedge候选边到landmarkedge
		for (String key : candidateedge.keySet()) {
			if (candidateedge.get(key).size() >= 10) {
				landmarkedge.put(key,
						new landmarkedge(key, candidateedge.get(key)));
			}
		}
		// System.out.println(landmarkedge.size());
	}

	/**
	 * 计算两段时间之差
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	private int getTimeBetweenPoi(String time1, String time2) {
		String[] a1 = time1.split(" ");
		String[] a2 = time2.split(" ");
		int minute1 = Integer.parseInt(a1[2].split(":")[1]);
		int minute2 = Integer.parseInt(a2[2].split(":")[1]);
		return ((minute2 + 60 - minute1) % 60);
	}

	public List<Integer> getLandMarknode() {
		return LandMarknode;
	}

}
