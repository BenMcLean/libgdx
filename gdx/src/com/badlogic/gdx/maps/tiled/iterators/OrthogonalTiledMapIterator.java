package com.badlogic.gdx.maps.tiled.iterators;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import java.util.Iterator;

/**
 * Iterates over tiles in visible area using same logic as OrthogonalTiledMapRenderer from com.badlogic.gdx.maps.tiled.renderers.
 * Created by BenMcLean on 5/5/2016.
 */
public class OrthogonalTiledMapIterator implements Iterator<Vector2> {
    protected int x1, x2, y1, y2, y, x;
    protected OrthographicCamera camera;
    protected TiledMapTileLayer layer;
    protected float unitScale;
    protected Rectangle viewBounds = new Rectangle();

    public OrthogonalTiledMapIterator(OrthographicCamera camera, TiledMapTileLayer layer) {
        this(camera, layer, 1.0f);
    }

    public OrthogonalTiledMapIterator(OrthographicCamera camera, TiledMapTileLayer layer, float unitScale) {
        this.camera = camera;
        this.layer = layer;
        this.unitScale = unitScale;
        reset();
    }

    public OrthogonalTiledMapIterator reset() {
        float width = camera.viewportWidth * camera.zoom;
        float height = camera.viewportHeight * camera.zoom;
        viewBounds.set(camera.position.x - width / 2, camera.position.y - height / 2, width, height);

        final int layerWidth = layer.getWidth();
        final int layerHeight = layer.getHeight();

        final float layerTileWidth = layer.getTileWidth() * unitScale;
        final float layerTileHeight = layer.getTileHeight() * unitScale;

        x1 = Math.max(0, (int) (viewBounds.x / layerTileWidth));
        x2 = Math.min(layerWidth, (int) ((viewBounds.x + viewBounds.width + layerTileWidth) / layerTileWidth));

        y1 = Math.max(0, (int) (viewBounds.y / layerTileHeight));
        y2 = Math.min(layerHeight, (int) ((viewBounds.y + viewBounds.height + layerTileHeight) / layerTileHeight));

        y = y2 - 1;
        x = x1 - 1;

        return this;
    }

    @Override
    public boolean hasNext() {
        return !(y == y1 && x + 1 >= x2);
    }

    @Override
    public Vector2 next() {
        x++;
        if (x >= x2) {
            y--;
            x = x1;
        }
        return new Vector2(x, y);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
