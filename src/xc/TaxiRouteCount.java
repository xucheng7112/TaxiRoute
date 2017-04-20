package xc;

import traj.database.io.TrajDataFileConfiguration;
import traj.database.io.TrajDataFileInput;
import traj.database.management.TrajDataFileManagement;

public class TaxiRouteCount {
	TrajDataFileConfiguration tdfc;
	TrajDataFileManagement tdfm;
	Graph g;

	public static void main(String[] args) {
		TaxiRouteCount trc = new TaxiRouteCount();
		trc.mapMatching();
//		trc.getroute();
	}

	private void mapMatching() {
		// TODO Auto-generated method stub
		g = new Graph();
		g.mapMatching();
	}

	private void getroute() {
		g = new Graph();
		g.getroute();
	}

	/*
	 * private void mapMatching() { tdfc = new
	 * TrajDataFileConfiguration("TraLib/configuration1.txt"); tdfm = new
	 * TrajDataFileManagement(tdfc.getTrajDataFilesDir()); List<TrajDataFile>
	 * tdfList = tdfm.getTrajDataFileList(); Executor executor =
	 * Executors.newFixedThreadPool(6); for (int i = 0; i < tdfList.size(); i++)
	 * { TrajDataFile tdf = tdfList.get(i); TrajDataFileInput tdfi = new
	 * TrajDataFileInput(tdf); ThreadGraph t = new ThreadGraph(tdfi, i);
	 * executor.execute(t); } }
	 */
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
		g.mapMatching();
	}

}