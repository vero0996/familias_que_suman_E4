# Evidencia de ejecución de pruebas

Fecha de ejecución: 2026-10-21 16:02:54

## Backend
Comando: .\.venv\Scripts\python.exe -m pytest .\backend -q
Resultado: 233 passed, 3 skipped
Código de salida: 0

## Android - pruebas unitarias
Comando: .\gradlew.bat testDebugUnitTest
Resultado: 23 pruebas, 0 fallidas, 0 errores, 0 omitidas. BUILD SUCCESSFUL

## Android - pruebas instrumentadas
Comando: .\gradlew.bat connectedDebugAndroidTest
Dispositivo: Pixel_10 (AVD) - 17
Resultado: 17 pruebas completadas, 0 fallidas, 0 omitidas
Estado de Gradle: BUILD SUCCESSFUL

