package com.lokeshsoni.imagefilter;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.lokeshsoni.imagefilter.Adapter.ViewPagerAdapter;
import com.lokeshsoni.imagefilter.Interface.EditImageFragmentListner;
import com.lokeshsoni.imagefilter.Interface.FilterListFragmentListner;
import com.lokeshsoni.imagefilter.Utils.BitmapUtils;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FilterListFragmentListner,EditImageFragmentListner{

    public static final String pictureName = "drstrange.jpg";
    public static final  int PERMISSION_PICK_IMG =1000;

    ImageView imageView;
    TabLayout tabLayout;
    ViewPager viewPager;
    CoordinatorLayout coordinatorLayout;

    Bitmap originalBitmap,filteredBitmap,finalBitmap;

    FiltersListFragment filtersListFragment;
    EditImageFragment editImageFragment;

    int finalBrightness = 0;
    float finalSaturation = 1.0f;
    float finalConstrant = 10.f;

    // load native image  filter lib

    static {
        System.loadLibrary("NativeImageProcessor");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Instagram Filter");

        // View

        imageView = findViewById(R.id.imagePreview);
        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewpager);
        coordinatorLayout = findViewById(R.id.coordinatorlayout);


        loadimage();
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);


    }

    private void loadimage() {

        originalBitmap = BitmapUtils.getBitmapFromAssets(this,pictureName,300,300);
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888,true);

        imageView.setImageBitmap(originalBitmap);
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        filtersListFragment = new FiltersListFragment();
        filtersListFragment.setListner(this);

        editImageFragment = new EditImageFragment();
        editImageFragment.setListner(this);

        viewPagerAdapter.addFragment(filtersListFragment,"FILTERS");
        viewPagerAdapter.addFragment(editImageFragment,"EDIT");

        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public void onBrightnessChanged(int brightness) {
        finalBrightness = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        imageView.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));

    }

    @Override
    public void onSaturationChanged(float saturation) {

        finalSaturation = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        imageView.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));

    }

    @Override
    public void onConstrantChanged(float constrant) {

        finalConstrant = constrant;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(constrant));
        imageView.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));

    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditComplete() {

        Bitmap bitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888,true);
        Filter myFilter = new Filter();

        myFilter.addSubFilter(new BrightnessSubFilter(finalBrightness));
        myFilter.addSubFilter(new SaturationSubfilter(finalSaturation));
        myFilter.addSubFilter(new ContrastSubFilter(finalConstrant));

        finalBitmap = myFilter.processFilter(bitmap);


    }

    @Override
    public void onFilterSelected(Filter filter) {

        resetControl();

        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        imageView.setImageBitmap(filter.processFilter(filteredBitmap));
        finalBitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888,true);


    }

    private void resetControl() {
        if(editImageFragment != null)
            editImageFragment.resetControls();

         finalBrightness = 0;
         finalSaturation = 1.0f;
         finalConstrant = 10.f;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_open){
            openImageFromGallery();
            return  true;
        }
        if(id == R.id.action_save){
            saveImageToGallery();
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveImageToGallery() {


        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){

                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent,PERMISSION_PICK_IMG);

                        }else {
                            Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }


    private void openImageFromGallery() {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){

                            try {
                                final String path = BitmapUtils.insertImage(getContentResolver(),
                                        filteredBitmap,System.currentTimeMillis() + "profile.jpg",
                                        null);
                                if(!TextUtils.isEmpty(path)){

                                    Snackbar snackbar = Snackbar.make(coordinatorLayout,"Image saved to gallery",Snackbar.LENGTH_LONG)
                                            .setAction("OPEN", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    openImage(path);

                                                }
                                            });
                                    snackbar.show();
                                }
                                else {
                                    Snackbar snackbar = Snackbar.make(coordinatorLayout,"Image saved to gallery",Snackbar.LENGTH_LONG);

                                    snackbar.show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }else {
                            Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void openImage(String path) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(path),"image/*");
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RESULT_OK && requestCode == PERMISSION_PICK_IMG){

            Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this,data.getData(),800,800);

            originalBitmap.recycle();
            filteredBitmap.recycle();
            finalBitmap.recycle();

            originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true);
            filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
            finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888,true);

            imageView.setImageBitmap(originalBitmap);

            bitmap.recycle();

            // render selected img thumbnail

            filtersListFragment.displayThumbnail(originalBitmap);





        }
    }

}
