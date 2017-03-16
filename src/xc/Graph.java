package xc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import traj.database.io.TrajDataFileInput;
import traj.util.Point;
import traj.util.Trajectory;

public class Graph {
	private Map<Integer, List<Integer>> vertexMap;// 邻接表
	private Map<Integer, MapNode> nodeMap; // 存储所有点
	private Map<Integer, MapEdge> edgeMap; // 存储所有边

	public Graph() {
		// 初始化邻接表
		vertexMap = new HashMap<Integer, List<Integer>>();
		nodeMap = new HashMap<Integer, MapNode>();
		edgeMap = new HashMap<Integer, MapEdge>();
		init("MapData/edges.txt", "MapData/vertices.txt");
	}

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

	public void mapMatching(TrajDataFileInput tdfi, int id) {
		// 路网匹配
		String filename = "H:/taxidata/mapMatchingResult/" + id + "-0" + ".txt";
		int index = 0;
		int count = 0;
		while (tdfi.hasNextTrajectory()) {
			if (count > 499) {
				index++;
				count -= 500;
				filename = "H:/taxidata/mapMatchingResult/" + id + "-" + index
						+ ".txt";
			}else{
				count++;
			}
			Trajectory tra = tdfi.readTrajectory();
			List<Point> tralist = tra.getTrajPtList();
			List<MapResult> traresult = new ArrayList<MapResult>();
			List<Integer> temp = new ArrayList<Integer>();
			for (int i = 0; i < tra.size(); i++) {
				Point p = tralist.get(i);
				MapEdge mapedge = getNearEdge(p.getLat(), p.getLng());
				if (!temp.contains(mapedge.getEdgeId())) {
					temp.add(mapedge.getEdgeId());
					traresult.add(new MapResult(mapedge.getEdgeId(), p
							.getTime()));
				}
			}
			// 保存倒文件
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(filename,
						true));
				String line = "";
				for (MapResult mapre : traresult) {
					line = mapre.getRoadid() + " ; " + mapre.getTime();
					bw.write(line);
					bw.newLine();
				}
				bw.write(" ");
				bw.newLine();
				bw.flush();
				bw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
				System.out.println("车辆" + tra.getTrajID() + "匹配结束");
			}
		}

	}

	// 轨迹点匹配路网
	private MapEdge getNearEdge(double lat, double lng) {
		MapEdge e = null;
		double distance = 1000;
		Iterator iter = edgeMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			MapEdge tempE = (MapEdge) entry.getValue();
			int node1 = tempE.getStartNode();
			int node2 = tempE.getEndNode();
			double tempDistance = getDistance(lat, lng, node1, node2);
			if (tempDistance < distance) {
				distance = tempDistance;
				e = tempE;
			}
		}
		// System.out.println(e.getEdgeId()+" "+distance);
		return e;
	}

	private double getDistance(double lat, double lng, int node1, int node2) {
		MapNode n1 = nodeMap.get(node1);
		MapNode n2 = nodeMap.get(node2);
		double x1 = n1.getxPoint();
		double y1 = n1.getyPoint();
		double x2 = n2.getxPoint();
		double y2 = n2.getyPoint();
		// System.out.println(x0+" "+y0+" "+x1+" "+y1+" "+x2+" "+y2);
		double space = 0;
		double a, b, c;
		a = lineSpace(x1, y1, x2, y2);// 线段的长度
		b = lineSpace(x1, y1, lat, lng);// (x1,y1)到点的距离
		c = lineSpace(x2, y2, lat, lng);// (x2,y2)到点的距离
		if (c <= 0.000001 || b <= 0.000001) {
			space = 0;
			return space;
		}
		if (a <= 0.000001) {
			space = b;
			return space;
		}
		if (c * c >= a * a + b * b) {
			space = b;
			return space;
		}
		if (b * b >= a * a + c * c) {
			space = c;
			return space;
		}
		double p = (a + b + c) / 2;// 半周长
		double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));// 海伦公式求面积
		space = 2 * s / a;// 返回点到线的距离（利用三角形面积公式求高）
		return space;
	}

	private double lineSpace(double x1, double y1, double x2, double y2) {
		double lineLength = 0;
		lineLength = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
		return lineLength;
	}
}

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