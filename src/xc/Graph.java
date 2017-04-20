package xc;

import index.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import traj.database.io.TrajDataFileInput;
import traj.util.Point;
import traj.util.Trajectory;
import datastruct.LandMarkGraph;
import datastruct.MapEdge;
import datastruct.MapNode;

public class Graph {
	/**
	 * 路网邻接表
	 */
	private Map<Integer, List<Integer>> vertexMap;
	/**
	 * 存储所有 点的id-点
	 */
	private Map<Integer, MapNode> nodeMap;
	/**
	 * 存储所有 边的id-边
	 */
	private Map<Integer, MapEdge> edgeMap;
	/**
	 * 边的 id-密度
	 */
	// private Map<Integer, Integer> edgeDensity;
	/**
	 * 地标图
	 */
	private LandMarkGraph lmg;
	/**
	 * 网格索引
	 */
	private GridIndex gi;

	public Graph() {
		// 初始化邻接表
		vertexMap = new HashMap<Integer, List<Integer>>();
		nodeMap = new HashMap<Integer, MapNode>();
		edgeMap = new HashMap<Integer, MapEdge>();
		System.out.println("正在初始化路网：");
		init("MapData/edges.txt", "MapData/vertices.txt");
		System.out.println("正在初始化网格索引：");
		initGridindex();

	}

	/**
	 * 初始化网格索引
	 */
	private void initGridindex() {
		MBR mapScale = new MBR(115.416666, 39.43333, 117.5000, 41.05);// 地图边界经纬度
		double side = 2000;
		List<MapNode> ps = new ArrayList<MapNode>();
		for (Integer i : nodeMap.keySet()) {
			MapNode node = nodeMap.get(i);
			ps.add(node);
		}
		gi = new GridIndex(mapScale, side, ps);
	}

	/**
	 * 初始化图
	 * 
	 * @param edgeFile
	 *            边文件
	 * @param nodeFile
	 *            点文件
	 */
	private void init(String edgeFile, String nodeFile) {
		// 初始化点信息
		String thisLine = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(nodeFile));
			while ((thisLine = br.readLine()) != null) {
				String[] a = thisLine.split("\\s+");
				int nId = Integer.parseInt(a[0]);
				MapNode n = new MapNode(Integer.parseInt(a[0]),
						Double.parseDouble(a[1]), Double.parseDouble(a[2]));
				nodeMap.put(nId, n);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// System.out.println("初始化点集合完毕");
		}

		// 初始化边信息
		try {
			BufferedReader br = new BufferedReader(new FileReader(edgeFile));

			while ((thisLine = br.readLine()) != null) {
				String[] a = thisLine.split("\\s+");
				int eid = Integer.parseInt(a[0]);
				int n1 = Integer.parseInt(a[1]);
				int n2 = Integer.parseInt(a[2]);
				// System.out.println(a[0] + " " + a[1] + " " + a[2]);
				MapEdge e = new MapEdge(eid, n1, n2);
				edgeMap.put(eid, e);
				if (vertexMap.containsKey(n1)) {
					vertexMap.get(n1).add(eid);
				} else {
					List<Integer> ls = new ArrayList<Integer>();
					ls.add(eid);
					vertexMap.put(n1, ls);
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// System.out.println("初始化边集合完毕");
		}

	}

	/**
	 * 路网匹配 出租车轨迹->路网seqence
	 * 
	 * @param tdfi
	 * @param id
	 */
	public void mapMatching() {
		// 路网匹配
		MBR mapScale = new MBR(115.416666, 39.43333, 117.5000, 41.05);// 地图边界经纬度
		double side = 1000;
		MapMatchingGridIndex MMGI= new MapMatchingGridIndex(mapScale, side);
		String path = "G:/TrajectoryData/test";
		File file = new File(path);
		File[] filelist = file.listFiles();
		for (int i = 0; i < filelist.length; i++) {
			String filename = filelist[i].getAbsolutePath();
			int roadid=-1;
			try {
				String thisLine = null;
				BufferedReader br = new BufferedReader(new FileReader(filename));
				BufferedWriter bw = new BufferedWriter(new FileWriter("G:/TrajectoryData/test/1.txt"));
				while((thisLine=br.readLine())!=null){
					String[] a=thisLine.split(",");
					int tmproadid=MMGI.getRoadIDFromGrids(new MapNode(0, Double.parseDouble(a[2]),Double.parseDouble(a[1])));
					if(tmproadid!=roadid && tmproadid!=-1){
						bw.write(tmproadid+";"+a[3]);
						bw.newLine();
						roadid=tmproadid;
					}
				}
				bw.flush();
				bw.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(filename+"   匹配完毕");
		}

	}

	/**
	 * 轨迹计算
	 */
	@SuppressWarnings("resource")
	public void getroute() {
		System.out.println("正在初始化轨迹计算环境：");
		lmg = new LandMarkGraph();
		Scanner sc = new Scanner(System.in);
		int count = 10;
		while (count != -1) {
			System.out.println("请依次输入起点的Lat，Lng：");
			MapNode mapNode = new MapNode(0, sc.nextDouble(),
					sc.nextDouble());
			System.out.println("请依次输入终点的Lat，Lng：");
			MapNode mapNode2 = new MapNode(1, sc.nextDouble(),
					sc.nextDouble());
			MapEdge mapedge = getNearEdge(mapNode.getLat(), mapNode.getLng(),
					lmg.getLandMarknode());
			MapEdge mapedge2 = getNearEdge(mapNode2.getLat(),
					mapNode2.getLng(), lmg.getLandMarknode());
			List<Integer> roughroute = lmg.getRoughRoute(mapedge.getEdgeId(),
					mapedge2.getEdgeId());
			System.out.println("RoughRoute匹配完毕");
			List<Integer> finalresult = new ArrayList<Integer>();
			for (int a = 0; a < roughroute.size() - 1; a++) {
				Integer roughroadid = roughroute.get(a);
				if (!finalresult.contains(edgeMap.get(roughroadid)
						.getStartNode())) {
					finalresult.add(edgeMap.get(roughroadid).getStartNode());
				}
				if (!finalresult
						.contains(edgeMap.get(roughroadid).getEndNode())) {
					finalresult.add(edgeMap.get(roughroadid).getEndNode());
				}
				String twonodeid = getTwoNearNode(roughroute.get(a),
						roughroute.get(a + 1));
				List<Integer> exactway = getExactRoute(
						Integer.parseInt(twonodeid.split("\\+")[0]),
						Integer.parseInt(twonodeid.split("\\+")[1]));
				if (exactway != null) {
					for (Integer i : exactway) {
						if (!finalresult.contains(i)) {
							finalresult.add(i);
						}
					}
				}
				// System.out.println("once");
			}
			if (!finalresult.contains(edgeMap.get(
					roughroute.get(roughroute.size() - 1)).getStartNode())) {
				finalresult.add(edgeMap.get(
						roughroute.get(roughroute.size() - 1)).getStartNode());
			}
			if (!finalresult.contains(edgeMap.get(
					roughroute.get(roughroute.size() - 1)).getEndNode())) {
				finalresult.add(edgeMap.get(
						roughroute.get(roughroute.size() - 1)).getEndNode());
			}
			List<MapNode> finalresultnodels = new ArrayList<MapNode>();
			for (Integer i : finalresult) {
				// System.out.println(nodeMap.get(i).getLng() + ","
				// + nodeMap.get(i).getLat());
				finalresultnodels.add(nodeMap.get(i));
			}
			System.out.println("导入数据库中：");
			TrajMath.LeadingIntoPostgreSql(finalresultnodels, "table" + count);
			count++;
		}
	}

	/**
	 * 由两个点获取两点之间的最短路径
	 * 
	 * @param nodeid1
	 * @param nodeid2
	 * @return
	 */
	private List<Integer> getExactRoute(int nodeid1, int nodeid2) {
		MapNode node1 = nodeMap.get(nodeid1);
		MapNode node2 = nodeMap.get(nodeid2);
		IndexPoint ip1 = gi.getIndex(node1.getLng(), node1.getLat());
		IndexPoint ip2 = gi.getIndex(node2.getLng(), node2.getLat());
		int m, n, p, q;
		m = ip1.getIndexLat() < ip2.getIndexLat() ? ip1.getIndexLat() : ip2
				.getIndexLat();
		n = ip1.getIndexLat() > ip2.getIndexLat() ? ip1.getIndexLat() : ip2
				.getIndexLat();
		p = ip1.getIndexLng() < ip2.getIndexLng() ? ip1.getIndexLng() : ip2
				.getIndexLng();
		q = ip1.getIndexLng() > ip2.getIndexLng() ? ip1.getIndexLng() : ip2
				.getIndexLng();
		// System.out.println(m + " " + n + " " + p + " " + q + " ");
		List<Integer> nodelist = new ArrayList<Integer>();
		Map<Integer, List<Integer>> vertex = new HashMap<Integer, List<Integer>>();
		nodelist.add(nodeid1);
		nodelist.add(nodeid2);
		for (int i = m; i < n + 1; i++) {
			for (int j = p; j < q + 1; j++) {
				List<MapNode> pslist = gi.getPointsFromGrid(i, j);
				for (MapNode node : pslist) {
					nodelist.add(node.getNodeId());
				}
			}
		}
		if (nodelist.size() == 0) {
			return null;
		}
		for (int i = 0; i < nodelist.size(); i++) {
			for (int j = 0; j < nodelist.size(); j++) {
				if (vertexMap.keySet().contains(nodelist.get(i))) {
					if ((vertexMap.get(nodelist.get(i))).contains(nodelist
							.get(j))) {
						if (vertex.containsKey(nodelist.get(i))) {
							vertex.get(nodelist.get(i)).add(nodelist.get(j));
						} else {
							List<Integer> tmp = new ArrayList<Integer>();
							tmp.add(nodelist.get(j));
							vertex.put(nodelist.get(i), tmp);
						}
					}
				}
			}
		}
		List<Integer> exactway = TrajMath.Floyd(nodelist, vertex, nodeid1,
				nodeid2);
		return exactway;
	}

	/**
	 * 返回两条边之间比较近的两点
	 * 
	 * @param edgeid1
	 * @param edgeid2
	 * @return
	 */
	private String getTwoNearNode(Integer edgeid1, Integer edgeid2) {
		MapEdge edge1 = edgeMap.get(edgeid1);
		MapEdge edge2 = edgeMap.get(edgeid2);
		MapNode node11 = nodeMap.get(edge1.getStartNode());
		MapNode node12 = nodeMap.get(edge1.getEndNode());
		MapNode node21 = nodeMap.get(edge2.getStartNode());
		MapNode node22 = nodeMap.get(edge2.getEndNode());
		double d1 = TrajMath.LineSpace(node11.getLat(), node11.getLng(),
				node21.getLat(), node21.getLng());
		double d2 = TrajMath.LineSpace(node12.getLat(), node12.getLng(),
				node21.getLat(), node21.getLng());
		double d3 = TrajMath.LineSpace(node11.getLat(), node11.getLng(),
				node22.getLat(), node22.getLng());
		double d4 = TrajMath.LineSpace(node12.getLat(), node12.getLng(),
				node22.getLat(), node22.getLng());
		double min = d1;
		if (min > d2) {
			min = d2;
		}
		if (min > d3) {
			min = d3;
		}
		if (min > d4) {
			min = d4;
		}
		if (min == d1) {
			return (node11.getNodeId() + "+" + node21.getNodeId());
		} else if (min == d2) {
			return (node12.getNodeId() + "+" + node21.getNodeId());
		} else if (min == d3) {
			return (node11.getNodeId() + "+" + node22.getNodeId());
		} else {
			return (node12.getNodeId() + "+" + node22.getNodeId());
		}
	}

	/**
	 * 轨迹点匹配路网
	 * 
	 * @param lat
	 *            维度
	 * @param lng
	 *            经度
	 * @return
	 */
	private MapEdge getNearEdge(double lat, double lng) {
		MapEdge e = null;
		double distance = 1000;
		for (Integer i : edgeMap.keySet()) {
			MapEdge tempE = edgeMap.get(i);
			int node1 = tempE.getStartNode();
			int node2 = tempE.getEndNode();
			double tempDistance = TrajMath.getDistance(lat, lng,
					nodeMap.get(node1), nodeMap.get(node2));
			if (tempDistance < distance) {
				distance = tempDistance;
				e = tempE;
			}
		}
		// System.out.println(e.getEdgeId()+" "+distance);
		return e;
	}

	/**
	 * 出租车起始轨迹点匹配路标网
	 * 
	 * @param lat
	 *            维度
	 * @param lng
	 *            经度
	 * @return
	 */
	private MapEdge getNearEdge(double lat, double lng,
			List<Integer> landMarknode) {
		MapEdge e = null;
		double distance = 1000;
		for (Integer landmarkid : landMarknode) {
			MapEdge tempE = edgeMap.get(landmarkid);
			int node1 = tempE.getStartNode();
			int node2 = tempE.getEndNode();
			double tempDistance = TrajMath.getDistance(lat, lng,
					nodeMap.get(node1), nodeMap.get(node2));
			if (tempDistance < distance) {
				distance = tempDistance;
				e = tempE;
			}
		}
		// System.out.println(e.getEdgeId()+" "+distance);
		return e;
	}

}
