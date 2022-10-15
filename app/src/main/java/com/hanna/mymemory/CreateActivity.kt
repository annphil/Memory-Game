@file:Suppress("DEPRECATION")

package com.hanna.mymemory

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hanna.mymemory.models.BoardSize
import com.hanna.mymemory.utils.EXTRA_BOARD_SIZE
import com.hanna.mymemory.utils.isPermissionGranted
import com.hanna.mymemory.utils.requestPermission

class CreateActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CreateActivity"
        private const val PICK_PHOTO_CODE = 655
        private const val READ_EXTERNAL_PHOTOS_CODE = 248
        private const val READ_PHOTOS_PERMISSION = android.Manifest.permission.READ_EXTERNAL_STORAGE
        private const val MIN_GAME_NAME_LENGTH = 3
        private const val MAX_GAME_NAME_LENGTH = 14
    }

    private lateinit var rvImagePicker: RecyclerView
    private lateinit var etGameName: EditText
    private lateinit var btnSave: Button

    lateinit var adapter: ImagePickerAdapter
    private lateinit var boardSize: BoardSize
    private var numImagesRequired = -1
    private val chosenImageUris = mutableListOf<Uri>() //URI - Unified Resource identifier (identifies resource directory path)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        rvImagePicker = findViewById(R.id.rvImagePicker)
        etGameName = findViewById(R.id.etGameName)
        btnSave = findViewById(R.id.btnSave)

        //Back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)                   //To add back button option
        boardSize = intent.getSerializableExtra(EXTRA_BOARD_SIZE) as BoardSize
        numImagesRequired = boardSize.getNumPairs()
        supportActionBar?.title = "Choose pics(0/$numImagesRequired)"

        btnSave.setOnClickListener {
            Toast.makeText(this, "Oops! That's as far as I go for now :/ Rest after exams", Toast.LENGTH_LONG).show()
            saveDataToFirebase()
        }

        //To add max game name length
        etGameName.filters = arrayOf(InputFilter.LengthFilter(MAX_GAME_NAME_LENGTH))
        //To enable Save button if Game name has been changed
        etGameName.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                btnSave.isEnabled = shouldEnableSaveButton()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

         adapter = ImagePickerAdapter(this, chosenImageUris, boardSize, object : ImagePickerAdapter.ImageClickListener {
            // 'object' / 'class X' is an anonymous class which implements(overrides interface functions) the interface ImageClickListener
            override fun onPlaceHolderClicked() {
                // Following funs is to interact with Android Permissions API
                if (isPermissionGranted(this@CreateActivity, READ_PHOTOS_PERMISSION)) {
                    launchIntentForPhotos()
                } else {
                    requestPermission(this@CreateActivity, READ_PHOTOS_PERMISSION, READ_EXTERNAL_PHOTOS_CODE)
                }
            }
        })
        rvImagePicker.adapter = adapter
        rvImagePicker.setHasFixedSize(true)
        rvImagePicker.layoutManager = GridLayoutManager(this, boardSize.getWidth())
    }



    //requestPermission() will launch an AS dialogue asking the user if they wanna allow App to READ_EXTERNAL_STORAGE (AndrManifest)
    //regardless of whether user has allowed or declined permission, we'll get a call back ie, onRequestPermissionsResult()
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray) {
        if (requestCode == READ_EXTERNAL_PHOTOS_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchIntentForPhotos()
            } else {
                Toast.makeText(this, "To create a custom game, you need to provide access to your photos ", Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()        // To finish and go back to MainActivity
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    //result of startActivityForResult() inside launchIntentForPhotos(), comes in this fun
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != PICK_PHOTO_CODE || resultCode != Activity.RESULT_OK || data == null) {
            Log.w(TAG, "Did not get back from the launched activity, user likely cancelled flow")
            return
        }
        val selectedUri: Uri? = data.data  // If appl allows only 1 photo to be selected, data comes back in data.data as Uri
        val clipData: ClipData? = data.clipData // If appl allows multiple photos to be selected at a time
        if (clipData != null) {
            Log.i(TAG, "clipData numImages ${clipData.itemCount}: $clipData")
            for (i: Int in 0 until clipData.itemCount) {
                val clipItem: ClipData.Item = clipData.getItemAt(i)
                if (chosenImageUris.size < numImagesRequired)
                    chosenImageUris.add(clipItem.uri)
            }
        } else if (selectedUri != null) {
            Log.i(TAG, "data: $selectedUri")
            chosenImageUris.add(selectedUri)
        }
        adapter.notifyDataSetChanged()
        supportActionBar?.title = "Choose pics(${chosenImageUris.size}/$numImagesRequired)"
        btnSave.isEnabled = shouldEnableSaveButton()
    }

    private fun saveDataToFirebase() {
        Log.i(TAG, "saveDataToFirebase")
        // Downscaling image size to save space in firebase and make app faster
        for((index: Int , photoUri: Uri) in chosenImageUris.withIndex()){
          //  val imageByteArray = getImageByteArray(photoUri)
        }
    }


    private fun shouldEnableSaveButton(): Boolean {
        if(chosenImageUris.size != numImagesRequired)
            return false
        if(etGameName.text == null || etGameName.text.length < MIN_GAME_NAME_LENGTH)
            return false
        return true
    }

    private fun launchIntentForPhotos() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*" // Only of tye image
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) //To choose multiple photos
        startActivityForResult(Intent.createChooser(intent, "Choose pics"), PICK_PHOTO_CODE)
    }
    // Add permission in AndroidManifest.
    // Cont.. READ_EXTERNAL_STORAGE is dangerous type permission. Therefore code added in manifest alone will not give permission
    // Cont.. User should be given choice to allow/ reject dynamically.
}