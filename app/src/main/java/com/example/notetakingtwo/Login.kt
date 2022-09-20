package com.example.notetakingtwo

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CancellationSignal
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import com.example.notetakingtwo.Retrofit.NoteRestApiService
import com.example.notetakingtwo.databinding.ActivityLoginBinding
import com.example.notetakingtwo.repositories.UserRepository
import com.example.notetakingtwo.viewmodels.LoginViewModel
import com.example.notetakingtwo.viewmodels.LoginViewModelFactory

class Login : AppCompatActivity() {
    private val userRepository: UserRepository = UserRepository(NoteRestApiService())
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private var cancellationSignal: CancellationSignal? = null
    private lateinit var sharedPref: SharedPreferences
    private lateinit var biometricPrompt: BiometricPrompt
    private val authenticaionCallback: BiometricPrompt.AuthenticationCallback
    get() =
        @RequiresApi(Build.VERSION_CODES.P)
        object: BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                super.onAuthenticationError(errorCode, errString)
                println("Authentication err $errString")

            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                viewModel.verifyBiometric(binding.loginName.text.toString(), binding.password.text.toString())
            }
        }
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = (application as NoteTakingApplication).getSharedPreferences("credentials", Context.MODE_PRIVATE)
       viewModel = ViewModelProviders.of(this, LoginViewModelFactory(application)).get(LoginViewModel::class.java)

        checkBiometricSupport()
        biometricPrompt = BiometricPrompt.Builder(this).setTitle("Welcome to NoteTaker!").setSubtitle(
            "Authentication is required!"
        ).setDescription("This app uses fingerprint protection to keep your data secure").setNegativeButton("Cancel", this.mainExecutor,
            DialogInterface.OnClickListener {
                    dialog, which -> println("Auth cancelled")
            }).build()
        binding.biometricsButton?.setOnClickListener {
            biometricPrompt.authenticate(getCancellationSignal(), mainExecutor, authenticaionCallback)
        }


        binding.apply {
            loginButton.setOnClickListener {
                var emptyForm = false
                if(loginName.text.isEmpty()) {
                    loginName.error = "Login name is empty!"
                    emptyForm = true
                }
                if(password.text.isEmpty()) {
                    password.error = "Password is empty!"
                    emptyForm = true
                }

                if(!emptyForm) viewModel.verifyCredentials(loginName.text.toString(), password.text.toString())
            }
            registerButton.setOnClickListener {
                val intent: Intent = Intent(this@Login, Register::class.java)
                startActivity(intent)
            }
        }

        binding.tryAgainButton.setOnClickListener {
            enableNetworkLayoutVisibiltiy()
        }
        enableNetworkLayoutVisibiltiy()


        observeLoginEvent()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onStart() {
        super.onStart()

        val biometricsUsername: String? = sharedPref.getString("BiometricsUsername", "")
        val biometricsPassword: String? = sharedPref.getString("BiometricsPassword", "")
        if(((biometricsUsername != "") or (biometricsPassword != "")) and (isNetworkAvailable())) {
            biometricPrompt.authenticate(getCancellationSignal(), mainExecutor, authenticaionCallback)
        }

    }

    private fun observeLoginEvent() {
        viewModel.loginFromLiveData.observe(this@Login) {
            println(it)
            if (it) {
                val intent: Intent = Intent(this@Login, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun checkBiometricSupport(): Boolean {
        /*val keyguardManager: KeyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if(!keyguardManager.isKeyguardSecure) {
            Toast.makeText(applicationContext, "Fingerprint authentication has not been enabled (1)!", Toast.LENGTH_SHORT).show()
            return false

        }*/
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(applicationContext, "Fingerprint authentication has not been enabled (2)!", Toast.LENGTH_SHORT).show()
            return false
        }

        return if(packageManager.hasSystemFeature((PackageManager.FEATURE_FINGERPRINT))) {
            true
        } else {
            true
        }
    }

    private fun getCancellationSignal(): CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            Toast.makeText(applicationContext, "Authentication success!", Toast.LENGTH_SHORT).show()
        }
        return cancellationSignal as CancellationSignal
    }

    private fun authenticateWithBiometrics() {

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isNetworkAvailable(): Boolean { //access network
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        print((capabilities != null && capabilities.hasCapability(NET_CAPABILITY_INTERNET)))
        return (capabilities != null && capabilities.hasCapability(NET_CAPABILITY_INTERNET))

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun enableNetworkLayoutVisibiltiy() {
        if(isNetworkAvailable()) {
            binding.noInternet?.visibility = GONE
            binding.internet?.visibility = VISIBLE
            println("hoi")

        } else {
            binding.internet?.visibility = GONE
            binding.noInternet?.visibility = VISIBLE
            println("hi")

        }
    }

}