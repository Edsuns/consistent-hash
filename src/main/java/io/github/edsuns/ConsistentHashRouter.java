package io.github.edsuns;

import java.io.Serializable;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * Created by Edsuns@qq.com on 2022/6/22.
 */
public class ConsistentHashRouter<T> {

    public interface Hash extends Comparable<Hash>, Serializable {
    }

    @FunctionalInterface
    public interface HashFunction extends Function<Object, Hash> {
    }

    private final HashFunction hashFunction;
    private final int numberOfReplicas;
    private final SortedMap<Hash, T> circle = new TreeMap<>();

    public ConsistentHashRouter(int numberOfReplicas, Collection<T> nodes) {
        this(new MD5HashFunction(), numberOfReplicas, nodes);
    }

    public ConsistentHashRouter(HashFunction hashFunction, int numberOfReplicas, Collection<T> nodes) {
        this.hashFunction = hashFunction;
        this.numberOfReplicas = numberOfReplicas;

        for (T node : nodes) {
            add(node);
        }
    }

    public void add(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.put(hashFunction.apply(node.toString() + i), node);
        }
    }

    public void remove(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.remove(hashFunction.apply(node.toString() + i));
        }
    }

    public T get(Object key) {
        if (circle.isEmpty()) return null;

        Hash hash = hashFunction.apply(key);

        T node;
        if ((node = circle.get(hash)) != null) {
            return node;
        }

        SortedMap<Hash, T> tailMap = circle.tailMap(hash);
        hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        return circle.get(hash);
    }
}
