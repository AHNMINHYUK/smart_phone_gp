package net.scgyong.and.cookierun.game;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.JsonReader;

import net.scgyong.and.cookierun.R;
import net.scgyong.and.cookierun.framework.interfaces.BoxCollidable;
import net.scgyong.and.cookierun.framework.interfaces.GameObject;
import net.scgyong.and.cookierun.framework.objects.SheetSprite;
import net.scgyong.and.cookierun.framework.res.BitmapPool;
import net.scgyong.and.cookierun.framework.res.Metrics;
import net.scgyong.and.cookierun.framework.view.GameView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Player extends SheetSprite implements BoxCollidable {

    private static final float FRAMES_PER_SECOND = 8f;
    private static final String TAG = Player.class.getSimpleName();

    public void changeBitmap() {
        int nextIndex = (cookieIndex + 1) % cookieInfos.size();
        selectCookie(nextIndex);
        setState(state);
    }

    public  enum MoveState{
        left,right, stop ,COUNT, shot;
    }

    private enum State {
        run, jump, doubleJump, falling, slide, COUNT, shot;
        Rect[] srcRects() {
            return rectsArray[this.ordinal()];
        }
        void applyInsets(RectF dstRect) {
            float[] inset = insets[this.ordinal()];
            float w = dstRect.width();
            float h = dstRect.height();
            dstRect.left += w * inset[0];
            dstRect.top += h * inset[1];
            dstRect.right -= w * inset[2];
            dstRect.bottom -= h * inset[3];
        }
        static Rect[][] rectsArray;
        static void initRects() {
            int[][] indices = {
                    new int[] { 0,1 ,2, 3,4,5,6,7,8 }, // run
                    new int[] { 7, 8 }, // jump
                    new int[] { 1, 2, 3, 4 }, // doubleJump
                    new int[] { 0 }, // falling
                    new int[] { 9, 10 },
            };
            rectsArray = new Rect[indices.length][];
            for (int r = 0; r < indices.length; r++) {
                int[] ints = indices[r];
                Rect[] rects = new Rect[ints.length];
                for (int i = 0; i < ints.length; i++) {
                    int idx = ints[i];

                    Rect rect = new Rect(0 + (i * 64),0,(i+1 )*64,64);
                    rects[i] = rect;
                }
                rectsArray[r] = rects;
            }
        }
        static float[][] insets = {
                new float[] { 0.f, 0.f, 0.f, 0.00f }, // run
                new float[] { 85/270f, 158/270f, 80/270f, 0.00f }, // jump
                new float[] { 85/270f, 150/270f, 80/270f, 0.00f }, // doubleJump
                new float[] { 85/270f, 125/270f, 80/270f, 0.00f }, // falling
                new float[] { 80/270f, 204/270f, 50/270f, 0.00f }, // slide
        };
    }
    private State state = State.run;
    private final float jumpPower;
    private final float gravity;
    private float jumpSpeed;
    protected RectF collisionBox = new RectF();
    public MoveState moveState;
    public boolean isCheck = false;

    public Player(float x, float y) {
        super(R.mipmap.charcter, FRAMES_PER_SECOND);
        this.x = x;
        this.y = y;
        setDstRect(0, 0);
        loadCookiesInfo();
        selectCookie(0);
        jumpPower = Metrics.size(R.dimen.player_jump_power);
        gravity = Metrics.size(R.dimen.player_gravity);
        setState(State.run);
        moveState = MoveState.stop;
        //bitmap = BitmapPool.get();
        dstRect.set(0,0,128,128);

    }

    private void selectCookie(int cookieIndex) {
        this.cookieIndex = cookieIndex;
        CookieInfo info = cookieInfos.get(cookieIndex);
        State.initRects();
        AssetManager assets = GameView.view.getContext().getAssets();
//        try {
//            String filename = "cookies/" + info.id + "_sheet.png";
//            InputStream is = assets.open(filename);
//            bitmap = BitmapFactory.decodeStream(is);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        float bottom = dstRect.bottom;
        float size = 64;
        dstRect.set(0,0,64,64);
    }

    private class CookieInfo {
        int id;
        String name;
        int size;
        int xcount;
        int ycount;
    }
    private ArrayList<CookieInfo> cookieInfos;
    private int cookieIndex;


    private void loadCookiesInfo() {
        cookieInfos = new ArrayList<>();
        AssetManager assets = GameView.view.getContext().getAssets();
        try {
            InputStream is = assets.open("cookies.json");
            JsonReader reader = new JsonReader(new InputStreamReader(is, "UTF-8"));
            reader.beginArray();
            while (reader.hasNext()) {
                reader.beginObject();
                CookieInfo info = new CookieInfo();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals("id")) {
                        info.id = reader.nextInt();
                    } else if (name.equals("name")) {
                        info.name = reader.nextString();
                    } else if (name.equals("size")) {
                        info.size = reader.nextInt();
                    } else if (name.equals("xcount")) {
                        info.xcount = reader.nextInt();
                    } else if (name.equals("ycount")) {
                        info.ycount = reader.nextInt();
                    }
                }
                reader.endObject();
                cookieInfos.add(info);
            }
            reader.endArray();

            cookieIndex = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public RectF getBoundingRect() {
        return collisionBox;
    }

    @Override
    public void update(float frameTime) {
        float foot = collisionBox.bottom;
        switch (state) {
            case jump:
            case doubleJump:
            case falling:
                float dy = jumpSpeed * frameTime;
                jumpSpeed += gravity * frameTime;
                if (jumpSpeed >= 0) {
                     float platformTop = findNearestPlatformTop(foot);
                    if (foot + dy >= platformTop) {
                        dy = platformTop - foot;
                        setState(State.run);
                    }
                }
                y += dy;
                dstRect.offset(0, dy);
                collisionBox.offset(0, dy);
                break;
            case run:
                if(moveState == MoveState.stop)
                {
                    isCheck = false;

                }
                else if(moveState == MoveState.left)
                {
                    MoveLeft();
                    isCheck = false;
                }
                else if(moveState == MoveState.right)
                {
                    MoveRight();
                }

                case slide:
                float platformTop = findNearestPlatformTop(foot);
                if (foot < platformTop) {
                    setState(State.falling);
                    jumpSpeed = 0;
                }
                break;
        }
    }

    private float findNearestPlatformTop(float foot) {
        Platform platform = findNearestPlatform(foot);
        if (platform == null) return Metrics.height;
        return platform.getBoundingRect().top;
    }

    private Platform findNearestPlatform(float foot) {
        Platform nearest = null;
        MainScene game = MainScene.get();
        ArrayList<GameObject> platforms = game.objectsAt(MainScene.Layer.platform.ordinal());
        float top = Metrics.height;
        for (GameObject obj: platforms) {
            Platform platform = (Platform) obj;
            RectF rect = platform.getBoundingRect();
            if (rect.left > x || x > rect.right) {
                continue;
            }
            if (rect.top < foot) {
                continue;
            }
            if (top > rect.top) {
                top = rect.top;
                nearest = platform;
            }
        }
        return nearest;
    }

    public void ChangeMoveState(int state)
    {
        if(state == 0)
            moveState = MoveState.stop;
        if(state == 1)
            moveState = MoveState.left;
        if(state == 2)
            moveState = MoveState.right;
    }

    public void MoveRight()
    {
        if(dstRect.right < 1500){
            setState(State.run);
            float dx = 10;

            x += dx;

            dstRect.offset(dx, 0);

            collisionBox.offset(dx, 0);
        }
        else{
            isCheck = true;
        }
    }

    public void MoveLeft()
    {
        setState(State.run);
        float dx = -10;
        x -= dx;
        dstRect.offset(dx, 0);
        collisionBox.offset(dx, 0);
    }


    public void jump() {
//        Log.d(TAG, "Jump");
        if (state == State.run) {
            setState(State.jump);
            jumpSpeed = -jumpPower;
        } else if (state == State.jump){
            setState(State.doubleJump);
            jumpSpeed = -jumpPower;
        }
    }

    public void slide(boolean startsSlide) {
        if (state == State.run && startsSlide) {
            setState(State.slide);
            return;
        }
        if (state == State.slide && !startsSlide) {
            setState(State.run);
            return;
        }
    }

    public void fire(){
        float power = 10;
        Fire bullet = new Fire( dstRect.centerX() ,dstRect.centerY(), power);
        MainScene.get().add(MainScene.Layer.fire.ordinal(),bullet);
    }

    public void fall() {
        if (state != State.run) return;
        float foot = collisionBox.bottom;
        Platform platform = findNearestPlatform(foot);
        if (platform == null) return;
        if (!platform.canPass()) return;
        setState(State.falling);
        dstRect.offset(0, 0.001f);
        collisionBox.offset(0, 0.001f);
        jumpSpeed = 0;
    }

    private void setState(State state) {
        this.state = state;
        srcRects = state.srcRects();
        collisionBox.set(dstRect);
        state.applyInsets(collisionBox);
    }
}
