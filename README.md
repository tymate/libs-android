# libs-android
Set of useful Android librairies

- Image viewer
- Image picker
- Kotlin Core :
    - Databinding
    - Rx
    - Pagination
    - Base views
    - Base viewModels
    - Navigation
    - Errors
    - Utils
    
# Installation

```gradle
allprojects {
    repositories {
	    ...
		maven { url 'https://jitpack.io' }
	}
}
```

```gradle
dependencies {
    implementation 'com.github.tymate.libs-android:image_picker:v1.0.0'
    implementation 'com.github.tymate.libs-android:image_viewer:v1.0.0'
    implementation 'com.github.tymate.libs-android:kt_core:v1.0.0'
}
```
