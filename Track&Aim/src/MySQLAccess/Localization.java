package MySQLAccess;

public class Localization {
	 float longitude;
	 float latitude;
	 
	 public Localization(){
		 
	 }
	 public Localization(float longitude, float latitude){
		 this.longitude = longitude;
		 this.latitude = latitude;	 
	 }
		 
	 public float getLongitude(){
		 return longitude;
	 }
	 
	 public float getLatitude(){
		 return latitude;
	 }
}
