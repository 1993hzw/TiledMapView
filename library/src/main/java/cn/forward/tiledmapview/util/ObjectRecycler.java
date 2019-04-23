package cn.forward.tiledmapview.util;

public class ObjectRecycler<T> {

    private ObjectGenerator<T> mGenerator;
    private int mMaxRowCount, mMaxColCount;
    private T[][] mObjects;

    public ObjectRecycler(ObjectGenerator<T> generator) {
        mGenerator = generator;
    }

    public void resize(int rowCount, int colCount) {
        T[][] old = mObjects;
        boolean needResize = false;
        if (mMaxRowCount < rowCount) {
            mMaxRowCount = rowCount;
            needResize = true;
        }

        if (mMaxColCount < colCount) {
            mMaxColCount = colCount;
            needResize = true;
        }

        if (old == null || needResize) {
            mObjects = (T[][]) new Object[mMaxRowCount][mMaxColCount];
            for (int row = 0; row < mMaxRowCount; row++) {
                for (int col = 0; col < mMaxColCount; col++) {
                    if (old != null && row < old.length && col < old[0].length) {
                        mObjects[row][col] = old[row][col];
                    } else {
                        mObjects[row][col] = mGenerator.generate();
                    }
                }
            }
        }
    }


    public T get(int row, int col) {
        return mObjects[row % mObjects.length][col % mObjects[0].length];
    }

    public interface ObjectGenerator<T> {
        T generate();
    }
}
