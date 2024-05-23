package ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    title: String,
    btnText: String,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraSmall
    ) {

        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Icon(modifier = Modifier.align(Alignment.CenterHorizontally).size(40.dp), imageVector = Icons.Default.Info, contentDescription = "")
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onClick() }) {
                Icon(modifier = Modifier.size(20.dp), imageVector = Icons.Default.Add, contentDescription = "")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = btnText, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}