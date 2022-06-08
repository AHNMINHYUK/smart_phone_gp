package net.scgyong.and.cookierun.game;

import net.scgyong.and.cookierun.R;
import net.scgyong.and.cookierun.framework.game.Scene;
import net.scgyong.and.cookierun.framework.objects.Button;
import net.scgyong.and.cookierun.framework.objects.HorzScrollBackground;
import net.scgyong.and.cookierun.framework.res.Metrics;
import net.scgyong.and.cookierun.framework.res.Sound;

public class MainScene extends Scene {
    public static final String PARAM_STAGE_INDEX = "stage_index";
    private static final String TAG = MainScene.class.getSimpleName();
    private Player player;
    private Fire fire;
    private static MainScene singleton;
    public static MainScene get() {
        if (singleton == null) {
            singleton = new MainScene();
        }
        return singleton;
    }
    public enum Layer {
        bg, platform, item, obstacle, player,fire ,ui, touchUi, controller, COUNT, monster;
    }

    public float size(float unit) {
        return Metrics.height / 9.5f * unit;
    }

    public void setMapIndex(int mapIndex) {
        this.mapIndex = mapIndex;
    }
    public Player GetPlayer(){
        return  player;
    }
    public Fire GetFire() {
        return fire;
    }
    protected int mapIndex;

    public void init() {
        super.init();

        initLayers(Layer.COUNT.ordinal());

        player = new Player(size(10), size(10));
        add(Layer.player.ordinal(), player);
        add(Layer.bg.ordinal(), new HorzScrollBackground(R.mipmap.back1, Metrics.size(R.dimen.bg_scroll_1)));

        MapLoader mapLoader = MapLoader.get();
        mapLoader.init(mapIndex);
        add(Layer.controller.ordinal(), mapLoader);
        add(Layer.controller.ordinal(), new CollisionChecker(player));
        //add(Layer.controller.ordinal(), new CollisionChecker(fire));
        float btn_x = size(1.5f);
        float btn_y = size(8.75f);
        float btn_w = size(8.0f / 3.0f);
        float btn_h = size(1.0f);
        add(Layer.touchUi.ordinal(), new Button(
                Metrics.width - btn_x , btn_y, btn_w, btn_h, R.mipmap.btn_jump_n, R.mipmap.btn_jump_p,
                new Button.Callback() {
            @Override
            public boolean onTouch(Button.Action action) {
                if (action != Button.Action.pressed) return false;
                player.jump();
                return true;
            }
        }));

        add(Layer.touchUi.ordinal(), new Button(
                btn_x , btn_y, btn_w, btn_h, R.mipmap.lbutton, R.mipmap.lbutton,
                new Button.Callback() {
            @Override
            public boolean onTouch(Button.Action action) {
                if (action == Button.Action.pressed) {
                    player.ChangeMoveState(1);
                    return true;
                }
                if (action == Button.Action.released) {
                    player.ChangeMoveState(0);
                    return true;
                }
                else return  false;
            }
        }));

        add(Layer.touchUi.ordinal(), new Button(
                btn_x + 1500 , btn_y, btn_w, btn_h, R.mipmap.bullet, R.mipmap.bullet,
                new Button.Callback() {
                    @Override
                    public boolean onTouch(Button.Action action) {
                        if (action == Button.Action.pressed) {
                            player.ChangeMoveState(5);
                            player.fire();
                            return true;
                        }
                        if (action == Button.Action.released) {
                            player.ChangeMoveState(0);
                            return true;
                        }
                        else return  false;
                    }
                }));

        add(Layer.touchUi.ordinal(), new Button(
                btn_x + btn_x / 2 + btn_w, btn_y, btn_w, btn_h, R.mipmap.rbutton, R.mipmap.rbutton,
                new Button.Callback() {
                    @Override
                    public boolean onTouch(Button.Action action) {
                        if (action == Button.Action.pressed) {
                            player.ChangeMoveState(2);
                            return true;
                        }
                        if (action == Button.Action.released) {
                            player.ChangeMoveState(0);
                            return true;
                        }
                        else return  false;
                    }
                }));
    }

    @Override
    public boolean handleBackKey() {
        push(PausedScene.get());
        return true;
    }

    @Override
    protected int getTouchLayerIndex() {
        return Layer.touchUi.ordinal();
    }

    @Override
    public void start() {
        Sound.playMusic(R.raw.main);
    }

    @Override
    public void pause() {
        Sound.pauseMusic();
    }

    @Override
    public void resume() {
        Sound.resumeMusic();
    }

    @Override
    public void end() {
        Sound.stopMusic();
    }
}
