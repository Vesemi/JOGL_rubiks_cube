import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.jogamp.opengl.GL.*;


public class main extends JFrame implements GLEventListener, KeyListener {

    private GLU glu = new GLU();
    cube cube;
    GL2 gl;
    private float aspect;
    List<Integer> textures = new ArrayList<Integer>();
    private final String[] colors = {"red", "green", "blue", "orange", "yellow", "white"};
    public main(String string) {

        super(string);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Toolkit kit=Toolkit.getDefaultToolkit();
        Dimension d=kit.getScreenSize();
        setBounds(d.width/4, d.height/4, d.width/2, d.height/2);
        GLProfile profile=GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities=new GLCapabilities(profile);
        GLCanvas canvas=new GLCanvas(capabilities);
        canvas.addGLEventListener(this);
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

            case 'e' -> cube.startRotate(0,0,1);
            case 'd' -> cube.startRotate(0,1,1);
            case 'c' -> cube.startRotate(0,2,1);

            case 'w' -> cube.startRotate(1,0,1);
            case 's' -> cube.startRotate(1,1,1);
            case 'x' -> cube.startRotate(1,2,1);

            case 'q' -> cube.startRotate(2,0,1);
            case 'a' -> cube.startRotate(2,1,1);
            case 'z' -> cube.startRotate(2,2,1);

        }
    }

    static void setHexColor(GL2 gl, String color){
                    int r = Integer.valueOf( color.substring( 1, 3 ), 16 );
                    int g = Integer.valueOf( color.substring( 3, 5 ), 16 );
                    int b = Integer.valueOf( color.substring( 5, 7 ), 16 );

        gl.glColor3f( (float)r/255.0f, (float)g/255.0f, (float)b/255.0f);
        }

    @Override
    public void display(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        cube.rebuild();
        camera(aspect);

    }


    @Override
    public void dispose(GLAutoDrawable drawable) {

    }

    @Override
    public void init(GLAutoDrawable drawable) {
        gl=drawable.getGL().getGL2();
        gl.glEnable(GL2.GL_CULL_FACE);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthMask(true);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glDepthRangef(0.0f, 1.0f);

        gl.glEnable(GL2.GL_TEXTURE_2D);
        Arrays.stream(colors).forEach((x) -> {
            try{
            File f=new File("textures/%s.png".formatted(x));
            Texture t=TextureIO.newTexture(f, true);
            textures.add(t.getTextureObject(gl));
        }catch(IOException e){
            e.printStackTrace();
        }});



        cube = new cube(gl, textures, 3);
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
    glu.gluLookAt(5.0f, 5f, 5f,
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
