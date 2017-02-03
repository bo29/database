package whatever;//ignore this
import java.sql.*;
import java.util.Scanner;
public class Drive {
	private static void print(String s){
		System.out.print(s);
	}//for simplicity
	private static void println(String s){
		System.out.println(s);
	}//for simplicity
	public static void main(String[] args) {
		Scanner scan=new Scanner(System.in);
		String url="jdbc:mysql://localhost:3306/university";
		String user="root";
		String password="root";
		String sql;
		ResultSet rs=null;
		int result;
		try{
			Connection connection=DriverManager.getConnection(url, user, password);
			Statement state=connection.createStatement();
			boolean student=false;
			boolean executive=false;
			boolean faculty=false;
			boolean staff=false;
			print("Enter user ID: ");
			int login=scan.nextInt();
			int length=String.valueOf(login).length();
			if(length==4)
				student=true;//student IDs are 4 digits
			else if(length==3)
				staff=true;//staff IDs are 3 digits
			else if(length==2)
				faculty=true;//faculty IDs are 2 digits
			else if(length==1)
				executive=true;//executive IDs are 1 digit
			print("Enter a command. Enter 'menu' with no quotes for available commands.\n");
			while(scan.hasNext()){
				String choice=scan.next();
				switch(choice){
				case "menu":
					println("Command\t\tDescription");
					println("-------\t\t-----------");
					println("lookup\t\tDisplays directory info for anyone relevant to your position.");
					println("courses\t\tDisplays information for all courses.");
					if(!student)
						println("self\t\tDisplay your info.");
					if(student){
						println("self\t\tDisplay your info and grades.");
						println("add\t\t    Sign up for a class.");
						println("drop\t\tDrop a class to W status.");
					}
					if(faculty || executive){
						println("add\t\t    Add a student to a course.");
						println("drop\t\tDrop a student from a course.");
						println("changegrade\tUpdate a student's grade.");
					}
					if(executive){
						println("addcourse\tOpen a new course for students.");
						println("dropcourse\tCancel a course.");
					}
					println("logout\t\tEnd session.\n");
					break;
				case "lookup":
					if(student){
						sql="SELECT * FROM People WHERE Position LIKE 'Student';";
						rs=state.executeQuery(sql);
						while(rs.next()){
							String lastName=rs.getString("LastName");
							String firstName=rs.getString("FirstName");
							String email=rs.getString("Email");
							String position=rs.getString("Position");
							int id=rs.getInt("ID");
							System.out.printf("%-15s %-15s %-15s %-15s %-15s\n", "Last", "First", "Email", "Position", "ID");
							System.out.printf("%-15s %-15s %-15s %-15s %-15s\n\n", lastName, firstName, email, position, id);
						}
					}//displays directory with student layout
					else if(executive || faculty){
						sql="SELECT * FROM People;";
						rs=state.executeQuery(sql);
						while(rs.next()){
							String lastName=rs.getString("LastName");
							String firstName=rs.getString("FirstName");
							String email=rs.getString("Email");
							String position=rs.getString("Position");
							String phone=rs.getString("Phone");
							int id=rs.getInt("ID");
							System.out.printf("%-15s %-15s %-15s %-15s %-15s %-15s\n",
									"Last", "First", "Email", "Position", "Phone", "ID");
							System.out.printf("%-15s %-15s %-15s %-15s %-15s %-15s\n\n",
									lastName, firstName, email, position, phone, id);
						}
					}//displays every entry in directory
					else{
						sql="SELECT * FROM People WHERE Position LIKE 'Executive' OR Position"
								+ " LIKE 'Instructor' OR Position like 'Staff';";
						rs=state.executeQuery(sql);
						while(rs.next()){
							String lastName=rs.getString("LastName");
							String firstName=rs.getString("FirstName");
							String email=rs.getString("Email");
							String position=rs.getString("Position");
							String phone=rs.getString("Phone");
							int id=rs.getInt("ID");
							System.out.printf("%-15s %-15s %-15s %-15s %-15s %-15s\n",
									"Last", "First", "Email", "Phone", "Position", "ID");
							System.out.printf("%-15s %-15s %-15s %-15s %-15s %-15s\n\n",
									lastName, firstName, email, phone, position, id);
						}
					}//displays directory info for staff
					break;
				case "courses":
					sql="SELECT * FROM Courses;";
					rs=state.executeQuery(sql);
					System.out.printf("%-15s %-15s %-15s %-15s %-15s %-15s %-15s %-15s\n", 
							"Course ID", "Course Name", "Instructor", "Day", "Time", "Start Date", "End Date", "Room");
					while(rs.next()){
						int id=rs.getInt("CourseID");
						String courseName=rs.getString("CourseName");
						String instructor=rs.getString("Instructor");
						String day=rs.getString("Day");
						int time=rs.getInt("Time");
						Date startDate=rs.getDate("StartDate");
						Date endDate=rs.getDate("EndDate");
						int room=rs.getInt("room");
						System.out.printf("%-15s %-15s %-15s %-15s %-15s %-15s %-15s %-15s\n\n", 
								id, courseName, instructor, day, time, startDate, endDate, room);
					}//this displays course information
					break;
				case "drop":
					if(student){
						print("Course ID for course to drop: ");
						int courseID=scan.nextInt();
						sql="UPDATE student"+login+" SET Grade='W' WHERE CourseID="+courseID+";";
						println("");
						result=state.executeUpdate(sql);
						println("Course successfully dropped; grade changed to W.\n");
					}//allows a student to drop one of their own classes
					if(faculty || executive){
						print("Enter student ID and course ID separated by a space: ");
						int studentID=scan.nextInt();
						int courseID=scan.nextInt();
						sql="UPDATE student"+studentID+" SET Grade='W' WHERE CourseID="+courseID+";";
						print("");
						result=state.executeUpdate(sql);
						println("Course successfully dropped; grade changed to W.\n");
					}//allows a professor or executive to drop a student from a class
					break;
				case "add":
					if(student){
						print("Enter Course ID to add: ");
						int courseID=scan.nextInt();
						sql="INSERT INTO student"+login+" VALUES("+courseID+", NULL);";
						println("");
						result=state.executeUpdate(sql);
						println("Course successfully added.\n");
					}//allows a student to sign up for a course
					else if(faculty || executive){
						print("Enter student ID and course ID separated by a space: ");
						int studentID=scan.nextInt();
						int courseID=scan.nextInt();
						sql="INSERT INTO student"+studentID+" VALUES("+courseID+", NULL);";
						print("");
						result=state.executeUpdate(sql);
						println("Course successfully added.\n");
					}//allows a faculty member or executive to sign up a student for a class
					break;
				case "changegrade":
					if(faculty){
						print("Enter student ID, course ID, and new grade separated by spaces: ");
						int studentID=scan.nextInt();
						int courseID=scan.nextInt();
						String grade=scan.next();
						print("");
						sql="UPDATE student"+studentID+" SET Grade='"+grade+"' WHERE CourseID="+courseID+" AND GRADE NOT"
								+ " LIKE 'W';";
						result=state.executeUpdate(sql);
						println("Grade successfully updated\n.");					
					}//allows a faculty member to change a grade
					if(executive){
						print("Enter student ID, course ID, and new grade separated by spaces: ");
						int studentID=scan.nextInt();
						int courseID=scan.nextInt();
						String grade=scan.next();
						print("");
						sql="UPDATE student"+studentID+" SET Grade='"+grade+"' WHERE CourseID="+courseID+";";
						result=state.executeUpdate(sql);
						println("Grade successfully updated.\n");					
					}//allows an executive to change a grade or reinstate a student (change W to another grade)
					break;
				case "addcourse":
					if(executive){
						print("Course ID: ");
						int courseID=scan.nextInt();
						print("Course name: ");
						String courseName=scan.next();
						print("Instructor: ");
						String instructor=scan.next();
						print("Day: ");
						String day=scan.next();
						print("Time: ");
						int time=scan.nextInt();
						print("Start date: ");
						String start=scan.next();
						print("End date: ");
						String end=scan.next();
						print("Room: ");
						int room=scan.nextInt();
						sql="INSERT INTO Courses VALUES('"+courseID+"', '"+courseName+"', '"+instructor+"',"
								+ " '"+day+"', '"+time+"', '"+start+"', '"+end+"', '"+room+"');";
						result=state.executeUpdate(sql);
						println("Course successfully added.\n");
					}//allows executives to open a new course
					break;
				case "dropcourse":
					if(executive){
						print("ID of course to drop: ");
						int id=scan.nextInt();
						sql="DELETE from Courses WHERE CourseID="+id+" LIMIT 1;";
						result=state.executeUpdate(sql);
						println("Course successfully dropped.\n");
					}//allows executives to close a course
					break;
				case "self":
					if(student){
						sql="SELECT * FROM People WHERE ID="+login+";";
						rs=state.executeQuery(sql);
						while(rs.next()){
							String lastName=rs.getString("LastName");
							String firstName=rs.getString("FirstName");
							String email=rs.getString("Email");
							String position=rs.getString("Position");
							String phone=rs.getString("Phone");
							int id=rs.getInt("ID");
							System.out.printf("%-15s %-15s %-15s %-15s %-15s "
									+ "%-15s\n", "Last", "First", "Email", "Position", "Phone", "ID");
							System.out.printf("%-15s %-15s %-15s %-15s %-15s "
									+ "%-15s\n\n", lastName, firstName, email, position, phone, id);
						}
						sql="SELECT * FROM student"+login+";";
						rs=state.executeQuery(sql);
						while(rs.next()){
							int courseID=rs.getInt("CourseID");
							String grade=rs.getString("Grade");
							println("Course ID\tGrade");
							println("    "+courseID+"\t\t  "+grade);
						}
					}//displays the info of a student and their grades
					else{
						sql="SELECT * FROM People WHERE ID="+login+";";
						rs=state.executeQuery(sql);
						while(rs.next()){
							String lastName=rs.getString("LastName");
							String firstName=rs.getString("FirstName");
							String email=rs.getString("Email");
							String position=rs.getString("Position");
							String phone=rs.getString("Phone");
							int id=rs.getInt("ID");
							System.out.printf("%-15s %-15s %-15s %-15s %-15s "
									+ "%-15s\n", "Last", "First", "Email", "Position", "Phone", "ID");
							System.out.printf("%-15s %-15s %-15s %-15s %-15s "
									+ "%-15s\n\n", lastName, firstName, email, position, phone, id);					
						}//displays own info for anyone who is not a student

					}
					break;
				case "logout":
					println("Goodbye.");
					connection.close();
					System.exit(0);//ends program
				default:
					println("You have entered an incorrect command.");
					break;
				}
				println("Enter a new command.");
			}
		}
		catch (Exception e){
			e.printStackTrace();
			System.exit(0);
		}
	}
}