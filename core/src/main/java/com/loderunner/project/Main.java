package com.loderunner.project;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.loderunner.project.entity.Player;
import com.loderunner.project.entity.Character.Direction;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Input;

public class Main extends ApplicationAdapter {
    private Game g = new Game();
    private Player p = new Player(g.getMaze().getExit(), 0);
    private SpriteBatch batch;
    private Texture bedrock;
    private Texture ladder;
    private Texture wall;
    private Texture playerright;
    private Texture playerleft;
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
        g.addPlayer(p);
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

        int hauteur = g.getMaze().getHeight();
        int largeur = g.getMaze().getWidth();

        int tileWidth = largeurecran / largeur;
        int tileHeight = hauteurecran / hauteur;

        batch.begin();
        for (int y = 0; y < hauteur; y++){
            for(int x = 0 ; x <largeur; x++){
                int type = g.getMaze().getTile(x, y).getType();
                switch (type) {
                    case 1:
                        batch.draw(wall, x*tileWidth, (hauteur - 1 - y) * tileHeight, tileWidth, tileHeight); 
                        break;
                    case 2:
                        batch.draw(ladder, x*tileWidth, (hauteur - 1 - y) * tileHeight, tileWidth, tileHeight); 
                        break;
                    case 3:
                        batch.draw(bedrock, x*tileWidth, (hauteur - 1 - y) * tileHeight, tileWidth, tileHeight); 
                        break;
                    default:
                        break;
                }
            }
        }
        for (Player p: g.getPlay()){
            if(p.getDirection()==Direction.RIGHT){
                batch.draw(playerright, p.getX()*tileWidth, (hauteur - 1 - p.getY())*tileHeight, tileWidth, tileHeight);
            }else if (p.getDirection()==Direction.LEFT){
                batch.draw(playerleft, p.getX()*tileWidth, (hauteur - 1 - p.getY())*tileHeight, tileWidth, tileHeight);
            }
        }
        batch.end();
        if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)){
        g.movePlayerRight(0);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)){
            g.movePlayerLeft(0);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
            g.movePlayerUp(0);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
            g.movePlayerDown(0);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            g.dig(0);
        }
        g.sec();
    }

    @Override
    public void dispose() { // super projet
        batch.dispose();
        bedrock.dispose();
        wall.dispose();
        ladder.dispose();
        playerleft.dispose();
        playerright.dispose();
    }
}