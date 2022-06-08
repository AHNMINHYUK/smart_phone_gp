package net.scgyong.and.cookierun.game;

import android.graphics.Canvas;
import android.graphics.RectF;

import net.scgyong.and.cookierun.R;
import net.scgyong.and.cookierun.framework.interfaces.BoxCollidable;
import net.scgyong.and.cookierun.framework.objects.Sprite;
import net.scgyong.and.cookierun.framework.res.Metrics;

public class Fire extends Sprite implements BoxCollidable {
    protected float x, y;

    private float dx;
    private float dy;
    private float tx;
    private float ty;
    private float power;
    protected RectF boundingRect = new RectF();
    public Fire(float x, float y,float power) {
        super(x, y, R.dimen.fire_speed, R.mipmap.bullet);
        this.power=power;
        this.x=x;
        this.y=y;

    }

    private static float size, inset;
    public static void setSize(float size) {
        Fire.size = size;
        Fire.inset = size / 16;
    }


    public void update(float frameTime) {
        MainScene game = MainScene.get();
        float dx = this.x * frameTime;

        dx+=500*frameTime;
        if (x > Metrics.width || x<0) {
            game.remove(this);
            //recycleBin.add(this);
        }


        //  setDstRectWithRadius();
        boundingRect.set(dstRect);
        boundingRect.inset(inset, inset);
        dstRect.offset(dx, dy);
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, null, dstRect, null);
    }



    public float getPower() {

        return this.power;
    }

    @Override
    public RectF getBoundingRect() {
        return boundingRect;
    }
}
