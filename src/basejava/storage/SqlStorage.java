package basejava.storage;

import basejava.exception.NotExistStorageException;
import basejava.exception.StorageException;
import basejava.model.ContactType;
import basejava.model.Resume;
import basejava.sql.SqlHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SqlStorage implements Storage {

    public final SqlHelper sqlHelper;

    public SqlStorage(String dbUrl, String dbUser, String dbPassword) {
        sqlHelper = new SqlHelper(() -> DriverManager.getConnection(dbUrl, dbUser, dbPassword));
    }

    @Override
    public void clear() {
        sqlHelper.execute("""
            DELETE
              FROM resume""");
    }

    @Override
    public Resume get(String uuid) {
        return sqlHelper.execute("""
            SELECT      *
              FROM      resume r 
              left join contact c
                on      r.uuid = c.resume_uuid  
              WHERE     r.uuid = ?""",
            ps -> {
                ps.setString(1, uuid);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new NotExistStorageException(uuid);
                }
                Resume r = new Resume(uuid, rs.getString("full_name"));
                do {
                    String value = rs.getString("value");
                    ContactType type = ContactType.valueOf(rs.getString("type"));
                    r.addContact(type, value);
                } while (rs.next());
                return r;
            });
    }

    @Override
    public void update(Resume r) {
        sqlHelper.<Void>execute("""
            update  resume
              set   full_name = ?
              where uuid = ?""",
            ps -> {
                ps.setString(1, r.getFullName());
                ps.setString(2, r.getUuid());
                if (ps.executeUpdate() == 0) {
                    throw new NotExistStorageException(r.getUuid());
                }
                return null;
            });
    }

    @Override
    public void save(Resume r) {
        sqlHelper.transactionalExecute(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("""
                    INSERT
                      INTO   resume (uuid, full_name)
                      VALUES (?, ?)""")) {
                ps.setString(1, r.getUuid());
                ps.setString(2, r.getFullName());
                ps.execute();
            }
            try (PreparedStatement ps = conn.prepareStatement("""
                    INSERT
                      INTO   contact (resume_uuid, type, value)
                      VALUES (?, ?, ?)""")) {
                for (Map.Entry<ContactType, String> e : r.getContacts().entrySet()) {
                    ps.setString(1, r.getUuid());
                    ps.setString(2, e.getKey().name());
                    ps.setString(3, e.getValue());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            return null;
        });
    }

    @Override
    public void delete(String uuid) {
        sqlHelper.<Void>execute("""
            delete
              from  resume
              where uuid = ?""",
            ps -> {
                ps.setString(1, uuid);
                if (ps.executeUpdate() == 0) {
                    throw new NotExistStorageException(uuid);
                }
                return null;
            });
    }

    @Override
    public List<Resume> getAllSorted() {
        return sqlHelper.execute("""
            SELECT     *
              FROM     resume r
              order by full_name, uuid""",
            ps -> {
                ResultSet rs = ps.executeQuery();
                List<Resume> resumes = new ArrayList<>();
                while (rs.next()) {
                    resumes.add(new Resume(rs.getString("uuid"), rs.getString("full_name")));
                }
                return resumes;
            });
    }

    @Override
    public int size() {
        return sqlHelper.execute("""
            select count(*)
              from resume""",
            ps -> {
                ResultSet rs = ps.executeQuery();
                return rs.next() ? rs.getInt(1) : 0;
            });
    }
}