# Most used commands

## Work with AWS Code Artifact with MFA enabled
With this we need to get the session token from Security Token Service (STS). Then, the session token will be used to obtain the authorization code, used for exchange the Code Artifact token.

From any location, trigger the following batch script from a Command Prompt

```
obtain_codeartifact_token.bat
```
After execution, it shows you the message of setting env variable ```CODEARTIFACT_AUTH_TOKEN``` successfully. Then, you can go ahead and use the token within your Maven ```setting.xml```