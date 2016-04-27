package storage;

import java.sql.ResultSet;

public interface MyClass {
	void insert(String req);
	ResultSet query(String req);
}
