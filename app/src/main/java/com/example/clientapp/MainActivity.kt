package com.example.clientapp

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.clientapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity - Client App"
        const val DATA_VAULT_PACKAGE = "com.example.openssldemo"
        const val SERVICE_NAME = "MainService"
        const val REGISTER_ACTION = "com.example.openssldemo.REGISTER"
        const val STORAGE_ACTION = "com.example.openssldemo.STORAGE"
        const val LOAD_ACTION = "com.example.openssldemo.LOAD"
        const val PACKAGE_ID = "packageID"

    }

    var mService: IMyAidlInterface? = null
    private var isBound = false;
    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            isBound = true
            mService = IMyAidlInterface.Stub.asInterface(service)
            Toast.makeText(applicationContext, "Connected to remote service", Toast.LENGTH_SHORT).show()
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
        binding.apply {
            registerButton.setOnClickListener { handleRegister() }
            storeButton.setOnClickListener { handleStore() }
            loadButton.setOnClickListener { handleLoad() }
        }
    }


    private fun handleRegister() {
        Toast.makeText(this, "Register", Toast.LENGTH_SHORT).show()
        val intent = Intent()
        intent.setAction(REGISTER_ACTION)
        intent.setComponent(ComponentName(DATA_VAULT_PACKAGE, "$DATA_VAULT_PACKAGE.$SERVICE_NAME"))
        intent.putExtra(PACKAGE_ID, packageName)
        bindService(intent, mConnection, BIND_AUTO_CREATE)

    }

    private fun handleStore() {}
    private fun handleLoad() {}
}