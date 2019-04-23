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


