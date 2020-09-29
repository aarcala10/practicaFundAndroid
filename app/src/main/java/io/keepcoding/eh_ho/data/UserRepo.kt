package io.keepcoding.eh_ho.data

import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import io.keepcoding.eh_ho.R
import java.util.*

const val PREFERENCES_SESSION = "session"
const val PREFERENCES_USERNAME = "username"

object UserRepo {

    fun signIn(
        context: Context,
        signInModel: SignInModel,
        success: (SignInModel) -> Unit,
        error: (RequestError) -> Unit,
    ) {

        val request = JsonObjectRequest(
            Request.Method.GET,
            ApiRoutes.signIn(signInModel.username),
            null,
            {
                success(signInModel)
                saveSession(context, signInModel.username)
            },
            { e: VolleyError ->
                e.printStackTrace()

                val errorObject = if (e is ServerError && e.networkResponse.statusCode == 404) {
                    RequestError(e, messageResId = R.string.error_not_registered)

                } else if (e is NetworkError) {
                    RequestError(e, messageResId = R.string.error_no_internet)
                } else {
                    RequestError(e, messageResId = null)
                }
                error(errorObject)
            }
        )
        // Cola de petición
        ApiRequestQueue.getRequestQueue(context).add(request)

        // Pedir permisos de acceso a internet

    }

    fun signUp(
        context: Context,
        signUpModel: SignUpModel,
        success: (SignUpModel) -> Unit,
        error: (RequestError) -> Unit
    ) {
        val request = PostRequest(
            Request.Method.POST,
            ApiRoutes.signUp(),
            signUpModel.toJson(),
            { response ->
                val successStatus = response?.getBoolean("success") ?: false
                if (successStatus) {
                    success(signUpModel)
                } else {
                    error(RequestError(message = response?.getString("message")))
                }
            },
            { e ->
                e.printStackTrace()

                val requestError =
                    if (error is NetworkError)
                        RequestError(e, messageResId = R.string.error_no_internet)
                    else
                        RequestError(e)

                error(requestError)

            },
            null,
            false,

        )

        ApiRequestQueue
            .getRequestQueue(context)
            .add((request))
    }

    private fun saveSession(context: Context, username: String) {
        val preferences = context.getSharedPreferences(PREFERENCES_SESSION, Context.MODE_PRIVATE)
        preferences
            .edit()
            .putString(PREFERENCES_USERNAME, username)
            .apply()
    }

    fun getUsername(context: Context): String? {
        val preferences = context.getSharedPreferences(PREFERENCES_SESSION, Context.MODE_PRIVATE)
        return preferences.getString(PREFERENCES_USERNAME, null)

    }

    fun logout(context: Context) {
        val preferences = context.getSharedPreferences(PREFERENCES_SESSION, Context.MODE_PRIVATE)
        preferences
            .edit()
            .putString(PREFERENCES_USERNAME, null)
            .apply()

    }

    fun isLogged(context: Context): Boolean {
        val preferences = context.getSharedPreferences(PREFERENCES_SESSION, Context.MODE_PRIVATE)
        val username = preferences.getString(PREFERENCES_USERNAME, null)
        return username != null
    }
}