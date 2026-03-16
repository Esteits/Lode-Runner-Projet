package com.loderunner.project;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.loderunner.project.engine.EnemyThread;
import com.loderunner.project.engine.Game;
import com.loderunner.project.engine.StartEnemyThread;
import com.loderunner.project.entity.Enemy;
import com.loderunner.project.entity.Player;
import com.loderunner.project.entity.Treasure;
import com.loderunner.project.entity.Character.Direction;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Input;

public class Main extends ApplicationAdapter {
    private Game g = new Game(5);
    private Player p = new Player(g.getMaze().getExit(), 1);
    private StartEnemyThread set = new StartEnemyThread();
    private SpriteBatch batch;
    private Texture bedrock;
    private Texture ladder;
    private Texture wall;
    private Texture wallbreak;
    private Texture playerright;
    private Texture playerleft;
    private Texture treasure ;
    private Texture enemy;
    OrthographicCamera camera = new OrthographicCamera();
    Viewport viewport = new ScreenViewport(camera);

    @Override
    public void create() {
        batch = new SpriteBatch();
        ladder = new Texture("ladder.png");
        wall = new Texture("wall.png");
        bedrock = new Texture("bedrock.png");
        playerright = new Texture("playerright.png");
        playerleft = new Texture("playerleft.png");
        wallbreak = new Texture("wallbreak.png");
        treasure = new Texture("treasure.png");
        enemy = new Texture("enemy.png");
        g.addPlayer(p);
        for(int i = 0 ; i < g.getEne().size() ; i++){
            EnemyThread ia = new EnemyThread(g, i);
            set.addThreadEnemy(ia);
        }
        set.start();
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.3f, 0.6f, 0.3f, 0);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        int largeurecran = Gdx.graphics.getWidth();
        int hauteurecran = Gdx.graphics.getHeight();

        int height = g.getMaze().getHeight();
        int width = g.getMaze().getWidth();

        int tileWidth = largeurecran / width;
        int tileHeight = hauteurecran / height;

        batch.begin();

        drawMaze(width, height, tileWidth, tileHeight);

        drawPlayer(width, height, tileWidth, tileHeight);

        drawTreasure(width, height, tileWidth, tileHeight);

        drawEnemy(width, height, tileWidth, tileHeight);

        batch.end();
       
        inputPlayer(0);
        
        g.sec();
    }

    @Override
    public void dispose() {
        batch.dispose();
        bedrock.dispose();
        wall.dispose();
        ladder.dispose();
        playerleft.dispose();
        playerright.dispose();
        wallbreak.dispose();
        set.stopAll();
    }

    public void drawMaze(int width, int height, int tileWidth, int tileHeight){
        for (int y = 0; y < height; y++){
            for(int x = 0 ; x <width; x++){
                int type = g.getMaze().getTile(x, y).getType();
                switch (type) {
                    case 1:
                        if(g.getMaze().getTile(x, y).getState()){
                            batch.draw(wall, x*tileWidth, (height - 1 - y) * tileHeight, tileWidth, tileHeight);     
                        }else{
                            batch.draw(wallbreak, x*tileWidth, (height - 1 - y) * tileHeight, tileWidth, tileHeight);     
                        }
                        break;
                    case 2:
                            batch.draw(ladder, x*tileWidth, (height - 1 - y) * tileHeight, tileWidth, tileHeight);     
                        break;
                    case 3:
                        batch.draw(bedrock, x*tileWidth, (height - 1 - y) * tileHeight, tileWidth, tileHeight); 
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void drawPlayer(int width, int height, int tileWidth, int tileHeight){
        for (Player p: g.getPlay()){
            if(p.getDirection()==Direction.RIGHT){
                batch.draw(playerright, p.getX()*tileWidth, (height - 1 - p.getY())*tileHeight, tileWidth, tileHeight);
            }else if (p.getDirection()==Direction.LEFT){
                batch.draw(playerleft, p.getX()*tileWidth, (height - 1 - p.getY())*tileHeight, tileWidth, tileHeight);
            }
        }
    }

    public void drawTreasure(int width, int height, int tileWidth, int tileHeight){
        for(Treasure t : g.getTre()){
            if(!t.getCollect()){
                batch.draw(treasure, t.getX()*tileWidth, (height - 1 - t.getY())*tileHeight, tileWidth, tileHeight);
            }
        }
    }

    public void drawEnemy(int width, int height, int tileWidth, int tileHeight){
        for(Enemy e : g.getEne()){
            if(e.getState()){
                batch.draw(enemy, e.getX()*tileWidth, (height - 1 - e.getY())*tileHeight, tileWidth, tileHeight);
            }
        }
    }

    public void inputPlayer(int ind){
        if(Gdx.input.isKeyJustPressed(Input.Keys.D)){
            g.movePlayerRight(ind);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.A)){
            g.movePlayerLeft(ind);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.W)){
            g.movePlayerUp(ind);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.S)){
            g.movePlayerDown(ind);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            g.dig(ind);
        }
    }

}