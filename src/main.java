import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.jogamp.opengl.GL.*;


public class main extends JFrame implements GLEventListener, KeyListener{

    private GLU glu = new GLU();
    private int size = 3;
    cube cube;
    GL2 gl;
    private float aspect;
    List<Integer> textures = new ArrayList<Integer>();
    private final String[] colors = {"red", "green", "blue", "orange", "yellow", "white", "black"};
    private int mouseX = 0;
    private int mouseY = 0;
    private float cameraAngleX = 0f;
    private float cameraAngleY = 0f;
    private float cameraAngleZ = 8f;
    private float rotationSpeed = 20f;
    private final float rotationMaxSpeed = 40f;
    private boolean isLightOn;
    private int TexBackground;

    public main(String string) {
        super(string);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Toolkit kit=Toolkit.getDefaultToolkit();
        Dimension d=kit.getScreenSize();
        setBounds(d.width/4, d.height/4, d.width/2, d.height/2);
        mouseX = d.width/2;
        mouseY = d.height/2;
        GLProfile profile=GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities=new GLCapabilities(profile);
        GLCanvas canvas=new GLCanvas(capabilities);
        canvas.addGLEventListener(this);
        canvas.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int moved = e.getWheelRotation();
                if (moved == -1) cameraAngleZ += 0.3;
                else if (moved == 1) cameraAngleZ -= 0.3;

            }
        });
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                super.mouseDragged(e);
                int buffer = 2;

                if (e.getX() < mouseX - buffer) cameraAngleX += 5.0;
                else if (e.getX() > mouseX + buffer) cameraAngleX -= 5.0;

                if (e.getY() < mouseY - buffer) cameraAngleY -= 5.0;
                else if (e.getY() > mouseY + buffer) cameraAngleY += 5.0;
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });


        setLayout(new BorderLayout());
        add(canvas);

        JPanel panelBottom = new JPanel(new FlowLayout());
        JButton btnReset = new JButton("RESET");
        JButton btnShuffle = new JButton("SHUFFLE");
        JButton btnPlus = new JButton("+");
        JButton btnMinus = new JButton("-");
        JButton btnLight = new JButton("LIGHT");
        JSlider sdlSpeed = new JSlider(JSlider.HORIZONTAL, 1, (int)rotationMaxSpeed, (int)rotationSpeed);

        panelBottom.add(btnPlus);
        panelBottom.add(btnMinus);
        panelBottom.add(btnReset);
        panelBottom.add(btnShuffle);
        panelBottom.add(btnLight);
        panelBottom.add(new JLabel("SPEED:"));
        panelBottom.add(sdlSpeed);
        add(panelBottom, BorderLayout.SOUTH);
        setVisible(true);
        final FPSAnimator animator = new FPSAnimator(canvas,200,true);
        animator.start();
        canvas.addKeyListener(this);
        btnReset.addActionListener(e -> {cube = new cube(gl, textures, size);
                                    resetGlobalRotation();});

        btnShuffle.addActionListener(e -> cube.shuffle(0));

        btnMinus.addActionListener(e -> {if(size>3) size--;
                                    cube = new cube(gl, textures, size);});

        btnPlus.addActionListener(e -> { size++;
                                    cube = new cube(gl, textures, size);});

        btnLight.addActionListener(e -> {isLightOn = !isLightOn;
                                        if(isLightOn) btnLight.setBackground(Color.GREEN);
                                        else btnLight.setBackground(btnReset.getBackground());});


        sdlSpeed.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if(!source.getValueIsAdjusting()){
                    rotationSpeed = rotationMaxSpeed - source.getValue();
                }
            }
        });
    }
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        System.out.println(e.getKeyChar());
        switch (e.getKeyChar()) {
            /*
            section 0:rows; 1:walls; 2:columns;
            direction 1:clockwise; -1:counterclockwise;
            Shift+Letter for direction change
             */
            case 'e' -> cube.startRotate(0,0,1);
            case 'd' -> cube.startRotate(0,1,1);
            case 'c' -> cube.startRotate(0,2,1);

            case 'w' -> cube.startRotate(1,0,1);
            case 's' -> cube.startRotate(1,1,1);
            case 'x' -> cube.startRotate(1,2,1);

            case 'q' -> cube.startRotate(2,0,1);
            case 'a' -> cube.startRotate(2,1,1);
            case 'z' -> cube.startRotate(2,2,1);

            case 'E' -> cube.startRotate(3,0,-1);
            case 'D' -> cube.startRotate(3,1,-1);
            case 'C' -> cube.startRotate(3,2,-1);

            case 'W' -> cube.startRotate(4,0,-1);
            case 'S' -> cube.startRotate(4,1,-1);
            case 'X' -> cube.startRotate(4,2,-1);

            case 'Q' -> cube.startRotate(5,0,-1);
            case 'A' -> cube.startRotate(5,1,-1);
            case 'Z' -> cube.startRotate(5,2,-1);

        }
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        setWindowBackground(TexBackground);
        lightControl();
        setLightPosition(GL2.GL_LIGHT0,new float[] {(float)size+1,0.0f,0.0f,1.0f});
        gl.glLoadIdentity();

        cube.setCameraAngle(cameraAngleX, cameraAngleY, 0f);
        cube.rebuild(rotationSpeed);
        camera(aspect);
        gl.glFlush();

    }

    private void lightControl() {

        if (isLightOn) {
            gl.glEnable(gl.GL_LIGHTING);
        } else {
            gl.glDisable(gl.GL_LIGHTING);
        }
    }

    private void setWindowBackground(int texture) {
        gl.glDisable(GL2.GL_DEPTH_TEST);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texture);
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f,0.0f); gl.glVertex3f(-1.0f,-1.0f,-1.0f);
        gl.glTexCoord2f(1.0f,0.0f); gl.glVertex3f( 1.0f,-1.0f,-1.0f);
        gl.glTexCoord2f(1.0f,1.0f); gl.glVertex3f( 1.0f, 1.0f,-1.0f);
        gl.glTexCoord2f(0.0f,1.0f); gl.glVertex3f(-1.0f, 1.0f,-1.0f);
        gl.glEnd();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_DEPTH_TEST);
    }


    private void resetGlobalRotation()
    {
        cameraAngleX = 0f;
        cameraAngleY = 0f;
        cameraAngleZ = 8f;
        size = 3;
    }
    @Override
    public void dispose(GLAutoDrawable drawable) {

    }

    @Override
    public void init(GLAutoDrawable drawable) {
        gl=drawable.getGL().getGL2();
        glu=GLU.createGLU(gl);
        gl.glClearColor(0.5f, 0.5f, 1f, 1.0f);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glEnable(GL_TEXTURE_2D);
        gl.glEnable(GL2.GL_CULL_FACE);
        //setup
        setUpLight();
        loadTextures();
        //create cube
        cube = new cube(gl, textures, size);
    }

    private void loadTextures() {
        try{
            File f=new File("textures/bg.jpg");
            Texture t=TextureIO.newTexture(f, true);
            TexBackground = t.getTextureObject(gl);
        }catch(IOException e){
            e.printStackTrace();
        }
        Arrays.stream(colors).forEach((x) -> {
            try{
                File f=new File("textures/%s.png".formatted(x));
                Texture t=TextureIO.newTexture(f, true);
                textures.add(t.getTextureObject(gl));
            }catch(IOException e){
                e.printStackTrace();
            }
        });
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
                        int height) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(0, 0, width, height);
        if(height==0)
            height=1;
        aspect=(float)width/height;
        camera(aspect);
    }

public void camera(float aspect){
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();
    glu.gluPerspective(60.0, aspect, 1.0, 50f);
    glu.gluLookAt(cameraAngleZ, 0f, 0f,
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f);
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glLoadIdentity();
}
private void setLightPosition(int light, float[] position) {
        gl.glLightfv(light, GL2.GL_POSITION,position,0);
    }
private void setUpLight(){
    float matSpec[]={1.0f,1.0f,1.0f,1.0f};
    gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpec, 0);
    gl.glMateriali(GL2.GL_FRONT, GL2.GL_SHININESS, 128);

    gl.glEnable(GL2.GL_COLOR_MATERIAL);
    gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);

    float ambientLight[]={0.1f,0.1f,0.1f,1.0f};
    gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, ambientLight, 0);

    gl.glEnable(GL2.GL_LIGHTING);

    float ambient[]={0.1f,0.1f,0.1f,1.0f};
    float diffuse[]={0.5f,0.5f,0.5f,1.0f};
    float specular[]={1.0f,1.0f,1.0f,1.0f};
    float position[]={(float)size,0.0f,0.0f,1.0f};


    gl.glLightfv(GL2.GL_LIGHT0,GL2.GL_AMBIENT,ambient,0);
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse,0);
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, specular,0);
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position,0);

    gl.glEnable(GL2.GL_LIGHT0);

    gl.glEnable(gl.GL_LIGHTING);

    isLightOn = false;

}
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new main("Rubiks Cube");
            }
        });
    }



}
