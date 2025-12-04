package net.laurus.starmapper.ui.component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.laurus.starmapper.model.Star;

/**
 * Lightweight KD-tree for 3D points (only used for nearest/range queries). Not
 * highly optimized but fine for ~10k-100k points.
 */
public class KDTree {

    private final Node root;

    public KDTree(List<Star> points) {
        this.root = build(points, 0);
    }

    private static class Node {

        Star star;

        Node left, right;

        int axis;

        Node(Star s, int axis) {
            this.star = s;
            this.axis = axis;
        }

    }

    private Node build(List<Star> pts, int depth) {
        if (pts == null || pts.isEmpty())
            return null;
        int axis = depth % 3;
        pts.sort(Comparator.comparingDouble(s -> getCoord(s, axis)));
        int mid = pts.size() / 2;
        Node n = new Node(pts.get(mid), axis);
        n.left = build(new ArrayList<>(pts.subList(0, mid)), depth + 1);
        n.right = build(new ArrayList<>(pts.subList(mid + 1, pts.size())), depth + 1);
        return n;
    }

    private static double getCoord(Star s, int axis) {
        return (axis == 0) ? s.getX() : (axis == 1) ? s.getY() : s.getZ();
    }

    /**
     * Nearest neighbour to (x,y,z) within optional maxDist (use
     * Double.POSITIVE_INFINITY if none)
     */
    public Star nearest(double x, double y, double z, double maxDist) {
        NearestState st = new NearestState(maxDist * maxDist);
        searchNearest(root, x, y, z, st);
        return st.best;
    }

    private static class NearestState {

        double bestSq;

        Star best;

        NearestState(double maxSq) {
            this.bestSq = maxSq;
            this.best = null;
        }

    }

    private void searchNearest(Node node, double x, double y, double z, NearestState st) {
        if (node == null)
            return;
        double dx = node.star.getX() - x;
        double dy = node.star.getY() - y;
        double dz = node.star.getZ() - z;
        double distSq = dx * dx + dy * dy + dz * dz;

        if (distSq < st.bestSq) {
            st.bestSq = distSq;
            st.best = node.star;
        }

        int axis = node.axis;
        double deltaAxis = getAxisVal(node.star, axis) - (axis == 0 ? x : (axis == 1 ? y : z));
        Node first = (deltaAxis > 0) ? node.left : node.right;
        Node second = (deltaAxis > 0) ? node.right : node.left;
        if (first != null)
            searchNearest(first, x, y, z, st);
        if (second != null && deltaAxis * deltaAxis < st.bestSq)
            searchNearest(second, x, y, z, st);
    }

    private double getAxisVal(Star s, int axis) {
        return (axis == 0) ? s.getX() : (axis == 1) ? s.getY() : s.getZ();
    }

    /** Range query: return stars with squared distance <= rSq */
    public List<Star> range(double cx, double cy, double cz, double r) {
        double rSq = r * r;
        List<Star> out = new ArrayList<>();
        rangeSearch(root, cx, cy, cz, rSq, out);
        return out;
    }

    private void
            rangeSearch(Node node, double cx, double cy, double cz, double rSq, List<Star> out) {
        if (node == null)
            return;
        double dx = node.star.getX() - cx;
        double dy = node.star.getY() - cy;
        double dz = node.star.getZ() - cz;
        double dsq = dx * dx + dy * dy + dz * dz;
        if (dsq <= rSq)
            out.add(node.star);
        int axis = node.axis;
        double delta = (axis == 0 ? cx - node.star.getX()
                : axis == 1 ? cy - node.star.getY()
                : cz - node.star.getZ());
        if (delta <= 0)
            rangeSearch(node.left, cx, cy, cz, rSq, out);
        if (Math.abs(delta) <= Math.sqrt(rSq))
            rangeSearch(node.right, cx, cy, cz, rSq, out);
    }

}
