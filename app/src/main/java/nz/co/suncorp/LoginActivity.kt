package nz.co.suncorp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.LoaderManager.LoaderCallbacks
import android.support.v4.content.Loader
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle

import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView

import android.os.StrictMode
import android.util.Log
import android.widget.EditText
import android.widget.Toast


import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import org.json.JSONTokener
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), LoaderCallbacks<Cursor> {
    override fun openOrCreateDatabase(name: String?, mode: Int, factory: SQLiteDatabase.CursorFactory?): SQLiteDatabase {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Cursor> {
        TODO("not implemented")
        //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private var mAuthTask: UserLoginTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        // Set up the login form.
        val password = findViewById<EditText>(R.id.password)
        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })



        sign_in_button.setOnClickListener { attemptLogin() }
    }


    private fun loginFailed() {
        val text = "Could not login. Please try again."
        val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
        val view = toast.view
        view.setBackgroundColor(Color.YELLOW)
     //   val tv = TextView(applicationContext)
     //   tv.setBackgroundColor(Color.YELLOW)
    //    toast.view = tv
        toast.show()
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {

        if (mAuthTask != null) {
            return
        }

        // Reset errors.

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        username.error = null
        password.error = null

        // Store values at the time of the login attempt.
        val userNameStr = username.text.toString()
        val passwordStr = password.text.toString()

          //  getGoole()

            showProgress(true)
            mAuthTask = UserLoginTask(userNameStr, passwordStr)
            mAuthTask!!.execute(null as Void?)



    }

    private fun getGoole() {
        val url = URL("http://www.google.com/")
        val urlConnection = url.openConnection() as HttpURLConnection
        try {
            Log.i("Heloooooo" , "get google....")
             Log.i("url connection ******* : ", urlConnection.inputStream.toString())
           // urlConnection.connect()
        } finally {
            urlConnection.disconnect()
        }
    }

    private fun getLoginInfo(userName: String, password: String): Boolean {
        //TODO - write code to auth
        var CLIENT_ID = "c1da6b73-fced-454e-9086-bba38817af42"  //PROD
        var CLIENT_ID_HEADER = "client_id"
        var CONTENT_TYPE_HEADER = "Content-Type"
        var APPLICATION_URLENCODED = "application/x-www-form-urlencoded"

        val urlAddress = "https://api.aai.com.au/oauth/v1/token"
        val url = URL(urlAddress)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.doOutput = true
        conn.setRequestProperty(CLIENT_ID_HEADER, CLIENT_ID)
        conn.setRequestProperty(CONTENT_TYPE_HEADER, APPLICATION_URLENCODED)
        OutputStreamWriter(conn.outputStream).use {
            // by `it` value you can get your OutputStreamWriter
            it.write("username=" + userName + "&password=" + password + "&grant_type=password")

        }

      //  conn.connect()
        try {

            Log.i("Real MAdrid..." , "getLoginInfo....")

            if(conn.responseCode == 200) {

                Log.i("url connection ******* : ", conn.inputStream.toString())
                Log.i("responseCode " , conn.responseCode.toString())
                Log.i("responseMessage " , conn.responseMessage.toString())


                val abc  = conn.inputStream
                val allText = conn.inputStream.bufferedReader().use(BufferedReader::readText)
                Log.i("allText",allText)



                val json = JSONTokener(allText).nextValue() as JSONObject
                val accessToken =  json.get("access_token") as String

                Log.i("access_token : ", accessToken)

                Log.i("", "")

                return true

            } else {
                //TODO display error message

                //this.runOnUiThread()
                return false
            }


        } finally {
            conn.disconnect()

        }

    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            login_form.visibility = if (show) View.GONE else View.VISIBLE
            login_form.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 0 else 1).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_form.visibility = if (show) View.GONE else View.VISIBLE
                        }
                    })

            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_progress.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_progress.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_form.visibility = if (show) View.GONE else View.VISIBLE
        }
    }


    override fun onLoadFinished(cursorLoader: Loader<Cursor>, cursor: Cursor) {


    }

    override fun onLoaderReset(cursorLoader: Loader<Cursor>) {

    }



    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    inner class UserLoginTask internal constructor(private val userName: String, private val password: String) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void): Boolean? {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000)
                Log.i("call getLoginInfo : " , "***********")
                return getLoginInfo(userName, password);

            } catch (e: InterruptedException) {
                return false
            }

            return  false
        }

        override fun onPostExecute(success: Boolean?) {
            mAuthTask = null
            showProgress(false)

            if (success!!) {

                    // Simulate network access.
                    Thread.sleep(2000)

                showPolicyScreen()
              //  finish()
            } else {
                //password.error = getString(R.string.error_incorrect_password)
                //password.requestFocus()
                //TODO - show Error message
                Log.e("ERROR : ", "Login failed.....")
                loginFailed()
            }
        }

        override fun onCancelled() {
            mAuthTask = null
            showProgress(false)
        }
    }

    fun showPolicyScreen()
    {
        val intent = Intent(this, PolicyActivity::class.java)
        startActivity(intent)
    }

    companion object {


    }
}


