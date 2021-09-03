## Step 1 - Create powershell profile

*open a new powershell, run the following commands:* 
```
#create a profile file
New-item –type file –force $profile`

#open profile in notepad, save/exit
Notepad $profile 

# allow profile to be loaded
Set-ExecutionPolicy RemoteSigned

# load in the new profile. try du command to verify
. $profile

# get ssh
Add-WindowsCapability -Online -Name OpenSSH.Client~~~~0.0.1.0
```



## Step 2 - Install choco packaging, ref https://chocolatey.org/install

```
# allow loading of remote install
Set-ExecutionPolicy Bypass -Scope Process -Force;

# run installer, Note iex is short for Invoke-Expression
iex ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))
```


## Step 3 - Install recommended packages

*Using powershell, run the following:*
```
choco install --yes notepadplusplus 
choco install --yes 7zip
choco install --yes treesizefree
choco install --yes virtualbox
choco install --yes sysinternals
choco install --yes cygwin cyg-get 
choco install --yes git
choco install --yes openjdk11
choco install --yes git wget rsync bind-toolsonly

# quick commands
choco list --local-only
choco uninstall <...>
choco update <...>


```

## Step 3 - Install sts



