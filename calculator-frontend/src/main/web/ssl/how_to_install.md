# How to install the certs.
## Windows 10
1. Open the folder in the Windows Explorer
1. Double click on the certificate (server.crt)
1. Click on the button “Install Certificate …”
1. Select whether you want to store it on user level or on machine level
1. Click “Next”
1. Select “Place all certificates in the following store”
1. Click “Browse”
1. Select “Trusted Root Certification Authorities”
1. Click “Ok”
1. Click “Next”
1. Click “Finish”
1. If you get a prompt, click “Yes”
1. The certificate is now installed.

### Troubleshooting
1.  If you have previously installed certs for the localhost it may not play well with new ones. To remove them from Chrome go to Settings -> (Advanced) -> Manage certificates, after click you will see a tool window, examine both Intermediate and Trusted Root certification authorities tabs and remove all uniportal and localhost certs. To remove them from the Windows launch `certmgr.msc` and do the same.
1. Generate new certs by running `generate-cedrtificate.sh`. _It will create cert files (with The given non-default config `openssl-custom.cnf`) that will be served by the angular dev server and be passed to the Chrome upon navigation, the Chrome will compare them to the ones it loaded from the Windows on startup._
1. Install the certs (following the above instarction).
1. Don't forget to restart the Chrome.

## OS X

1. Double click on the certificate (server.crt)
1. Select your desired keychain (login should suffice)
1. Add the certificate
1. Open Keychain Access if it isn’t already open
1. Select the keychain you chose earlier
1. You should see the certificate localhost
1. Double click on the certificate
1. Expand Trust
1. Select the option Always Trust in When using this certificate
1. Close the certificate window
1. The certificate is now installed.
