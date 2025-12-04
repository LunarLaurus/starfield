package net.laurus.starmapper.app;

import java.util.ArrayList;
import java.util.List;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.BufferUtils;

import net.laurus.starmapper.model.Star;
import net.laurus.starmapper.model.StarColour;
import net.laurus.starmapper.ui.component.KDTree;
import net.laurus.starmapper.util.StarLoader;

public class StarMapper4D extends SimpleApplication {

    private static final float SCALE_SIZE = 3f;

    private static final float SCALE_DISTANCE = 4f;

    private static final float MAX_STAR_RADIUS = 2f;

    private static final float MIN_STAR_RADIUS = 0.05f;

    private Node starsNode;

    private Node starBatchNode;

    private BitmapText hoverText;

    private Geometry highlighted = null;

    private Material defaultStarMat, highlightMat;

    private final List<Star> starCache = StarLoader.loadStars();

    private KDTree starTree;

    private double centerX, centerY, centerZ;

    private Geometry playerGeom;

    private Material playerMat;

    private final List<Geometry> nearestStarMarkers = new ArrayList<>();

    private Material nearestStarMat;

    Material starMat;

    public static void main(String[] args) {
        new StarMapper4D().start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(500f);
        inputManager.setCursorVisible(true);

        computeClusterCenter();
        setupScene();
        setupMaterials();
        addStars(starCache);

        starTree = new KDTree(starCache);

        setupGUI();
        setupInput();
        setupCamera();
        initPlayer();
    }

    private void initPlayer() {
        // Player marker
        playerMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        playerMat.setColor("Color", ColorRGBA.Cyan);
        Sphere playerSphere = new Sphere(8, 8, 0.5f); // small sphere for player
        playerGeom = new Geometry("player", playerSphere);
        playerGeom.setMaterial(playerMat);
        rootNode.attachChild(playerGeom);

        // Material for highlighting nearest stars
        nearestStarMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        nearestStarMat.setColor("Color", ColorRGBA.Orange);

    }

    private void computeClusterCenter() {
        centerX = starCache.stream().mapToDouble(Star::getX).average().orElse(0);
        centerY = starCache.stream().mapToDouble(Star::getY).average().orElse(0);
        centerZ = starCache.stream().mapToDouble(Star::getZ).average().orElse(0);
    }

    private void setupScene() {
        starsNode = new Node("stars");
        rootNode.attachChild(starsNode);

        // Scene-wide lights
        PointLight sun = new PointLight();
        sun.setPosition(Vector3f.ZERO);
        sun.setColor(ColorRGBA.White.mult(2f));
        rootNode.addLight(sun);

        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.25f));
        rootNode.addLight(ambient);
    }

    private void setupMaterials() {
        defaultStarMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        highlightMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        highlightMat.setColor("Color", ColorRGBA.Yellow);
        // highlightMat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
        starMat = new Material(assetManager, "Materials/Shaders/Star.j3md");
        starMat.setFloat("PointSize", 5f);
        starMat.getAdditionalRenderState().setBlendMode(BlendMode.Additive);
        starMat.getAdditionalRenderState().setDepthWrite(false);
    }

    private void setupGUI() {
        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
        hoverText = new BitmapText(font);
        hoverText.setSize(font.getCharSet().getRenderedSize() + 2);
        hoverText.setColor(ColorRGBA.White);
        hoverText.setText("");
        hoverText.setLocalTranslation(10, settings.getHeight() - 10, 0);
        guiNode.attachChild(hoverText);
    }

    private void setupInput() {
        inputManager.addMapping("SelectStar", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(selectStarListener(), "SelectStar");

        inputManager
                .addMapping(
                        "SpeedUp", new KeyTrigger(KeyInput.KEY_EQUALS), new KeyTrigger(
                                KeyInput.KEY_ADD
                        )
                );
        inputManager
                .addMapping(
                        "SpeedDown", new KeyTrigger(KeyInput.KEY_MINUS), new KeyTrigger(
                                KeyInput.KEY_SUBTRACT
                        )
                );
        inputManager.addListener(speedListener(), "SpeedUp", "SpeedDown");
    }

    private ActionListener selectStarListener() {
        return (name, isPressed, tpf) -> {
            if (!isPressed || !name.equals("SelectStar"))
                return;

            Vector2f cursor = inputManager.getCursorPosition();
            Vector3f origin = cam.getWorldCoordinates(cursor, 0f);

            // Use KDTree nearest search with radius
            Star nearest = starTree.nearest(origin.x, origin.y, origin.z, 2.0);
            if (nearest != null)
                selectStarById(nearest.getId());
        };
    }

    private ActionListener speedListener() {
        return (name, isPressed, tpf) -> {
            if (!isPressed)
                return;
            float step = 50f;
            if (name.equals("SpeedUp"))
                flyCam.setMoveSpeed(flyCam.getMoveSpeed() + step);
            else if (name.equals("SpeedDown"))
                flyCam.setMoveSpeed(Math.max(10f, flyCam.getMoveSpeed() - step));
            System.out.println("Camera speed: " + flyCam.getMoveSpeed());
        };
    }

    private void setupCamera() {
        cam.setLocation(new Vector3f(0, 0, 50f));
        cam.setFrustumNear(0.5f);
        cam.setFrustumFar(500f);
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    }

    private void addStars(List<Star> stars) {
        Mesh mesh = new Mesh();
        mesh.setMode(Mesh.Mode.Points);

        int n = stars.size();
        Vector3f[] positions = new Vector3f[n];
        ColorRGBA[] colors = new ColorRGBA[n];
        float[] sizes = new float[n]; // per-star size

        for (int i = 0; i < n; i++) {
            Star s = stars.get(i);

            float x = (float) ((s.getX() - centerX) * SCALE_DISTANCE);
            float y = (float) ((s.getY() - centerY) * SCALE_DISTANCE);
            float z = (float) ((s.getZ() - centerZ) * SCALE_DISTANCE);
            positions[i] = new Vector3f(x, y, z);

            StarColour c = s.getColour();
            if (c == null)
                c = StarColour.WHITE;
            colors[i] = new ColorRGBA(
                    (float) c.getRed(), (float) c.getGreen(), (float) c.getBlue(), 1f
            );

            // Set size based on star magnitude or radius
            sizes[i] = magnitudeToRadius(s.getMagnitude()) * SCALE_SIZE;
        }

        mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(positions));
        mesh.setBuffer(VertexBuffer.Type.Color, 4, BufferUtils.createFloatBuffer(colors));
        mesh.setBuffer(VertexBuffer.Type.Size, 1, BufferUtils.createFloatBuffer(sizes));
        mesh.updateBound();

        Geometry starGeom = new Geometry("starCloud", mesh);

        starGeom.setMaterial(starMat);
        starsNode.attachChild(starGeom);
    }

    private void selectStarById(int starId) {

        if (highlighted != null) {
            highlighted.setMaterial(highlighted.getUserData("starMat"));
        }

        for (Spatial sp : starBatchNode.getChildren()) {
            Geometry g = (Geometry) sp;
            Integer id = g.getUserData("starId");

            if (id != null && id == starId) {
                highlighted = g;
                g.setMaterial(highlightMat);
                Star s = starCache.get(starId);
                hoverText.setText(String.format("%s (id=%d)", s.getName(), s.getId()));
                break;
            }

        }

    }

    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);

        Vector3f camPos = cam.getLocation();
        playerGeom.setLocalTranslation(camPos);

        // Remove previous nearest markers
        for (Geometry g : nearestStarMarkers)
            rootNode.detachChild(g);
        nearestStarMarkers.clear();

        // Get 3 nearest stars
        List<Star> nearest = starTree.nearestK(camPos.x, camPos.y, camPos.z, 3);

        // Render markers
        for (Star s : nearest) {
            Sphere sphere = new Sphere(8, 8, 0.4f);
            Geometry g = new Geometry("nearest-" + s.getId(), sphere);
            g.setMaterial(nearestStarMat);
            g
                    .setLocalTranslation(
                            (float) ((s.getX() - centerX)
                                    * SCALE_DISTANCE), (float) ((s.getY() - centerY)
                                            * SCALE_DISTANCE), (float) ((s.getZ() - centerZ)
                                                    * SCALE_DISTANCE)
                    );
            rootNode.attachChild(g);
            nearestStarMarkers.add(g);
        }

        // Update GUI overlay
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Camera XYZ: [%.2f, %.2f, %.2f]\n", camPos.x, camPos.y, camPos.z));
        sb.append(String.format("FPS: %.1f\n", timer.getFrameRate()));
        sb.append("Nearest stars:\n");

        for (Star s : nearest) {
            double dist = Math
                    .sqrt(
                            (s.getX() - camPos.x) * (s.getX() - camPos.x)
                                    + (s.getY() - camPos.y) * (s.getY() - camPos.y)
                                    + (s.getZ() - camPos.z) * (s.getZ() - camPos.z)
                    );
            sb.append(String.format("%s (%.2f)\n", s.getName(), dist));
        }

        hoverText.setText(sb.toString());
    }

    private float magnitudeToRadius(double magnitude) {
        // Invert magnitude: smaller number → bigger star
        // Clamp the output between MIN_STAR_RADIUS and MAX_STAR_RADIUS
        float radius = (float) ((MAX_STAR_RADIUS - MIN_STAR_RADIUS)
                * Math.pow(2.512, -magnitude / 2.5));
        return Math.min(MAX_STAR_RADIUS, Math.max(MIN_STAR_RADIUS, radius));
    }

}
