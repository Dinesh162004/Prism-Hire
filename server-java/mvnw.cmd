@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET __MVNW_CMD__=
@SET __MVNW_ERROR__=
@SET __MVNW_PSMODULEP_SAVE=%PSModulePath%
@SET PSModulePath=
@FOR /F "usebackq tokens=1* delims==" %%A IN (`powershell -noprofile "& {$scriptDir='%~dp0'; $script='%~dp0mvnw.ps1'; if (Test-Path $script) { & $script @args } else { Write-Error 'Cannot find mvnw.ps1' }}" %*`) DO @(
  IF "%%A"=="MVN_CMD" (set __MVNW_CMD__=%%B)
  IF "%%A"=="MVN_ERROR" (set __MVNW_ERROR__=%%B)
)
@SET PSModulePath=%__MVNW_PSMODULEP_SAVE%
@SET __MVNW_PSMODULEP_SAVE=
@IF NOT "%__MVNW_CMD__%"=="" (%__MVNW_CMD__%)
@IF NOT "%__MVNW_ERROR__%"=="" (echo %__MVNW_ERROR__%)
