import org.json.JSONObject;

import java.util.Date;

public class JSONDatabaseTest {
	public static void main(String[] args) {
		new JSONDatabaseTest();
	}

	public JSONDatabaseTest() {
//		dropTest();

//		insertTest();
//		updateTest();
//		deleteTest();

//		selectTest();
		selectAllTest();
	}

	private void dropTest() {
		try(JSONDatabase jsondb = new JSONDatabase("test")) {
			jsondb.drop();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void insertTest() {
		try(JSONDatabase jsondb = new JSONDatabase("test")) {
			jsondb.insert(new JSONObject()
					.put("idx", 0)
					.put("username", "grast")
					.put("password", "password")
					.put("regdate", new Date())
			);

			jsondb.save();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void updateTest() {
		try(JSONDatabase jsondb = new JSONDatabase("test")) {
			jsondb.update(item -> "grast".equals(item.getString("username")), item -> item.put("regdate", new Date()));

			jsondb.save();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void deleteTest() {
		try(JSONDatabase jsondb = new JSONDatabase("test")) {
			jsondb.delete(item -> "grast".equals(item.getString("username")));

			jsondb.save();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void selectTest() {
		try(JSONDatabase jsondb = new JSONDatabase("test")) {
			System.out.println("select(username == grast): " + jsondb.select(item -> "grast".equals(item.getString("username"))));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void selectAllTest() {
		try(JSONDatabase jsondb = new JSONDatabase("test")) {
			System.out.println("selectAll(): " + jsondb.selectAll());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}