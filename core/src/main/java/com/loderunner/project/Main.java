package com.loderunner.project;

import java.io.IOException;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.loderunner.project.engine.Game;
import com.loderunner.project.entity.Enemy;
import com.loderunner.project.entity.Player;
import com.loderunner.project.entity.Treasure;
import com.loderunner.project.network.Client;
import com.loderunner.project.entity.Character.Direction;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class Main extends ApplicationAdapter {
    private Game g ;
    private Client client;
    private int playerId;
    private SpriteBatch batch;
    private Texture bedrock;
    private Texture ladder;
    private Texture wall;
    private Texture wallbreak;
    private Texture playerright;
    private Texture playerleft;
    private Texture treasure ;
    private Texture enemy;
    private Texture heart;
    private BitmapFont scoreText;
    private BitmapFont gameOver;
    OrthographicCamera camera = new OrthographicCamera();
    Viewport viewport = new ScreenViewport(camera);
    int tick;
    int tickDep;

    @Override
    public void create() {
        scoreText = new BitmapFont();
        gameOver = new BitmapFont();
        batch = new SpriteBatch();
        ladder = new Texture("ladder.png");
        wall = new Texture("wall.png");
        bedrock = new Texture("bedrock.png");
        playerright = new Texture("playerright.png");
        playerleft = new Texture("playerleft.png");
        wallbreak = new Texture("wallbreak.png");
        treasure = new Texture("treasure.png");
        enemy = new Texture("enemy.png");
        heart = new Texture("heart.png");
        tick = 0;
        tickDep = 0;
        try {
        client = new Client("localhost",8080);
        } catch (IOException e) {
            e.printStackTrace();
            client = null;
        }
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

        int widthScreen = Gdx.graphics.getWidth();
        int heightScreen = Gdx.graphics.getHeight();
        if (client == null) {
            batch.begin();
            scoreText.draw(batch, "Connexion échouée...", 100, 100);
            batch.end();
            return;
        }

        Game newGame = client.getGame();
        if (newGame != null) {
            g = newGame;
        }
        else{
            batch.begin();
            scoreText.getData().setScale(2);
            scoreText.setColor(Color.WHITE);
            scoreText.draw(batch, "En attente du serveur...", 100, Gdx.graphics.getHeight() / 2f);
            batch.end();
            return;
        }
        int height = g.getMaze().getHeight();
        int width = g.getMaze().getWidth();

        int tileWidth = widthScreen / width;
        int tileHeight = heightScreen / height;

        tick+=1;

        batch.begin();

        drawMaze(width, height, tileWidth, tileHeight);

        drawHeart(widthScreen, heightScreen, tileWidth, tileHeight);

        drawPlayer(width, height, tileWidth, tileHeight);

        drawTreasure(width, height, tileWidth, tileHeight);

        drawEnemy(width, height, tileWidth, tileHeight);

        score();

        lose();

        batch.end();

        inputPlayer(playerId);
    }

    @Override
    public void dispose() {
        g.saveToFile();
        batch.dispose();
        bedrock.dispose();
        wall.dispose();
        ladder.dispose();
        playerleft.dispose();
        playerright.dispose();
        wallbreak.dispose();
        heart.dispose();
        scoreText.dispose();
        gameOver.dispose();
    }

    public void drawMaze(int width, int height, int tileWidth, int tileHeight){
        for (int y = 0; y < height; y++){
            for(int x = 0 ; x < width; x++){
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

    public void drawHeart(int widthScreen, int heightScreen, int tileWidth, int tileHeight){
        int j = 0;
        for(Player p: g.getPlay()){
            j += 1;
            for(int i = 1 ; i <= p.getHp() ; i++) {
                batch.draw(heart, widthScreen-tileWidth*i, heightScreen-tileHeight*j, tileWidth, tileHeight);
            }
        }
    }

    public void drawPlayer(int width, int height, int tileWidth, int tileHeight){
        for (Player p: g.getPlay()){
            if(!p.playerDead()){
                if(p.getDirection()==Direction.RIGHT){
                    batch.draw(playerright, p.getX()*tileWidth, (height - 1 - p.getY())*tileHeight, tileWidth, tileHeight);
                }else {
                    batch.draw(playerleft, p.getX()*tileWidth, (height - 1 - p.getY())*tileHeight, tileWidth, tileHeight);
                }
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
        if(Gdx.input.isKeyJustPressed(Input.Keys.ALT_LEFT)){
            client.action("LOAD");;
        }
        if(!g.getPlay().get(ind).playerDead()){
            if(Gdx.input.isKeyPressed(Input.Keys.D)){
                tickDep += 1;
                if (tickDep >= 10){
                    client.action("RIGHT");;
                    tickDep = 0;
                }
            }
            if(Gdx.input.isKeyPressed(Input.Keys.A)){
                tickDep += 1;
                if (tickDep >= 10){
                    client.action("LEFT");
                    tickDep = 0;
                }
            }
            if(Gdx.input.isKeyPressed(Input.Keys.W)){
                tickDep += 1;
                if (tickDep >= 10){
                    client.action("UP");
                    tickDep = 0;
                }
            }
            if(Gdx.input.isKeyPressed(Input.Keys.S)){
                tickDep += 1;
                if (tickDep >= 10){
                    client.action("DOWN");
                    tickDep = 0;
                }
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
                client.action("DIG");;
            }
        }else{
            if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
                client.action("RESTART");
            }
        }
    }

    public void score(){
        scoreText.draw(batch, ""  + g.getScore(), 0, 22);
        scoreText.getData().setScale(2);
        scoreText.setColor(Color.BLUE);
    }
    
    public void lose(){
        if(g.gameOver()){
            gameOver.getData().setScale(5);

            GlyphLayout layout = new GlyphLayout(gameOver, "GAME OVER");

            float x = (Gdx.graphics.getWidth() - layout.width) / 2;
            float y = (Gdx.graphics.getHeight() + layout.height) / 2;

            gameOver.setColor(Color.BLACK);
            gameOver.draw(batch, layout, x, y);
        }
    }
}