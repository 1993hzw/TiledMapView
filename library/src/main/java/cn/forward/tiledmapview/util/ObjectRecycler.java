/*
 * Copyright (C) 2019  Ziwei Huang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
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
