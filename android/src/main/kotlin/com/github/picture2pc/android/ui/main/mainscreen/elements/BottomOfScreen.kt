package com.github.picture2pc.android.ui.main.mainscreen.elements

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.github.picture2pc.android.R
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel
import com.github.picture2pc.android.viewmodel.screenselectorviewmodels.ScreenSelectorViewModel
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.TextStyles
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.rememberKoinInject
import org.koin.core.qualifier.named

@Composable
fun BottomOfScreen(
    screenSelectorViewModel: ScreenSelectorViewModel = rememberKoinInject(),
    cameraViewModel: CameraViewModel = rememberKoinInject(),
    coroutineScope: CoroutineScope = rememberKoinInject<CoroutineScope>(
        named("backgroundCoroutineScope")
    ),
    ioDispatcher: CoroutineDispatcher = rememberKoinInject<CoroutineDispatcher>(
        named("ioDispatcher")
    ),
) {
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri == null) return@rememberLauncherForActivityResult
            coroutineScope.launch(ioDispatcher) {
                val loader = ImageLoader(context)
                val request = ImageRequest.Builder(context).data(uri).build()

                val result = (loader.execute(request) as SuccessResult).drawable
                bitmap.value = (result as BitmapDrawable).bitmap

                bitmap.value?.let { cameraViewModel.injectImage(it) }
                screenSelectorViewModel.toBigPicture()
            }
        }
    )

    Row(modifier = Modifier.fillMaxWidth()) {
        IconButton(
            onClick = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            modifier = Modifier,
            colors = IconButtonDefaults.iconButtonColors(Colors.PRIMARY)
        ) {
            Icon(
                painter = painterResource(R.drawable.photo_library),
                contentDescription = "Gallery",
                tint = Colors.TEXT
            )
        }
        Button(
            onClick = screenSelectorViewModel::toCamera,
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(Colors.PRIMARY),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Take Picture",
                style = TextStyles.NORMAL
            )
        }
    }
}

