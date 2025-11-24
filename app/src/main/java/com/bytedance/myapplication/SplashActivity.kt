package com.bytedance.myapplication
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import com.bytedance.myapplication.viewmodel.SplashViewModel
import androidx.compose.ui.platform.LocalContext
import android.os.Bundle
import android.content.Intent
import com.bytedance.myapplication.ui.SplashScreen

class SplashActivity : ComponentActivity() {
    private val viewModel: SplashViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            MaterialTheme {
                val context = LocalContext.current


// Pass a drawable resource id if you have an actual logo in drawable resources.
// You can replace R.drawable.splash_logo with your own, or pass null to use the text logo.
                val logoResId: Int? = try {
// replace the name below with your drawable name if present
                    val resId = resources.getIdentifier("splash_logo", "drawable", packageName)
                    if (resId != 0) resId else null
                } catch (e: Exception) {
                    null
                }


//                SplashScreen(viewModel = viewModel, logoResId = logoResId) {
//                SplashScreen(viewModel = viewModel) {
//// onFinished -> navigate to home screen
//                    val intent = Intent(context, MainActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                }
                SplashScreen(
                    viewModel = viewModel,
                    onFinished = {
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    logoResId = null
                )

            }
        }
    }
}