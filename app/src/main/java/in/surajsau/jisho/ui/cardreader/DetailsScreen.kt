package `in`.surajsau.jisho.ui.cardreader

import `in`.surajsau.jisho.base.use
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DetailsScreen(
    modifier: Modifier = Modifier,
    screen: CardReaderViewModel.Screen,
    navigateBack: () -> Unit,
) {

    val (_, event) = use(
        viewModel = LocalOnboardingViewModel.current,
        initialStateValue = CardReaderViewModel.State(),
    )

    var membershipNumber by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var degree by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }

    LaunchedEffect(screen) {
        if (screen is CardReaderViewModel.Screen.FilledDetails) {
            membershipNumber = screen.front.membershipNumber
            name = screen.front.name
            year = screen.front.year
            degree = screen.front.degree
            dob = screen.front.dateOfBirth.toString()

            address = screen.back.address
            mobileNumber = screen.back.mobileNumber
            email = screen.back.email
        }
    }
    
    Column(modifier = modifier) {

        TopAppBar(backgroundColor = Color.White, elevation = 0.dp) {

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {
                IconButton(onClick = { navigateBack.invoke() }) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "back")
                }

                Text(text = screen.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = name,
                onValueChange = { name = it },
                label = { Text(text = "Name") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = membershipNumber,
                onValueChange = { membershipNumber = it },
                label = { Text(text = "Membership No.") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {

                TextField(
                    modifier = Modifier.weight(3f),
                    value = degree,
                    onValueChange = { degree = it },
                    label = { Text(text = "Degree") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Characters,
                    ),
                )

                TextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                    value = year,
                    onValueChange = { year = it },
                    label = { Text(text = "Year") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = address,
                label = { Text(text = "Address") },
                onValueChange = { address = it },
                maxLines = 3,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    modifier = Modifier.weight(1f),
                    value = mobileNumber,
                    onValueChange = { mobileNumber = it },
                    label = { Text(text = "Mobile No.") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                Spacer(modifier = Modifier.width(4.dp))

                TextField(
                    modifier = Modifier.weight(1f),
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = "Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = dob,
                onValueChange = { dob = it },
                label = { Text(text = "Date of Birth") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                onClick = { event(CardReaderViewModel.Event.ConfirmClicked) }
            ) {
                Text(text = "Confirm")
            }
        }
    }

}