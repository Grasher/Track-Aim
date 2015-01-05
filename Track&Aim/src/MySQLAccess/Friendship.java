package MySQLAccess;

public class Friendship {
	 int user1;
	 int user2;
	 
	 public Friendship(){
		 
	 }
	 public Friendship(int user1, int user2){
		 this.user1 = user1;
		 this.user2 = user2;	 
	 }
	 
	 public Friendship(int user2){
		this.user2 = user2;	 
	 }
	 
	 public int getFriendshipUser1(){
		 return user1;
	 }
	 
	 public int getFriendshipUser2(){
		 return user2;
	 }
}

