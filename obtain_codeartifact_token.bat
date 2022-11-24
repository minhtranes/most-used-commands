@echo off 

set WORK_DIR=%~dp0
echo Work Dir: %WORK_DIR%
set JAR_FILE=%WORK_DIR%aws-security-token-session.jar
set TEMP_CREDENTIAL_JSON=%WORK_DIR%get_session_response.json
set /p "MFA_TOKEN=Enter MFA Token: "

del %TEMP_CREDENTIAL_JSON%

FOR /F "tokens=* USEBACKQ" %%F IN (`aws sts get-session-token --serial-number arn:aws:iam::253032955724:mfa/minh.tran --output json --token-code %MFA_TOKEN%`) DO (
echo %%F>>%TEMP_CREDENTIAL_JSON%
)


@REM ECHO %GET_SESSION_TOKEN_RESPONSE%>>get_session_response.json

java -jar %JAR_FILE% %TEMP_CREDENTIAL_JSON%
@REM set x=Sample
@REM echo %x:~0,-1%

@REM for /f "tokens=1,2 delims=:{} " %%A in (get_session_response.json) do (
@REM     @REM If "%%~A"=="AccessKeyId" Echo %%~B
@REM     @REM call :strLen %%~B bLen
@REM     @REM echo %%~B:~0,-2%
@REM     echo %%B:~0,-1%
@REM     echo %%A : %III%
@REM )

FOR /F "tokens=* USEBACKQ" %%F IN (`aws codeartifact get-authorization-token --domain cdipp --domain-owner 253032955724 --region eu-central-1 --query authorizationToken --output text --profile mfa`) DO (
SET CODEARTIFACT_AUTH_TOKEN=%%F
)
echo -----------------------
ECHO %CODEARTIFACT_AUTH_TOKEN%
echo -----------------------
echo ``
echo Set env variable CODEARTIFACT_AUTH_TOKEN successfully