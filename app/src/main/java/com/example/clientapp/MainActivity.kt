package com.example.clientapp

import android.R
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.clientapp.databinding.ActivityMainBinding
import com.example.openssldemo.IMyAidlInterface
import java.io.FileInputStream
import java.security.KeyStore
import java.security.KeyStoreException

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity - Client App"
        const val DATA_VAULT_PACKAGE = "com.example.openssldemo"
        const val SERVICE_NAME = "MainService"
        const val PACKAGE_ID = "packageID"

    }

    var mService: IMyAidlInterface? = null
    private var isBound = false;
    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            isBound = true
            mService = IMyAidlInterface.Stub.asInterface(service as IBinder)
            Toast.makeText(applicationContext, "Connected to remote service", Toast.LENGTH_SHORT)
                .show()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            mService = null
            Log.d(TAG, "Disconnected from remote service")

        }

    }

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val listType: List<String> = listOf("password", "username", "email")
        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, listType)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerType.adapter = adapter

        binding.apply {
            registerButton.setOnClickListener { handleRegister() }
            storeButton.setOnClickListener { handleStore() }
            loadButton.setOnClickListener { handleLoad() }
            inputTextEdit.setOnKeyListener { v, keyCode, _ -> handleKeyEvent(v, keyCode) }
            inputTextEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s != null) {
                        binding.storeButton.isEnabled = s.isNotEmpty()
                    }
                }

                override fun afterTextChanged(s: Editable?) {

                }

            })
        }
    }

    override fun onStart() {
        super.onStart()
//        Toast.makeText(this, "Register", Toast.LENGTH_SHORT).show()
//        val intent = Intent()
//        //intent.setAction(REGISTER_ACTION)
//        intent.setComponent(ComponentName(DATA_VAULT_PACKAGE, "$DATA_VAULT_PACKAGE.$SERVICE_NAME"))
//        //intent.putExtra(PACKAGE_ID, packageName)
//        bindService(intent, mConnection, BIND_AUTO_CREATE)

//        Toast.makeText(this, isBound.toString(), Toast.LENGTH_SHORT).show()
//        Toast.makeText(this,( mService == null).toString(), Toast.LENGTH_SHORT).show()
        val intent = Intent()
        //intent.setAction(REGISTER_ACTION)
        intent.setComponent(ComponentName(DATA_VAULT_PACKAGE, "$DATA_VAULT_PACKAGE.$SERVICE_NAME"))
        intent.putExtra(PACKAGE_ID, packageName)
        bindService(intent, mConnection, BIND_AUTO_CREATE)
    }

    private fun handleRegister() {

        if (isBound) {
            try {
                val response = mService?.register(packageName)
                Log.d(TAG, response.toString())
                Toast.makeText(this, response , Toast.LENGTH_SHORT).show()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    private fun handleStore() {
        val dataValue = binding.inputTextEdit.text.toString()
        val dataType = binding.spinnerType.selectedItem.toString()
        if (isBound) {
            try {
                val response = mService?.store(packageName, dataValue, dataType)
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    private fun handleLoad() {
        val dataType = binding.spinnerType.selectedItem.toString()
        if (isBound) {
            try {
                val dataValue = mService?.load(packageName, dataType)

                binding.loadDataEdt.setText(dataValue)

            } catch (e: RemoteException) {
                e.printStackTrace()
            }

        }

    }

    private fun handleKeyEvent(view : View, keyCode : Int) : Boolean  {
        if(keyCode == KeyEvent.KEYCODE_ENTER) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            return true
        }
        return false
    }
}