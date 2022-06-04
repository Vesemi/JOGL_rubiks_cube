import com.jogamp.opengl.GL2;

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
    void rotateColumn(int coll, int direction){
        int cubeLength = this.cube.length;
        for( int wall=0; wall<cubeLength;wall++){
            for(int row = 0; row<wall;row++){
                qb temp = this.getQb(wall,coll,row);
                this.setQb(this.getQb(row,coll,wall),wall,coll,row);
                this.setQb(temp,row,coll,wall);;
            }
        }
        if (direction == -1)
        {
            for (int i = 0; i < cubeLength; i++) {
                for (int j = 0; j < cubeLength / 2; j++) {
                    qb temp = this.getQb(j, coll, i);
                    this.setQb(this.getQb(cubeLength - j - 1, coll,i ), j, coll, i);
                    this.setQb(temp, cubeLength - j - 1, coll, i);
                }
            }

        }
        else{
            for (int i = 0; i < cubeLength; i++) {
                for (int j = 0; j < cubeLength / 2; j++) {
                    qb temp = this.getQb(i, coll, j);
                    this.setQb(this.getQb(i, coll, cubeLength - j - 1), i, coll, j);
                    this.setQb(temp, i, coll, cubeLength - j - 1);
                }
            }

        }
    }


    void rotateRow(int row, int direction){
        int cubeLength = this.cube.length;

        for( int i=0; i<cubeLength;i++){
            for(int j = 0; j<i;j++){
                qb temp = this.getQb(i,j,row);
                this.setQb(this.getQb(j,i,row), i, j, row);
                this.setQb(temp, j,i,row);
            }
        }
        if (direction == 1)
        {
            for( int i=0; i<cubeLength;++i){
                for(int j = 0; j<cubeLength/2;++j){
                    qb temp = getQb(i, j ,row);
                    this.setQb(this.getQb(i, cubeLength-j-1, row), i,j,row);
                    this.setQb(temp, i,cubeLength-j-1, row);
            }
        }
        }else
        {
            for (int i = 0; i < cubeLength; ++i) {
                for (int j = 0; j < cubeLength / 2; ++j) {
                    qb temp = getQb(j, i, row);
                    this.setQb(this.getQb(cubeLength - j - 1, i, row), j, i, row);
                    this.setQb(temp, cubeLength - j - 1, i, row);
                }
            }
        }
    }
    void rotateWall(int wall, int direction){
        int cubeLength = this.cube.length;
        for( int i=0; i<cubeLength;i++){
            for(int j = 0; j<i;j++){
                qb temp = this.getQb(wall,i,j);
                this.setQb(this.getQb(wall, j,i), wall, i, j);
                this.setQb(temp,wall, j, i);
            }
        }

        if(direction == 1) {
            for (int i = 0; i < cubeLength; ++i) {
                for (int j = 0; j < cubeLength / 2; ++j) {
                    qb temp = getQb(wall, i, j);
                    this.setQb(this.getQb(wall, i, cubeLength - j - 1), wall, i, j);
                    this.setQb(temp, wall, i, cubeLength - j - 1);
                }
            }
        }
        else{
            for (int i = 0; i < cubeLength; ++i) {
                for (int j = 0; j < cubeLength / 2; ++j) {
                    qb temp = getQb(wall, j, i);
                    this.setQb(this.getQb(wall, cubeLength - j - 1, i), wall, j, i);
                    this.setQb(temp, wall, cubeLength - j - 1, i );
                }
            }
        }

    }
    private qb getQb(int wall, int column, int row){
        return this.cube[wall][column][row];
    }

    private void setQb(qb qb, int wall, int column, int row) {
        this.cube[wall][column][row] = qb;
    }
    public void rebuild(float rotationSpeed) {
        allowRotate = false;

        for( int i = 0;i<this.size;i++){ //walls

            for( int j = 0;j<this.size;j++){ //column

                for( int k = 0;k<this.size;k++){ // rows
                   // gl.glPushMatrix();
                    this.cube[i][j][k].location(i, j, k);
                    if (rotateReq){
                        this.cube[i][j][k].rotate(section ,phase, rotationSpeed);
                    }
                    this.cube[i][j][k].setGlobalrotate(globalRotate);
                    drawQb(i,j,k);
                   // gl.glPopMatrix();
                                    }
            }
            allowRotate = true;
            
        }

        if (rotateReq && allowRotate && !this.isRotating()) {

            switch (section){
                case 0 -> rotateWall(phase, 1);
                case 1 -> rotateColumn(phase, 1);
                case 2 -> rotateRow(phase, 1);

                case 3 -> rotateWall(phase, -1);
                case 4 -> rotateColumn(phase, -1);
                case 5 -> rotateRow(phase, -1);
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
        if (!this.rotateReq && !this.isRotating())
        {
            this.setRotation(section, phase, direction);
            this.rotateReq = true;
        }

    }

    private void setRotation(int section, int phase, int direction){
        this.section = section;
        this.phase = phase;
        this.direction = direction;
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
        this.startRotate(rand.nextInt(6), rand.nextInt(size), rand.nextInt(size));
    }

    public void setCameraAngle(float cameraAngleX, float cameraAngleY, float cameraAngleZ) {
        globalRotate[0] = cameraAngleX;
        globalRotate[1] = cameraAngleY;
        globalRotate[2] = cameraAngleZ;
    }
}