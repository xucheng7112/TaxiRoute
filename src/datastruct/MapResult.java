package datastruct;

import java.sql.Timestamp;

public class MapResult {
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
