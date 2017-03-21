package index;

/**
 * 用来表示二维的索引号 Created by jdxg on 2017/3/4.
 */
public class IndexPoint {

	public int indexLng;// 经度索引号
	public int indexLat;// 纬度索引号

	/**
	 * 空的构造器
	 */
	public IndexPoint() {

	}

	/**
	 * 构造器
	 * 
	 * @param indexLng
	 *            经度方向的下标
	 * @param indexLat
	 *            纬度方向的下标
	 */
	public IndexPoint(int indexLng, int indexLat) {
		this.indexLng = indexLng;
		this.indexLat = indexLat;
	}

	public String getIndexString() {
		String str = indexLng + "," + indexLat;
		return str;
	}

	public int getIndexLng() {
		return indexLng;
	}

	public int getIndexLat() {
		return indexLat;
	}
}
