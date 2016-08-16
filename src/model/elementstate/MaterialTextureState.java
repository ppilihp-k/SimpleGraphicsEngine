package model.elementstate;

import java.util.Enumeration;

/**
 * Created by PhilippKroll on 16.08.2016.
 */
public class MaterialTextureState {

    enum TextureState {
        COLOR_ONLY,TEXTURE_ONLY,COLOR_BACKGROUND_WITH_TEXTURE
    }

    private TextureState textureState;
    private int color;
    private String pathToTexture;

    public MaterialTextureState(){
        textureState = TextureState.COLOR_ONLY;
        color = 0;
    }

    public void setTextureState(TextureState state){
        this.textureState = state;
    }

    public int getColor(){
        if(textureState == TextureState.TEXTURE_ONLY){
            throw new IllegalArgumentException("the textures state is COLOR_ONLY");
        }
        return color;
    }

    public String getTexture(){
        if(textureState == TextureState.COLOR_ONLY){
            throw new IllegalArgumentException("the textures state is TEXTURE_ONLY");
        }
        return pathToTexture;
    }

    public void setColor(int color){
        this.color = color;
    }

    public void setTexture(String path){
        this.pathToTexture = path;
    }
}
