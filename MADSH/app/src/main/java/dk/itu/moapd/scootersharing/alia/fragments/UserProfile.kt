package dk.itu.moapd.scootersharing.alia.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.alia.R


class UserProfile : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth.
        auth = Firebase.auth
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.baseline_person2_24),
                        contentDescription = stringResource(R.string.user_profile),
                        contentScale = ContentScale.Fit
                    )

                    Text(
                        text = auth.currentUser?.displayName ?: "",
                        fontWeight = Bold,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )

                    Text(
                        text = auth.currentUser?.email ?: "",
                        fontWeight = Bold,
                        modifier = Modifier.padding(bottom = 50.dp)
                    )

                    Button(
                        onClick = { /* Handle button click */ },
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                            .width(250.dp)
                            .height(53.dp),
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp, pressedElevation = 2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.change_mail),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { /* Handle button click */ },
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                            .width(250.dp)
                            .height(53.dp),
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp, pressedElevation = 2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.change_password),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { /* Handle button click */ },
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                            .width(250.dp)
                            .height(53.dp),
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp, pressedElevation = 2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.payment),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { /* Handle button click */ },
                        modifier = Modifier
                            .width(250.dp)
                            .height(53.dp),
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp, pressedElevation = 2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.settings),
                            fontWeight = FontWeight.Bold
                        )
                    }


                }

            }
        }
    }


}