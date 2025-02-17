package Shared;

import java.util.List;

/**
 * Wrapper-Class wich holds the hierarchy of a class and depth-of-hierarchy starting from Object
 */
public class HierarchyResult {
    private final List<String> hierarchy;
    private final int depth;

    public HierarchyResult(List<String> hierarchy, int depth) {
        this.hierarchy = hierarchy;
        this.depth = depth;
    }

    public List<String> getHierarchy() {
        return hierarchy;
    }

    public int getDepth() {
        return depth;
    }
}
