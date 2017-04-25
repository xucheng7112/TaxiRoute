package index;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import datastruct.MapNode;

public class MapMatchingGridIndex {
	/**
	 * 最小经纬度 最大经纬度
	 */
	private double minLng, minLat;
	private double maxLng, maxLat;
	/**
	 * 经度与纬度方向需要划分为多少格，一共多少格
	 */
	private int lngAmount, latAmount, alAmount;
	/**
	 * 每个格子的边长（米）
	 */
	private double side;
	/**
	 * 索引包含的所有点集合（列表）（引用）
	 */
	private List<MapNode> pointsList;
	/**
	 * 网格列表,行表示经度划分，列表示经度划分
	 */
	private Grid[][] grids;

	public MapMatchingGridIndex(MBR mapScale, double side) {
		this.side = side;

		initLngLat(mapScale);

		// 初始化网格数组
		initGrids();
	}

	/**
	 * 初始化网格数组
	 * 
	 * @throws IOException
	 */
	private void initGrids() {
		// 计算平面长宽
		double disLng = TrajMath.compMeterDistance(minLng, minLat, maxLng,
				minLat);
		double disLat = TrajMath.compMeterDistance(minLng, minLat, minLng,
				maxLat);
		// 计算长宽划分网格数量
		this.lngAmount = ((int) (disLng / side)) + 1;
		this.latAmount = ((int) (disLat / side)) + 1;
		this.alAmount = lngAmount * latAmount;
		// 建立网格二位数组
		this.grids = new Grid[lngAmount][latAmount];

		// 初始化网格列表,无用网格太多，选择不初始化
		// initGridsSub();
		// 填充网格
		fillGrids();

	}

	private void fillGrids() {
		BufferedReader br;
		String thisLine = null;
		try {
			br = new BufferedReader(new FileReader("MapData/geos.txt"));
			while ((thisLine = br.readLine()) != null) {
				String a[] = thisLine.split("\\s+");
				int edgeID = Integer.parseInt(a[0]);
				for (int i = 1; i < a.length; i++) {
					double lat = Double.parseDouble(a[i]);
					i++;
					double lng = Double.parseDouble(a[i]);
					MapNode node = new MapNode(0, lat, lng, edgeID);
					IndexPoint indexPoint = getIndex(node);
					if (indexPoint != null) {
						if (grids[indexPoint.indexLng][indexPoint.indexLat] == null) {
							grids[indexPoint.indexLng][indexPoint.indexLat] = new Grid();
						} else {
							grids[indexPoint.indexLng][indexPoint.indexLat].substances
									.add(node);
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getRoadIDFromGrids(MapNode p) {
		// // 获取网格号
		IndexPoint indexPoint = getIndex(p);
		List<MapNode> pl = null;
		if (indexPoint != null) {
			if (grids[indexPoint.indexLng][indexPoint.indexLat] != null) {
				pl = grids[indexPoint.indexLng][indexPoint.indexLat].substances;
			} else {
				return -1;
			}
		}
		if (pl == null) {
			return -1;
		}
		double distance = Integer.MAX_VALUE;
		double tmpDistance;
		int roadID = -1;
		for (int i = 0; i < pl.size(); i++) {
			MapNode node = pl.get(i);
			tmpDistance = Math.sqrt(Math.pow(p.getLat() - node.getLat(), 2)
					+ Math.pow(p.getLng() - node.getLng(), 2));
			if (tmpDistance < distance) {
				roadID = node.getMapMatchingRoadID();
				distance = tmpDistance;
			}
		}
		return roadID;

		// // 开始搜索
		// for (int i = indexLng - n; i <= indexLng + n; i++) {
		// for (int j = indexLat - n; j <= indexLat + n; j++) {
		// List<MapNode> ps = getPointsFromGrid(i, j);
		// for (int x = 0; x < ps.size(); x++) {
		// points.add(ps.get(x));
		// }
		// }
		// }
		// return points;
	}

	/**
	 * 计算所属下标的点
	 * 
	 * @param point
	 *            需要计算所属下标的点
	 * @return 下标
	 */
	public IndexPoint getIndex(MapNode point) {
		if (point.getLng() < minLng || point.getLat() < minLat
				|| point.getLng() > maxLng || point.getLat() > maxLat)
			return null;
		return getIndex(point.getLng(), point.getLat());
	}

	/**
	 * * 计算所属下标的点
	 * 
	 * @param lng
	 *            经度
	 * @param lat
	 *            纬度
	 * @return 下标
	 */
	public IndexPoint getIndex(double lng, double lat) {
		if (lng < minLng || lat < minLat || lng > maxLng || lat > maxLat)
			return null;
		IndexPoint indexPoint = new IndexPoint();
		double disLng = TrajMath.compMeterDistance(lng, minLat, minLng, minLat);
		double disLat = TrajMath.compMeterDistance(minLng, lat, minLng, minLat);
		indexPoint.indexLng = (int) (disLng / side);
		indexPoint.indexLat = (int) (disLat / side);
		return indexPoint;
	}

	private void initLngLat(MBR mapScale) {
		this.minLng = mapScale.minLng;
		this.minLat = mapScale.minLat;
		this.maxLng = mapScale.maxLng;
		this.maxLat = mapScale.maxLat;

	}

	/**
	 * 基于点的网格类
	 */
	class Grid {
		/**
		 * 网格包含的点的id集合
		 */
		public List<MapNode> substances;

		public Grid() {
			this.substances = new ArrayList<MapNode>();
		}

	}
}
