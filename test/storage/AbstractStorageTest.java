package storage;

import exception.ExistStorageException;
import exception.NotExistStorageException;
import model.Resume;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractStorageTest {
    protected final Storage storage;

    protected static final int INITIAL_SIZE = 3;
    protected static final int WRONG_INITIAL_SIZE = 10;

    private static final String UUID_1 = "uuid1";
    private static final String NAME_1 = "FullName1";
    protected static final Resume RESUME_1 = new Resume(UUID_1, NAME_1);
    private static final String UUID_2 = "uuid2";
    private static final String NAME_2 = "FullName2";
    protected static final Resume RESUME_2 = new Resume(UUID_2, NAME_2);
    private static final String UUID_3 = "uuid3";
    private static final String NAME_3 = "FullName3";
    protected static final Resume RESUME_3 = new Resume(UUID_3, NAME_3);
    private static final String UUID_NEW = "uuid_new";
    private static final String NAME_MEW = "FullName_4_new";
    private static final Resume RESUME_NEW = new Resume(UUID_NEW, NAME_MEW);
    private static final String UUID_EXIST = UUID_2;
    private static final String NAME_EXIST = "FullName2";
    private static final Resume RESUME_EXIST = new Resume(UUID_EXIST);
    private static final String UUID_NOT_EXIST = "dummy";
    private static final String NAME_NOT_EXIST = "dummy_name";
    private static final Resume RESUME_NOT_EXIST = new Resume(UUID_NOT_EXIST);

    protected AbstractStorageTest(Storage storage) {
        this.storage = storage;
    }

    @BeforeEach
    public void setUp() {
        storage.clear();
        storage.save(RESUME_1);
        storage.save(RESUME_2);
        storage.save(RESUME_3);
    }

    @Test
    public void size() {
        assertSize(INITIAL_SIZE);
    }

    @Test
    public void wrongSize() {
        assertWrongSize(WRONG_INITIAL_SIZE);
    }


    private void assertSize(int size) {
        assertEquals(size, storage.size());
    }

    public void assertWrongSize(int wrongSize) {
        assertThrows(AssertionFailedError.class,() ->
                assertSize(wrongSize));
    }

    @Test
    public void clear() {
        storage.clear();
        assertSize(0);
        assertArrayEquals(new Resume[0], storage.getAllSorted().toArray());
    }

    @Test
    public void update() {
        storage.update(RESUME_EXIST);
        assertSame(RESUME_EXIST, storage.get(UUID_EXIST));
    }

    @Test
    public void updateNotExist() {
        assertThrows(NotExistStorageException.class,() ->
                storage.update(RESUME_NOT_EXIST));
    }

    @Test
    public void save() {
        storage.save(RESUME_NEW);
        assertGet(RESUME_NEW);
        assertSize(INITIAL_SIZE + 1);
    }

    @Test
    public void saveExist() {
        assertThrows(ExistStorageException.class,() ->
                storage.save(RESUME_EXIST));
    }

    @Test
    public void delete() {
        storage.delete(UUID_EXIST);
        assertSize(INITIAL_SIZE - 1);
    }

    @Test
    public void deleteNotExist() {
        Assertions.assertThrows(NotExistStorageException.class, () ->
                storage.delete(UUID_NOT_EXIST));
    }

    @Test
    public void getAllSorted() {
        Resume[] expected = new Resume[]{RESUME_1, RESUME_2, RESUME_3};
        assertArrayEquals(storage.getAllSorted().toArray(), expected);
    }

    @Test
    public void get() {
        assertGet(RESUME_1);
        assertGet(RESUME_2);
        assertGet(RESUME_3);
    }

    private void assertGet(Resume resume) {
        Assertions.assertEquals(resume, storage.get(resume.getUuid()));
    }

    @Test
    public void getNotExist() {
        Exception e = assertThrows(NotExistStorageException.class, () ->
                storage.get(UUID_NOT_EXIST));
        System.out.println(e.getMessage());
    }
}