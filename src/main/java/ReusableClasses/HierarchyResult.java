package ReusableClasses;

import java.util.List;

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
