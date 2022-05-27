import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import javax.swing.*;
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

    private  GL2 GL ;
    private GLU glu = new GLU();
    cube cube;
    GL2 gl;
    private float aspect;
    List<Integer> textures = new ArrayList<Integer>();
    private final String[] colors = {"red", "green", "blue", "orange", "yellow", "white"};
    private int mouseX = 0;
    private int mouseY = 0;
    private float cameraAngleX = 0f;
    private float cameraAngleY = 0f;
    private float cameraAngleZ = 0f;
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
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                super.mouseDragged(e);
                final int buffer = 5;

                if (e.getX() < mouseX - buffer) cameraAngleY += 0.1;
                else if (e.getX() > mouseX + buffer) cameraAngleY -= 0.1;

                if (e.getY() < mouseY - buffer) cameraAngleX += 0.1;
                else if (e.getY() > mouseY + buffer) cameraAngleX -= 0.1;
                System.out.println("mouse");
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });

        setLayout(new BorderLayout());
        add(canvas);
        JPanel panelBottom = new JPanel(new FlowLayout());
        JButton btnReset = new JButton("RESET");
        JButton btnShuffle  = new JButton("SHUFFLE");
        panelBottom.add(btnReset);
        panelBottom.add(btnShuffle);
       // add(btnReset, BorderLayout.SOUTH);
        add(panelBottom, BorderLayout.SOUTH);
        setVisible(true);
        final FPSAnimator animator = new FPSAnimator(canvas,60,true);
        animator.start();
        canvas.addKeyListener(this);
        btnReset.addActionListener(e -> cube = new cube(gl, textures, 3));
        btnShuffle.addActionListener(e -> cube.shuffle(0));

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

            case 'E' -> cube.startRotate(0,0,-1);
            case 'D' -> cube.startRotate(0,1,-1);
            case 'C' -> cube.startRotate(0,2,-1);

            case 'W' -> cube.startRotate(1,0,-1);
            case 'S' -> cube.startRotate(1,1,-1);
            case 'X' -> cube.startRotate(1,2,-1);

            case 'Q' -> cube.startRotate(2,0,-1);
            case 'A' -> cube.startRotate(2,1,-1);
            case 'Z' -> cube.startRotate(2,2,-1);

        }
    }


    @Override
    public void display(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        cube.setCameraAngle(cameraAngleX, cameraAngleY, cameraAngleZ);
        cube.rebuild();

        camera(aspect);

        gl.glFlush();
        gl.glLoadIdentity();



    }


    @Override
    public void dispose(GLAutoDrawable drawable) {

    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL=drawable.getGL().getGL2();
        gl = drawable.getGL().getGL2();
        gl.glEnable(GL2.GL_CULL_FACE);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthMask(true);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glDepthRangef(0.0f, 1.0f);

        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL.GL_LIGHTING);
        gl.glDepthFunc(gl.GL_LEQUAL);
        gl.glShadeModel(gl.GL_SMOOTH);

        gl.glLightModelfv(gl.GL_LIGHT_MODEL_AMBIENT, FloatBuffer.wrap(new float[] {1f, 1f, 1f, 1f}));

        gl.glLightfv(gl.GL_LIGHT1, gl.GL_DIFFUSE, FloatBuffer.wrap(new float[] { 1f, 1f, 1f, 0f}));
        gl.glLightfv(gl.GL_LIGHT1, gl.GL_POSITION, FloatBuffer.wrap(new float[] { 0.5f, 0.5f, 3.5f, 0f}));

        gl.glEnable(gl.GL_LIGHTING);
        gl.glEnable(gl.GL_LIGHT1);
        Arrays.stream(colors).forEach((x) -> {
            try{
            File f=new File("textures/%s.png".formatted(x));
            Texture t=TextureIO.newTexture(f, true);
            textures.add(t.getTextureObject(gl));
        }catch(IOException e){
            e.printStackTrace();
        }});



        cube = new cube(gl, textures, 5);
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
    glu.gluPerspective(60.0, aspect, 1.0, 20f);
    glu.gluLookAt(7.0f, 5f, 5f,
            0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f);
    gl.glMatrixMode(GL2.GL_MODELVIEW);
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
