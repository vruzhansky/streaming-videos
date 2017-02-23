package org.blotter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class YouTube {
    final int numOfCaches;
    final int cacheSize;
    final List<Endpoint> endpoints;

    public YouTube(int numOfCaches, int cacheSize, Collection<Endpoint> endpoints) {
        this.numOfCaches = numOfCaches;
        this.cacheSize = cacheSize;
        this.endpoints = new ArrayList<>(endpoints);
    }
}
