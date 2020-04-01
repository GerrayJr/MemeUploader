package com.gerray.memeuploader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public BottomNavigationView mNav;
    private ImageUploader imageUploader;
    private VideoUploader videoUploader;
    private ArticleUploader articleUploader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout mFrame = findViewById(R.id.mFrame);
        mNav = findViewById(R.id.bottom_nav);

        imageUploader = new ImageUploader();
        articleUploader = new ArticleUploader();
        videoUploader = new VideoUploader();

        setFragment(imageUploader);

        mNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_photos:
                        setFragment(imageUploader);
                        return true;

                    case R.id.nav_video:
                        setFragment(videoUploader);
                        return true;

                    case R.id.nav_articles:
                        setFragment(articleUploader);
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mFrame, fragment);
        fragmentTransaction.commit();
    }
}
