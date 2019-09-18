package jme3test.hellonode;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.shape.Line;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import jme3tools.optimize.GeometryBatchFactory;

/**
 * Sample 2 - How to use nodes as handles to manipulate objects in the scene.
 * You can rotate, translate, and scale objects by manipulating their parent
 * nodes. The Root Node is special: Only what is attached to the Root Node
 * appears in the scene.
 */
public class HelloNode extends SimpleApplication {

    public static void main(String[] args) {
        HelloNode app = new HelloNode();
        
        AppSettings settings = new AppSettings(true);
        //settings.getSettingsDialogImage()
        settings.setTitle("3D Graphing Calculator");
        ClassLoader classLoader = app.getClass().getClassLoader();
        BufferedImage[] icons = new BufferedImage[2];
        BufferedImage img = null;
        try {
            icons[0] = ImageIO.read(classLoader.getResource("graph icon 32x32.png"));
        } catch (IOException e) {
            System.err.println("Could not find image");
        }

        try {
            icons[1] = ImageIO.read(classLoader.getResource("graph icon 32x32.png"));
        } catch (IOException e) {
            System.err.println("Could not find image");
        }
        settings.setIcons(icons);
        settings.setSettingsDialogImage("dialog image.png");
        settings.setResizable(true);
        app.setDisplayStatView(false);
        app.setDisplayFps(false);
        settings.setFullscreen(true);
        settings.setVSync(true);
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int w = gd.getDisplayMode().getWidth();
        int h = gd.getDisplayMode().getHeight();
        settings.setResolution(w, h);
        settings.setSamples(16);
        app.setSettings(settings);
        app.start();
    }
    
    private String txtB
            = "Made by Robert";
    private int res;
    private int range;
    private int xmin, xmax, ymin, ymax, zCenter;
    private Vector3f center;
    private Geometry geometry1;
    private Material mat2, mat3;
    private float speed;
    private float timeCount;
    private BitmapText helloText, txt, txt2, ztxt,
            xMinTxt, xMaxTxt, yMinTxt, yMaxTxt, resTxt, xTxt, xComma, xParen, yTxt, yComma, yParen, rTxt, errorTxt;
    private BitmapText[][] menu, typeMenu;
    private ArrayList<Line> axis;
    private ColorRGBA comp, mat3Color, mat2Color, mat1Color, viewPortColor;
    private Graph grid;
    private int menuRow, menuCol;
    
    @Override
    public void simpleInitApp() {
        comp = ColorRGBA.White;
        timeCount = 0;
        initGraph();
        initKeys();
        initText();
        speed = 20;
        flyCam.setMoveSpeed(speed);
        cam.setFrustumPerspective(45.0f, (float) settings.getWidth() / (float) settings.getHeight(), 0.01f, 10000);
        Node pivot = new Node("pivot");
        rootNode.attachChild(pivot); // put this node in the scene       
        GeometryBatchFactory.optimize(rootNode);
    }

    private void initGraph() {
        System.out.println("Near:" + cam.getFrustumNear() + " Far:" + cam.getFrustumFar());
        mat2Color = ColorRGBA.Red;
        mat3Color = ColorRGBA.Green;
        Material mat1 = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Blue);
        mat2 = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", mat2Color);
        mat3 = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat3.setColor("Color", mat3Color);

        grid = new Graph();
        Line[] lines = grid.getLines();
        for (Line line1 : lines) {
            if (line1 != null) {
                Geometry lineGeo = new Geometry("Line", line1);
                lineGeo.setMaterial(mat3);
                rootNode.attachChild(lineGeo);
            }
        }
        axis = new ArrayList();
        axis.add(new Line(new Vector3f(0, 0, 0), new Vector3f(grid.getXMax(), 0, 0)));
        axis.add(new Line(new Vector3f(grid.getXMin(), 0, 0), new Vector3f(0, 0, 0)));
        axis.add(new Line(new Vector3f(0, 0, 0), new Vector3f(0, 0, grid.getYMax())));
        axis.add(new Line(new Vector3f(0, 0, grid.getYMin()), new Vector3f(0, 0, 0)));
        axis.add(new Line(new Vector3f(0, -1 * grid.getXMax(), 0), new Vector3f(0, grid.getXMax(), 0)));

        for (int i = 0; i < 5; i++) {
            geometry1 = new Geometry("Axis", axis.get(i));
            geometry1.setMaterial(mat2);
            rootNode.attachChild(geometry1);
        }
        cam.setLocation(new Vector3f(grid.getXMin(), grid.getYMax(), grid.getYMax()));
    }

    private void initText() {
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        helloText = new BitmapText(guiFont, false);
        /*helloText.setSize(guiFont.getCharSet().getRenderedSize());
        helloText.setText("3D Calculator by Robert Wetzler");
        helloText.setLocalTranslation(300, helloText.getLineHeight(), 0);*/
        guiNode.attachChild(helloText);
        BitmapFont calibri = assetManager.loadFont("Interface/Fonts/Calibri.fnt");
        calibri.setStyle(0);
        BitmapFont bCalibri = assetManager.loadFont("Interface/Fonts/bCalibri.fnt");
        bCalibri.setStyle(1);
        calibri.merge(bCalibri);
        ztxt = new BitmapText(calibri, false);
        txt = new BitmapText(calibri, false);
        xTxt = new BitmapText(calibri, false);
        xMinTxt = new BitmapText(calibri, false);
        xComma = new BitmapText(calibri, false);
        xMaxTxt = new BitmapText(calibri, false);
        xParen = new BitmapText(calibri, false);
        yTxt = new BitmapText(calibri, false);
        yMinTxt = new BitmapText(calibri, false);
        yComma = new BitmapText(calibri, false);
        yMaxTxt = new BitmapText(calibri, false);
        yParen = new BitmapText(calibri, false);
        rTxt = new BitmapText(calibri, false);
        resTxt = new BitmapText(calibri, false);
        errorTxt = new BitmapText(calibri, false);
        ztxt.setText("z = ");
        txt.setText(grid.getFunction());
        xTxt.setText("X-Range: (");
        xMinTxt.setText("" + grid.getXMin());
        xComma.setText(",");
        xMaxTxt.setText("" + grid.getXMax());
        xParen.setText(")");
        yTxt.setText("Y-Range: (");
        yMinTxt.setText("" + grid.getYMin());
        yComma.setText(",");
        yMaxTxt.setText("" + grid.getYMax());
        yParen.setText(")");
        rTxt.setText("Resolution: ");
        resTxt.setText("" + grid.getResolution());
        menu = new BitmapText[][]{{ztxt, txt}, {xTxt, xMinTxt, xComma, xMaxTxt, xParen}, {yTxt, yMinTxt, yComma, yMaxTxt, yParen}, {rTxt, resTxt}, {errorTxt}};
        typeMenu = new BitmapText[][]{{txt}, {xMinTxt, xMaxTxt}, {yMinTxt, yMaxTxt}, {resTxt}};
        for (int i = 0; i < menu.length; i++) {
            for (int j = 0; j < menu[i].length; j++) {
                menu[i][j].setSize(calibri.getPreferredSize());
                menu[i][j].setColor(mat2Color);
                if (j == 0) {
                    menu[i][j].setLocalTranslation(0, (int) settings.getHeight() - (i * menu[i][j].getLineHeight()), 0);
                } else {
                    menu[i][j].setLocalTranslation(menu[i][j - 1].getLocalTranslation().add(menu[i][j - 1].getLineWidth(), 0, 0));
                }
            }
        }
        menuRow = 0;
        menuCol = 0;
    }

    private void initKeys() {
        //add key mappings
        inputManager.addMapping("Speed", new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addMapping("Slow", new KeyTrigger(KeyInput.KEY_LCONTROL));
        inputManager.addMapping("RandomColor", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("DefaultColor", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("ViewText", new KeyTrigger(KeyInput.KEY_TAB));
        //add listeners
        inputManager.addListener(actionListener, "Speed", "Slow", "DefaultColor", "RandomColor", "ViewText");
        inputManager.addRawInputListener(textListener);
    }

    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {

            if ((name.equals("Speed")) && !keyPressed && !inputManager.isCursorVisible()) {
                speed *= (float) 3 / 2;
                flyCam.setMoveSpeed(speed);
            }
            if ((name.equals("RandomColor")) && !keyPressed && !inputManager.isCursorVisible()) {
                viewPortColor = ColorRGBA.randomColor();
                comp = new ColorRGBA(1 - viewPortColor.getRed(), 1 - viewPortColor.getGreen(), 1 - viewPortColor.getBlue(), 1.0f);
                txt.setColor(comp);
                ztxt.setColor(comp);
                mat2Color = ColorRGBA.randomColor();
                mat3Color = ColorRGBA.randomColor();
                mat2.setColor("Color", mat2Color);
                mat3.setColor("Color", mat3Color);
                viewPort.setBackgroundColor(viewPortColor);
                helloText.setColor(comp);
            }
            if ((name.equals("DefaultColor")) && !keyPressed && !inputManager.isCursorVisible()) {
                mat3Color = ColorRGBA.Green;
                mat2Color = ColorRGBA.Red;
                comp = ColorRGBA.White;
                viewPortColor = ColorRGBA.BlackNoAlpha;
                mat3.setColor("Color", mat3Color);
                mat2.setColor("Color", mat2Color);
                viewPort.setBackgroundColor(viewPortColor);
                helloText.setColor(comp);
            }

            if ((name.equals("Slow")) && !keyPressed && !inputManager.isCursorVisible()) {
                speed *= (float) 2 / 3;
                flyCam.setMoveSpeed(speed);
            }
            if ((name.equals("ViewText")) && !keyPressed) {
                flyCam.setEnabled(!flyCam.isEnabled());
                if (!flyCam.isEnabled()) {
                    inputManager.setCursorVisible(true);
                    for (BitmapText[] row : menu) {
                        for (BitmapText text : row) {
                            if (text != null) {
                                guiNode.attachChild(text);
                            }
                        }
                    }
                } else {
                    inputManager.setCursorVisible(false);
                    for (BitmapText[] row : menu) {
                        for (BitmapText text : row) {
                            if (text != null) {
                                guiNode.detachChild(text);
                            }
                        }
                    }
                }

            }

        }
    };

    private RawInputListener textListener = new RawInputListener() {
        private StringBuilder str = new StringBuilder();

        @Override
        public void onMouseMotionEvent(MouseMotionEvent evt) {

        }

        @Override
        public void beginInput() {
        }

        @Override
        public void endInput() {
        }

        @Override
        public void onJoyAxisEvent(JoyAxisEvent evt) {
        }

        @Override
        public void onJoyButtonEvent(JoyButtonEvent evt) {
        }

        @Override
        public void onMouseButtonEvent(MouseButtonEvent evt) {
            /*flyCam.setEnabled(!flyCam.isEnabled());
            inputManager.setCursorVisible(flyCam.isEnabled());
            timeCount = 0; */
        }

        @Override
        public void onKeyEvent(KeyInputEvent evt) {
            if (inputManager.isCursorVisible()) {
                if (evt.isReleased()) {
                    return;
                }

                if (evt.getKeyChar() == '\n' || evt.getKeyChar() == '\r' && str.length() > 0) {
                    graph(txt.getText(), Float.valueOf(xMinTxt.getText()), Float.valueOf(xMaxTxt.getText()), Float.valueOf(yMinTxt.getText()), Float.valueOf(yMaxTxt.getText()), Integer.valueOf(resTxt.getText()));
                    System.out.println(str);
                    str.setLength(0);
                } else if (evt.getKeyChar() == '\b' && typeMenu[menuRow][menuCol].getText().length() > 0) {
                    str = str = new StringBuilder(typeMenu[menuRow][menuCol].getText());
                    str.deleteCharAt(str.length() - 1);
                    typeMenu[menuRow][menuCol].setText(str.toString());
                    if (menuRow == 1 || menuRow == 2) {
                        for (int i = 1; i < menu[menuRow].length; i++) {
                            menu[menuRow][i].setLocalTranslation(menu[menuRow][i - 1].getLocalTranslation().add(menu[menuRow][i - 1].getLineWidth(), 0, 0));
                        }
                    }

                } else if (evt.getKeyCode() == KeyInput.KEY_DIVIDE || (evt.getKeyCode() != 42 && evt.getKeyCode() != 14 && evt.getKeyCode() != 15 && evt.getKeyCode() < 100)) {
                    System.out.println(evt);
                    str = new StringBuilder(typeMenu[menuRow][menuCol].getText());
                    str.append(evt.getKeyChar());
                    typeMenu[menuRow][menuCol].setText(str.toString());
                    if (menuRow == 1 || menuRow == 2) {
                        for (int i = 1; i < menu[menuRow].length; i++) {
                            menu[menuRow][i].setLocalTranslation(menu[menuRow][i - 1].getLocalTranslation().add(menu[menuRow][i - 1].getLineWidth(), 0, 0));
                        }
                    }
                } else if (evt.getKeyCode() == KeyInput.KEY_DOWN) {
                    menuCol = 0;
                    if (menuRow < menu.length - 2) {
                        menuRow++;
                    } else {
                        menuRow = 0;
                    }
                } else if (evt.getKeyCode() == KeyInput.KEY_UP) {
                    menuCol = 0;
                    if (menuRow > 0) {
                        menuRow--;
                    } else {
                        menuRow = menu.length - 2;
                    }
                } else if (evt.getKeyCode() == KeyInput.KEY_RIGHT) {
                    if (menuCol < typeMenu[menuRow].length - 1) {
                        menuCol++;
                    } else {
                        menuCol = 0;
                    }
                } else if (evt.getKeyCode() == KeyInput.KEY_LEFT) {
                    if (menuCol > 0) {
                        menuCol--;
                    } else {
                        menuCol = typeMenu[menuRow].length - 1;
                    }
                }

            }
        }

        @Override
        public void onTouchEvent(TouchEvent evt) {

        }
    };

    private void graph(String function, float xMin, float xMax, float yMin, float yMax, int res) {
        rootNode.detachAllChildren();
        Material mat1 = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Blue);
        mat2 = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", mat2Color);
        mat3 = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat3.setColor("Color", mat3Color);
        viewPort.setBackgroundColor(viewPortColor);
        //Graph grid = new Graph(function);
        grid = new Graph(function, xMin, xMax, yMin, yMax, res);

        Line[] lines = grid.getLines();
        for (Line line1 : lines) {
            if (line1 != null) {
                Geometry lineGeo = new Geometry("Line", line1);
                lineGeo.setMaterial(mat3);
                rootNode.attachChild(lineGeo);
            }
        }
        axis = new ArrayList();
        axis.add(new Line(new Vector3f(0, 0, 0), new Vector3f(grid.getXMax(), 0, 0)));
        axis.add(new Line(new Vector3f(grid.getXMin(), 0, 0), new Vector3f(0, 0, 0)));
        axis.add(new Line(new Vector3f(0, 0, 0), new Vector3f(0, grid.getYMax(), 0)));
        axis.add(new Line(new Vector3f(0, grid.getYMin(), 0), new Vector3f(0, 0, 0)));
        axis.add(new Line(new Vector3f(0, 0, -1 * grid.getXMax()), new Vector3f(0, 0, grid.getXMax())));

        for (int i = 0; i < axis.size(); i++) {
            geometry1 = new Geometry("Axis", axis.get(i));
            geometry1.setMaterial(mat2);
            rootNode.attachChild(geometry1);
        }
        if (grid.hasError()) {
            errorTxt.setText("Error: " + grid.getErrorInfo());
        } else {
            errorTxt.setText("");
        }
        GeometryBatchFactory.optimize(rootNode);
    }

    @Override
    public void simpleUpdate(float tpf) {
        cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
        if (!flyCam.isEnabled()) {
            for (int i = 0; i < typeMenu.length; i++) {
                for (int j = 0; j < typeMenu[i].length; j++) {
                    if (typeMenu[i][j] != null) {
                        if (i == menuRow & j == menuCol) {

                            typeMenu[i][j].setStyle(0, txt.getText().length(), 1);
                            typeMenu[i][j].setColor(mat2Color);

                        } else {
                            typeMenu[i][j].setStyle(0, txt.getText().length(), 0);
                            typeMenu[i][j].setColor(comp);
                        }
                    }
                }

            }

        }
    }
}
