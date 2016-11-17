package model.utils.render.strategys;

import model.elementstate.AffineTextureMappingStrategy;
import model.elementstate.TextureInterpolationStrategy;
import model.geometrie.Polygon;
import model.utils.PictureBufferState;

/**
 * Created by PhilippKroll on 09.11.2016.
 */
public abstract class RenderStrategy {
    public abstract void render(PictureBufferState pbs, Polygon p);
}
