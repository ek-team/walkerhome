package com.pharos.walker.ui;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.PlayerView;
import com.pharos.walker.R;
import com.pharos.walker.constants.AppKeyManager;
import com.pharos.walker.utils.VideoPlayUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/4/13
 * Describe:
 */
public class VideoPlayerActivity extends BaseActivity {
    @BindView(R.id.player_view)
    PlayerView playerView;
    @BindView(R.id.tv_back)
    TextView tvBack;
    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        String filepath = getIntent().getStringExtra(AppKeyManager.EXTRA_VIDEO_FILE);
        VideoPlayUtil.getInstance().setVideoPlayer(this,filepath,playerView);
        VideoPlayUtil.getInstance().startPlayer();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_video_player;
    }


    @OnClick(R.id.tv_back)
    public void onViewClicked() {
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        VideoPlayUtil.getInstance().destroyPlayer();
    }

}
