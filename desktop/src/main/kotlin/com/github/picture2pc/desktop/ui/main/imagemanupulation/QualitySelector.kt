
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment

val shape = RoundedCornerShape(20)

@Composable
fun QualitySelector() {
    val options = listOf("Low", "Medium", "High")
    val expanded = remember { mutableStateOf(false) }
    val text = remember { mutableStateOf(options.first()) }

    Row(verticalAlignment = Alignment.CenterVertically){
        Text("Output Quality")
    Button(onClick = { expanded.value = true }, shape = shape) {
        Text(text.value)
    }  }
    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false }
    ) {
        options.forEach { label ->
            DropdownMenuItem(onClick = {
                expanded.value = false
                text.value = label
            }, text = { Text(label) })
        }
    }
}