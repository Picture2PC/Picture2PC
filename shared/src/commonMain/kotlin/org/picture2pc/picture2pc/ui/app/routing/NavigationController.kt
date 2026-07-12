package org.picture2pc.picture2pc.ui.app.routing

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.picture2pc.picture2pc.ui.app.screens.App
import org.picture2pc.picture2pc.ui.app.screens.CameraScreen
import org.picture2pc.picture2pc.ui.app.screens.GroupSelect
import org.picture2pc.picture2pc.ui.app.screens.RoomSelect

@Composable
fun NavigationController() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.GROUP_SELECT,
    ) {
        composable(Routes.GROUP_SELECT) { GroupSelect(navController) }
        composable(Routes.ROOM_SELECT) { RoomSelect(navController) }
        composable(Routes.CAMERA_SCREEN) { CameraScreen(navController) }
        composable(Routes.TESTING_APP) { App() }
    }
}
