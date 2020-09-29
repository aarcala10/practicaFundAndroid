package io.keepcoding.eh_ho.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.RequestError
import io.keepcoding.eh_ho.data.SignInModel
import io.keepcoding.eh_ho.data.SignUpModel
import io.keepcoding.eh_ho.data.UserRepo
import io.keepcoding.eh_ho.topics.TopicsActivity
import io.keepcoding.eh_ho.isFirstTimeCreated
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_sign_in.*
import kotlinx.android.synthetic.main.fragment_sign_up.*


class LoginActivity : AppCompatActivity(), SignInFragment.SignInInteractionListener,
    SignUpFragment.SignUpInteractionListener {

    val signInFragment = SignInFragment()
    val signUpFragment = SignUpFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (isFirstTimeCreated(savedInstanceState)) {
            checkSession()
        }
    }

    private fun checkSession() {
        if (UserRepo.isLogged(this.applicationContext)) {
            showTopics()
        } else {
            onGoToSignIn()
        }
    }

    private fun showTopics() {
        val intent = Intent(this, TopicsActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onGoToSignUp() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, signUpFragment)
            .commit()
    }


    override fun onSignIn(signInModel: SignInModel) {
        enableLoading()
        if (isFormValid("signIn")) {
            UserRepo.signIn(this.applicationContext, signInModel,
                { showTopics() },
                { error ->
                    enableLoading(false)
                    handleError(error)
                }
            )

        }
        else {
            enableLoading(false)
            showErrors("signIn")

        }
    }

    private fun handleError(error: RequestError) {

        if (error.messageResId != null)
            Snackbar.make(container, error.messageResId, Snackbar.LENGTH_LONG).show()
        else if (error.message != null)
            Snackbar.make(container, error.message, Snackbar.LENGTH_LONG).show()
        else
            Snackbar.make(container, R.string.error_defualt, Snackbar.LENGTH_LONG).show()

    }

    override fun onGoToSignIn() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, signInFragment)
            .commit()
    }

    override fun onSignUp(signUpModel: SignUpModel) {
        enableLoading()
        if (isFormValid("signUp")) {
            UserRepo.signUp(
                this.applicationContext,
                signUpModel,
                {
                    enableLoading(false)
                    Snackbar.make(container, R.string.message_sign_up, Snackbar.LENGTH_LONG).show()
                },
                {
                    enableLoading(false)
                    handleError(it)
                }
            )
        } else {
            enableLoading(false)
            showErrors("signUp")

        }
    }

    private fun isFormValid(type: String): Boolean {
        if (type == "signIn") {
            return inputUsername.text.isNotEmpty() && inputPassword.text.isNotEmpty()
        }
        if (type == "signUp") {
            return inputEmail.text.isNotEmpty()
                    && inputUsernameSignUp.text.isNotEmpty()
                    && inputPasswordSignUp.text.isNotEmpty()
                    && inputConfirmPassword.text.isNotEmpty()
        } else {
            return false
        }
    }

    private fun showErrors(type: String) {
        if (type == "signIn") {
            if (inputUsername.text.isEmpty())
                inputUsername.error = getString(R.string.error_empty)

            if (inputPassword.text.isEmpty())
                inputPassword.error = getString(R.string.error_empty)
        }

        if (type == "signUp") {
            if (inputEmail.text.isEmpty())
                inputEmail.error = getString(R.string.error_empty)

            if (inputUsernameSignUp.text.isEmpty())
                inputUsernameSignUp.error = getString(R.string.error_empty)

            if (inputPasswordSignUp.text.isEmpty())
                inputPasswordSignUp.error = getString(R.string.error_empty)

            if (inputConfirmPassword.text.isEmpty())
                inputConfirmPassword.error = getString(R.string.error_empty)

            if (inputPasswordSignUp.text != inputConfirmPassword.text)
                inputConfirmPassword.error = getString(R.string.error_password_confirm)

        }


    }

    private fun enableLoading(enable: Boolean = true) {
        if (enable) {
            fragmentContainer.visibility = View.INVISIBLE
            viewLoading.visibility = View.VISIBLE
        } else {
            fragmentContainer.visibility = View.VISIBLE
            viewLoading.visibility = View.INVISIBLE
        }
    }

    private fun simulateLoading(signInModel: SignInModel) {
        /*val runnable = Runnable {
            Thread.sleep(3000)
            viewLoading.post {
                showTopics()
            }
        }
        Thread(runnable).start()
        */
        /*
        val task = @SuppressLint("StaticFieldLeak")
        object : AsyncTask<Long, Void, Boolean>() {
            override fun doInBackground(vararg time: Long?): Boolean {
                Thread.sleep(time[0] ?: 3000)
                return true
            }

            override fun onPostExecute(result: Boolean?) {
                UserRepo.signIn(applicationContext, signInModel.username)
                showTopics()
            }
        }
        task.execute(5000)
        */
    }

}