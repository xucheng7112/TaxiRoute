package datastruct;

public class MapEdge {
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
