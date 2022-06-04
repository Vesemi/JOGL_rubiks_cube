import com.jogamp.opengl.GL2;

import static com.jogamp.opengl.GL2.*;
import com.jogamp.opengl.math.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.PI;
import static java.lang.Math.abs;

public class qb{
    private float rotation, speed;
    private int currentWall, currentColumn, currentRow;
    int wall, coll, row;
    private float move = 1f;
    public boolean rotating = false;
    private float tempRotateY, tempRotateX, tempRotateZ = 0f;
    private final GL2 gl;
    private String qbTag;
    private int direction = -1;
    private boolean rotatingX, rotatingY, rotatingZ;
    public String[] colors = {"", "", "", "", "", ""};
    private final float[] axisZ = {0f,0f,1f}, axisY = {0f,1f,0f}, axisX = {1f,0f,0f};
    private Quaternion calcMat = new Quaternion().setIdentity();
    private Quaternion globalMat = new Quaternion().setIdentity();
    private Quaternion stableMat = new Quaternion().setIdentity();
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
        gl.glPushAttrib(GL2.GL_CURRENT_BIT);
        if (wall == 0){ //Colors 0
            colors[0] = "#BA0C2F";
        }
        gl.glPopAttrib();
        gl.glPushAttrib(GL2.GL_CURRENT_BIT);
        if (wall == size-1){ //Colors 1
            colors[1] = "#009A44";
        }
        gl.glPopAttrib();
        gl.glPushAttrib(GL2.GL_CURRENT_BIT);
        if (coll == size-1){ //Colors 2
            colors[2] = "#003DA5";
        }
        gl.glPopAttrib();
        gl.glPushAttrib(GL2.GL_CURRENT_BIT);
        if (coll == 0) { //Colors 3
            colors[3] = "#FE5000";
        }
        gl.glPopAttrib();
        gl.glPushAttrib(GL2.GL_CURRENT_BIT);
        if (row == 0) { //Colors 4
            colors[4] = "#FFD700";
        }
        gl.glPopAttrib();
        gl.glPushAttrib(GL2.GL_CURRENT_BIT);
        if (row == size-1) { //Colors 5
            colors[5] = "#FFFFFF";
        }
        gl.glPopAttrib();

    }

    private void setQbTag(String tag) {
        this.qbTag = tag;
    }
    public String getQbTag() {
        return this.qbTag;
    }

    void draw(int size){

        preRotate();
        translateGL( wall, coll, row, size);

        //draw walls
        wallFWD(colors[0]);
        wallBWD(colors[1]);
        wallRight(colors[2]);
        wallLeft(colors[3]);
        wallDOWN(colors[4]);
        wallUP(colors[5]);
    }

    private void translateGL(int wall, int coll, int row, int size)
    {
        gl.glTranslatef((size-3)/2f, (size-3)/2f, (size-3)/2f);
        gl.glTranslatef(-coll+1f, -row+1f, -wall+1f);
    }

    void rotateRow(int row) {
        if ((currentRow == row )) {
            if(!rotatingY){
                direction = -1;
                stableMat = calcRotationQuaternion((float) (-PI/2), axisY, calcMat);
            }

            rotatingY = true;
        }
    }
    void rotateWall(int wall) {
        if ((currentWall == wall )) {
            if(!rotatingZ) {
                direction = -1;
                stableMat = calcRotationQuaternion((float) (-PI / 2), axisZ, calcMat);
            }
            rotatingZ = true;
        }
    }

    void rotateColumn(int coll) {

        if ((currentColumn == coll)) {
            if (!rotatingX){
                direction = 1;
            stableMat = calcRotationQuaternion((float) (PI/2), axisX, calcMat);}
            rotatingX = true;
        }
    }

    void rotateRowCCw(int row) {
        if ((currentRow == row )) {
            if(!rotatingY){
                direction = 1;
                stableMat = calcRotationQuaternion((float) (PI/2), axisY, calcMat);
            }

            rotatingY = true;
        }
    }
    void rotateWallCCw(int wall) {
        if ((currentWall == wall )) {
            if(!rotatingZ) {
                direction = 1;
                stableMat = calcRotationQuaternion((float) (PI / 2), axisZ, calcMat);
            }
            rotatingZ = true;
        }
    }

    void rotateColumnCCw(int coll) {

        if ((currentColumn == coll)) {
            if (!rotatingX){
                direction = -1;
                stableMat = calcRotationQuaternion((float) (-PI/2), axisX, calcMat);}
            rotatingX = true;
        }
    }
    void rotate(int section, int phase, float rotationSpeed){
        this.speed = rotationSpeed;
        rotation = (float) ((float)2*PI/ this.speed /4);
        switch (section){
            case 0 -> rotateWall(phase);
            case 1 -> rotateColumn(phase);
            case 2 -> rotateRow(phase);

            case 3 -> rotateWallCCw(phase);
            case 4 -> rotateColumnCCw(phase);
            case 5 -> rotateRowCCw(phase);

        }
    }
    void preRotate(){

        if (this.rotatingY){
            tempRotateY +=1;
            if (tempRotateY>speed){
                tempRotateY = 0;
                calcMat = stableMat;
                this.rotatingY = false;
            }else {
                calcMat = calcRotationQuaternion(direction*rotation, axisY, calcMat);
            }
        }else
        if (this.rotatingX){
            tempRotateX +=1;
            if (tempRotateX>speed){
                tempRotateX = 0;
                calcMat = stableMat;
                this.rotatingX = false;
            }else {
                calcMat = calcRotationQuaternion(direction*rotation, axisX, calcMat);
            }
        }else
        if (rotatingZ){
            tempRotateZ +=1;
            if (tempRotateZ>speed){
                tempRotateZ = 0;
                calcMat = stableMat;
                rotatingZ = false;

            }else {
                calcMat = calcRotationQuaternion(direction*rotation, axisZ, calcMat);
            }
        }

        rotating = this.rotatingX || this.rotatingY || this.rotatingZ;

        globalMat.setIdentity();
        globalMat.setFromEuler((float)Math.toRadians(globalRotate[0]), (float)Math.toRadians(globalRotate[1]), 0f);
        globalMat.mult(calcMat);

        gl.glLoadMatrixf(globalMat.toMatrix(tempMat4, 0), 0);
    }


    private Quaternion calcRotationQuaternion(float angle, float[] axis, Quaternion currentMat){
        float[] tempVec3 = {0f,0f,0f};
        Quaternion tempQuat = new Quaternion().setIdentity();
        tempQuat.mult(new Quaternion().setFromAngleAxis(angle, axis, tempVec3));
        tempQuat.mult(currentMat);
        return tempQuat;
    }


    private void wallFWD(String Color) {
        if (Objects.equals(Color, "")) gl.glBindTexture(GL2.GL_TEXTURE_2D, textures.get(6));
        else gl.glBindTexture(GL2.GL_TEXTURE_2D, textures.get(0));
        gl.glBegin(GL_QUADS);

        gl.glNormal3f(0.0f, 0.0f, 1.0f);
        gl.glTexCoord2f(0.0f,0.0f); gl.glVertex3f(  -move/2, -move/2, move/2 );
        gl.glTexCoord2f(1f,0.0f); gl.glVertex3f(  move/2,  -move/2, move/2 );
        gl.glTexCoord2f(1f,1.0f); gl.glVertex3f( move/2,  move/2, move/2 );
        gl.glTexCoord2f(0.0f,1.0f); gl.glVertex3f( -move/2, move/2, move/2 );
        gl.glEnd();
    }
    private void wallBWD(String Color) {
        if (Objects.equals(Color, "")) gl.glBindTexture(GL2.GL_TEXTURE_2D, textures.get(6));
        else gl.glBindTexture(GL2.GL_TEXTURE_2D, textures.get(1));
        gl.glBegin(GL_QUADS);
        gl.glNormal3f(.0f, 0.0f, -1.0f);
        gl.glTexCoord2f(1f,0.0f);gl.glVertex3f( move/2, -move/2, -move/2 );
        gl.glTexCoord2f(1f,1.0f);gl.glVertex3f( -move/2,  -move/2, -move/2 );
        gl.glTexCoord2f(0f,1.0f);gl.glVertex3f(  -move/2,  move/2, -move/2 );
        gl.glTexCoord2f(0f,0.0f);gl.glVertex3f(  move/2, move/2, -move/2 );
        gl.glEnd();
    }
    private void wallLeft(String Color){
        if (Objects.equals(Color, "")) gl.glBindTexture(GL2.GL_TEXTURE_2D, textures.get(6));
        else gl.glBindTexture(GL2.GL_TEXTURE_2D, textures.get(2));
        gl.glBegin(GL_QUADS);
        gl.glNormal3f(1.0f, 0.0f, 0.0f); //checked OK
        gl.glTexCoord2f(0f,0.0f);gl.glVertex3f(  move/2, -move/2, move/2 );
        gl.glTexCoord2f(1f,0.0f);gl.glVertex3f(  move/2,  -move/2, -move/2 );
        gl.glTexCoord2f(1f,1.0f);gl.glVertex3f( move/2,  move/2, -move/2 );
        gl.glTexCoord2f(0f,1.0f);gl.glVertex3f( move/2, move/2, move/2 );
        gl.glEnd();
    }
    private void wallRight(String Color){
        if (Objects.equals(Color, "")) gl.glBindTexture(GL2.GL_TEXTURE_2D, textures.get(6));
        else gl.glBindTexture(GL2.GL_TEXTURE_2D, textures.get(3));
        gl.glBegin(GL_QUADS);
        gl.glNormal3f(-1f, 0.0f, 0.0f);
        gl.glTexCoord2f(0f,0.0f);gl.glVertex3f( -move/2, -move/2, -move/2 );
        gl.glTexCoord2f(0.0f,1.0f);gl.glVertex3f( -move/2,  -move/2, move/2 );
        gl.glTexCoord2f(1.0f,1.0f);gl.glVertex3f(  -move/2,  move/2, move/2 );
        gl.glTexCoord2f(1f,0.0f);gl.glVertex3f(  -move/2, move/2, -move/2 );
        gl.glEnd();
    }
    private void wallUP(String Color){
        if (Objects.equals(Color, "")) gl.glBindTexture(GL2.GL_TEXTURE_2D, textures.get(6));
        else gl.glBindTexture(GL2.GL_TEXTURE_2D, textures.get(4));

        gl.glBegin(GL_QUADS);
        gl.glNormal3f(0.0f, -1.0f, 0.0f);
        gl.glTexCoord2f(0.0f,0.0f); gl.glVertex3f( -move/2, -move/2, -move/2 );
        gl.glTexCoord2f(1.0f,0.0f); gl.glVertex3f( move/2,-move/2, -move/2 );
        gl.glTexCoord2f(1.0f,1.0f); gl.glVertex3f(  move/2,  -move/2, move/2 );
        gl.glTexCoord2f(0.0f,1.0f); gl.glVertex3f(  -move/2, -move/2, move/2 );
        gl.glEnd();

    }
    private void wallDOWN(String Color){
        if (Objects.equals(Color, "")) gl.glBindTexture(GL2.GL_TEXTURE_2D, textures.get(6));
        else gl.glBindTexture(GL2.GL_TEXTURE_2D, textures.get(5));
        gl.glBegin(GL_QUADS);
        gl.glNormal3f(0.0f, 1.0f, 0.0f);
        gl.glTexCoord2f(0.0f,0.0f);gl.glVertex3f(  -move/2, move/2, move/2 );
        gl.glTexCoord2f(1.0f,0.0f);gl.glVertex3f(  move/2,  move/2, move/2 );
        gl.glTexCoord2f(1.0f,1.0f);gl.glVertex3f( move/2,  move/2, -move/2 );
        gl.glTexCoord2f(0.0f,1.0f);gl.glVertex3f( -move/2, move/2, -move/2 );
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