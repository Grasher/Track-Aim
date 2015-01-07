package info.simov.trackaim;

public class Point {
	public double longitude = 0f;
	public double latitude = 0f;
	public double altitude = 0f;
	public float x, y = 0;

	public Point(double lat, double lon, double alt) {
		this.latitude = lat;
		this.longitude = lon;
		this.altitude = alt;
	}
}
