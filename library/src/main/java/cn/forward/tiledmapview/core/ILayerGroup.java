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
 
package cn.forward.tiledmapview.core;

/**
 * @author ziwei huang
 */
public interface ILayerGroup<T extends ILayer> extends ILayer {

    public void add(T layer);

    public void add(int index, T layer);

    public void clear();

    public T get(int index);

    public int size();

    public boolean remove(T layer);

    public T removeAt(int index);

    public int indexOf(T layer);

    public boolean contains(T layer);
}


