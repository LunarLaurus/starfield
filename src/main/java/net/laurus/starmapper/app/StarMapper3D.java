package net.laurus.starmapper.app;

import java.util.List;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

import net.laurus.starmapper.model.Star;
import net.laurus.starmapper.util.StarLoader;

public class StarMapper3D extends SimpleApplication {

    /** Root node for all stars (so we can manipulate separately) */
    private Node starNode = new Node("Stars");

    /** Timer accumulator similar to Swing Main.update(delta) */
    private double accumulator = 0;

    public static void main(String[] args) {
        StarMapper3D app = new StarMapper3D();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        // ───────────────────────────────────────────────────────────────
        // Load stars
        // ───────────────────────────────────────────────────────────────
        List<Star> stars = StarLoader.loadStars();
        addStars(stars);
        rootNode.attachChild(starNode);

        // camera speed
        flyCam.setMoveSpeed(20f);

        addSun();
        addLighting();
    }

    // ───────────────────────────────────────────────────────────────
    // Update loop — runs every frame (replaces Swing Timer.update)
    // ───────────────────────────────────────────────────────────────
    @Override
    public void simpleUpdate(float tpf) {
        accumulator += tpf;

        final double TARGET_DT = 1.0 / 60.0;

        // Run logic at a fixed 60 FPS simulation rate
        while (accumulator >= TARGET_DT) {
            updateApp(TARGET_DT);
            accumulator -= TARGET_DT;
        }

    }

    private void updateApp(double dt) {
        // Here you move planets, rotation, zoom, picking, etc
        // Equivalent to StarMapPanel.update(dt)
    }

    // ───────────────────────────────────────────────────────────────
    // Rendering helpers
    // ───────────────────────────────────────────────────────────────

    private void addSun() {
        Sphere sunShape = new Sphere(32, 32, 2f);
        Geometry sun = new Geometry("Sun", sunShape);
        sun.setQueueBucket(RenderQueue.Bucket.Transparent);

        Material sunMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        sunMat.setBoolean("UseMaterialColors", true);
        sunMat.setColor("Diffuse", ColorRGBA.Yellow);
        sunMat.setColor("Specular", ColorRGBA.White);
        sunMat.setFloat("Shininess", 32f);
        sun.setMaterial(sunMat);

        rootNode.attachChild(sun);
    }

    private void addLighting() {
        PointLight sunLight = new PointLight();
        sunLight.setPosition(new Vector3f(0, 0, 0));
        sunLight.setColor(ColorRGBA.White.mult(2f));
        rootNode.addLight(sunLight);

        AmbientLight amb = new AmbientLight();
        amb.setColor(ColorRGBA.White.mult(0.3f));
        rootNode.addLight(amb);
    }

    private void addStars(List<Star> stars) {

        for (Star s : stars) {
            Sphere starShape = new Sphere(8, 8, 0.05f);
            Geometry g = new Geometry(s.getName(), starShape);

            Material m = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            m.setColor("Color", ColorRGBA.White);
            g.setMaterial(m);

            g.setLocalTranslation((float) s.getX(), (float) s.getY(), (float) s.getZ());

            starNode.attachChild(g);
        }

    }

}
