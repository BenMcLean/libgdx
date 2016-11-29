package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.iterators.OrthogonalTiledMapIterator;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Tests OrthogonalTiledMapIterator by using it to draw a red box with a white border on selected tiles within the visible area of OrthogonalTiledMapRenderer
 * The green and blue squares are rendered by OrthogonalTiledMapRenderer and the red boxes with the white borders are things that can be rendered separately afterwards using the iterator
 * Created by BenMcLean on 11/28/2016.
 */
public class OrthogonalTiledMapIteratorTest extends GdxTest {
    public static final int VIRTUAL_WIDTH = 128;
    public static final int VIRTUAL_HEIGHT = 128;
    public static final int TILE_WIDTH = 8;
    public static final int TILE_HEIGHT = 8;
    public static final int SIZE_X = 128;
    public static final int SIZE_Y = 128;
    public int playerX = SIZE_X / 2, playerY = SIZE_Y / 2;

    private Color worldBackgroundColor;
    private Color screenBackgroundColor;
    public Texture one;
    public TextureAtlas.AtlasRegion water;
    public TextureAtlas.AtlasRegion grass;
    private SpriteBatch batch;
    private TiledMap map;
    private TiledMapRenderer tiledMapRenderer;
    private FrameBuffer frameBuffer;
    private Viewport worldView;
    private Viewport screenView;
    private Texture screenTexture;
    private TextureRegion screenRegion;
    private OrthogonalTiledMapIterator visibleIterator;

    @Override
    public void create () {
        worldBackgroundColor = Color.LIGHT_GRAY;
        screenBackgroundColor = Color.DARK_GRAY;
        batch = new SpriteBatch();
        map = new TiledMap();
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, true, true);
        worldView = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        screenView = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        screenRegion = new TextureRegion();

        Pixmap pixmap1 = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap1.drawPixel(0, 0, -1);
        one = new Texture(pixmap1);
        one.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        pixmap1.setColor(Color.BLUE);
        pixmap1.fill();
        Texture waterTesture = new Texture(pixmap1);
        waterTesture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        water = new TextureAtlas.AtlasRegion(waterTesture, 0, 0, TILE_WIDTH, TILE_HEIGHT);

        pixmap1.setColor(Color.GREEN);
        pixmap1.fill();
        Texture grassTesture = new Texture(pixmap1);
        grassTesture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        grass = new TextureAtlas.AtlasRegion(grassTesture, 0, 0, TILE_WIDTH, TILE_HEIGHT);

        pixmap1.dispose();

        MapLayers layers = map.getLayers();
        TiledMapTileLayer layer = new TiledMapTileLayer(SIZE_X, SIZE_Y, TILE_WIDTH, TILE_HEIGHT);
        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                StaticTiledMapTile tile = null;
                if (isLand(x, y))
                    tile = new StaticTiledMapTile(grass);
                else
                    tile = new StaticTiledMapTile(water);
                layer.setCell(x, y, new TiledMapTileLayer.Cell().setTile(tile));
            }
        }
        layers.add(layer);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(map);
        visibleIterator = new OrthogonalTiledMapIterator((OrthographicCamera) worldView.getCamera(), layer);
        screenView.getCamera().position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        screenView.update(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        batch.enableBlending();
    }

    @Override
    public void render () {
        frameBuffer.begin();
        Gdx.gl.glClearColor(worldBackgroundColor.r, worldBackgroundColor.g, worldBackgroundColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        worldView.apply();
        worldView.getCamera().position.set(playerX * TILE_WIDTH + (TILE_WIDTH / 2), playerY * TILE_HEIGHT + (TILE_HEIGHT / 2), 0);
        worldView.update(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        tiledMapRenderer.setView((OrthographicCamera) worldView.getCamera());
        tiledMapRenderer.render();
        batch.setProjectionMatrix(worldView.getCamera().combined);
        batch.begin();

        // Draw a red box with a white border on top of all visible tiles.
        visibleIterator.reset();
        while (visibleIterator.hasNext()) {
            GridPoint2 here = visibleIterator.next();
            if (isLand(here.x-1,  here.y-1)) {
                batch.setColor(Color.RED);
                drawSolidOverTile(batch, here.x, here.y);
                batch.setColor(Color.WHITE);
                drawOutlineOverTile(batch, here.x, here.y);
            }
        }

        batch.end();
        frameBuffer.end();

        Gdx.gl.glClearColor(screenBackgroundColor.r, screenBackgroundColor.g, screenBackgroundColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        screenView.apply();
        batch.setProjectionMatrix(screenView.getCamera().combined);
        batch.begin();
        screenTexture = frameBuffer.getColorBufferTexture();
        screenTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        screenRegion.setRegion(screenTexture);
        screenRegion.flip(false, true);
        batch.draw(screenRegion, 0, 0);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        screenView.update(width, height);
    }

    public Boolean isLand (int x, int y) {
        return x % 2 == 1 && y % 2 == 1;
    }

    public void drawSolidOverTile(SpriteBatch batch, int x, int y) {
        batch.draw(one, x * TILE_WIDTH, y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
    }

    public void drawOutlineOverTile(SpriteBatch batch, int x, int y) {
        drawRect(batch, x * TILE_WIDTH, y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
    }

    public void drawRect(SpriteBatch batch, int x, int y, int width, int height) {
        batch.draw(one, x + width - 1, y + 1, 1, height - 1);
        batch.draw(one, x + 1, y, width - 1, 1);
        batch.draw(one, x, y, 1, height - 1);
        batch.draw(one, x, y + height - 1, width - 1, 1);
    }
}
