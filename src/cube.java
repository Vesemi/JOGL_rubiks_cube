import com.jogamp.opengl.GL2;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Random;

public class cube{
    private final qb[][][] cube;
    private final GL2 gl;
    private boolean rotateReq;
    private int size;
    private boolean allowRotate;
    private int section, phase, direction;
    private int shuffleTimes;
    private boolean shuffle;
    private float[] globalRotate = {0f,0f,0f};

    public cube(GL2 gl, List<Integer> textures, int size) {
        this.size = size;
        cube = new qb[this.size][this.size][this.size];
        this.setCameraAngle(0f, 0f, 0f);
        this.gl = gl;
        for( int i = 0;i<this.size;i++){
            for( int j = 0;j<this.size;j++){
                for( int k = 0;k<this.size;k++){
                    cube[i][j][k] = new qb(gl, i , j, k);
                    cube[i][j][k].initSides(size, textures);
                }
            }
        }
    }
    void rotateColumnLeft(int coll){
        int cubeLength = this.cube.length;
        for( int wall=0; wall<cubeLength;wall++){
            for(int row = 0; row<wall;row++){
                qb temp = this.getQb(wall,coll,row);
                this.setQb(this.getQb(row,coll,wall),wall,coll,row);
                this.setQb(temp,row,coll,wall);;
            }
        }
        for( int wall=0; wall<cubeLength;wall++){
            for(int row = 0; row<cubeLength/2;row++){
                qb temp = this.getQb(wall,coll,row);
                this.setQb(this.getQb(wall, coll,cubeLength-row-1),wall,coll,row);
                this.setQb(temp, wall, coll,cubeLength-row-1);
            }
        }
    }
    private void setRotation(int section, int phase, int direction){
        this.section = section;
        this.phase = phase;
        this.direction = direction;
    }
    void rotateRowLeft(int row){
        int cubeLength = this.cube.length;

        for( int wall=0; wall<cubeLength;wall++){
            for(int column = 0; column<wall;column++){
                qb temp = this.getQb(wall,column,row);
                this.setQb(this.getQb(column,wall,row), wall, column, row);
                this.setQb(temp, column,wall,row);
            }
        }
        for( int i=0; i<cubeLength;++i){
            for(int j = 0; j<cubeLength/2;++j){
                qb temp = getQb(i, j ,row);
                this.setQb(this.getQb(i, cubeLength-j-1, row), i,j,row);
                this.setQb(temp, i,cubeLength-j-1, row);
            }
        }
    }
    void rotateWallLeft(int wall){
        int cubeLength = this.cube.length;
        for( int column=0; column<cubeLength;column++){
            for(int row = 0; row<column;row++){
                qb temp = this.getQb(wall,column,row);
                this.setQb(this.getQb(wall, row,column), wall, column, row);
                this.setQb(temp,wall, row, column);
            }
        }

        for( int i=0; i<cubeLength;++i){
            for(int j = 0; j<cubeLength/2;++j){
                qb temp = getQb(wall, i, j );
                this.setQb(this.getQb(wall, i, cubeLength-j-1),wall, i,j);
                this.setQb(temp,wall,  i,cubeLength-j-1);
            }
        }
    }
    private qb getQb(int wall, int column, int row){
        return this.cube[wall][column][row];
    }

    private void setQb(qb qb, int wall, int column, int row) {
        this.cube[wall][column][row] = qb;
    }
    public void rebuild() {
        allowRotate = false;

        for( int i = 0;i<this.size;i++){ //walls

            for( int j = 0;j<this.size;j++){ //column

                for( int k = 0;k<this.size;k++){ // rows
                    gl.glPushMatrix();
                    this.cube[i][j][k].location(i, j, k);
                    if (rotateReq){
                        this.cube[i][j][k].rotate(section ,phase, direction);
                    }
                    this.cube[i][j][k].setGlobalrotate(globalRotate);

                    drawQb(i,j,k);
                    gl.glPopMatrix();
                                    }
            }
            allowRotate = true;
            
        }

        if (rotateReq && allowRotate && !this.isRotating()) {

            switch (section){
                case 0 -> rotateWallLeft(phase);
                case 1 -> rotateColumnLeft(phase);
                case 2 -> rotateRowLeft(phase);
            }
            stopRotate();
        }
    }

    private boolean isRotating() {
        for( int i = 0;i<this.size;i++){ //walls
            for( int j = 0;j<this.size;j++){ //column
                for( int k = 0;k<this.size;k++){ // rows
                    if (this.cube[i][j][k].rotating){
                        return true;
                    }
                }
            }
        }
    return false;
    }

    public void stopRotate(){
            this.rotateReq = false;
            this.allowRotate = false;
            if(shuffle) shuffle();
        }

    public void startRotate(int section, int phase, int direction){
        if (!this.rotateReq && !this.isRotating()){
            this.setRotation(section, phase, direction);
            this.rotateReq = true;
        }

    }
    public void globalRotateX(int direction){
        if (!this.rotateReq && !this.isRotating()){
            this.setRotation(section, phase, direction);
            this.rotateReq = true;
        }

    }

    private void drawQb(int i, int j, int k) {
        this.cube[i][j][k].draw(this.size);
    }

    public void shuffle(int times) {
        shuffle = !shuffle;
        shuffleTimes = times;
        this.shuffle();
    }
    private void shuffle() {
        shuffleTimes -= 1;
        if (shuffleTimes == 0) shuffle = false;
        Random rand = new Random();
        this.startRotate(rand.nextInt(size), rand.nextInt(size), rand.nextInt(size));
    }

    public void setCameraAngle(float cameraAngleX, float cameraAngleY, float cameraAngleZ) {
        globalRotate[0] = cameraAngleX;
        globalRotate[1] = cameraAngleY;
        globalRotate[2] = cameraAngleZ;
    }
}