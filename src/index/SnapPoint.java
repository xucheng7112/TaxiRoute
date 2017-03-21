package index;

import traj.util.Point;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Point的子类 ，添加ID Created by jdxg on 2017/3/2.
 */
public class SnapPoint extends Point {

	protected String id;// 点的id，对应所属轨迹的id
	protected PointMark mark;// 点在聚类过程中的标记
	protected double kDistance;// k-距离

	/**
	 *
	 * @param lng
	 *            经度
	 * @param lat
	 *            维度
	 * @param time
	 *            位于时间点
	 * @param id
	 *            id
	 */
	public SnapPoint(double lng, double lat, Timestamp time, String id) {
		super(lng, lat, time);
		this.id = id;
		// 初始所有点标记为噪声
		this.mark = PointMark.NOISE;
	}

	/**
	 * @return 点的id，对应轨迹id即移动对象id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * 复制点集合
	 * 
	 * @return 复制的点集合
	 */
	public static List<SnapPoint> copySnapPoints(List<SnapPoint> points) {
		List<SnapPoint> copyPoints = new ArrayList<SnapPoint>();
		for (int i = 0; i < points.size(); i++) {
			copyPoints.add(SnapPoint.copySnapPoint(points.get(i)));
		}
		return copyPoints;
	}

	/**
	 * 拷贝属性相同的一个点
	 * 
	 * @param point
	 *            待拷贝的点
	 * @return 拷贝的点
	 */
	public static SnapPoint copySnapPoint(SnapPoint point) {
		double lng = point.getLng();
		double lat = point.getLat();
		Timestamp time = point.getTime();
		String id = point.getId();
		SnapPoint copyPoint = new SnapPoint(lng, lat, time, id);
		return copyPoint;
	}

	public String toString() {
		String str = this.getId() + "," + this.getLng() + "," + this.getLat();
		if (this.getTime() != null) {
			str = str + "," + this.getTime().toString();
		}

		if (this.getReservation() != null) {
			str = str + "," + this.getReservation();
		}

		str = str + "," + this.getMark();

		return str;
	}

	/**
	 * 设置标记
	 * 
	 * @param pm
	 *            标记
	 */
	public void setMark(PointMark pm) {
		this.mark = pm;
	}

	/**
	 * @return 标记
	 */
	public PointMark getMark() {
		return this.mark;
	}

	/**
	 * 设置k-距离
	 * 
	 * @param distance
	 */
	public void setkDistance(double distance) {
		this.kDistance = distance;
	}

	/**
	 * 获得k-距离
	 * 
	 * @return
	 */
	public double getkDistance() {
		return this.kDistance;
	}

	/**
	 * 点的标记
	 */
	public enum PointMark {
		NOISE, // 噪声点
		BOUNDRY, // 边界点
		CORE// 核心点
	}
}
