package com.axel.makecall.activity

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.axel.makecall.R
import com.axel.makecall.activity.classes.URIPathHelper
import com.axel.makecall.activity.constants.Constants.REQUEST_CODE
import com.axel.makecall.activity.constants.Constants.SELECT_IMAGE_CODE
import com.axel.makecall.activity.constants.Constants.TAG
import com.axel.makecall.databinding.ActivityMediaIntentsBinding
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.IOException

class MediaIntents : AppCompatActivity() {
    private lateinit var binding: ActivityMediaIntentsBinding
    //Initialize "registerForActivityResult" since *OnActivityResult* is deprecated
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var currentImageUri: Uri? = null
    //initialize the animations to make them accessible in the main activity
    private val rotateOpen:Animation by lazy { AnimationUtils.loadAnimation(this,
    R.anim.rotate_open_anim
) }
    private val rotateClose:Animation by lazy { AnimationUtils.loadAnimation(this,
        R.anim.rotate_close_anim
    ) }
    private val fromBottom:Animation by lazy { AnimationUtils.loadAnimation(this,
        R.anim.from_bottom_anim
    ) }
    private val toBottom:Animation by lazy { AnimationUtils.loadAnimation(this,
        R.anim.to_bottom_anim
    ) }
 //make the default value of the switch(clicked for when its clicked) to false
    private var clicked = false
    private lateinit var mImageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaIntentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Media Implicit Intents"
        //used binding to bind the views fo thier respective varriables
        var fab = binding.add
        var photo = binding.camera
        var media = binding.gallery
        var film = binding.video

        mImageView = binding.mView
// start with the button animations
        fab.setOnClickListener{
            onAddButtonClicked()
        }

        //result of open camera
        // this is new way to handle intent since onActivityResult is deprecated now
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->

            if (result.resultCode==Activity.RESULT_OK) {
                camera()
            }
        }

        photo.setOnClickListener{

            openCamera()
        }
        media.setOnClickListener{
            gallery(it)
        }

        film.setOnClickListener{
            video(it)
        }

    }

    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        //set value of the switch to true
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean) {
        //used binding to bind the views fo thier respective varriables

        val photo = binding.camera
        val media = binding.gallery
        val film = binding.video
        if (!clicked) {
            photo.visibility = View.VISIBLE
            media.visibility = View.VISIBLE
            film.visibility = View.VISIBLE

        }else{
            photo.visibility = View.INVISIBLE
            media.visibility = View.INVISIBLE
            film.visibility = View.INVISIBLE
        }
    }
    private fun setAnimation(clicked: Boolean){
        //used binding to bind the views fo thier respective varriables
        val fab = binding.add
        val photo = binding.camera
        val media = binding.gallery
        val film = binding.video
        if (!clicked){
            photo.startAnimation(fromBottom)
            media.startAnimation(fromBottom)
            film.startAnimation(fromBottom)
            fab.startAnimation(rotateOpen)
        }else{
            photo.startAnimation(toBottom)
            media.startAnimation(toBottom)
            film.startAnimation(toBottom)
            fab.startAnimation(rotateClose)
        }
    }
    private fun setClickable(clicked:Boolean){
        //used binding to bind the views fo thier respective varriables

        val photo = binding.camera
        val media = binding.gallery
        val film = binding.video

        if (!clicked) {
            photo.isClickable=true
            media.isClickable=true
            film.isClickable=true
        }else{
            photo.isClickable=false
            media.isClickable=false
            film.isClickable=false
        }
    }
    private fun openCamera() {
        //request for Camera and read storage permissions
        val permissions = mutableListOf<String>()
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)

        //ask write external storage permissions when sdk is less than 28
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        Dexter.withContext(this)
            .withPermissions(permissions).withListener(object : MultiplePermissionsListener{
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                    if (report != null) {
                        if(report.areAllPermissionsGranted()){
                            Log.d(TAG, "onPermissionsChecked: all granted")
                            val values = ContentValues()
                            values.put(MediaStore.Images.Media.TITLE,"New Picture")
                            Log.e("Values1",values.toString())
                            values.put(MediaStore.Images.Media.DESCRIPTION,"From your Camera")
                            Log.e("Values2",values.toString())
                            currentImageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
                            Log.e("URI",currentImageUri.toString())
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            intent.putExtra(MediaStore.EXTRA_OUTPUT,currentImageUri)
                            Log.e("URI value",currentImageUri.toString())
                            resultLauncher.launch(intent)
                        }else{
                            Log.e(TAG,"onPermissionsChecked: not granted")
//                            Toast.makeText(this, "Your app does not have permissions to access the camera ", Toast.LENGTH_LONG).show()
                        }
                    }


                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    /*...*/
                }
            }).check()
    }


    private fun camera(){

        Log.e("URI","this method was called")
        // I am using glide because it prevent image rotation after image click
        Glide.with(this).load(currentImageUri).into(mImageView)

    }

    private fun video(view:View){

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) { // First check if camera is available in the device
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            startActivityForResult(intent, REQUEST_CODE);
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode:Int,intent:Intent?){
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            if(intent?.data != null){
                val uriPathHelper = URIPathHelper()
                val videoFullPath = uriPathHelper.getPath(this, intent.data!!) // Use this video path according to your logic
                val filePath = currentImageUri?.let { uriPathHelper.getPath(this, it) }
                // if you want to play video just after recording it to check is it working (optional)
                if (videoFullPath != null){
                    playVideoInDevicePlayer(videoFullPath)
                }
            }
            //to AVOID conflictiong declarations for both Video and gallery intents we put here the pick from gallery intent function
                    try {
                        val bitmap:Bitmap = MediaStore.Images.Media.getBitmap(application.contentResolver,intent?.data)
                        mImageView.setImageBitmap(bitmap)
                    }catch (exp: IOException){
                        exp.printStackTrace()
                    }
                }
             if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            }
        }

    private fun playVideoInDevicePlayer(videoPath: String){
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoPath))
        intent.setDataAndType(Uri.parse(videoPath),"video/mp4")
        startActivity(intent)
    }

    private fun gallery(view: View){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"Select A Picture.."),SELECT_IMAGE_CODE)
    }
}