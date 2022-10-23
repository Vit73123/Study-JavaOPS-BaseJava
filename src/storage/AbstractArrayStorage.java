package storage;

import model.Resume;

import java.util.Arrays;

/**
 * Array based storage for Resumes
 */
public abstract class AbstractArrayStorage implements Storage {
    protected static final int STORAGE_LIMIT = 10000;
    protected final Resume[] storage = new Resume[STORAGE_LIMIT];
    protected int size = 0;

    public int size() {
        return size;
    }

    public void clear() {
        Arrays.fill(storage, 0, size, null);
        size = 0;
    }

    public void update(Resume r) {
        int index = findSearchKey(r.getUuid());
        if (index == -1) {
            System.out.println("Ошибка: резюме " + r.getUuid() + " нет.");
        }
        storage[index] = r;
    }

    public void save(Resume r) {
        if (size == storage.length) {
            System.out.println("Ошибка: достигнут предел количества резюме.");
            return;
        }
        if (saveResume(r) != -1) {
            System.out.println("Ошибка: резюме " + r.getUuid() + " уже есть.");
        }
    }

    protected abstract int saveResume(Resume r);

    public void delete(String uuid) {
        int index = findSearchKey(uuid);
        if (index == -1) {
            System.out.println("Ошибка: резюме " + uuid + " нет.");
        } else {
            deleteResume(index);
        }
    }

    protected abstract void deleteResume(int index);

    /**
     * @return array, contains only Resumes in storage (without null)
     */
    public Resume[] getAll() {
        return Arrays.copyOf(storage, size);
    }

    public Resume get(String uuid) {
        int index = findSearchKey(uuid);
        if (index == -1) {
            System.out.println("Ошибка: резюме " + uuid + " нет.");
            return null;
        }
        return storage[index];
    }

    protected abstract int findSearchKey(String uuid);
}