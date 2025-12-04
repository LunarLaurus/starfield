package net.laurus.starmapper.ui.component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import net.laurus.starmapper.model.Star;

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

    /** Single nearest */
    public Star nearest(double x, double y, double z, double maxDist) {
        NearestState st = new NearestState(maxDist * maxDist);
        searchNearest(root, x, y, z, st);
        return st.best;
    }

    /** k-nearest stars */
    public List<Star> nearestK(double x, double y, double z, int k) {
        PriorityQueue<StarDist> pq = new PriorityQueue<>(
                Comparator.comparingDouble(sd -> -sd.distSq)
        );
        searchK(root, x, y, z, k, pq);
        List<Star> result = new ArrayList<>();
        while (!pq.isEmpty())
            result.add(pq.poll().star);
        result.sort(Comparator.comparingDouble(s -> distanceSq(s, x, y, z)));
        return result;
    }

    private static class NearestState {

        double bestSq;

        Star best;

        NearestState(double maxSq) {
            this.bestSq = maxSq;
            this.best = null;
        }

    }

    private static class StarDist {

        Star star;

        double distSq;

        StarDist(Star s, double d) {
            star = s;
            distSq = d;
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

    private void
            searchK(Node node, double x, double y, double z, int k, PriorityQueue<StarDist> pq) {
        if (node == null)
            return;
        double dSq = distanceSq(node.star, x, y, z);
        if (pq.size() < k)
            pq.add(new StarDist(node.star, dSq));
        else if (dSq < pq.peek().distSq) {
            pq.poll();
            pq.add(new StarDist(node.star, dSq));
        }

        int axis = node.axis;
        double delta = getAxisVal(node.star, axis) - (axis == 0 ? x : (axis == 1 ? y : z));
        Node first = (delta > 0) ? node.left : node.right;
        Node second = (delta > 0) ? node.right : node.left;

        searchK(first, x, y, z, k, pq);
        if (second != null
                && delta * delta < (pq.size() < k ? Double.POSITIVE_INFINITY : pq.peek().distSq))
            searchK(second, x, y, z, k, pq);
    }

    private static double distanceSq(Star s, double x, double y, double z) {
        double dx = s.getX() - x, dy = s.getY() - y, dz = s.getZ() - z;
        return dx * dx + dy * dy + dz * dz;
    }

    private double getAxisVal(Star s, int axis) {
        return (axis == 0) ? s.getX() : (axis == 1) ? s.getY() : s.getZ();
    }

    /** Range query: return stars within radius */
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
        double dsq = distanceSq(node.star, cx, cy, cz);
        if (dsq <= rSq)
            out.add(node.star);

        int axis = node.axis;
        double delta = (axis == 0 ? cx - node.star.getX()
                : (axis == 1 ? cy - node.star.getY() : cz - node.star.getZ()));
        if (delta <= 0)
            rangeSearch(node.left, cx, cy, cz, rSq, out);
        if (Math.abs(delta) <= Math.sqrt(rSq))
            rangeSearch(node.right, cx, cy, cz, rSq, out);
    }

}
