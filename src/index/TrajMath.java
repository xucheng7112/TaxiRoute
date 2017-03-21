package index;

import index.SnapPoint;
import java.math.*;
import java.util.List;

/**
 * Created by jdxg on 2017/3/4.
 */
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
	 * 计算豪斯多夫距离
	 * 
	 * @param psa
	 * @param psb
	 * @return
	 */
	public static double compHuasDis(List<SnapPoint> psa, List<SnapPoint> psb) {
		double hda = compHDOne(psa, psb);
		double hdb = compHDOne(psb, psa);
		if (hda > hdb)
			return hda;
		else
			return hdb;
	}

	/**
	 * 单向豪斯多夫付距离
	 */
	private static double compHDOne(List<SnapPoint> psa, List<SnapPoint> psb) {
		double hd = 0;
		for (int i = 0; i < psa.size(); i++) {
			SnapPoint p = psa.get(i);
			double distance = compMeterDistance(p, psb.get(0));
			for (int j = 0; j < psb.size(); j++) {
				double dis = compMeterDistance(p, psb.get(j));
				if (dis < distance)
					distance = dis;
			}
			if (hd < distance)
				hd = distance;
		}
		return hd;
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
	 * 计算最小经度
	 * 
	 * @param points
	 * @return
	 */
	public static double compMinLng(List<SnapPoint> points) {
		double minLng = points.get(0).getLng();
		for (int i = 0; i < points.size(); i++) {
			if (minLng > points.get(i).getLng())
				minLng = points.get(i).getLng();
		}
		// int threadScale = compThreadScale;
		// if(threadScale >= points.size())
		// threadScale = points.size()-1;
		// compMinLngThread[] threads = new compMinLngThread[threadScale];
		// int taskAmount = points.size()/(threadScale-1);
		// for(int i=0;i<threadScale;i++) {
		// threads[i] = new
		// compMinLngThread(points,i*taskAmount,(i+1)*taskAmount-1);
		// }
		// //启动线程
		// for(int i=0;i<threadScale;i++) {
		// threads[i].start();
		// }
		// //等待线程
		// try {
		// for (int i=0;i<threadScale;i++) {
		// threads[i].join();
		// }
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// for(int i=0;i<threadScale;i++){
		// if(minLng > threads[i].getMinLng())
		// minLng = threads[i].getMinLng();
		// }
		return minLng;
	}

	/**
	 * 计算最小纬度
	 * 
	 * @param points
	 * @return
	 */
	public static double compMinLat(List<SnapPoint> points) {
		double minLat = points.get(0).getLat();
		for (int i = 0; i < points.size(); i++) {
			if (minLat > points.get(i).getLat())
				minLat = points.get(i).getLat();
		}
		// int threadScale = compThreadScale;
		// if(threadScale >= points.size())
		// threadScale = points.size()-1;
		// compMinLatThread[] threads = new compMinLatThread[threadScale];
		// int taskAmount = points.size()/(threadScale-1);
		// for(int i=0;i<threadScale;i++) {
		// threads[i] = new
		// compMinLatThread(points,i*taskAmount,(i+1)*taskAmount-1);
		// }
		// //启动线程
		// for(int i=0;i<threadScale;i++) {
		// threads[i].start();
		// }
		// //等待线程
		// try {
		// for (int i=0;i<threadScale;i++) {
		// threads[i].join();
		// }
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// for(int i=0;i<threadScale;i++){
		// if(minLat > threads[i].getMinLat())
		// minLat = threads[i].getMinLat();
		// }
		return minLat;
	}

	/**
	 * 计算最大经度
	 * 
	 * @param points
	 * @return
	 */
	public static double compMaxLng(List<SnapPoint> points) {
		double maxLng = points.get(0).getLng();

		for (int i = 0; i < points.size(); i++) {
			if (maxLng < points.get(i).getLng())
				maxLng = points.get(i).getLng();
		}

		// int threadScale = compThreadScale;
		// if(threadScale >= points.size())
		// threadScale = points.size()-1;
		// compMaxLngThread[] threads = new compMaxLngThread[threadScale];
		// int taskAmount = points.size()/(threadScale-1);
		// for(int i=0;i<threadScale;i++) {
		// threads[i] = new
		// compMaxLngThread(points,i*taskAmount,(i+1)*taskAmount-1);
		// }
		// //启动线程
		// for(int i=0;i<threadScale;i++) {
		// threads[i].start();
		// }
		// //等待线程
		// try {
		// for (int i=0;i<threadScale;i++) {
		// threads[i].join();
		// }
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// for(int i=0;i<threadScale;i++){
		// if(maxLng < threads[i].getMaxLng())
		// maxLng = threads[i].getMaxLng();
		// }
		return maxLng;
	}

	/**
	 * 计算最大纬度
	 * 
	 * @param points
	 * @return
	 */
	public static double compMaxLat(List<SnapPoint> points) {
		double maxLat = points.get(0).getLat();
		for (int i = 0; i < points.size(); i++) {
			if (maxLat < points.get(i).getLat())
				maxLat = points.get(i).getLat();
		}
		// int threadScale = compThreadScale;
		// if(threadScale >= points.size())
		// threadScale = points.size()-1;
		// compMaxLatThread[] threads = new compMaxLatThread[threadScale];
		// int taskAmount = points.size()/(threadScale-1);
		// for(int i=0;i<threadScale;i++) {
		// threads[i] = new
		// compMaxLatThread(points,i*taskAmount,(i+1)*taskAmount-1);
		// }
		// //启动线程
		// for(int i=0;i<threadScale;i++) {
		// threads[i].start();
		// }
		// //等待线程
		// try {
		// for (int i=0;i<threadScale;i++) {
		// threads[i].join();
		// }
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// for(int i=0;i<threadScale;i++){
		// if(maxLat < threads[i].getMaxLat())
		// maxLat = threads[i].getMaxLat();
		// }
		return maxLat;
	}

	/**
	 * 计算经纬度极值
	 * 
	 * @param points
	 * @return
	 */
	public static double[] compExLngLat(List<SnapPoint> points) {
		double[] extremes = new double[4];
		extremes[0] = points.get(0).getLng();
		extremes[1] = points.get(0).getLat();
		extremes[2] = points.get(0).getLng();
		extremes[3] = points.get(0).getLat();

		for (int i = 0; i < points.size(); i++) {
			if (extremes[0] > points.get(i).getLng())
				extremes[0] = points.get(i).getLng();
			if (extremes[1] > points.get(i).getLat())
				extremes[1] = points.get(i).getLat();
			if (extremes[2] < points.get(i).getLng())
				extremes[2] = points.get(i).getLng();
			if (extremes[3] < points.get(i).getLat())
				extremes[3] = points.get(i).getLat();
		}

		// int threadScale = compThreadScale;
		// if(threadScale >= points.size())
		// threadScale = points.size()-1;
		// compExLngLatThread[] threads = new compExLngLatThread[threadScale];
		// int taskAmount = points.size()/(threadScale-1);
		// for(int i=0;i<threadScale;i++) {
		// threads[i] = new
		// compExLngLatThread(points,i*taskAmount,(i+1)*taskAmount-1);
		// }
		// //启动线程
		// for(int i=0;i<threadScale;i++) {
		// threads[i].start();
		// }
		// //等待线程
		// try {
		// for (int i=0;i<threadScale;i++) {
		// threads[i].join();
		// }
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// for(int i=0;i<threadScale;i++){
		// if(extremes[0] > threads[i].getExtremes()[0])
		// extremes[0] = threads[i].getExtremes()[0];
		// if(extremes[1] > threads[i].getExtremes()[1])
		// extremes[1] = threads[i].getExtremes()[1];
		// if(extremes[2] < threads[i].getExtremes()[2])
		// extremes[2] = threads[i].getExtremes()[2];
		// if(extremes[3] < threads[i].getExtremes()[3])
		// extremes[3] = threads[i].getExtremes()[3];
		//
		// }
		return extremes;
	}

}

/**
 * 计算最小经度的线程
 */
class compMinLngThread extends Thread {
	/**
	 * 待计算的点集
	 */
	private List<SnapPoint> points;
	/**
	 * 负责处理的头和尾（包含）
	 */
	private int begin, end;
	/**
	 * 计算结果，最小经度
	 */
	private double minLng;

	/**
	 * 构造器
	 * 
	 * @param points
	 *            计算的点集
	 */
	public compMinLngThread(List<SnapPoint> points, int begin, int end) {
		this.begin = begin;
		this.end = end;
		this.points = points;
		this.minLng = points.get(begin).getLng();
	}

	/**
	 * 获得计算的最小经度
	 * 
	 * @return
	 */
	public double getMinLng() {
		return this.minLng;
	}

	@Override
	public void run() {
		for (int i = begin; i <= end; i++) {
			// 溢出检查
			if (i >= points.size())
				break;
			if (minLng > points.get(i).getLng())
				this.minLng = points.get(i).getLng();
		}
	}

}

/**
 * 计算最小纬度的线程
 */
class compMinLatThread extends Thread {
	/**
	 * 待计算的点集
	 */
	private List<SnapPoint> points;
	/**
	 * 负责处理的头和尾（包含）
	 */
	private int begin, end;
	/**
	 * 计算结果，最小经度
	 */
	private double minLat;

	/**
	 * 构造器
	 * 
	 * @param points
	 *            计算的点集
	 */
	public compMinLatThread(List<SnapPoint> points, int begin, int end) {
		this.begin = begin;
		this.end = end;
		this.points = points;
		this.minLat = points.get(begin).getLat();
	}

	/**
	 * 获得计算的最小纬度
	 * 
	 * @return
	 */
	public double getMinLat() {
		return this.minLat;
	}

	@Override
	public void run() {
		for (int i = begin; i <= end; i++) {
			// 溢出检查
			if (i >= points.size())
				break;
			if (minLat > points.get(i).getLat())
				this.minLat = points.get(i).getLat();
		}
	}

}

/**
 * 计算最大经度的线程
 */
class compMaxLngThread extends Thread {
	/**
	 * 待计算的点集
	 */
	private List<SnapPoint> points;
	/**
	 * 负责处理的头和尾（包含）
	 */
	private int begin, end;
	/**
	 * 计算结果，最小经度
	 */
	private double maxLng;

	/**
	 * 构造器
	 * 
	 * @param points
	 *            计算的点集
	 */
	public compMaxLngThread(List<SnapPoint> points, int begin, int end) {
		this.begin = begin;
		this.end = end;
		this.points = points;
		this.maxLng = points.get(begin).getLng();
	}

	/**
	 * 获得计算的最大经度
	 * 
	 * @return
	 */
	public double getMaxLng() {
		return this.maxLng;
	}

	@Override
	public void run() {
		for (int i = begin; i <= end; i++) {
			// 溢出检查
			if (i >= points.size())
				break;
			if (maxLng < points.get(i).getLng())
				this.maxLng = points.get(i).getLng();
		}
	}

}

/**
 * 计算最大纬度的线程
 */
class compMaxLatThread extends Thread {
	/**
	 * 待计算的点集
	 */
	private List<SnapPoint> points;
	/**
	 * 负责处理的头和尾（包含）
	 */
	private int begin, end;
	/**
	 * 计算结果，最小经度
	 */
	private double maxLat;

	/**
	 * 构造器
	 * 
	 * @param points
	 *            计算的点集
	 */
	public compMaxLatThread(List<SnapPoint> points, int begin, int end) {
		this.begin = begin;
		this.end = end;
		this.points = points;
		this.maxLat = points.get(begin).getLat();
	}

	/**
	 * 获得计算的最大纬度
	 * 
	 * @return
	 */
	public double getMaxLat() {
		return this.maxLat;
	}

	@Override
	public void run() {
		for (int i = begin; i <= end; i++) {
			// 溢出检查
			if (i >= points.size())
				break;
			if (maxLat < points.get(i).getLat())
				this.maxLat = points.get(i).getLat();
		}
	}

}

/**
 * 计算经纬度极值的线程
 */
class compExLngLatThread extends Thread {
	/**
	 * 待计算的点集
	 */
	private List<SnapPoint> points;
	/**
	 * 负责处理的头和尾（包含）
	 */
	private int begin, end;
	/**
	 * 计算结果，[最小经度，最小纬度，最大经度，最大纬度]
	 */
	private double[] extremes;

	/**
	 * 构造器
	 * 
	 * @param points
	 *            计算的点集
	 */
	public compExLngLatThread(List<SnapPoint> points, int begin, int end) {
		this.begin = begin;
		this.end = end;
		this.points = points;
		this.extremes = new double[4];
		extremes[0] = points.get(0).getLng();
		extremes[1] = points.get(0).getLat();
		extremes[2] = points.get(0).getLng();
		extremes[3] = points.get(0).getLat();
	}

	/**
	 * 获得计算的经纬度极值
	 * 
	 * @return
	 */
	public double[] getExtremes() {
		return this.extremes;
	}

	@Override
	public void run() {
		for (int i = begin; i <= end; i++) {
			// 溢出检查
			if (i >= points.size())
				break;
			if (extremes[0] > points.get(i).getLng())
				extremes[0] = points.get(i).getLng();
			if (extremes[1] > points.get(i).getLat())
				extremes[1] = points.get(i).getLat();
			if (extremes[2] < points.get(i).getLng())
				extremes[2] = points.get(i).getLng();
			if (extremes[3] < points.get(i).getLat())
				extremes[3] = points.get(i).getLat();
		}
	}

}
