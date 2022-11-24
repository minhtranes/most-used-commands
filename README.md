# Most used commands

## Work with AWS Code Artifact with MFA enabled
### Prerequisites
+ Open JRE/JDK 11+
+ Windows Command Prompt
### Get Started
With this we need to get the session token from Security Token Service (STS). Then, the session token will be used to obtain the authorization code, used for exchange the Code Artifact token.

From any location, trigger the following batch script from a Command Prompt

```
obtain_codeartifact_token.bat
```
The script prompt you for entering the MFA token. Just enter the OTP and hit enter

```
Enter MFA Token: 
```

After execution, it shows you the message of setting env variable ```CODEARTIFACT_AUTH_TOKEN``` successfully. Then, you can go ahead and use the token within your Maven ```setting.xml```