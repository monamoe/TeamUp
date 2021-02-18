package app.helloteam.sportsbuddyapp

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.parse.ParseUser
import java.lang.Exception
import java.lang.reflect.InvocationTargetException

object UserHandling {


    fun Login( userNameTxt: String,  passwordTxt: String, context:Context): Boolean {
        var success= false
        try {
            ParseUser.logIn(userNameTxt, passwordTxt)
            success=true
        }catch (ex: Exception){
            Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()

        }

        return success
    }


    fun SignUp( userNameTxt: String,  passwordTxt: String, email: String, context:Context): Boolean {
        var success= false;
        try{
        val user = ParseUser()
        user.username = userNameTxt
        user.setPassword(passwordTxt)
        user.email=email
        user.signUp()
        success=true
    }catch (ex: Exception){
        Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()

    }
        return success
    }

    fun Logout(){//logs out user
        ParseUser.logOut()
    }



}