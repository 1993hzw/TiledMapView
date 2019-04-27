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
public interface ITileLayer extends ILayer {
    public void clearCache();

    public ITileImageCache getTileImageCache();

    public void setTileImageCache(ITileImageCache tileImageCache);

    public void setOffscreenTileLimit(int limit);

    /**
     * Set the number of tiles that should be retained to either side of the current screen.
     * 设置应保留到当前屏幕任一侧的切片数量
     */
    public int getOffscreenTileLimit();

}


