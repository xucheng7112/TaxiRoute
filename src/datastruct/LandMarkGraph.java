package datastruct;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class LandMarkGraph {
	private List<Integer> LandMarkNode = new ArrayList<Integer>();
	private Map<String, LandMarkEdge> LandMarkEdgeMap;
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
		// CountLandMarkEdge();
		// initLandMarkEdge();
		// CountFlyod();
		initFlyod();
	}

	/**
	 * 初始化成员变量
	 */
	private void initMember() {
		// TODO Auto-generated method stub
		LandMarkEdgeMap = new HashMap<String, LandMarkEdge>();
		edgeDensity = new HashMap<Integer, Integer>();
		ntonodeid = new HashMap<Integer, Integer>(); // 1,2...n与n个点的映射
		nodeidton = new HashMap<Integer, Integer>(); // n个点与1,2...n的映射
		path = new int[nodelength][nodelength];
		dist = new double[nodelength][nodelength];
	}

	/**
	 * 根据两点 计算roughroute
	 * 
	 * @param startroadid
	 * @param endroadid
	 * @return
	 */
	public List<Integer> getRoughRoute(Integer startroadid, Integer endroadid) {
		// floyd算法
		// startroadid = Integer.parseInt("243612");
		// endroadid = Integer.parseInt("272565");
		result = new ArrayList<Integer>();
		findPath(nodeidton.get(startroadid), nodeidton.get(endroadid), path);
		List<Integer> tmp = result;
		result = new ArrayList<Integer>();
		for (Integer i : tmp) {
			result.add(ntonodeid.get(i));
		}
		result.add(startroadid);
		result.add(endroadid);
		return result;
	}

	/**
	 * 初始化floyd的PATH矩阵
	 */
	private void initFlyod() {
		for (int i = 0; i < nodelength; i++) {
			ntonodeid.put(i, LandMarkNode.get(i));
			nodeidton.put(LandMarkNode.get(i), i);
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"G:/taxidata/mapMatchingResult/FloydPath.txt"));
			String thisLine = null;
			int row = 0;
			while ((thisLine = br.readLine()) != null) {
				String[] s = thisLine.split(";");
				for (int j = 0; j < s.length; j++) {
					path[row][j] = Integer.parseInt(s[j]);
				}
				row++;
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 计算floyd矩阵并保存
	 */
	@SuppressWarnings("unused")
	private void CountFlyod() {
		System.out.println("正在计算Floyd矩阵：");
		int INF = Integer.MAX_VALUE;
		for (int i = 0; i < nodelength; i++) {
			ntonodeid.put(i, LandMarkNode.get(i));
			nodeidton.put(LandMarkNode.get(i), i);
		}
		double[][] matrix = new double[nodelength][nodelength];
		for (int i = 0; i < nodelength; i++) {
			for (int j = 0; j < nodelength; j++) {
				matrix[i][j] = INF;
			}
		}
		for (String s : LandMarkEdgeMap.keySet()) {
			String[] roadid = s.split("\\+");
			Integer road1 = Integer.parseInt(roadid[0]);
			Integer road2 = Integer.parseInt(roadid[1]);
			matrix[nodeidton.get(road1)][nodeidton.get(road2)] = LandMarkEdgeMap
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
		SavePathAndDist(path);
	}

	/**
	 * 保存floyd的PATH矩阵
	 * 
	 * @param path
	 */
	private void SavePathAndDist(int[][] path) {
		try {
			BufferedWriter bwPath = new BufferedWriter(new FileWriter(
					"G:/taxidata/mapMatchingResult/FloydPath.txt"));
			System.out.println(nodelength);
			for (int i = 0; i < nodelength; i++) {
				String line = "";
				for (int j = 0; j < nodelength; j++) {
					line += path[i][j] + ";";
				}
				System.out.println(i);
				bwPath.write(line);
				bwPath.newLine();
			}
			bwPath.flush();
			bwPath.close();
		} catch (IOException e) {
		} finally {
			System.out.println("Floyd Path矩阵保存完毕");
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
	 * 第二步 roadseqence转换成landmarkseqence
	 */
	@SuppressWarnings("unused")
	private void ConvertTraToLandMarkSeq() {
		try {
			String path = "G:/TrajectoryData/usefulMapMatchingData";
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
						if (LandMarkNode.contains(roadid)) {
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
			String thisLine = null;
			BufferedReader br = new BufferedReader(new FileReader(
					"G:/taxidata/mapMatchingResult/landmarknode.txt"));
			while ((thisLine = br.readLine()) != null) {
				int a = Integer.parseInt(thisLine);
				LandMarkNode.add(a);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			nodelength = LandMarkNode.size();
		}
	}

	/**
	 * 第一步 计算出landmarknode并保存
	 */
	@SuppressWarnings("unused")
	private void CountLandMarknode() {
		try {
			String path = "G:/TrajectoryData/usefulMapMatchingData";
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
			String filename = "G:/taxidata/mapMatchingResult/landmarknode.txt";
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
			System.out.println("landmarknode统计完毕");
		}
	}

	/**
	 * 初始化landmarkedge地标信息
	 */
	private void initLandMarkEdge() {
		try {
			String thisLine = null;
			BufferedReader br = new BufferedReader(new FileReader(

			"G:/taxidata/mapMatchingResult/landmarkedge size=200.txt"));
			while ((thisLine = br.readLine()) != null) {
				String[] s = thisLine.split(";");
				LandMarkEdge lme = new LandMarkEdge();
				lme.setLandmarkedgeID(s[0]);
				lme.setStartEdgeID(Integer.parseInt(s[1]));
				lme.setEndEdgeID(Integer.parseInt(s[2]));
				for (int i = 0; i < 24; i++) {
					lme.setWorkdayTimeSplit(i, Double.parseDouble(s[i + 3]));
					lme.setWorkdayTimeSplit(i, Double.parseDouble(s[i + 27]));
				}
				LandMarkEdgeMap.put(s[0], lme);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
	}

	/**
	 * 第三步 计算出landmarkedge并保存
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private void CountLandMarkEdge() {
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
				System.out.println(filename);
				while ((twoLine = br.readLine()) != null) {
					if (twoLine.length() != 1) {
						String[] s2 = twoLine.split(";");
						int time = getTimeBetweenPoi(s1[1], s2[1]);
						if (time <= 30) {
							String candidateid = s1[0].trim() + "+"
									+ s2[0].trim();
							String day = (s1[1].split(" ")[0]).split("-")[2];
							String hour = (s1[1].split(" ")[1]).split(":")[0];
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
		}
		// System.out.println("~~~~~~~~~~~~~");
		// 从candidateedge候选边到landmarkedge
		for (String key : candidateedge.keySet()) {
			if (candidateedge.get(key).size() >= 200) {
				LandMarkEdgeMap.put(key,
						new LandMarkEdge(key, candidateedge.get(key)));
			}
		}
		String filename = "G:/taxidata/mapMatchingResult/landmarkedge.txt";
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			for (String key : LandMarkEdgeMap.keySet()) {
				LandMarkEdge lme = LandMarkEdgeMap.get(key);
				String line = "";
				line += key + ";";
				line += lme.getStartEdgeID() + ";";
				line += lme.getEndEdgeID() + ";";
				for (int i = 0; i < 24; i++) {
					line += lme.getWorkdayTimeSplit(i) + ";";
				}
				for (int i = 0; i < 24; i++) {
					line += lme.getWeekendTimeSplit(i) + ";";
				}
				// System.out.println(line);
				bw.write(line);
				bw.newLine();
			}
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("landmarkedge保存完毕");
		}

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
		int minute1 = Integer.parseInt(a1[1].split(":")[1]);
		int minute2 = Integer.parseInt(a2[1].split(":")[1]);
		return ((minute2 + 60 - minute1) % 60);
	}

	public List<Integer> getLandMarknode() {
		return LandMarkNode;
	}

}
