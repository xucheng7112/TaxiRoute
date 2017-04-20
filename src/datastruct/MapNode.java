package datastruct;

public class MapNode {
	/**
	 * 点的id
	 */
	private Integer nodeId;
	/**
	 * lat 维度 lng 经度
	 */
	private double lat, lng;
	
	/**
	 *mappMatching时记录这个点属于哪条边 
	 */
	private  int mapMatchingRoadID;

	public MapNode(int nodeId, double lat, double lng) {
		this.nodeId = nodeId;
		this.lat = lat;
		this.lng = lng;
	}
	
	public MapNode(int nodeId, double lat, double lng,int mapMatchingRoadID) {
		this.nodeId = nodeId;
		this.lat = lat;
		this.lng = lng;
		this.mapMatchingRoadID=mapMatchingRoadID;
	}

	public int getNodeId() {
		return nodeId;
	}

	public String getNodeIdS() {
		return nodeId.toString();
	}

	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}

	public int getMapMatchingRoadID() {
		return mapMatchingRoadID;
	}

	public void printData() {
		System.out.println(nodeId + " " + lat + " " + lng);
	}
}
