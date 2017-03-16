package xc;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import traj.database.io.TrajDataFile;
import traj.database.io.TrajDataFileConfiguration;
import traj.database.io.TrajDataFileInput;
import traj.database.management.TrajDataFileManagement;
import traj.util.Trajectory;

public class TaxiRouteCount {
	TrajDataFileConfiguration tdfc;
	TrajDataFileManagement tdfm;

	private void mapMatching() {
		tdfc = new TrajDataFileConfiguration("TraLib/configuration1.txt");
		tdfm = new TrajDataFileManagement(tdfc.getTrajDataFilesDir());
		List<TrajDataFile> tdfList = tdfm.getTrajDataFileList();

		Executor executor = Executors.newFixedThreadPool(6);
		for (int i = 0; i < tdfList.size(); i++) {
			TrajDataFile tdf = tdfList.get(i);
			TrajDataFileInput tdfi = new TrajDataFileInput(tdf);
			ThreadGraph t = new ThreadGraph(tdfi, i);
			executor.execute(t);
		}
	}

	public static void main(String[] args) {
		TaxiRouteCount trc = new TaxiRouteCount();
		// trc.mapMatching();
		trc.getLandMarks();
	}

	private void getLandMarks() {
		Graph g = new Graph();
		g.getLandMarks();
	}
}

class ThreadGraph extends Thread {
	TrajDataFileInput tdfi;
	int id;

	public ThreadGraph(TrajDataFileInput tdfi, int i) {
		this.tdfi = tdfi;
		this.id = i;
	}

	public void run() {
		Graph g = new Graph();
		g.mapMatching(tdfi, id);
	}

}