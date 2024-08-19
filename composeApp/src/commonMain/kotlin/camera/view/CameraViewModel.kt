package camera.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import kotlinx.coroutines.launch

class CameraViewModel(private val permissionsController: PermissionsController): ViewModel() {
    fun onTakePhotoPressed() {
        viewModelScope.launch {
            try {
                permissionsController.providePermission(Permission.CAMERA)
            } catch (deniedAlways: DeniedAlwaysException) {
                

            } catch (denied: DeniedException) {

            }
        }
    }
}