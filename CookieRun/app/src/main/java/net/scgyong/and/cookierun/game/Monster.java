package net.scgyong.and.cookierun.game;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import net.scgyong.and.cookierun.R;
import net.scgyong.and.cookierun.framework.game.RecycleBin;
import net.scgyong.and.cookierun.framework.res.BitmapPool;

public class Monster extends MapSprite {

    private static final String TAG = JellyItem.class.getSimpleName();
    private final float inset;
    protected Rect srcRect = new Rect();
    protected RectF collisionBox = new RectF();
    public int index;

    protected int[] SOUND_IDS = {
            R.raw.jelly,
            R.raw.jelly_alphabet,
            R.raw.jelly_item,
            R.raw.jelly_gold,
            R.raw.jelly_coin,
            R.raw.jelly_big_coin,
    };
    public int soundId() {
        return SOUND_IDS[index % SOUND_IDS.length];
    }

    public void removeMonster(){
        MainScene game = MainScene.get();
        game.remove(this);
    }

    public static Monster get(int index, float unitLeft, float unitTop) {
        Monster item = (Monster) RecycleBin.get(Monster.class);
        if (item == null) {
            item = new Monster();
        }
        item.init(index, unitLeft, unitTop);
        return item;
    }

    private void init(int index, float unitLeft, float unitTop) {
        super.init();
        this.index = index;

        srcRect.set(0, 0,  60, 68);
        setUnitDstRect(unitLeft, unitTop, 2, 2);
    }

    @Override
    public void update(float frameTime) {
        super.update(frameTime);
        collisionBox.set(dstRect);
        collisionBox.inset(inset, inset);
    }

    @Override
    public RectF getBoundingRect() {
        return collisionBox;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, srcRect, dstRect, null);
    }

    private Monster() {
        bitmap = BitmapPool.get(R.mipmap.monster);
        inset = MainScene.get().size(0.15f);
    }
}
