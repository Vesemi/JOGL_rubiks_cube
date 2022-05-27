import com.jogamp.opengl.GL2;

import static com.jogamp.opengl.GL2.*;
import com.jogamp.opengl.math.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.PI;
import static java.lang.Math.abs;

public class qb{
    private float rotation = (float)PI/30f;
    private int currentWall, currentColumn, currentRow;
    int wall, coll, row;
    private float move = 1.0f;
    public boolean rotating = false;
    private float tempRotateY, tempRotateX, tempRotateZ = 0f;
    private final GL2 gl;
    private String qbTag;
    private boolean rotatingX, rotatingY, rotatingZ;
    public String[] colors = {"", "", "", "", "", ""};
    private final float[] axisZ = {0f,0f,1f}, axisY = {0f,1f,0f}, axisX = {1f,0f,0f};
    private Quaternion calcMat = new Quaternion().setIdentity();
    private Quaternion globalMat = new Quaternion().setIdentity();
    float[] tempMat4 = {0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f};
    public List<Integer> textures = new ArrayList<Integer>();
    private float[] globalRotate = {0f,0f,0f};

    public qb(GL2 gl, int iwall, int icoll, int irow){
        this.gl = gl;
        wall = iwall;
        coll = icoll;
        row = irow;
        setQbTag(String.format("%d %d %d", wall, coll, row ));

    }


    public void initSides(int size, List<Integer> textures) {
        this.textures = textures;
        if (wall == 0){ //Colors 0
            colors[0] = "#BA0C2F";
        }
        if (wall == size-1){ //Colors 1
            colors[1] = "#009A44";
        }
        if (coll == size-1){ //Colors 2
            colors[2] = "#003DA5";
        }
        if (coll == 0) { //Colors 3
            colors[3] = "#FE5000";
        }
        if (row == 0) { //Colors 4
            colors[4] = "#FFD700";
        }
        if (row == size-1) { //Colors 5
            colors[5] = "#FFFFFF";
        }

    }

    private void setQbTag(String tag) {
        this.qbTag = tag;
    }
    public String getQbTag() {
        return this.qbTag;
    }
    private void lighting(){


    }
    void draw(int size){


        preRotate();
       // lighting();
        translateGL( wall, coll, row, size);


        if (!Objects.equals(colors[0], "")) { //Colors 0 First Wall
            wallFWD(colors[0]);
        }
        if (!Objects.equals(colors[1], "")) { //Colors 1 Last Wall
            wallBWD(colors[1]);
        }
        if (!Objects.equals(colors[2], "")) { //Colors 2 Last Column
            wallRight(colors[2]);
        }
        if (!Objects.equals(colors[3], "")) { //Colors 3 First Column
            wallLeft(colors[3]);
        }
        if (!Objects.equals(colors[4], "")) { //Colors 4 First Row
            wallDOWN(colors[4]);
        }
        if (!Objects.equals(colors[5], "")) { //Colors 5 Last Row
            wallUP(colors[5]);
        }

    }
    private float limitRotate(float rotate){
        //to not over rotate check
        if(rotate>=PI/2) return (float)PI/2;
        else if(rotate <= -PI/2) return (float)(PI/2)*-1;
        else return 0f;

    }
    private void translateGL(int wall, int coll, int row, int size)
    {

        gl.glTranslatef(-move*coll+1.0f, -move*row+1.0f, -move*wall+1.0f);
    }

    void rotateRow(int direction,int row) {
        if ((currentRow == row )) {
            rotatingY = true;
        }
    }
    void rotateWall(int direction,int wall) {
        if ((currentWall == wall )) {
            rotatingZ = true;
        }
    }

    void rotateColumn(int direction, int coll) {

        if ((currentColumn == coll)) {
            rotatingX = true;
        }
    }
    void rotate(int section, int phase, int direction){
        switch (section){
            case 0 -> rotateWall(direction, phase);
            case 1 -> rotateColumn(direction, phase);
            case 2 -> rotateRow(direction, phase);
        }
    }
    void preRotate(){
        globalMat.setFromEuler(globalRotate);
        if (this.rotatingY){
            tempRotateY +=1;
            if (tempRotateY>15){
                tempRotateY = 0;
                this.rotatingY = false;
            }else {
                calcMat = rotationManip(-rotation, axisY, calcMat);
            }
        }else
        if (this.rotatingX){
            tempRotateX +=1;
            if (tempRotateX>15){
                tempRotateX = 0;
                this.rotatingX = false;
            }else {
                calcMat = rotationManip(rotation, axisX, calcMat);
            }
        }else
        if (rotatingZ){
            tempRotateZ +=1;
            if (tempRotateZ>15){
                tempRotateZ = 0;
                rotatingZ = false;
            }else {
                calcMat = rotationManip(-rotation, axisZ, calcMat);
            }
        }
        rotating = this.rotatingX || this.rotatingY || this.rotatingZ;

        gl.glLoadMatrixf(globalMat.mult(calcMat).toMatrix(tempMat4, 0), 0);



    }


    private Quaternion rotationManip(float angle, float[] axis, Quaternion currentMat){
        float[] tempVec3 = {0f,0f,0f};
        Quaternion tempQuat = new Quaternion().setIdentity();
        tempQuat.mult(new Quaternion().setFromAngleAxis(angle, axis, tempVec3));

        tempQuat.mult(currentMat);
        return tempQuat;
    }

    private void wallFWD(String Color) {
        gl.glBindTexture(GL2.GL_TEXTURE_2D, textures.get(0));
        gl.glBegin(GL_QUADS);
        gl.glNormal3f(1.0f, 1.0f, 1.0f);
        gl.glTexCoord2f(0.0f,0.0f); gl.glVertex3f(  0.5f, -0.5f, 0.5f );
        gl.glTexCoord2f(1.0f,0.0f); gl.glVertex3f(  0.5f,  0.5f, 0.5f );
        gl.glTexCoord2f(1.0f,1.0f); gl.glVertex3f( -0.5f,  0.5f, 0.5f );
        gl.glTexCoord2f(0.0f,1.0f); gl.glVertex3f( -0.5f, -0.5f, 0.5f );
        gl.glEnd();
    }
    private void wallBWD(String Color) {
        gl.glBindTexture(GL2.GL_TEXTURE_2D, textures.get(1));
        gl.glBegin(GL_QUADS);
        gl.glNormal3f(0.0f, 0.0f, 1.0f);
        gl.glTexCoord2f(0.0f,0.0f);gl.glVertex3f( -0.5f, -0.5f, -0.5f );
        gl.glTexCoord2f(1.0f,0.0f);gl.glVertex3f( -0.5f,  0.5f, -0.5f );
        gl.glTexCoord2f(1.0f,1.0f);gl.glVertex3f(  0.5f,  0.5f, -0.5f );
        gl.glTexCoord2f(0.0f,1.0f);gl.glVertex3f(  0.5f, -0.5f, -0.5f );
        gl.glEnd();
    }
    private void wallLeft(String Color){
        gl.glBindTexture(GL2.GL_TEXTURE_2D, textures.get(2));
        gl.glBegin(GL_QUADS);
        gl.glNormal3f(0.0f, 0.0f, 1.0f);
        gl.glTexCoord2f(0.0f,0.0f);gl.glVertex3f( 0.5f, -0.5f, 0.5f );
        gl.glTexCoord2f(1.0f,0.0f);gl.glVertex3f( 0.5f,  -0.5f, -0.5f );
        gl.glTexCoord2f(1.0f,1.0f);gl.glVertex3f(  0.5f,  0.5f, -0.5f );
        gl.glTexCoord2f(0.0f,1.0f);gl.glVertex3f(  0.5f, 0.5f, 0.5f );
        gl.glEnd();
    }
    private void wallRight(String Color){
        gl.glBindTexture(GL2.GL_TEXTURE_2D, textures.get(3));
        gl.glBegin(GL_QUADS);
        gl.glNormal3f(0.0f, 0.0f, 1.0f);
        gl.glTexCoord2f(0.0f,0.0f);gl.glVertex3f(  -0.5f, 0.5f, 0.5f );
        gl.glTexCoord2f(1.0f,0.0f);gl.glVertex3f(  -0.5f,  0.5f, -0.5f );
        gl.glTexCoord2f(1.0f,1.0f);gl.glVertex3f( -0.5f,  -0.5f, -0.5f );
        gl.glTexCoord2f(0.0f,1.0f);gl.glVertex3f( -0.5f, -0.5f, 0.5f );
        gl.glEnd();
    }
    private void wallUP(String Color){
        gl.glBindTexture(GL2.GL_TEXTURE_2D, textures.get(4));
        gl.glBegin(GL_QUADS);
        gl.glNormal3f(0.0f, 0.0f, 1.0f);
        gl.glTexCoord2f(0.0f,0.0f);gl.glVertex3f(  0.5f, -0.5f, 0.5f );
        gl.glTexCoord2f(1.0f,0.0f);gl.glVertex3f(  -0.5f,  -0.5f, 0.5f );
        gl.glTexCoord2f(1.0f,1.0f);gl.glVertex3f( -0.5f,  -0.5f, -0.5f );
        gl.glTexCoord2f(0.0f,1.0f);gl.glVertex3f( 0.5f, -0.5f, -0.5f );
        gl.glEnd();
    }
    private void wallDOWN(String Color){
        gl.glBindTexture(GL2.GL_TEXTURE_2D, textures.get(5));
        gl.glBegin(GL_QUADS);
        gl.glNormal3f(0.0f, 0.0f, 1.0f);
        gl.glTexCoord2f(0.0f,0.0f); gl.glVertex3f( 0.5f, 0.5f, -0.5f );
        gl.glTexCoord2f(1.0f,0.0f); gl.glVertex3f( -0.5f,0.5f, -0.5f );
        gl.glTexCoord2f(1.0f,1.0f); gl.glVertex3f(  -0.5f,  0.5f, 0.5f );
        gl.glTexCoord2f(0.0f,1.0f); gl.glVertex3f(  0.5f, 0.5f, 0.5f );
        gl.glEnd();
    }

    public void location(int i, int j, int k) {
        currentWall = i;
        currentColumn = j;
        currentRow = k;
    }

    public void setGlobalrotate(float[] globalRotate) {
        this.globalRotate = globalRotate;
    }
}