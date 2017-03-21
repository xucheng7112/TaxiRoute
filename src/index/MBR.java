package index;

/**
 * 最小外包矩形 Created by jdxg on 2017/3/7.
 */
public class MBR {

	public double minLng;// 最小经度
	public double minLat;// 最小纬度
	public double maxLng;// 最大经度
	public double maxLat;// 最大纬度

	/**
	 * 构造器
	 * 
	 * @param minLng
	 *            最小经度
	 * @param minLat
	 *            最小纬度
	 * @param maxLng
	 *            最大经度
	 * @param maxLat
	 *            最大纬度
	 */
	public MBR(double minLng, double minLat, double maxLng, double maxLat) {
		this.minLng = minLng;
		this.minLat = minLat;
		this.maxLng = maxLng;
		this.maxLat = maxLat;
	}
}
