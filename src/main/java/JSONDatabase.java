import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class JSONDatabase implements Closeable {
	private final static String FILE_PATH = "./store/";

	private boolean isClosed = false;
	private final String documentName;
	private JSONArray jsonArray;

	public JSONDatabase(String documentName) {
		this.documentName = documentName;

		load();
	}

	private void create() {
		assertClosed();

		File file = new File(FILE_PATH + documentName + ".json");
		if(file.exists()) {
			throw new RuntimeException("Document is already exists");
		}

		try {
			writeFile(new JSONArray().toString());
		} catch(Exception e) {
			throw new RuntimeException("Document create failed", e);
		}
	}
	public void load() {
		assertClosed();

		File file = new File(FILE_PATH + documentName + ".json");
		if(!file.exists()) {
			create();
		}

		try {
			jsonArray = new JSONArray(readFile());
		} catch(Exception e) {
			throw new RuntimeException("Document load failed", e);
		}
	}
	public void save() {
		assertClosed();

		try {
			writeFile(jsonArray.toString());
		} catch(Exception e) {
			throw new RuntimeException("Document save failed", e);
		}
	}
	public void drop() {
		assertClosed();

		File file = new File(FILE_PATH + documentName + ".json");

		if(file.exists()) {
			if(!file.delete()) {
				throw new RuntimeException("Document drop failed");
			}
		}
	}

	public JSONDatabase insert(JSONObject jsonObject) {
		assertClosed();

		jsonArray.put(jsonObject);

		return this;
	}
	public JSONDatabase update(Predicate<JSONObject> predicateUpdate, Function<JSONObject, JSONObject> functionUpdate) {
		assertClosed();

		List<Integer> indexList = new ArrayList<>();

		for(int index=0 ; index<jsonArray.length() ; index++) {
			if(predicateUpdate.test(jsonArray.getJSONObject(index))) {
				indexList.add(index);
			}
		}

		indexList.forEach(item -> {
			JSONObject jsonObject = jsonArray.getJSONObject(item);

			jsonArray.put(item, functionUpdate.apply(jsonObject));
		});

		return this;
	}
	public JSONDatabase delete(Predicate<JSONObject> predicateDelete) {
		assertClosed();

		List<Integer> indexList = new ArrayList<>();

		for(int index=0 ; index<jsonArray.length() ; index++) {
			if(predicateDelete.test(jsonArray.getJSONObject(index))) {
				indexList.add(index);
			}
		}

		indexList.stream()
				.sorted(Comparator.comparingInt(i -> -1))
				.forEach(jsonArray::remove);

		return this;
	}

	public JSONArray selectAll() {
		return jsonArray;
	}
	public JSONArray select(Predicate<JSONObject> predicateSelect) {
		assertClosed();

		JSONArray result = new JSONArray();

		for(int index=0 ; index<jsonArray.length() ; index++) {
			if(predicateSelect.test(jsonArray.getJSONObject(index))) {
				result.put(jsonArray.getJSONObject(index));
			}
		}

		return result;
	}

	private JSONArray readFile() throws FileNotFoundException {
		assertClosed();

		File file = new File(FILE_PATH + documentName + ".json");
		if(!file.exists()) {
			throw new FileNotFoundException("File not found");
		}

		try(
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr)
		) {
			StringBuilder sb = new StringBuilder();
			String line;
			while((line = br.readLine()) != null) {
				sb.append(line);
			}

			return new JSONArray(sb.toString());
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	private void writeFile(String data) throws IOException {
		assertClosed();

		File file = new File(FILE_PATH + documentName + ".json");
		System.out.println("file.getAbsolutePath(): " + file.getAbsolutePath());
		if(!file.exists()) {
			if(!file.createNewFile()) {
				throw new IOException("File write failed");
			}
		}

		try(
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw)
		) {
			bw.write(data);
			bw.flush();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void assertClosed() throws IllegalStateException {
		if(isClosed) {
			throw new IllegalStateException("Already Closed");
		}
	}
	@Override
	public void close() {
		this.jsonArray = null;
		this.isClosed = true;
	}
}