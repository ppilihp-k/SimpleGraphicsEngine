package model.elementstate;

/**
 * Created by PhilippKroll on 16.08.2016.
 */
public class MaterialLightState {

    enum LightningState{
        AMBIENT
    }

    private LightningState lightningState;
    private float ambient;

    public MaterialLightState(){
        lightningState = LightningState.AMBIENT;
        ambient = 0.5f;
    }

    public float getAmbientIntensity(){
        return ambient;
    }
}
