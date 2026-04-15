package com.loderunner.project;

import java.io.IOException;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.loderunner.project.engine.Game;
import com.loderunner.project.entity.Character.Direction;
import com.loderunner.project.entity.Enemy;
import com.loderunner.project.entity.Player;
import com.loderunner.project.entity.Treasure;
import com.loderunner.project.network.Client;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Main extends ApplicationAdapter {
    private Game g ;
    private Client client;
    private int playerId;
    private SpriteBatch batch;

    private Texture bedrock;
    private Texture ladder;
    private Texture background;
    private Texture wallbreak;
    private Texture wall;

    private Animation<TextureRegion> animMarcheDroite;
    private Animation<TextureRegion> animMarcheGauche;
    private Animation<TextureRegion> animIdle;
    private Animation<TextureRegion> animEnemyDroite;
    private Animation<TextureRegion> animEnemyGauche;
    private Animation<TextureRegion> animHeart;
    private Animation<TextureRegion> animTreasure; 
    
    private TextureRegion imageChute; 
    private TextureRegion imageDosEchelle;
    private float stateTime = 0f;

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
        wallbreak = new Texture("wallbreak.png");
        background = new Texture("Background.png");
        
        animEnemyDroite = creerAnimation("EnnemyRight.png", 7, 32, 32);
        animEnemyGauche = creerAnimation("EnnemyLeft.png", 7, 32, 32);
        animHeart = creerAnimation("heart.png", 6, 64, 64);
        animMarcheDroite = creerAnimation("PlayerRunRight.png", 6, 32, 32);
        animMarcheGauche = creerAnimation("PlayerRunLeft.png", 6, 32, 32);
        animIdle = creerAnimation("PlayerWait.png", 9, 32, 32);
        animTreasure = creerAnimation("treasure.png", 7, 32, 32);
        
        Texture sheetChute = new Texture(Gdx.files.internal("PlayerFall.png"));
        imageChute = new TextureRegion(sheetChute, 32, 32);

        Texture sheetEchelle = new Texture(Gdx.files.internal("PlayerBack.png"));
        imageDosEchelle = new TextureRegion(sheetEchelle, 32, 32);

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
        ScreenUtils.clear(0.46f, 0.71f, 0.99f, 1f);

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
        } else {
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

        tick += 1;
        stateTime += Gdx.graphics.getDeltaTime();

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
        batch.dispose();
        bedrock.dispose();
        wall.dispose();
        ladder.dispose();
        wallbreak.dispose();
        scoreText.dispose();
        gameOver.dispose();
        background.dispose();
        imageDosEchelle.getTexture().dispose();
        imageChute.getTexture().dispose();
    }

    public void drawMaze(int width, int height, int tileWidth, int tileHeight){
        for (int y = 0; y < height; y++){
            for(int x = 0 ; x < width; x++){
                int type = g.getMaze().getTile(x, y).getType();
                switch (type) {
                    case 0: 
                        batch.draw(background, x*tileWidth, (height - 1 - y) * tileHeight, tileWidth, tileHeight);
                        break;
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
        TextureRegion frameActuelle = animHeart.getKeyFrame(stateTime, true);
        for(Player p: g.getPlay()){
            j += 1;
            for(int i = 1 ; i <= p.getHp() ; i++) {
                batch.draw(frameActuelle, widthScreen - tileWidth * i, heightScreen - tileHeight * j, tileWidth, tileHeight);
            }
        }
    }

    public void drawPlayer(int width, int height, int tileWidth, int tileHeight){
        for (Player p: g.getPlay()){
            if(!p.playerDead()){
                TextureRegion frameActuelle;
                int typeCaseActuelle = g.getMaze().getTile(p.getX(), p.getY()).getType();
                switch (p.getDirection()) {
                    case RIGHT: frameActuelle = animMarcheDroite.getKeyFrame(stateTime, true); break;
                    case LEFT: frameActuelle = animMarcheGauche.getKeyFrame(stateTime, true); break;
                    case UP: frameActuelle = imageDosEchelle; break;
                    case DOWN:
                        if (typeCaseActuelle == 2) frameActuelle = imageDosEchelle;
                        else frameActuelle = imageChute;
                        break;
                    case NONE: default: frameActuelle = animIdle.getKeyFrame(stateTime, true); break;
                }
                batch.draw(frameActuelle, p.getX() * tileWidth, (height - 1 - p.getY()) * tileHeight, tileWidth, tileHeight);
            }
        }
    }

    public void drawTreasure(int width, int height, int tileWidth, int tileHeight){
        for(Treasure t : g.getTre()){
            if(!t.getCollect()){
                TextureRegion frameActuelle = animTreasure.getKeyFrame(stateTime, true);
                batch.draw(frameActuelle, t.getX() * tileWidth, (height - 1 - t.getY()) * tileHeight, tileWidth, tileHeight);
            }
        }
    }

    public void drawEnemy(int width, int height, int tileWidth, int tileHeight){
        for(Enemy e : g.getEne()){
            if(e.getState()){
                TextureRegion frameActuelle;
                if (e.getDirection() == Direction.LEFT) {
                    frameActuelle = animEnemyGauche.getKeyFrame(stateTime, true);
                } else {
                    frameActuelle = animEnemyDroite.getKeyFrame(stateTime, true);
                }
                batch.draw(frameActuelle, e.getX() * tileWidth, (height - 1 - e.getY()) * tileHeight, tileWidth, tileHeight);
            }
        }
    }

    public void inputPlayer(int ind){
        if(Gdx.input.isKeyJustPressed(Input.Keys.ALT_RIGHT)){
            client.action("SAVE");
        }
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
    
    private Animation<TextureRegion> creerAnimation(String nomFichier, int nbFrames, int frameWidth, int frameHeight) {
        Texture sheet = new Texture(Gdx.files.internal(nomFichier));
        TextureRegion[][] tmp = TextureRegion.split(sheet, frameWidth, frameHeight); 
        TextureRegion[] frames = new TextureRegion[nbFrames];
        int index = 0;
        for (int i = 0; i < tmp.length; i++) {
            for (int j = 0; j < tmp[i].length; j++) {
                if (index < nbFrames) {
                    frames[index] = tmp[i][j];
                    index++;
                }
            }
        }
        return new Animation<TextureRegion>(0.1f, frames); 
    }
}