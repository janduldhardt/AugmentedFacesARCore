# AugmentedFacesARCore
This is my term project for Computer Vision / Intelligent System Development 2/2019.

Check out [Presentation.pdf](https://github.com/janduldhardt/AugmentedFacesARCore/blob/master/Presentation.pdf) 
or the presentation [video](https://drive.google.com/file/d/18GuDxeR4XwS8XtEvhr0xqwwJsLcqDcIM/view?fbclid=IwAR0dr-yAUX-A4CDTcAsNsEbAilOEclBi_zDvf5Y01vi7T7mnXsktkhYxMhA)

## Goal
The goal is to detect faces and apply graphics on those detected faces.
For implementation this Android application written in Kotlin uses ARCore SDK by Google.

## Tools used
- Kotlin
- Android Studio 3.5 (will not work with newer version)
- Sceneform 1.15
- CircleImageView by [hdodenhof](https://github.com/hdodenhof/CircleImageView)

## Requirements to use the app
This application needs to use OpenGL version 3.0 or higher and needs to support ARCore
[List of supported devices](https://developers.google.com/ar/discover/supported-devices)

## Features
The app allows the user two ways of applying graphics:
- Apply via facial landmarks
- Apply based on face mesh

![alt text](https://github.com/janduldhardt/AugmentedFacesARCore/blob/master/Sample1.png?raw=true)


