package app.helloteam.sportsbuddyapp.views

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.helloteam.sportsbuddyapp.R
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

@SuppressLint("StaticFieldLeak")
private lateinit var googleSignInClient: GoogleSignInClient
private lateinit var auth: FirebaseAuth

//
//class LoginActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            TeamUpTheme {
//                LoginPage()
//            }
//        }
//    }
//}
//
//
//@Preview
//@Composable
//fun LoginScreenPreview() {
//    TeamUpTheme {
//        LoginPage()
//    }
//}
//
//
//@Composable
//fun LoginPage() {
//    val logo = painterResource(id = R.drawable.logoteamupsmall)
//    val emailValue = remember { mutableStateOf("") }
//    val passwordValue = remember { mutableStateOf("") }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black),
//        contentAlignment = Alignment.TopCenter
//    ) {
//        Image(logo, null)
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color.Black)
//    )
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center,
//        modifier = Modifier
//            .fillMaxWidth()
//            .fillMaxHeight()
//    ) {
//        Text(
//            text = "Sign In",
//            style = MaterialTheme.typography.h2,
//            color = Color.Black
//        )
//        Spacer(modifier = Modifier.padding(20.dp))
//        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//            OutlinedTextField(
//                value = emailValue.value,
//                onValueChange = { emailValue.value = it },
//                label = { Text(text = "Email Address") },
//                placeholder = { Text(text = "Email Address") },
//                singleLine = true,
//                modifier = Modifier.fillMaxWidth(9f)
//            )
//
//            OutlinedTextField(
//                value = passwordValue.value,
//                onValueChange = { passwordValue.value = it },
//                visualTransformation = PasswordVisualTransformation(),
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
//                label = { Text(text = "Password") },
//                placeholder = { Text(text = "Password") },
//                singleLine = true,
//                modifier = Modifier.fillMaxWidth(9f)
//            )
//
//            Spacer(modifier = Modifier.padding(10.dp))
//            Button(
//                onClick = {},
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(50.dp)
//            ) {
//                Text(text = "Sign In", style = MaterialTheme.typography.h1)
//            }
//
//            Spacer(modifier = Modifier.padding(20.dp))
//            Text(text = "Create an Account", modifier = Modifier.clickable { })
//            Spacer(modifier = Modifier.padding(20.dp))
//
//            Button(
//                onClick = {},
//                modifier = Modifier
//                    .padding(10.dp)
//                    .height(30.dp)
//            ) {
//                Text(text = "Sign In With Google", color = Color.Black)
//            }
//        }
//    }
//}


class LoginActivity : AppCompatActivity() {
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth

        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) // set up google sign in
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //Sign Up button is pressed
        findViewById<SignInButton>(R.id.googleSignIn).setOnClickListener {//go to sign up activity
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        var user = FirebaseAuth.getInstance().getCurrentUser()?.uid
        val db = Firebase.firestore
        var testUser = false
        db.collection("User")
            .document(user.toString())
            .get()
            .addOnSuccessListener { document ->
                testUser = document.get("testUser").toString().toBoolean()
                if (user != null && (FirebaseAuth.getInstance().currentUser?.isEmailVerified == true || testUser)) {
                    toLanding()
                }
            }


        //Sign Up button is pressed
        findViewById<TextView>(R.id.signUpButtonMain).setOnClickListener {//go to sign up activity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        //login button pressed
        findViewById<Button>(R.id.LoginButton).setOnClickListener {

            val emailTxt = findViewById<TextView>(R.id.emailText).text.toString()
            val passwordTxt = findViewById<TextView>(R.id.PasswordText).text.toString()

            if (emailTxt.equals("") || passwordTxt.equals("")) {
                Toast.makeText(this, "Please enter required fields", Toast.LENGTH_SHORT).show()
            } else {

                //login with firebase Auth
                FirebaseAuth.getInstance().signInWithEmailAndPassword(emailTxt, passwordTxt)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.i(
                                "LOG_TAG",
                                "Login Successful with uid of: " + it.result.user?.uid
                            )
                            db.collection("User")
                                .document(it.result.user?.uid.toString())
                                .get()
                                .addOnSuccessListener { document ->
                                    testUser = document.get("testUser").toString().toBoolean()
                                    if (Firebase.auth.currentUser?.isEmailVerified == true || testUser) {
                                        toLanding()
                                    } else {
                                        Toast.makeText(this, "Verify Email", Toast.LENGTH_LONG)
                                            .show()
                                    }
                                }
                        }
                    }.addOnFailureListener {
                        Log.i(
                            "LOG_TAG",
                            "Login failed: " + it.localizedMessage
                        )
                        Toast.makeText(this, "Login Error ${it.message} ", Toast.LENGTH_SHORT)
                            .show()
                    }
            }
        }
    }

    //go to landing activity when called
    fun toLanding() {

        supportFragmentManager.beginTransaction()
            .replace(R.id.compose_view, LandingPage())
            .commit()

//        val intent = Intent(this, LandingPageActivity::class.java)
//        startActivity(intent)
        finish()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) { //go to google sign in
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) { //google authentication
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    val userHashMap = hashMapOf(
                        "userName" to user?.displayName,
                        "userEmail" to user?.email,
                        "testUser" to false,
                        "dateCreated" to Timestamp(Date()),
                        "favouriteSport" to "none",
                        "distance" to 20
                    )
                    Firebase.firestore.collection("User")
                        .document(FirebaseAuth.getInstance().uid.toString()).get()
                        .addOnSuccessListener { x ->
                            if (!x.exists()) {
                                Firebase.firestore.collection("User")
                                    .document(FirebaseAuth.getInstance().uid.toString())
                                    .set(userHashMap, SetOptions.merge())
                                    .addOnSuccessListener {
                                        val friend = hashMapOf(
                                            "user" to FirebaseAuth.getInstance().currentUser?.uid.toString(),
                                        )
                                        var u = Firebase.firestore.collection("User")
                                            .document(FirebaseAuth.getInstance().uid.toString())
                                            .collection("FriendCode")
                                        u.add(friend).addOnSuccessListener { friend ->
                                            friend.update("code", friend.id.takeLast(6))
                                        }
                                        Log.d("CreatingEvent", "Created new user")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w("a", "Error creating location document", e)
                                    }
                            }
                            toLanding()
                        }


                }
            }
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}