package index;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import datastruct.MapNode;
import index.SnapPoint;

public class TrajMath {

	/**
	 * 计算时候的线程池规模
	 */
	public static int compThreadScale = 16;

	/**
	 * 由两点计算球面距离
	 * 
	 * @param pa
	 *            点a
	 * @param pb
	 *            点b
	 * @return 球面距离
	 */
	public static double compMeterDistance(SnapPoint pa, SnapPoint pb) {
		double EARTH_RADIUS = 6378137.0D;
		double lon1 = pa.getLng();
		double lat1 = pa.getLat();
		double lon2 = pb.getLng();
		double lat2 = pb.getLat();
		return compMeterDistance(lon1, lat1, lon2, lat2);
	}

	/**
	 * 由经纬度计算球面距离
	 * 
	 * @param lon1
	 *            经度1
	 * @param lat1
	 *            维度1
	 * @param lon2
	 *            经度2
	 * @param lat2
	 *            维度2
	 * @return 球面距离
	 */
	public static double compMeterDistance(double lon1, double lat1,
			double lon2, double lat2) {
		double EARTH_RADIUS = 6378137.0D;
		if (lon1 >= 1.0E-6D && lon2 >= 1.0E-6D) {
			double radLat1 = lat1 * 3.141592653589793D / 180.0D;
			double radLat2 = lat2 * 3.141592653589793D / 180.0D;
			double a = radLat1 - radLat2;
			double b = (lon1 - lon2) * 3.141592653589793D / 180.0D;
			double s = 2.0D * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2.0D),
					2.0D)
					+ Math.cos(radLat1)
					* Math.cos(radLat2)
					* Math.pow(Math.sin(b / 2.0D), 2.0D)));
			s *= 6378137.0D;
			return s;
		} else {
			return 0.0D;
		}
	}

	/**
	 * 点到直线距离
	 * 
	 * @param lat
	 *            维度
	 * @param lng
	 *            经度
	 * @param node1
	 * @param node2
	 * @return
	 */
	public static double getDistance(double lat, double lng, MapNode n1,
			MapNode n2) {
		double x1 = n1.getLat();
		double y1 = n1.getLng();
		double x2 = n2.getLat();
		double y2 = n2.getLng();
		// System.out.println(x0+" "+y0+" "+x1+" "+y1+" "+x2+" "+y2);
		double space = 0;
		double a, b, c;
		a = LineSpace(x1, y1, x2, y2);// 线段的长度
		b = LineSpace(x1, y1, lat, lng);// (x1,y1)到点的距离
		c = LineSpace(x2, y2, lat, lng);// (x2,y2)到点的距离
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

	/**
	 * 点到点距离
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static double LineSpace(double x1, double y1, double x2, double y2) {
		double lineLength = 0;
		lineLength = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
		return lineLength;
	}

	/**
	 * 将结果数据直接导入postgresql
	 * 
	 * @param sqlls
	 * @param tablename
	 */
	public static void LeadingIntoPostgreSql(List<MapNode> sqlls,
			String tablename) {
		Connection c = null;
		Statement stmt = null;
		try {
			// 连接数据库
			Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection(
					"jdbc:postgresql://localhost:5432/osm", "postgres",
					"117112");
			System.out.println("Opened database successfully");
			// 创建表
			stmt = c.createStatement();
			String sqltable = "CREATE TABLE " + tablename
					+ "(lng double precision," + " lat double precision "
					+ " )";
			stmt.executeUpdate(sqltable);
			stmt.close();
			System.out.println("Table " + tablename + "created successfully");
			// 插入数据
			c.setAutoCommit(false);
			stmt = c.createStatement();
			for (MapNode node : sqlls) {
				String sql = "INSERT INTO " + tablename + " VALUES ("
						+ node.getLng() + "," + node.getLat() + ");";
				stmt.executeUpdate(sql);
			}
			// 添加gis location 字段
			String sqlalt = "alter table " + tablename
					+ "  add location geometry(Point,900913);";
			stmt.executeUpdate(sqlalt);
			// 根据经纬度生成location字段的数据
			String sqlup = "UPDATE "
					+ tablename
					+ "  SET location= ST_TransForm(ST_GeomFromText('POINT(' || lng || ' ' || lat || ')',4326),900913);";
			stmt.executeUpdate(sqlup);
			System.out.println("Records created successfully");

			stmt.close();
			c.commit();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	public static double Euclid(MapNode node1, MapNode node2) {
		double distance = Math
				.sqrt(Math.pow(node1.getLat() - node2.getLat(), 2)
						+ Math.pow(node1.getLng() - node2.getLng(), 2));
		return distance;
	}
}